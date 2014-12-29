<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "whitelist");%>
<%pageContext.setAttribute("currentMenu", "whitelist");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#demoForm").validate({
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
    <%@include file="/header/whitelist.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/whitelist.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="demo.demo.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="whitelist-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="whitelistApp_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="whitelistApp_type">申请服务类型</label>
	<div class="controls">
	  <c:if test="${not empty whitelistType}">
	    ${whitelistType.name}
		<input type="hidden" name="typeId" value="${whitelistType.id}">
	  </c:if>
	  <c:if test="${empty whitelistType}">
	    <c:forEach items="${whitelistTypes}" var="item">
	    <select name="typeId">
		  <option value="${item.id}">${item.name}</option>
		</select>
		</c:forEach>
	  </c:if>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="whitelistApp_name">系统名称</label>
	<div class="controls">
	  <input id="whitelistApp_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="whitelistApp_description">系统说明</label>
	<div class="controls">
	  <textarea id="whitelistApp_description" name="description" class="required">${model.description}</textarea>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="whitelistApp_host">域名</label>
	<div class="controls" id="hostContent">
	  <textarea id="whitelistApp_host" name="host" class="required"><c:forEach items="${model.whitelistHosts}" var="item">${item.value}
</c:forEach></textarea>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="whitelistApp_ip">IP</label>
	<div class="controls" id="ipContent">
	  <textarea id="whitelistApp_ip" name="ip" class="required"><c:forEach items="${model.whitelistIps}" var="item">${item.value}
</c:forEach></textarea>
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
