<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "whitelist");%>
<%pageContext.setAttribute("currentMenu", "whitelist");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#whitelist-adminForm").validate({
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
	  <%@include file="/menu/whitelist-admin.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="demoForm" method="post" action="whitelist-admin-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="whitelistApp_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_type">申请服务类型</label>
	<div class="col-sm-5">
	  <c:if test="${not empty whitelistType}">
	    ${whitelistType.name}
		<input type="hidden" name="typeId" value="${whitelistType.id}">
	  </c:if>
	  <c:if test="${empty whitelistType}">
	    <select name="typeId">
	      <c:forEach items="${whitelistTypes}" var="item">
		  <option value="${item.id}">${item.name}</option>
		  </c:forEach>
		</select>
	  </c:if>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_name">系统名称</label>
	<div class="col-sm-5">
	  <input id="whitelistApp_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_description">系统说明</label>
	<div class="col-sm-5">
	  <textarea id="whitelistApp_description" name="description" class="required">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_host">域名</label>
	<div class="col-sm-5" id="hostContent">
	  <textarea id="whitelistApp_host" name="host" class="required"><c:forEach items="${model.whitelistHosts}" var="item">${item.value}
</c:forEach></textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_ip">IP</label>
	<div class="col-sm-5" id="ipContent">
	  <textarea id="whitelistApp_ip" name="ip" class="required"><c:forEach items="${model.whitelistIps}" var="item">${item.value}
</c:forEach></textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_level">等级</label>
	<div class="col-sm-5">
	  <select id="whitelistApp_level" name="level">
	    <option value="0" ${model.level == 0 ? 'selected' : ''}>低安全级别</option>
	    <option value="1" ${model.level == 1 || empty model ? 'selected' : ''}>中安全级别</option>
	    <option value="2" ${model.level == 2 ? 'selected' : ''}>高安全级别</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="whitelistApp_userId">接口人</label>
	<div class="col-sm-5">
	  <input id="whitelistApp_userId" type="text" name="userId" value="${model.userId}" size="40" class="form-control required" minlength="2" maxlength="50">
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

