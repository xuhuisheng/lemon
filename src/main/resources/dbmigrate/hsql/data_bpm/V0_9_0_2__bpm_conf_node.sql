
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(1,'vacation','全局','process',1,2,0,2,0,2,2);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(2,'vacation_1','填写请假单','startEvent',1,2,0,2,2,2,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(3,'taskuser-1','填写请假单','userTask',1,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(4,'taskuser-2','部门领导审批','userTask',1,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(5,'taskuser-3','人事审批','userTask',1,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(6,'taskuser-5','调整申请','userTask',1,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(7,'endnone-2','endnone-2','endEvent',1,2,0,2,2,2,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(8,'taskuser-6','销假','userTask',1,0,0,0,0,0,0);

INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(11,'permission','审批权限','process',3,2,0,2,0,2,2);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(12,'startEvent-1','','startEvent',3,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(13,'userTask-1','经理审批','userTask',3,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(14,'userTask-2','模块负责人审批','userTask',3,0,0,0,0,0,0);
INSERT INTO BPM_CONF_NODE(ID,code,name,type,conf_base_id,conf_user,conf_listener,conf_rule,conf_form,conf_operation,conf_notice) VALUES(15,'endEvent-1','','endEvent',3,2,0,2,2,2,0);

INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(1,'saveDraft',3);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(2,'completeTask',3);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(3,'saveDraft',4);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(4,'completeTask',4);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(5,'saveDraft',5);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(6,'completeTask',5);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(7,'saveDraft',6);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(8,'completeTask',6);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(9,'saveDraft',8);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(10,'completeTask',8);

INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(11,'saveDraft',14);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(12,'completeTask',14);
INSERT INTO BPM_CONF_OPERATION(id,value,node_id) values(13,'rollbackPrevious',14);

INSERT INTO BPM_CONF_NOTICE(id,type,receiver,due_date,node_id,template_id) values(1,0,'任务接收人',null,4,1);
INSERT INTO BPM_CONF_NOTICE(id,type,receiver,due_date,node_id,template_id) values(2,0,'流程发起人',null,5,2);
INSERT INTO BPM_CONF_NOTICE(id,type,receiver,due_date,node_id,template_id) values(3,1,'1',null,8,3);
INSERT INTO BPM_CONF_NOTICE(id,type,receiver,due_date,node_id,template_id) values(4,2,'任务接收人','P1H',8,4);

