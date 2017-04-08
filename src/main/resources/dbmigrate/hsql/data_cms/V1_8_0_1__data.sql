

INSERT INTO CMS_ARTICLE(ID,code,title,content,create_time,user_id,CATALOG_ID,hit_count,comment_count,tenant_id) VALUES(170,'lemon-1-7-0','LemonOA 1.7.0发布','features:<br>
<ul>
    <li>界面全面升级Bootstrap3。好看多了</li>
    <li>可以切换语言了。不过没精力翻译，所以没啥作用</li>
    <li>使用snowflake生成id。目前没啥好处，为以后兼容oracle打基础</li>
    <li>使用jcache统一缓存。</li>
    <li>启动android移动端。</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.7.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.7.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.7.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.7.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1sj52GpR" target="_blank">http://pan.baidu.com/s/1sj52GpR</a></li>
</ul>','2016-06-07 00:00:00',1,1,0,0,1);

UPDATE CMS_ARTICLE SET PUBLISH_TIME=CREATE_TIME;

UPDATE CMS_ARTICLE SET CONTENT='第一个版本<br>
<br>
<ul>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-0.8.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-0.8.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1ePwRI" target="_blank">http://pan.baidu.com/s/1ePwRI</a></li>
</ul>
' WHERE ID=8;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>集成了Modeler。参考集成方法</li>
    <li>支持mysql。参考配置mysql</li>
    <li>不重新发布流程，直接修改流程定义。</li>
    <li>实现规则式的组织机构与参与者配置。参考表达式解析算法</li>
    <li>发起流程前，指定每个环节的负责人。参考指定任务负责人</li>
    <li>流程跟踪图中，高亮显示已经过的连线。</li>
    <li>任务邮件提醒。参考任务邮件提醒</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A0.9.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A0.9.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-0.9.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-0.9.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1sj52GpR" target="_blank">http://pan.baidu.com/s/1sj52GpR</a></li>
</ul>
' WHERE ID=7;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>配置流程定义的负责人，表单，操作和提醒。参考配置流程</li>
    <li>初步回退与撤销。参考回退</li>
    <li>任务协办委托。参考任务协办委托</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.0.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.0.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.0.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.0.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1eQef8aA" target="_blank">http://pan.baidu.com/s/1eQef8aA</a></li>
</ul>
' WHERE ID=6;

INSERT INTO CMS_ARTICLE(ID,CODE,title,content,create_time,publish_time,user_id,CATALOG_ID,hit_count,comment_count,tenant_id) VALUES(101,'lemon-1-0-1','LemonOA 1.0.1发布','bugfix<br>
<ul>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.0.1" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.0.1</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1dDh51uD" target="_blank">http://pan.baidu.com/s/1dDh51uD</a></li>
</ul>
','2014-02-13 00:00:00','2014-02-13 00:00:00',1,1,0,0,1);

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>使用springmvc替换struts2</li>
    <li>去除第三方依赖，减少war的大小，减低permgen的使用量</li>
    <li>跳过节点</li>
    <li>流程实例迁移</li>
    <li>会签的配置界面</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.1.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.1.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.1.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.1.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1eQIc0NK" target="_blank">http://pan.baidu.com/s/1eQIc0NK</a></li>
</ul>
' WHERE ID=5;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>支持任务抄送</li>
    <li>重新规划xform电子表单，初步支持单元格合并</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.2.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.2.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.2.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.2.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1hqBpSZM" target="_blank">http://pan.baidu.com/s/1hqBpSZM</a></li>
</ul>
' WHERE ID=4;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>简化组织机构</li>
    <li>电子表单支持删除行</li>
    <li>流程提醒支持站内信</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.3.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.3.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.3.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.3.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1c0w6Guc" target="_blank">http://pan.baidu.com/s/1c0w6Guc</a></li>
</ul>
' WHERE ID=3;

UPDATE CMS_ARTICLE SET CONTENT='bugfix:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.3.1" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.3.1</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.3.1" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.3.1</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1pJFhzeF" target="_blank">http://pan.baidu.com/s/1pJFhzeF</a></li>
</ul>
' WHERE ID=2;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>使用接口把form与bpm完全分离</li>
    <li>简化dbmigrate的配置</li>
    <li>提供notification接口，支持多种提醒方式</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.4.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.4.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.4.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.4.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1sjug013" target="_blank">http://pan.baidu.com/s/1sjug013</a></li>
</ul>
' WHERE ID=1;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>把人工任务相关的操作都抽离到任务中心，有了单独的humantask模块。</li>
    <li>增加一个start-hsqldb-client.bat可以连接数据库。</li>
    <li>xform支持附件。</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.5.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.5.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.5.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.5.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1mgEdsEG" target="_blank">http://pan.baidu.com/s/1mgEdsEG</a></li>
</ul>
' WHERE ID=9;

UPDATE CMS_ARTICLE SET CONTENT='bugfix:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.5.1" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.5.1</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.5.1" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.5.1</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1eQ0duwU" target="_blank">http://pan.baidu.com/s/1eQ0duwU</a></li>
</ul>
' WHERE ID=10;

UPDATE CMS_ARTICLE SET CONTENT='features:<br>
<ul>
    <li>Activiti升级到5.18.0。</li>
    <li>添加Portal。</li>
    <li>添加网盘。</li>
    <li>美化form，修复bug。</li>
    <li>调整bpm的跟踪页面。</li>
</ul>
resources:<br>
<ul>
    <li>详细修订列表： <a href="https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.6.0" target="_blank">https://github.com/xuhuisheng/lemon/issues?q=milestone%3A1.6.0</a></li>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.6.0" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.6.0</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1o6OptTS" target="_blank">http://pan.baidu.com/s/1o6OptTS</a></li>
</ul>
' WHERE ID=11;

UPDATE CMS_ARTICLE SET CONTENT='bugfix:<br>
<ul>
    <li>源码地址： <a href="https://github.com/xuhuisheng/lemon/tree/lemon-1.6.1" target="_blank">https://github.com/xuhuisheng/lemon/tree/lemon-1.6.1</a></li>
    <li>独立运行包： <a href="http://pan.baidu.com/s/1jGqAoYE" target="_blank">http://pan.baidu.com/s/1jGqAoYE</a></li>
</ul>
' WHERE ID=12;


