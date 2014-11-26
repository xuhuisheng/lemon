

INSERT INTO TEMPLATE_INFO(ID,NAME,CODE) VALUES(1,'任务到达（任务处理人）','arrival-assignee');
INSERT INTO TEMPLATE_INFO(ID,NAME,CODE) VALUES(2,'任务到达（流程发起人）','arrival-initiator');
INSERT INTO TEMPLATE_INFO(ID,NAME,CODE) VALUES(3,'任务完成','complete');
INSERT INTO TEMPLATE_INFO(ID,NAME,CODE) VALUES(4,'任务超时提醒','timeout');

INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(1,1,'subject','您有新任务需要处理');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(2,1,'content','${task.assignee}您好，您有新任务需要处理——${task.name}。');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(3,2,'subject','您的流程已经到达${task.name}环节');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(4,2,'content','${initiator}您好，您的流程已经到达${task.name}环节');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(5,3,'subject','流程已经完成${task.name}环节');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(6,3,'content','您好，${initiator}的流程已经完成${task.name}环节');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(7,4,'subject','任务即将过期');
INSERT INTO TEMPLATE_FIELD(ID,INFO_ID,NAME,CONTENT) VALUES(8,4,'content','${task.assignee}您好，任务${task.name}已经即将到期');
