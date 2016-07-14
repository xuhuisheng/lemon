<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑岗位类型</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            orgname: {
                remote: {
                    url: 'org-position-type-checkOrgname.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#org_id').val();
                        }
                        </c:if>
                    }
                }
            }
        },
        messages: {
            orgname: {
                remote: "存在重复岗位类型"
            }
        }
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/org-sys.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/org-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑岗位类型</h4>
		</header>

		<div class="content content-inner">

<form id="orgForm" method="post" action="org-position-type-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="org_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="org_orgname"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="controls">
	  <input id="org_orgname" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
