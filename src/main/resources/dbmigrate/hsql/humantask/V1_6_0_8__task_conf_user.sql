

-------------------------------------------------------------------------------
--  task conf user
-------------------------------------------------------------------------------
CREATE TABLE TASK_CONF_USER(
	ID BIGINT NOT NULL,
	BUSINESS_KEY VARCHAR(200),
	CODE VARCHAR(200),
	VALUE VARCHAR(200),
        CONSTRAINT PK_TASK_CONF_USER PRIMARY KEY(ID)
);

COMMENT ON TABLE TASK_CONF_USER IS '任务实例配置用户';
COMMENT ON COLUMN TASK_CONF_USER.ID IS '主键';
COMMENT ON COLUMN TASK_CONF_USER.BUSINESS_KEY IS '业务标识';
COMMENT ON COLUMN TASK_CONF_USER.CODE IS '编码';
COMMENT ON COLUMN TASK_CONF_USER.VALUE IS '值';


