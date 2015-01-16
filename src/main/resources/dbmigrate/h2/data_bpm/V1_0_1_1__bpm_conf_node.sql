
UPDATE BPM_CONF_NODE SET code='usertask1',name='发起申请' where id=13;
UPDATE BPM_CONF_NODE SET code='usertask2',name='部门经理审批' where id=14;
UPDATE BPM_CONF_NODE SET code='endevent1' where id=15;
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(16,'usertask3','总经理审批','userTask',3,0,0,0,0,0,0);

INSERT INTO BPM_CONF_RULE(id,value,node_id) values(1,'职位',14);

INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(14,'saveDraft',16);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(15,'completeTask',16);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(16,'rollbackPrevious',16);
