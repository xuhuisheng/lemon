package com.mossle.humantask.rule;

import java.util.List;

public interface AssigneeRule {
    // FIXME: 两个接口返回的值不一致，后续需要梳理规则
    String process(String initiator);

    List<String> process(String value, String initiator);
}
