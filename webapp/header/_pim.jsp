<%@ page language="java" pageEncoding="UTF-8" %>
<div class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="${tenantPrefix}/">
	    <img src="${tenantPrefix}/s/logo32.png" class="img-responsive pull-left" style="margin-top:-5px;margin-right:5px;">
	    Lemon <sub><small>1.7.0</small></sub>
      </a>
    </div>

    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav" id="navbar-menu">
		<tags:menuNav3 systemCode="pim"/>
      </ul>

      <ul class="nav navbar-nav navbar-right">
	    <li>
          <form class="navbar-form navbar-search" action="${tenantPrefix}/pim/address-list-list.do" role="search">
            <div class="form-group">
              <input type="text" class="form-control search-query" placeholder="搜索" name="username">
            </div>
          </form>
	    </li>
	  
		<tags:menuSystem3/>

        <li class="dropdown">
          <a data-toggle="dropdown" class="dropdown-toggle" href="#">
		    <img src="${tenantPrefix}/rs/avatar?id=<tags:currentUserId/>&width=16" style="width:16px;height:16px;" class="img-circle">
			<tags:currentUser/>
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu">
            <li><a href="${tenantPrefix}/user/my-info-input.do"><i class="glyphicon glyphicon-user"></i>个人信息</a></li>
            <li class="divider"></li>
			  <li><a href="${tenantPrefix}/j_spring_security_logout"><i class="glyphicon glyphicon-user"></i>退出</a></li>
          </ul>
        </li>
		<li>
          <a href="${tenantPrefix}/msg/msg-info-listReceived.do">
            <i class="glyphicon glyphicon-bell"></i>
			<i id="unreadMsg" class="badge"></i>
	      </a>
		</li>

      </ul>
    </div>

  </div>
</div>
