

alter table FORM_TEMPLATE add code varchar(50);

alter table FORM_TEMPLATE alter column content varchar(65535);

COMMENT ON COLUMN FORM_TEMPLATE.CODE IS '编码';

