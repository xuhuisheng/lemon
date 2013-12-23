<%@ page language="java" pageEncoding="UTF-8" %>

    <s:actionmessage id="m-success-message" cssStyle="display:none;"/>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a href="${scopePrefix}/" class="brand">Mossle</a>
          <div class="nav-collapse collapse navbar-responsive-collapse">
            <ul class="nav">
              <li class="divider-vertical"></li>
              <li class="${currentHeader == 'dashboard' ? 'active' : ''}"><a href="${scopePrefix}/dashboard/dashboard.do">首页</a></li>
              <li class="${currentHeader == 'bpm-workspace' ? 'active' : ''}"><a href="${scopePrefix}/bpm/workspace!home.do">个人事务</a></li>
              <li class="${currentHeader == 'report' ? 'active' : ''}"><a href="${scopePrefix}/report/chart!mostActiveProcess.do">统计报表</a></li>
              <li class="dropdown ${currentHeader == 'scope' ? 'active' : ''}">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">系统管理 <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="${scopePrefix}/user/user-base.do">用户管理</a></li>
                  <li><a href="${scopePrefix}/group/org.do">组织机构</a></li>
                  <li class="divider"></li>
                  <li><a href="${scopePrefix}/auth/user-connector.do">权限管理</a></li>
                  <li class="divider"></li>
				  <li><a href="${scopePrefix}/bpm/console!listProcessDefinitions.do">流程管理</a></li>
				  <li><a href="${scopePrefix}/form/form-template.do">表单管理</a></li>
                  <li class="divider"></li>
                  <li><a href="${scopePrefix}/cms/cms-article.do">公告管理</a></li>
                  <li><a href="${scopePrefix}/party/tree.do">系统配置</a></li>
                </ul>
              </li>
            </ul>

            <ul class="nav pull-right">
              <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                  <sec:authentication property="principal.displayName" />
                  <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                  <li><a href="${scopePrefix}/user/change-password.do">修改密码</a></li>
                  <li><a href="${scopePrefix}/user/profile.do">个人信息</a></li>
                  <li class="divider"></li>
                  <li><a href="${scopePrefix}/j_spring_security_logout">退出</a></li>
                </ul>
              </li>
            </ul>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->
