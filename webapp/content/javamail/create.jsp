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
	
	var editor = CKEDITOR.replace('message_content');
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
              <span class="title">新邮件</span>
            </a>
          </div>
          <div class="accordion-body collapse in full-height" style="overflow:auto;">

    <form class="form-horizontal" action="send.do" method="post">

<div>
<div class="input-prepend">
<span class="add-on">收件人：</span>
<input type="text" placeholder="" style="height:20px;" name="receiver">
</div>
</div>

<div>
<div class="input-prepend">
<span class="add-on">&nbsp;&nbsp;&nbsp;&nbsp;主题：</span>
<input type="text" placeholder="" style="height:20px;" name="subject">
</div>
</div>

<div>
<textarea id="message_content" name="content"></textarea>
</div>

<div>
<button>发送</button>
</div>

	</form>
          </div>
        </div>
      </aside>
	
	</div>
	</div>

  </body>

</html>
