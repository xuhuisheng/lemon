package com.mossle.bpm.listener;

import com.mossle.bpm.notice.ArrivalNotice;
import com.mossle.bpm.notice.CompleteNotice;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;

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
