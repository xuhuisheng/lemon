<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="account-device.account-device.input.title" text="编辑"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#account-deviceForm").validate({
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
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

		<h4>账号详情 - ${accountInfo.username}</h4>

		<ul class="nav nav-tabs">
		  <li role="presentation" class=""><a href="account-detail-index.do?infoId=${param.infoId}">详情</a></li>
		  <li role="presentation" class="active"><a href="account-detail-password.do?infoId=${param.infoId}">密码</a></li>
		  <li role="presentation" class=""><a href="account-detail-avatar.do?infoId=${param.infoId}">头像</a></li>
		  <li role="presentation" class=""><a href="account-detail-log.do?infoId=${param.infoId}">日志</a></li>
		  <li role="presentation" class=""><a href="account-detail-device.do?infoId=${param.infoId}">设备</a></li>
		  <li role="presentation" class=""><a href="account-detail-token.do?infoId=${param.infoId}">会话</a></li>
		  <li role="presentation" class=""><a href="account-detail-person.do?infoId=${param.infoId}">人员</a></li>
		</ul>

      <div class="panel panel-default">

		<table class="table">
		  <thead>
		    <tr>
			  <th>凭证</th>
			  <th>分类</th>
			  <th>凭证种类</th>
			  <th>过期时间</th>
			  <th>修改时间</th>
			  <th>状态</th>
			  <th>&nbsp;</th>
			</tr>
		  </thead>
		  <tbody>
		    <c:forEach var="item" items="${accountCredentials}">
		    <tr>
			  <td>
				<c:if test="${not empty param.password && param.id==item.id}">
				  <label class="label label-default">${param.password}</label>
				</c:if>
			    <c:if test="${empty param.password || param.id != item.id}">
  			      ******
				</c:if>
			  </td>
			  <td>${item.catalog}</td>
			  <td>${item.type}</td>
			  <td><fmt:formatDate value="${item.expireTime}" type="both"/></td>
			  <td><fmt:formatDate value="${item.modifyTime}" type="both"/></td>
			  <td>
			    <c:set var="status" value="active"/>
			    <c:if test="${item.status=='disabled'}">
				  <span class="label label-danger">禁用</span>
				  <c:set var="status" value="disabled"/>
			    </c:if>
			    <c:if test="${item.expireTime lt now}">
				  <span class="label label-danger">过期</span>
				  <c:set var="status" value="expired"/>
			    </c:if>
			    <c:if test="${status=='active'}">
				  <span class="label label-info">正常</span>
			    </c:if>
			  </td>
			  <td>
			    <a href="account-credential-generate.do?id=${item.id}">重置密码</a>
			  </td>
		    </tr>
			</c:forEach>
		  </tbody>
		</table>

      </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
