<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dashboard");%>
<%pageContext.setAttribute("currentMenu", "dashboard");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>dashboard</title>
    <%@include file="/common/s.jsp"%>

	<script type="text/javascript">
$(function(){
    function widget2ToggleContent() {
        var self = $(this);
        self.toggleClass('icon-chevron-up');
        self.toggleClass('icon-chevron-down');
        var widget = self.parents('.m-widget-2');
        var content = widget.find('.content');
        content.toggle(200);
    }

    $(document).delegate('.m-widget-2 .header .ctrl .icon-chevron-up', 'click', widget2ToggleContent);
    $(document).delegate('.m-widget-2 .header .ctrl .icon-chevron-down', 'click', widget2ToggleContent);
});
    </script>
	<style type="text/css">
.m-widget-container-2 {
	width: 96%;
	margin-left: 2%;
}

.m-widget-2 {
	margin-top: 0px;
	margin-bottom: 20px;
	border-radius: 3px;
	box-shadow: #E6E6E6 0px 1px 1px 0px;
}

.m-widget-2 .header {
	height: 20px;
	padding: 5px 15px;
	border: 1px solid #C2C2C3;
	padding-left: 10px;
	border-radius: 3px 3px 0px 0px;
	background-color: #CCCCCC;
	box-shadow: 0 1px 0 0 rgba(255, 255, 255, 0.5) inset;
	background: linear-gradient(to bottom, #FAFAFA 0%, #EFEFEF 100%) repeat scroll 0 0 transparent;
	text-shadow: 0 1px 0 #FFFFFF;
	color: #333333;
}

.m-widget-2 .header .title {
	float: left;
	margin: 0px;
	font-size: 14px;
}

.m-widget-2 .header .ctrl {
	float: right;
}

.m-widget-2 .header .ctrl .btn {
	margin: 0px;
	padding-left: 3px;
	padding-right: 3px;
	padding-top: 0px;
	padding-bottom: 0px;
}

.m-widget-2 .content {
	border-left: 1px solid #C2C2C3;
	border-right: 1px solid #C2C2C3;
	border-bottom: 1px solid #C2C2C3;
	border-radius: 0px 0px 3px 3px;
	height: 200px;
	overflow: hide;
}

.m-widget-2 .content.content-inner {
	padding-left: 10px;
	padding-top:10px;
	font-size: 12px;
}

.m-widget-2 .content .m-table {
	margin-top: 0px;
	margin-bottom: 0px;
}
	</style>
  </head>

  <body>
    <%@include file="/header/dashboard.jsp"%>

    <div class="row-fluid m-widget-container-2">
	  <div class="span4">


	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">待办任务</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content">
		  <table class="m-table table-hover">
			<thead>
			  <tr>
				<th>编号</th>
				<th>名称</th>
				<th>创建时间</th>
				<th>&nbsp;</th>
			  </tr>
			</thead>
			<tbody>
			<s:iterator value="personalTasks" var="item">
			  <tr>
				<td>${item.id}</td>
				<td>${item.name}</td>
				<td><s:date name="createTime" format="yyyy-MM-dd HH:mm:ss" /></td>
				<td>
				  <a href="${scopePrefix}/form/form!viewTaskForm.do?taskId=${item.id}" class="btn btn-small btn-primary">处理</a>
				</td>
			  </tr>
			  </s:iterator>
			</tbody>
		  </table>
		</div>
	  </article>

	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">通知公告</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content content-inner">
		  <marquee direction="up" scrollamount="2" >
		  <s:iterator value="cmsArticles" var="item">
		    <div>
			  <h4>${item.name}</h4>
			  <p>${item.content}</p>
			</div>
			</s:iterator>
		  </marquee>
		</div>
	  </article>

	  </div>

	  <div class="span4">

	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">我的流程</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content">
		  <table class="m-table table-hover">
			<thead>
			  <tr>
				<th>编号</th>
				<th>流程定义</th>
				<th>创建时间</th>
				<th>&nbsp;</th>
			  </tr>
			</thead>

			<tbody>
			<s:iterator value="historicProcessInstances" var="item">
			  <tr>
				<td>${item.id}</td>
				<td>${item.processDefinitionId}</td>
				<td><s:date name="startTime" format="yyyy-MM-dd HH:mm:ss" /></td>
				<td>
                  <a href="${scopePrefix}/bpm/workspace!viewHistory.do?processInstanceId=${item.id}" class="btn btn-small btn-primary">历史</a>
				</td>
			  </tr>
			  </s:iterator>
			</tbody>
		  </table>
		</div>
	  </article>

	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">常用工具</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content">
		  <table class="m-table table-hover">
			<tbody>
			  <tr>
				<td>地图</td>
				<td><a href="http://ditu.baidu.com">ditu.baidu.com</a></td>
			  </tr>
			  <tr>
				<td>翻译</td>
				<td><a href="http://translate.google.com">translate.google.com</a></td>
			  </tr>
			</tbody>
		  </table>
		</div>
	  </article>

	  </div>

	  <div class="span4">

	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">常用流程</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content">
		  <table class="m-table table-hover">
			<thead>
			  <tr>
				<th>代码</th>
				<th>名称</th>
				<th>版本</th>
				<th>&nbsp;</th>
			  </tr>
			</thead>
			<tbody>
			<s:iterator value="processDefinitions" var="item">
			  <tr>
				<td>${item.key}</td>
				<td>${item.name}</td>
				<td>${item.version}</td>
				<td>
				  <a href="${scopePrefix}/form/form!viewStartForm.do?processDefinitionId=${item.id}" class="btn btn-small btn-primary">发起</a>
				</td>
			  </tr>
			  </s:iterator>
			</tbody>
		  </table>
		</div>
	  </article>

	  <article class="m-widget-2">
        <header class="header">
		  <h4 class="title">天气预报</h4>
		  <div class="ctrl">
		    <a class="btn"><i class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div class="content content-inner">
		  <iframe src="http://m.weather.com.cn/m/pn12/weather.htm" style="border:0px"></iframe>
		</div>
	  </article>


	  </div>

    </div>

  </body>

</html>
