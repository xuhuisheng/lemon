package com.mossle.humantask.listener;

import com.mossle.humantask.persistence.domain.TaskInfo;

public interface HumanTaskListener {
    void onCreate(TaskInfo taskInfo) throws Exception;

    void onComplete(TaskInfo taskInfo) throws Exception;
}
