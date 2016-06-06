
-- 任务到达提醒（处理人）
UPDATE TEMPLATE_FIELD SET CONTENT='${humanTask.presentationSubject}需要您审批' WHERE ID=1;
UPDATE TEMPLATE_FIELD SET CONTENT='${task.assignee}您好，您有新任务需要处理——${humanTask.presentationSubject}。<br><a href="${baseUrl}/operation/task-operation-viewTaskForm.do?humanTaskId=${humanTask.id?c}">${baseUrl}/operation/task-operation-viewTaskForm.do?humanTaskId=${humanTask.id?c}</a>' WHERE ID=2;

-- 任务到达提醒（发起人）
UPDATE TEMPLATE_FIELD SET CONTENT='您的流程${humanTask.presentationSubject}已经到达${task.name}环节' WHERE ID=3;
UPDATE TEMPLATE_FIELD SET CONTENT='${initiator}您好，您的流程${humanTask.presentationSubject}已经到达${task.name}环节。<br><a href="${baseUrl}/bpm/workspace-viewHistory.do?processInstanceId=${humanTask.processInstanceId}">${baseUrl}/bpm/workspace-viewHistory.do?processInstanceId=${humanTask.processInstanceId}</a>' WHERE ID=4;

-- 任务完成提醒（发起人）
UPDATE TEMPLATE_FIELD SET CONTENT='您的流程${humanTask.presentationSubject}已经完成${task.name}环节' WHERE ID=5;
UPDATE TEMPLATE_FIELD SET CONTENT='${initiator}您好，您的流程${humanTask.presentationSubject}已经完成${task.name}环节。<br><a href="${baseUrl}/bpm/workspace-viewHistory.do?processInstanceId=${humanTask.processInstanceId}">${baseUrl}/bpm/workspace-viewHistory.do?processInstanceId=${humanTask.processInstanceId}</a>' WHERE ID=6;

-- 任务超时提醒（处理人）
UPDATE TEMPLATE_FIELD SET CONTENT='您负责的任务${humanTask.presentationSubject}即将过期' WHERE ID=7;
UPDATE TEMPLATE_FIELD SET CONTENT='${task.assignee}您好，您负责的任务${humanTask.presentationSubject}即将过期。<br><a href="${baseUrl}/operation/task-operation-viewTaskForm.do?humanTaskId=${humanTask.id?c}">${baseUrl}/operation/task-operation-viewTaskForm.do?humanTaskId=${humanTask.id?c}</a>' WHERE ID=8;

