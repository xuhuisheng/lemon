

-------------------------------------------------------------------------------
--  asset category
-------------------------------------------------------------------------------
CREATE TABLE ASSET_CATEGORY(
        ID BIGINT NOT NULL,
	NAME VARCHAR(200),
	CODE VARCHAR(200),
	DESCRIPTION VARCHAR(200),
	PARENT_ID BIGINT,
	CONSTRAINT PK_ASSET_CATEGORY PRIMARY KEY(ID),
        CONSTRAINT FK_ASSET_CATEGORY_PARENT FOREIGN KEY(PARENT_ID) REFERENCES ASSET_CATEGORY(ID)
);

