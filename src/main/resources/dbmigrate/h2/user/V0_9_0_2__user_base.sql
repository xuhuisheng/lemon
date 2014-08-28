
alter table USER_BASE alter column REFERENCE rename to REF;

alter table USER_BASE add EMAIL varchar(100);

alter table USER_BASE add MOBILE varchar(50);
