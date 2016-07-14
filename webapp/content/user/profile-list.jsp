<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>修改信息</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            username: {
                remote: {
                    url: 'user-base-checkUsername.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#user-base_id').val();
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

    $('#myTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.input.title" text="编辑用户"/></h4>
		</header>
		<div class="content content-inner">
<!--
		<ul class="nav nav-pills" id="myTab">
		  <li class="active"><a href="#identity">账号信息</a></li>
		  <li><a href="#contract">联系方式</a></li>
		</ul>
		
		<hr>
     
		<div class="tab-content">
		  <div class="tab-pane active" id="identity">
-->
<form id="userForm" method="post" action="profile-save.do" class="form-horizontal">
  <input id="user-base_userRepoId" type="hidden" name="userRepoId" value="1">
  <c:if test="${model != null}">
  <input id="user-base_id" type="hidden" name="id" value="${accountInfo.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="user-base_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="controls">
	  <div style="padding-top:5px;">${accountInfo.username}</div>
	</div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_displayName">显示名</label>
	<div class="controls">
	  ${accountInfo.displayName}
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_email">邮箱</label>
	<div class="controls">
	  <input id="userBase_email" type="text" name="email" value="${personInfo.email}">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_cellphone">手机</label>
	<div class="controls">
	  <input id="userBase_cellphone" type="text" name="cellphone" value="${personInfo.cellphone}">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>
<!--
		  </div>
		  <div class="tab-pane" id="contract">

<form id="contractForm" method="post" action="profile-contract-save.do" class="form-horizontal">
  <c:if test="${contract != null}">
  <input id="contract_id" type="hidden" name="id" value="${contract.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="userBase_username">姓名</label>
	<div class="controls">
	  <div style="padding-top:5px;">${contract.name}</div>
	</div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_nickName"></label>
	<div class="controls">
	  <input id="userBase_nickName" type="text" name="nickName" value="${model.nickName}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_email">邮箱</label>
	<div class="controls">
	  <input id="userBase_email" type="text" name="email" value="${model.email}" class="email" maxlength="100">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_mobile">手机</label>
	<div class="controls">
	  <input id="userBase_mobile" type="text" name="mobile" value="${model.mobile}">
    </div>
  </div>
  <c:forEach items="${userBaseWrapper.userAttrWrappers}" var="item">
  <div class="control-group">
    <label class="control-label" for="user-base_${item.code}">${item.name}</label>
	<div class="controls">
	  <input id="user-base_${item.code}" type="text" name="_user_attr_${item.code}" size="40" class="text" maxlength="50" value="${item.value}">
    </div>
  </div>
  </c:forEach>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

		  </div>
-->
        </div>
		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
