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


<form method="post" action="configSave.do" class="form-horizontal">
  <div class="form-group">
	<label class="control-label col-md-2" for="username">账号:</label>
    <div class="col-sm-5">
      <input id="username" type="text" name="username" value="${javamailConfig.username}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="password">密码:</label>
    <div class="col-sm-5">
      <input id="password" type="password" name="password" value="" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="receiveType">收信类型:</label>
    <div class="col-sm-5">
      <input id="receiveType" type="text" name="receiveType" value="${javamailConfig.receiveType}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="receiveHost">收信地址:</label>
    <div class="col-sm-5">
      <input id="receiveHost" type="text" name="receiveHost" value="${javamailConfig.receiveHost}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="receivePort">收信端口:</label>
    <div class="col-sm-5">
      <input id="receivePort" type="text" name="receivePort" value="${javamailConfig.receivePort}" class="form-control">pop3: 110, 995
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="receiveSecure">收信安全:</label>
    <div class="col-sm-5">
      <input id="receiveSecure" type="text" name="receiveSecure" value="${javamailConfig.receiveSecure}" class="form-control">ssl, ssl-all, none
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="sendType">发信类型:</label>
    <div class="col-sm-5">
      <input id="sendType" type="text" name="sendType" value="${javamailConfig.sendType}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="sendHost">发信地址:</label>
    <div class="col-sm-5">
      <input id="sendHost" type="text" name="sendHost" value="${javamailConfig.sendHost}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="sendPort">发信端口:</label>
    <div class="col-sm-5">
      <input id="sendPort" type="text" name="sendPort" value="${javamailConfig.sendPort}" class="form-control">smtp: 25, 465
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-2" for="sendSecure">发信安全:</label>
    <div class="col-sm-5">
      <input id="sendSecure" type="text" name="sendSecure" value="${javamailConfig.sendSecure}" class="form-control">ssl, ssl-all, none
    </div>
  </div>
  <div>
	<button class="btn btn-default">保存</button>
  </div>

</form>


	  
	  </div>

    </div>
  </div>

</div>

	</div>

  </body>

</html>
