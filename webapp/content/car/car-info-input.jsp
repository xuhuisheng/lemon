<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "car");%>
<%pageContext.setAttribute("currentMenu", "car");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="car-info.car-info.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#car-infoForm").validate({
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
    <%@include file="/header/car-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/car-info.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="car-info.car-info.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="car-infoForm" method="post" action="car-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="car-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="car-info_name"><spring:message code="car-info.car-info.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="car-info_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="car-info_address">牌照</label>
	<div class="controls">
	  <input id="car-info_address" type="text" name="code" value="${model.code}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="car-info_type">状态</label>
	<div class="controls">
	  <input id="car-info_type" type="text" name="status" value="${model.status}" size="40" class="text number">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="car-info_startTIme">载重</label>
	<div class="controls">
	  <input id="car-info_startTIme" type="text" name="weight" value="${model.weight}" size="40" class="text number">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="car-info_endTime">载人</label>
	<div class="controls">
	  <input id="car-info_endTime" type="text" name="people" value="${model.people}" size="40" class="text number">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
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
