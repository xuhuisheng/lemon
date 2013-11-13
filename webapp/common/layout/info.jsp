<%@ page language="java" pageEncoding="UTF-8" %>

      <div class="m-user">
		<div class="dropdown">
		  <a role="button" class="dropdown-toggle" data-toggle="dropdown" href="#">
		    <span class="m-user-info">
			  <sec:authentication property="name" />
			  <b class="caret" style="margin-top:14px;"></b>
			</span>
		  </a>
		  <ul class="dropdown-menu" role="menu">
		    <li><a href="${ctx}/j_spring_security_logout"><spring:message code="core.header.logout" text="注销"/></a></li>
            <li><a href="${ctx}/auth/change-password.do"><spring:message code="core.header.changepassword" text="修改密码"/></a></li>
		  </ul>
		</div>
      </div>
