package com.mossle.core.id;

/*
 1
 0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000

 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，然后5位datacenter标识位，5位机器ID（并不算标识符，实际是为线程标识），然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。

 这样的好处是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和机器ID作区分），并且效率较高，经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 */
public class SnowFlakeIdGenerator implements IdGenerator {
    private static final long workerIdBits = 4L;
    public static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static final long sequenceBits = 10L;
    private static final long workerIdShift = sequenceBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits;
    public static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // private static final long twepoch = 1361753741828L;
    // (2016 - 1970) * 365 * 24 * 60 * 60 * 1000
    // private long twepoch = 1450656000000L;
    // 2016-2-14 11:08
    private long twepoch = 1455419300740L;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlakeIdGenerator(final long workerId) {
        super();

        if ((workerId > this.maxWorkerId) || (workerId < 0)) {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0",
                    this.maxWorkerId));
        }

        this.workerId = workerId;
    }

    public long generateId() {
        return this.nextId();
    }

    public long generateId(String name) {
        return this.generateId();
    }

    public long generateId(Class<?> clz) {
        return this.generateId();
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();

        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & this.sequenceMask;

            if (this.sequence == 0) {
                System.out.println("###########" + sequenceMask);

                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(
                        String.format(
                                "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                                this.lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.lastTimestamp = timestamp;

        long nextId = ((timestamp - twepoch) << timestampLeftShift)
                | (this.workerId << this.workerIdShift) | (this.sequence);

        // System.out.println("timestamp:" + timestamp + ",timestampLeftShift:"

        // + timestampLeftShift + ",nextId:" + nextId + ",workerId:"

        // + workerId + ",sequence:" + sequence);
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }

        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public void setTwepoch(long twepoch) {
        this.twepoch = twepoch;
    }

    public static void main(String[] args) {
        SnowFlakeIdGenerator worker2 = new SnowFlakeIdGenerator(2);

        System.out.println(worker2.nextId());
    }
}
