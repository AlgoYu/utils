package cn.algo.yu;

import org.apache.commons.math3.stat.StatUtils;

/**
 * 此工具类适用于AB测试中的计算。
 * 依赖commons-math3的包。
 * 虽然可以不依赖，但是考虑到commons-math3的严谨性，选择不重写。
 * 只有在commons-math3包中没有的计算，才会实现。
 */
public class AbTestingUtil {
    /**
     * 置信水平常量
     */
    public enum Confidence {
        NinetyNinePointsNine(3.291D),// 99.9%
        NinetyNinePointsFive(2.807D), // 99.5%
        NinetyNine(2.5676D),// 99%
        NinetyFive(1.960D),// 95%
        Ninety(1.645D),// 90%
        EightyFive(1.440D),//85%
        Eighty(1.282D),//80%
        ;
        double value;

        Confidence(double value) {
            this.value = value;
        }
    }

    /**
     * 获取一组数据的平均值
     *
     * @param data 数据
     * @return 平均值
     */
    public static double mean(double[] data) {
        return StatUtils.mean(data);
    }

    /**
     * 此函数是求样本的方差
     * 样本方差最后是除以(n - 1)
     *
     * @param data 数据
     * @return 样本方差
     */
    public static double variance(double[] data) {
        return StatUtils.variance(data);
    }

    /**
     * 求总体数据的方差
     * 总体方差最后是除以n
     *
     * @param data 数据
     * @return 总体方差
     */
    public static double populationVariance(double[] data) {
        return StatUtils.populationVariance(data);
    }

    /**
     * 标准差计算函数
     *
     * @param data 数据
     * @return 标准差
     */
    public static double standardDeviation(double[] data) {
        return Math.sqrt(variance(data));
    }

    /**
     * 获取置信区间的误差上下限值
     *
     * @param data       数据
     * @param confidence 置信水平
     * @return 均值的+-误差界限值
     */
    public static double confidenceDifference(double[] data, Confidence confidence) {
        int n = data.length;
        double standard = standardDeviation(data);
        return confidence.value * standard / Math.sqrt(n);
    }

    /**
     * 获取置信区间
     *
     * @param data       数据
     * @param confidence 置信度
     * @return 置信区间[下限，上限]
     */
    public static double[] confidenceInterval(double[] data, Confidence confidence) {
        double mean = StatUtils.mean(data);
        double difference = confidenceDifference(data, confidence);
        return new double[]{mean - difference, mean + difference};
    }
}
