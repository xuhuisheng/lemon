

ALTER TABLE TICKET_CATALOG ADD COLUMN PARENT_ID BIGINT;
ALTER TABLE TICKET_CATALOG ADD CONSTRAINT FK_TICKET_CATALOG_PARENT FOREIGN KEY(PARENT_ID) REFERENCES TICKET_CATALOG(ID);
