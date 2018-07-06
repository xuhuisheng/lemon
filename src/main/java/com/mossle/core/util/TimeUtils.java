package com.mossle.core.util;

import java.util.Date;

public class TimeUtils {
    public static boolean timeCross(Date startTime1, Date endTime1,
            Date startTime2, Date endTime2) {
        return timeCross(startTime1.getTime(), endTime1.getTime(),
                startTime2.getTime(), endTime2.getTime());
    }

    public static boolean timeCross(long startTime1, long endTime1,
            long startTime2, long endTime2) {
        // 时间边界判断，因为每个时间段的startTime必然早于（小于）endTime。
        // 视作数据已经预先排序
        // 只要保证start1（一个时间段的最小值）大于end2（另一个时间段的最大值）
        // 或者end1（一个时间段的最大值）小于start2（另一个时间段的最小值）
        // 就可以保证两个时间段不相交。
        return (startTime1 < endTime2) && (endTime1 > startTime2);
    }
}
