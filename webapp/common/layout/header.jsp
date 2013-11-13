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
          <a href="${ctx}/" class="brand">Mossle</a>
          <div class="nav-collapse collapse navbar-responsive-collapse">
            <ul class="nav">
              <li class="divider-vertical"></li>
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#">Link</a></li>
              <li><a href="#">Link</a></li>
              <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">Dropdown <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#">Action</a></li>
                  <li><a href="#">Another action</a></li>
                  <li><a href="#">Something else here</a></li>
                  <li class="divider"></li>
                  <li class="nav-header">Nav header</li>
                  <li><a href="#">Separated link</a></li>
                  <li><a href="#">One more separated link</a></li>
                </ul>
              </li>
              <li class="divider-vertical"></li>
            </ul>
            <form action="" class="navbar-search pull-left">
              <input type="text" placeholder="Search" class="search-query span1">
            </form>
            <ul class="nav pull-right">
              <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                  <sec:authentication property="principal.displayName" />
                  <i class="badge">5</i>
                  <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                  <li><a href="${ctx}/auth/change-password.do">修改密码</a></li>
                  <li><a href="#">Another action</a></li>
                  <li><a href="#">Something else here</a></li>
                  <li class="divider"></li>
                  <li><a href="#">Separated link</a></li>
                </ul>
              </li>
              <li><a href="${ctx}/j_spring_security_logout">退出</a></li>
            </ul>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->
