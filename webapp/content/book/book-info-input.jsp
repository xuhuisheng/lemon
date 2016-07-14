<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "book");%>
<%pageContext.setAttribute("currentMenu", "book");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#book-infoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/book-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/book-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="book-infoForm" method="post" action="book-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="book-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="book-info_name"><spring:message code="book-info.book-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="book-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="book-info_address">ISBN</label>
	<div class="col-sm-5">
	  <input id="book-info_address" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="book-info_type">作者</label>
	<div class="col-sm-5">
	  <input id="book-info_type" type="text" name="author" value="${model.author}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="book-info_startTIme">出版社</label>
	<div class="col-sm-5">
	  <input id="book-info_startTIme" type="text" name="weight" value="${model.weight}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="book-info_endTime">定价</label>
	<div class="col-sm-5">
	  <input id="book-info_endTime" type="text" name="price" value="${model.price}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>

