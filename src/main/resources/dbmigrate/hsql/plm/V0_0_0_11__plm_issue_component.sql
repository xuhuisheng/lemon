

-------------------------------------------------------------------------------
--  plm issue component
-------------------------------------------------------------------------------
CREATE TABLE PLM_ISSUE_COMPONENT(
	ISSUE_ID BIGINT,
	COMPONENT_ID BIGINT,
	CONSTRAINT PK_PLM_ISSUE_COMPONENT PRIMARY KEY(ISSUE_ID,COMPONENT_ID),
        CONSTRAINT FK_PIM_ISSUE_COMPONENT_ISSUE FOREIGN KEY(ISSUE_ID) REFERENCES PLM_ISSUE(ID),
        CONSTRAINT FK_PIM_ISSUE_COMPONENT_COMPONENT FOREIGN KEY(COMPONENT_ID) REFERENCES PLM_COMPONENT(ID)
);

