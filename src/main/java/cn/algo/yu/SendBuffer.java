package cn.algo.yu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;

/**
 * 这是一个容器
 * 可以按固定的速度或者最大size进行操作
 * 构造函数传入尺寸，延迟（毫秒），以及处理函数（lambda）就可以。
 * 返回值是处理的布尔值
 * @param <T>
 */
public class SendBuffer<T> {
    private List<T> data;
    private final ReentrantLock lock;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long delay;
    private final BiPredicate<List<T>, Action> consumeFunction;
    private final int size;

    public SendBuffer(int size, long delay, BiPredicate<List<T>, Action> consumeFunction) {
        // 最小1个
        this.size = Math.max(size, 1);
        // 最小100毫秒
        this.delay = Math.max(delay, 100L);
        // 到达尺寸和时间后的处理方法
        this.consumeFunction = consumeFunction;
        data = new ArrayList<>(size);
        lock = new ReentrantLock();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledTaskStart();
    }

    public boolean add(T t) {
        if (t == null) {
            return false;
        }
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                return addAndCompute(t);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 添加到容器里，若达到发送尺寸则调用发送函数进行发送
     *
     * @param t
     * @return 处理结果
     */
    private boolean addAndCompute(T t) {
        data.add(t);
        boolean success = true;
        if (data.size() >= size) {
            List<T> copy = copyAndClean();
            lock.unlock();
            success = consumeFunction.test(copy, Action.SEND);
        }
        return success;
    }

    /**
     * 重新建立一个容器已供添加
     *
     * @return
     */
    private List<T> copyAndClean() {
        List<T> copy = data;
        data = new ArrayList<>(size);
        return copy;
    }

    public boolean addList(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            return false;
        }
        try {
            if (lock.tryLock(1000, TimeUnit.MINUTES)) {
                // 以遍历的方式添加
                for (T t : ts) {
                    /*
                     * 分成了多次消费，这里比较难处理
                     * 其中如果有包含消费失败的，整体该算成功还是失败，还得根据具体场景来看。
                     * 考虑到使用这个容器的多是日志、埋点，丢失部分也没有关系。
                     * 这里不先做复杂，不抛异常先按成功算。
                     */
                    addAndCompute(t);
                }
                return true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 启动定时任务
     */
    private void scheduledTaskStart() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS) && !data.isEmpty()) {
                    List<T> copy = copyAndClean();
                    lock.unlock();
                    consumeFunction.test(copy, Action.TASK);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    public void shutDown() {
        try {
            lock.lock();
            if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdown();
            }
            if (data.isEmpty()) {
                return;
            }
            List<T> copy = copyAndClean();
            consumeFunction.test(copy, Action.DESTROY);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 发送的动作来源
     */
    public static enum Action {
        // send函数还是定时任务
        SEND, TASK, DESTROY;
    }
}