package cn.algo.yu;

public class IpUtil {
    public static long ip2long(String ip) {
        String[] fields = ip.split("\\.");
        if (fields.length != 4) {
            return 0L;
        }
        return Long.parseLong(fields[0]) << 24 | Long.parseLong(fields[1]) << 16 | Long.parseLong(fields[2]) << 8 | Long.parseLong(fields[3]);
    }

    public static String long2ip(long ip) {
        int[] b = new int[4];
        b[0] = (int) ((ip >> 24) & 0xff);
        b[1] = (int) ((ip >> 16) & 0xff);
        b[2] = (int) ((ip >> 8) & 0xff);
        b[3] = (int) (ip & 0xff);
        return Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
    }
}
