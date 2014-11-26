

UPDATE BPM_CONF_NODE SET CONF_FORM=0 where id=2;
UPDATE BPM_CONF_NODE SET CONF_FORM=0 where id=12;

UPDATE BPM_CONF_NOTICE set template_code='arrival-assignee',notification_type='msg,email' where id=1;
UPDATE BPM_CONF_NOTICE set template_code='arrival-initiator',notification_type='msg,email' where id=2;
UPDATE BPM_CONF_NOTICE set template_code='complete',notification_type='msg,email' where id=3;
UPDATE BPM_CONF_NOTICE set template_code='timeout',notification_type='msg,email' where id=4;


