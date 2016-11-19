package com.mossle.button;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonHelper {
    private static Logger logger = LoggerFactory.getLogger(ButtonHelper.class);
    private Map<String, ButtonDTO> map = new HashMap<String, ButtonDTO>();

    public ButtonHelper() {
        this.addButton("saveDraft", "保存草稿");
        this.addButton("taskConf", "配置任务");
        this.addButton("confirmStartProcess", "提交数据");
        this.addButton("startProcess", "发起流程");
        this.addButton("completeTask", "完成任务");

        this.addButton("claimTask", "认领任务");
        this.addButton("releaseTask", "释放任务");
        this.addButton("transfer", "转办");
        this.addButton("rollback", "退回");
        this.addButton("rollbackPrevious", "回退（上一步）");
        this.addButton("rollbackAssignee", "回退（指定负责人）");
        this.addButton("rollbackActivity", "回退（指定步骤）");
        this.addButton("rollbackActivityAssignee", "退回（指定步骤，指定负责人）");
        this.addButton("rollbackStart", "回退（开始节点）");
        this.addButton("rollbackInitiator", "回退（发起人）");
        this.addButton("delegateTask", "协办");
        this.addButton("delegateTaskCreate", "协办（链式）");
        this.addButton("resolveTask", "还回");
        this.addButton("endProcess", "终止流程");
        this.addButton("suspendProcess", "暂停流程");
        this.addButton("resumeProcess", "恢复流程");
        this.addButton("viewHistory", "查看流程状态");
        this.addButton("addCounterSign", "加签");
        this.addButton("jump", "自由跳转");
        this.addButton("reminder", "催办");
        this.addButton("withdraw", "撤销");

        this.addButton("communicate", "沟通");
        this.addButton("callback", "反馈");
    }

    public void addButton(String name, String label) {
        this.addButton(new ButtonDTO(name, label));
    }

    public void addButton(ButtonDTO buttonDto) {
        this.map.put(buttonDto.getName(), buttonDto);
    }

    public ButtonDTO findButton(String name) {
        ButtonDTO buttonDto = map.get(name);

        if (buttonDto == null) {
            logger.info("button {} not exists", name);
        }

        return buttonDto;
    }

    public Map<String, ButtonDTO> getMap() {
        return this.map;
    }
}
