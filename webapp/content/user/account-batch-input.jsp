<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userBaseForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            username: {
                remote: {
                    url: 'account-info-checkUsername.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#userBase_id').val();
                        }
                        </c:if>
                    }
                }
            }
        },
        messages: {
            username: {
                remote: "<spring:message code='user.user.input.duplicate' text='存在重复账号'/>"
            }
        }
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="user.user.input.title" text="编辑用户"/>
		  <c:forEach items="${accountInfos}" var="item">
			${item.username}
		  </c:forEach>
		</div>

		<div class="panel-body">

<form id="userBaseForm" method="post" action="account-batch-save.do" class="form-horizontal">
  <c:forEach items="${accountInfos}" var="item">
    <input type="hidden" name="selectedItem" value="${item.id}">
  </c:forEach>

  <div class="form-group status-section">
    <label class="control-label col-md-1 checkbox-inline">
	  <input type="checkbox" name="type" value="status" onchange="$('.status-section input[name=value]').attr('disabled', !this.checked)">
	  <b>状态</b>
	</label>
	<div class="col-sm-5">
	  <label class="radio-inline">
	    <input type="radio" name="value" value="active" disabled>
		启用
	  </label>
	  <label class="radio-inline">
	    <input type="radio" name="value" value="disabled" disabled>
		禁用
	  </label>
    </div>
  </div>

  <div class="form-group type-section">
    <label class="control-label col-md-1 checkbox-inline">
	  <input type="checkbox" name="type" value="type" onchange="$('.type-section input[name=value]').attr('disabled', !this.checked)">
	  <b>类型</b>
	</label>
	<div class="col-sm-5">
	  <input type="text" name="value" class="form-control" value="" disabled>
    </div>
  </div>

  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn btn-link a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
