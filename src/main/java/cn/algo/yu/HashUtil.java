package cn.algo.yu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具
 */
public class HashUtil {

    public static String MD5UpperCase16(String original) {
        String hash = MD5UpperCase32(original);
        return hash.equals("") ? "" : hash.toUpperCase();
    }

    /**
     * MD5哈希算法（16为小写）
     *
     * @return
     */
    public static String MD5LowerCase16(String original) {
        String hash = MD5LowerCase32(original);
        return hash == null || hash.equals("") ? "" : hash.substring(8, 24);
    }

    /**
     * MD5哈希算法（32位大写）
     *
     * @param original
     * @return
     */
    public static String MD5UpperCase32(String original) {
        return MD5LowerCase32(original).toUpperCase();
    }

    /**
     * MD5哈希算法（32位小写）
     *
     * @param original
     * @return
     */
    public static String MD5LowerCase32(String original) {
        String result = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(original.getBytes());

            byte[] b = md.digest();

            int i;

            StringBuffer buf = new StringBuffer();

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];

                if (i < 0)

                    i += 256;

                if (i < 16)

                    buf.append("0");

                buf.append(Integer.toHexString(i));

            }

            result = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
