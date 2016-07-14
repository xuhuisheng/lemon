<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "javamail");%>
<%pageContext.setAttribute("currentMenu", "javamail");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>邮件</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgGrid',
    pageNo: '${page.pageNo}',
    pageSize: '${page.pageSize}',
    totalCount: '${page.totalCount}',
    resultSize: '${page.resultSize}',
    pageCount: '${page.pageCount}',
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: '${page.asc}',
    params: {
        'filter_LIKES_orgname': '${param.filter_LIKES_orgname}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgGridForm',
	exportUrl: 'group-base-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');

	$('.full-height').height($(window).height() - 150);
});
    </script>

  </head>

  <body>
    <%@include file="/header/javamail.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/javamail.jsp"%>



<div class="panel-group col-md-3" id="accordion2" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-javamail" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-javamail" aria-expanded="true" aria-controls="collapse-body-javamail">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        邮件
      </h4>
    </div>
    <div id="collapse-body-javamail" class="panel-collapse collapse ${currentMenu == 'javamail' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-javamail">
      <div class="panel-body full-height">
<c:forEach var="item" items="${javamailMessages}">
		    <blockquote>
			  <p>
			    <a href="${tenantPrefix}/javamail/index.do?id=${item.id}"><c:out value="${item.sender}"/></a>
				<br>
			    <a href="${tenantPrefix}/javamail/index.do?id=${item.id}">${item.subject}</a>
			  </p>
			  <footer>
			    &nbsp;
              </footer>
			</blockquote>
</c:forEach>      </div>
    </div>
  </div>

</div>






<div class="panel-group col-md-7" id="accordion3" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-javamail" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-javamail" aria-expanded="true" aria-controls="collapse-body-javamail">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        配置
      </h4>
    </div>
    <div id="collapse-body-javamail" class="panel-collapse collapse ${currentMenu == 'javamail' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-javamail">
      <div class="panel-body full-height">

    <form class="form-horizontal" action="configSave.do" method="post">

<div>
<div class="input-prepend">
<span class="add-on">账号：</span>
<input type="text" placeholder="" style="height:20px;" name="username" value="${javamailConfig.username}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">密码：</span>
<input type="password" placeholder="" style="height:20px;" name="password">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">收信类型：</span>
<input type="text" placeholder="" style="height:20px;" name="receiveType" value="${javamailConfig.receiveType}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">收信地址：</span>
<input type="text" placeholder="" style="height:20px;" name="receiveHost" value="${javamailConfig.receiveHost}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">收信端口：</span>
<input type="text" placeholder="" style="height:20px;" name="receivePort" value="${javamailConfig.receivePort}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">收信安全：</span>
<input type="text" placeholder="" style="height:20px;" name="receiveSecure" value="${javamailConfig.receiveSecure}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">发信类型：</span>
<input type="text" placeholder="" style="height:20px;" name="sendType" value="${javamailConfig.sendType}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">发信地址：</span>
<input type="text" placeholder="" style="height:20px;" name="sendHost" value="${javamailConfig.sendHost}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">发信端口：</span>
<input type="text" placeholder="" style="height:20px;" name="sendPort" value="${javamailConfig.sendPort}">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">发信安全：</span>
<input type="text" placeholder="" style="height:20px;" name="sendSecure" value="${javamailConfig.sendSecure}">
</div>
</div>


<div>
<button>保存</button>
</div>

	</form>


	  
	  </div>

    </div>
  </div>

</div>

	</div>

  </body>

</html>
