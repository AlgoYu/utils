package cn.algo.yu;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateUtil {
    /**
     * 获取指定的时区在指定时间时的零时区时间。
     */
    public static long getTimeStamp(int year, int month, int dayOfMonth, int hour, int minute, int second, String zone) {
        // 初始化相关时区的时间
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        ZonedDateTime utc = localDateTime.atZone(ZoneId.of(zone));
        // 转换为零时区
        ZonedDateTime zonedDateTime = utc.withZoneSameInstant(ZoneId.from(ZoneOffset.UTC));
        // 返回时间戳
        return Timestamp.valueOf(zonedDateTime.toLocalDateTime()).getTime();
    }
}
