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

	$('.full-height').height($(window).height() - 85);
	
	var editor = CKEDITOR.replace('message_content');
});
    </script>

  </head>

  <body>
    <%@include file="/header/javamail.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/javamail.jsp"%>



<div class="col-md-3 full-height" id="accordion2" style="padding-top:65px;">

  <div class="panel panel-default" style="height:100%">
    <div class="panel-heading">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        邮件
      </h4>
    </div>
    <div class="panel-body">
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

</div>






<div class="col-md-7 full-height" id="accordion3" style="padding-top:65px;">

  <div class="panel panel-default" style="height:100%">
    <div class="panel-heading">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        新邮件
      </h4>
    </div>
    <div class="panel-body">

    <form class="form-horizontal" action="send.do" method="post">

<div class="form-group">
    <div class="input-group">
      <div class="input-group-addon">收件人：</div>
      <input type="text" class="form-control" placeholder="" name="receiver">
    </div>
</div>

<div class="form-group">
    <div class="input-group">
      <div class="input-group-addon">主题：</div>
      <input type="text" class="form-control" placeholder="" name="subject">
    </div>
</div>

<div class="form-group">
<textarea id="message_content" name="content"></textarea>
</div>

<div class="form-group">
<button class="btn btn-default">发送</button>
</div>

	</form>


	  
	  </div>

    </div>
  </div>

</div>

	</div>

  </body>

</html>
