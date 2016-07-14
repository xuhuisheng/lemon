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

	$('.full-height').height($(window).height() - 100);
});
    </script>
  </head>

  <body>
    <%@include file="/header/javamail.jsp"%>

    <div class="row-fluid">

	<%@include file="/menu/javamail.jsp"%>

<div class="panel-group col-md-3" id="accordion2" role="tablist" aria-multiselectable="true" style="padding-top:65px;height:100%;padding-right:10px;">

  <div class="panel panel-default" style="height:100%;">
    <div class="panel-heading" role="tab" id="collapse-header-javamail" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-javamail" aria-expanded="true" aria-controls="collapse-body-javamail">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        邮件
      </h4>
    </div>
    <div class="panel-body full-height" style="overflow: auto; height: 100px;">
<c:forEach var="item" items="${javamailMessages}">
	    <div>
			    <a href="${tenantPrefix}/javamail/index.do?id=${item.id}"><c:out value="${item.sender}"/></a>
				<ftm:formatDate value="${item.sendTime}" type="both"/>
				<br>
			    <a href="${tenantPrefix}/javamail/index.do?id=${item.id}">${item.subject}</a>
		</div>
		<hr>
</c:forEach>
	</div>
<%--
    <div id="collapse-body-javamail" class="panel-collapse collapse ${currentMenu == 'javamail' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-javamail">
      <div class="panel-body full-height" style="overflow: auto; height: 520px;">
<c:forEach var="item" items="${javamailMessages}">
		    <blockquote>
			  <p>
			    <a href="${tenantPrefix}/javamail/index.do?id=${item.id}"><c:out value="${item.sender}"/></a>
				<ftm:formatDate value="${item.sendTime}" type="both"/>
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
--%>
  </div>

</div>




<div class="panel-group col-md-7" id="accordion3" role="tablist" aria-multiselectable="true" style="padding-top:65px;">


  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-javamail" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-javamail" aria-expanded="true" aria-controls="collapse-body-javamail">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        ${javamailMessage.subject}
      </h4>
    </div>
    <div id="collapse-body-javamail" class="panel-collapse collapse ${currentMenu == 'javamail' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-javamail">
      <div class="panel-body full-height" style="overflow: auto; height: 520px;">

		    <p><c:out value="${javamailMessage.sender}"/><fmt:formatDate value="${javamailMessage.sendTime}" type="both"/></p>
            ${javamailMessage.content}
      </div>
    </div>
  </div>

	</div>

  </body>

</html>
