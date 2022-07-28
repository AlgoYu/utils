package cn.algo.yu;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;

/**
 * 此工具类适用于AB测试中的计算。
 * 依赖commons-math3的包。
 * 虽然可以不依赖，但是考虑到commons-math3的严谨性，选择不重写。
 * 只有在commons-math3包中没有的计算，才会实现。
 */
public class AbTestingUtil {
    // 正态（高斯）分布
    private static final NormalDistribution normalDistribution = new NormalDistribution();

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
     * 获取一组样本数据的标准差
     *
     * @param data 数据
     * @return 标准差
     */
    public static double standardDeviation(double[] data) {
        return Math.sqrt(variance(data));
    }

    /**
     * 获取一组全体数据的标准差
     *
     * @param data 数据
     * @return 标准差
     */
    public static double populationDeviation(double[] data) {
        return Math.sqrt(populationVariance(data));
    }

    /**
     * 通过方差获取标准差
     *
     * @param variance 方差
     * @return 标准差
     */
    public static double standardDeviation(double variance) {
        return Math.sqrt(variance);
    }

    /**
     * 获取一组样本数据的置信区间的误差上下限值
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
     * 通过样本数量、标准差、置信水平获取置信区间上下限
     *
     * @param n                 样本数量
     * @param standardDeviation 标准差
     * @param confidence        置信水平
     * @return
     */
    public static double confidenceDifference(int n, double standardDeviation, Confidence confidence) {
        return confidence.value * standardDeviation / Math.sqrt(n);
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

    /**
     * 通过样本数量、标准差、置信水平获取置信区间
     *
     * @param n                 样本数量
     * @param mean              均值
     * @param standardDeviation 标准差
     * @param confidence        置信水平
     * @return
     */
    public static double[] confidenceInterval(int n, int mean, double standardDeviation, Confidence confidence) {
        double difference = confidenceDifference(n, standardDeviation, confidence);
        return new double[]{mean - difference, mean + difference};
    }

    /**
     * 当数据相互独立、是从两个正态分布的样本中随机抽样的、并且两个独立组有相等的方差时，您可以使用该检验。
     * 如果两组方差不等，该怎么办？
     * 您仍可以使用双样本 t 检验。可使用其他的标准差估计值。
     *
     * @param nA                 样本A的数量
     * @param nB                 样本B的数量
     * @param meanA              样本A的均值
     * @param meanB              样本B的均值
     * @param standardDeviationA 样本A的标准差
     * @param standardDeviationB 样本B的标准差
     * @return T统计量
     */
    public static double studentT(int nA, int nB, double meanA, double meanB, double standardDeviationA,
        double standardDeviationB) {
        double meanDiff = meanA - meanB;
        double denominator =
            (standardDeviationA * standardDeviationA / nA) + (standardDeviationB * standardDeviationB / nB);
        return meanDiff / Math.sqrt(denominator);
    }

    /**
     * 当数据相互独立、是从两个正态分布的样本中随机抽样的、并且两个独立组有相等的方差时，您可以使用该检验。
     * 如果两组方差不等，该怎么办？
     * 您仍可以使用双样本 t 检验。可使用其他的标准差估计值。
     *
     * @param nA        样本A的数量
     * @param nB        样本B的数量
     * @param meanA     样本A的均值
     * @param meanB     样本B的均值
     * @param varianceA 样本A的方差
     * @param varianceB 样本B的方差
     * @return T统计量
     */
    public static double studentT(double meanA, double meanB, double varianceA, double varianceB, int nA, int nB) {
        double meanDiff = meanA - meanB;
        double denominator = (varianceA / nA) + (varianceB / nB);
        return meanDiff / Math.sqrt(denominator);
    }

    /**
     * 根据T统计量计算P值
     *
     * @param tScore T统计量
     * @return P值
     */
    public static double getPValue(double tScore) {
        // For a random variable X whose values are distributed according to this distribution, this method returns P(X <= x).
        // 对tScore进行绝对值，拿分布图的右边计算面积
        double area = normalDistribution.cumulativeProbability(Math.abs(tScore));
        return (1 - area) * 2;
    }

    /**
     * 获取P值
     *
     * @param nA                 样本A的数量
     * @param nB                 样本B的数量
     * @param meanA              样本A的均值
     * @param meanB              样本B的均值
     * @param standardDeviationA 样本A的标准差
     * @param standardDeviationB 样本B的标准差
     * @return
     */
    public static double getPValue(int nA, int nB, double meanA, double meanB, double standardDeviationA,
        double standardDeviationB) {
        double tScore = studentT(nA, nB, meanA, meanB, standardDeviationA, standardDeviationB);
        return getPValue(tScore);
    }
}
