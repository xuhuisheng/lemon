
INSERT INTO CMS_CATALOG(ID,name,code,logo,type,template_index,template_list,template_detail) VALUES(1,'最新动态','news',null,0,'/default/index.html','/default/list.html','/default/detail.html');

INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(1,'LemonOA 1.4.0发布','LemonOA 1.4.0发布', '2014-11-26 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(2,'LemonOA 1.3.1发布','LemonOA 1.3.1发布', '2014-09-12 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(3,'LemonOA 1.3.0发布','LemonOA 1.3.0发布', '2014-08-29 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(4,'LemonOA 1.2.0发布','LemonOA 1.2.0发布', '2014-06-11 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(5,'LemonOA 1.1.0发布','LemonOA 1.1.0发布', '2014-03-31 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(6,'LemonOA 1.0.0发布','LemonOA 1.0.0发布', '2014-02-10 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(7,'LemonOA 0.9.0发布','LemonOA 0.9.0发布', '2013-12-31 00:00:00',1,1,0,0);
INSERT INTO CMS_ARTICLE(ID,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count) VALUES(8,'LemonOA 0.8.0发布','LemonOA 0.8.0发布', '2013-11-14 00:00:00',1,1,0,0);

INSERT INTO CMS_COMMENT(ID,CONTENT,create_time,user_id,article_id) values(1,'测试','2014-12-02 00:00:01','1',1);
INSERT INTO CMS_COMMENT(ID,CONTENT,create_time,user_id,article_id) values(2,'测试','2014-12-02 00:00:02','1',1);

