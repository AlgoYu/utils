package cn.algo.yu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class SendContainer<T> {
    private List<T> data;
    private ReentrantLock lock;
    private ScheduledExecutorService scheduledExecutorService;
    private long delay;
    private Consumer<List<T>> consumer;
    private int size;

    public SendContainer(int size, long delay, Consumer<List<T>> consumer) {
        // 最小50个，最大10000个
        this.size = Math.min(Math.max(size, 50), 10000);
        // 最小200毫秒，最大3000毫秒
        this.delay = Math.min(Math.max(delay, 200L), 3000L);
        // 处理方法
        this.consumer = consumer;
        data = new ArrayList<>(size);
        lock = new ReentrantLock();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledTaskStart();
    }

    public void add(T t) {
        if (t == null) {
            return;
        }
        try {
            boolean isLock = lock.tryLock(1000, TimeUnit.MINUTES);
            if (!isLock) {
                return;
            }
            data.add(t);
            if (data.size() >= size) {
                List<T> copy = copyAndClean();
                lock.unlock();
                consumer.accept(copy);
                System.out.println("尺寸限制发送" + copy.size() + "个");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void addList(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            return;
        }
        try {
            boolean isLock = lock.tryLock(1000, TimeUnit.MINUTES);
            if (!isLock) {
                return;
            }
            data.addAll(ts);
            while (data.size() >= size) {
                List<T> copy = data.subList(0, size);
                consumer.accept(copy);
                data = data.subList(size, data.size());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void scheduledTaskStart() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                boolean isLock = lock.tryLock(100, TimeUnit.MILLISECONDS);
                if (!isLock || data.isEmpty()) {
                    return;
                }
                List<T> copy = copyAndClean();
                lock.unlock();
                consumer.accept(copy);
                System.out.println("定时任务发送" + copy.size() + "个");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    private List<T> copyAndClean() {
        List<T> copy = new ArrayList<>(data);
        data.clear();
        return copy;
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
            consumer.accept(copy);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}