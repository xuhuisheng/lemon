<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "workcal");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑工作日历类型</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#scope-globalForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            name: {
                remote: {
                    url: 'scope-global!checkName.do',
                    data: {
                        <s:if test="model != null">
                        id: ${model.id}
                        </s:if>
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
})
    </script>
  </head>

  <body>
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/workcal.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="scope-global.scope-global.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="scope-globalForm" method="post" action="workcal-type-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="scope-global_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="scope-global_name"><spring:message code="scope-global.scope-global.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="scope-global_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="10">
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
