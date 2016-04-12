
alter table AUTH_PERM add column priority INTEGER;

COMMENT ON COLUMN AUTH_PERM.PRIORITY IS '排序';
