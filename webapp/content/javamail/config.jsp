<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "javamail");%>
<%pageContext.setAttribute("currentMenu", "javamail");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>邮件</title>
    <%@include file="/common/s.jsp"%>
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

	$('.full-height').height($(window).height() - 100);
});
    </script>

	<style>
fieldset {
    background: none;
    border: 0px;
    border-radius: 0px;
    margin: 0px;
    padding: 0px;
}
	</style>
  </head>

  <body>
    <%@include file="/header/javamail.jsp"%>

    <div class="row-fluid" style="display: table; height: 100%; margin-top: -66px; padding-top: 62px; box-sizing: border-box;">
	<div style="display:table-row;height:100%;">
	<%@include file="/menu/javamail.jsp"%>

	  <aside id="m-sidebar" class="span3" style="height:100%;">
        <div class="accordion-group" style="height:100%;">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#">
              <i class="icon-user"></i>
              <span class="title">邮件</span>
            </a>
          </div>
          <div class="accordion-body collapse in full-height" style="overflow:auto;">
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
</c:forEach>
          </div>
        </div>
      </aside>

	  <aside id="m-sidebar" class="span7" style="height:100%;padding-right:10px;">
        <div class="accordion-group" style="height:100%;">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#">
              <i class="icon-user"></i>
              <span class="title">配置</span>
            </a>
          </div>
          <div class="accordion-body collapse in full-height" style="overflow:auto;">

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
      </aside>
	
	</div>
	</div>

  </body>

</html>
