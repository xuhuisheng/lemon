package com.mossle.humantask.support;

public class HumanTaskConstants {
    /** 无特殊分配策略. */
    public static final String TYPE_NONE = "none";

    /** 流程创建人. */
    public static final String TYPE_CREATOR = "creator";

    /** 流程发起人. */
    public static final String TYPE_INITIATOR = "initiator";

    /** 只有一个候选人时，自动抢占. */
    public static final String TYPE_UNIQUE_AUTO = "unqiue-auto";

    /** 随机分配. */
    public static final String TYPE_RANDOM = "random";

    /** 分配工作最少的候选人. */
    public static final String TYPE_IDLE = "idle";

    /** 指定分配人. */
    public static final String TYPE_ASSIGNEE = "assignee";
}
