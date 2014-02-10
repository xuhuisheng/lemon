
alter table USER_BASE alter column reference rename to ref;

alter table USER_BASE add email varchar(100);

alter table USER_BASE add mobile varchar(50);
