package com.mossle.bpm.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.notice.ArrivalNotice;
import com.mossle.bpm.notice.CompleteNotice;
import com.mossle.bpm.notice.TimeoutNotice;
import com.mossle.bpm.persistence.domain.*;
import com.mossle.bpm.persistence.manager.*;
import com.mossle.bpm.support.DefaultTaskListener;

import com.mossle.ext.mail.MailFacade;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>
 * 任务到达提醒：xx您好，您有新任务需要处理。
 * </p>
 * <p>
 * 任务超时提醒：xx您好，您的任务还有xx时间即将过期，请尽快处理。
 * </p>
 * <p>
 * 提醒起草人：xx您好，您的流程已经到达xx环节，预计处理需要xx时间。
 * </p>
 * <p>
 * 提醒关键岗位：xx您好，xx任务已经交由xx处理，请知晓。
 * </p>
 * 
 * <p>
 * 超时提醒不是这个Listener里能判断的。
 * </p>
 */
public class NoticeTaskListener extends DefaultTaskListener {
    private ArrivalNotice arrivalNotice = new ArrivalNotice();
    private CompleteNotice completeNotice = new CompleteNotice();

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        arrivalNotice.process(delegateTask);
    }

    @Override
    public void onComplete(DelegateTask delegateTask) throws Exception {
        completeNotice.process(delegateTask);
    }
}
