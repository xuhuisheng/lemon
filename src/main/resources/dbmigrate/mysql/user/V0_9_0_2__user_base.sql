
alter table USER_BASE change REFERENCE REF varchar(64);

alter table USER_BASE add EMAIL varchar(100);

alter table USER_BASE add MOBILE varchar(50);
