package com.mossle.api.humantask;

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

    /** 普通状态. */
    public static final String CATALOG_NORMAL = "normal";

    /** 会签. */
    public static final String CATALOG_VOTE = "vote";

    /** 提交. */
    public static final String CATALOG_START = "start";

    /** 抄送. */
    public static final String CATALOG_COPY = "copy";

    /** 沟通. */
    public static final String CATALOG_COMMUNICATE = "communicate";
}
