<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "contract");%>
<%pageContext.setAttribute("currentMenu", "contract");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#contract-infoForm").validate({
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
    <%@include file="/header/contract-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/contract-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="contract-infoForm" method="post" action="contract-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="contract-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="contract-info_name"><spring:message code="contract-info.contract-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="contract-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="contractInfo_code">编号</label>
	<div class="col-sm-5">
	  <input id="contractInfo_code" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="contractInfo_type">类型</label>
	<div class="col-sm-5">
	  <input id="contractInfo_type" type="text" name="type" value="${model.type}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="contractInfo_status">状态</label>
	<div class="col-sm-5">
	  <input id="contractInfo_status" type="text" name="status" value="${model.status}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="contractInfo_startTime">开始时间</label>
	<div class="col-sm-5">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input id="contractInfo_startTime" name="startTime" type="text" value="<fmt:formatDate value='${model.startTime}' type='date'/>" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="contractInfo_endTime">结束时间</label>
	<div class="col-sm-5">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input id="contractInfo_endTime" name="endTime" type="text" value="<fmt:formatDate value='${model.endTime}' type='date'/>" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
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

