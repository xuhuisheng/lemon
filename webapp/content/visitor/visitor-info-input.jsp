<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "visitor");%>
<%pageContext.setAttribute("currentMenu", "visitor");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#visitor-infoForm").validate({
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
    <%@include file="/header/visitor-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/visitor-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="visitor-infoForm" method="post" action="visitor-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="visitor-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitor-info_name"><spring:message code="visitor-info.visitor-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="visitor-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitorInfo_mobile">电话</label>
	<div class="col-sm-5">
	  <input id="visitorInfo_mobile" type="text" name="mobile" value="${model.mobile}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitorInfo_companyName">公司</label>
	<div class="col-sm-5">
	  <input id="visitorInfo_companyName" type="text" name="companyName" value="${model.companyName}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitorInfo_visitTime">来访时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="visitorInfo_visitTime" type="text" name="visitTime" value="<fmt:formatDate value='${model.visitTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitorInfo_leaveTime">离开时间</label>
	<div class="col-sm-5">
      <div class="input-append datetimepicker date" style="padding-left: 0px;">
	    <input id="visitorInfo_leaveTime" type="text" name="leaveTime" value="<fmt:formatDate value='${model.leaveTime}' pattern='yyyy-MM-dd HH:mm'/>" size="40" class="form-control required" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="visitorInfo_description">备注</label>
	<div class="col-sm-5">
	  <textarea id="visitorInfo_description" name="description">${model.description}</textarea>
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

