
alter table USER_BASE change reference ref varchar(64);

alter table USER_BASE add email varchar(100);

alter table USER_BASE add mobile varchar(50);
