

INSERT INTO CMS_ARTICLE(ID,code,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(9,'lemon-1-5-0','LemonOA 1.5.0发布','LemonOA 1.5.0发布','2015-07-27 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,code,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(10,'lemon-1-5-1','LemonOA 1.5.1发布','LemonOA 1.5.1发布','2015-08-10 00:00:00',1,1,0,0);

UPDATE CMS_CATALOG SET TENANT_ID='1';
UPDATE CMS_ARTICLE SET TENANT_ID='1';

