<%@ page language="java" pageEncoding="UTF-8" %>

    <c:if test="${not empty flashMessages}">
	<div id="m-success-message" style="display:none;">
	  <ul>
	  <c:forEach items="${flashMessages}" var="item">
	    <li>${item}</li>
	  </c:forEach>
	  </ul>
	</div>
	</c:if>
	<script type="text/javascript">
function unreadCount() {
	$.getJSON('${scopePrefix}/rs/msg/unreadCount', {}, function(data) {
		if (data.data == 0) {
			$('#unreadMsg').html('');
		} else {
			$('#unreadMsg').html(data.data);
		}
	});
}

unreadCount();
setInterval(unreadCount, 10000);
	</script>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a href="${scopePrefix}/" class="brand">Lemon <sub><small>1.5.0-SNAPSHOT</small></sub></a>
          <div class="nav-collapse collapse navbar-responsive-collapse">
            <ul class="nav">
              <li class="divider-vertical"></li>
              <li class="${currentHeader == 'dashboard' ? 'active' : ''}"><a href="${scopePrefix}/dashboard/dashboard.do"><i class="icon-user"></i>首页</a></li>
              <li class="${currentHeader == 'bpm-workspace' ? 'active' : ''}"><a href="${scopePrefix}/bpm/workspace-home.do"><i class="icon-user"></i>个人事务</a></li>
              <li class="${currentHeader == 'report' ? 'active' : ''}"><a href="${scopePrefix}/report/chart-mostActiveProcess.do"><i class="icon-user"></i>统计报表</a></li>
              <li class="dropdown ${currentHeader == 'scope' ? 'active' : ''}">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#"><i class="icon-user"></i>系统管理 <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="${scopePrefix}/user/account-info-list.do"><i class="icon-user"></i>用户管理</a></li>
                  <li><a href="${scopePrefix}/party/org-list.do"><i class="icon-user"></i>组织机构</a></li>
                  <li class="divider"></li>
                  <li><a href="${scopePrefix}/auth/user-connector-list.do"><i class="icon-user"></i>权限管理</a></li>
                  <li class="divider"></li>
				  <li><a href="${scopePrefix}/bpm/bpm-process-list.do"><i class="icon-user"></i>流程管理</a></li>
				  <li><a href="${scopePrefix}/form/form-template-list.do"><i class="icon-user"></i>表单管理</a></li>
				  <li><a href="${scopePrefix}/humantask/task-info-list.do"><i class="icon-user"></i>任务管理</a></li>
                  <li class="divider"></li>
                  <li><a href="${scopePrefix}/cms/cms-article-list.do"><i class="icon-user"></i>公告管理</a></li>
                  <li><a href="${scopePrefix}/car/car-info-list.do"><i class="icon-user"></i>车辆管理</a></li>
                  <li><a href="${scopePrefix}/meeting/meeting-info-list.do"><i class="icon-user"></i>会议室管理</a></li>
                  <li><a href="${scopePrefix}/party/tree-list.do"><i class="icon-user"></i>系统配置</a></li>
                </ul>
              </li>
            </ul>

            <ul class="nav pull-right">
			  <li>
			    <form class="navbar-search" action="${ctx}/pim/address-list-list.do">
                  <input type="text" class="search-query" placeholder="搜索" name="username">
                </form>
			  </li>
              <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
			      <img src="${scopePrefix}/rs/avatar?id=<tags:currentUserId/>&width=16" style="width:16px;height:16px;" class="img-circle">
				  <tags:currentUser/>
                  <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                  <li><a href="${ctx}/user/change-password-input.do"><i class="icon-user"></i>修改密码</a></li>
                  <li><a href="${ctx}/user/profile-list.do"><i class="icon-user"></i>个人信息</a></li>
                  <li class="divider"></li>
				  <li><a href="${ctx}/j_spring_security_logout"><i class="icon-user"></i>退出</a></li>
                </ul>
              </li>
			  <li>
                <a href="${scopePrefix}/msg/msg-info-listReceived.do">
                  <i id="unreadMsg" class="badge"></i>
				</a>
			  </li>
            </ul>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->
