<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "scope");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="scope-local.scope-local.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#scope-localForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            name: {
                remote: {
                    url: 'scope-local!checkName.do',
                    data: {
                        <s:if test="model != null">
                        id: ${model.id},
                        </s:if>
						scopeGlobalId: function() {
							return $('#scope-local_global').val();
						}
                    }
                }
            }
        },
        messages: {
            name: {
                remote: "名称重复"
            }
        }
    });

	$("#scope_local_global").change(function() {
		$("#scope-localForm").validate().resetForm();
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/scope.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="scope-local.scope-local.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="scope-localForm" method="post" action="scope-local!save.do?operationMode=STORE" class="form-horizontal">
  <s:if test="model != null">
  <input id="scope-local_id" type="hidden" name="id" value="${model.id}">
  </s:if>
  <div class="control-group">
    <label class="control-label" for="scope-local_name"><spring:message code="scope-local.scope-local.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="scope-local_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-local_global">全局</label>
	<div class="controls">
	  <select id="scope-local_global" name="scopeGlobalId">
      <s:iterator value="scopeGlobals" var="item">
	    <option value="${item.id}" ${item.id==model.scopeGlobal.id ? 'selected' : ''}>${item.name}</option>
	  </s:iterator>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-local_global">共享</label>
	<div class="controls">
	  <input id="scope-local_shared" name="shared" type="checkbox" value="1" ${model.shared == 1 ? 'checked' : ''}>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
