<%@ page language="java" pageEncoding="UTF-8" %>

<%@include file="_header_first.jsp"%>

    <!-- start of header bar -->
    <div class="navbar navbar-default">
      <div class="navbar-inner">
        <div class="container">
          <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a href="${tenantPrefix}/" class="brand">
	        <img src="${tenantPrefix}/s/logo32.png" class="img-responsive pull-left" style="margin-top:-12px;margin-right:5px;">
		    Lemon <sub><small>1.7.0</small></sub>
		  </a>
          <div class="nav-collapse collapse navbar-responsive-collapse">
            <ul class="nav">
              <li class="divider-vertical"></li>

<tags:menuNav2 systemCode="user"/>

            </ul>

			<%@include file="_header_second.jsp"%>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->
