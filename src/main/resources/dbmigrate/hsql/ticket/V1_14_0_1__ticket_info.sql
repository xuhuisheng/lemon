

ALTER TABLE TICKET_INFO ADD COLUMN CATEGORY VARCHAR(50);

COMMENT ON COLUMN TICKET_INFO.CATEGORY IS '分类';

