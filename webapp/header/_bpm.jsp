<%@ page language="java" pageEncoding="UTF-8" %>

<%@include file="_header_first.jsp"%>

    <!-- start of header bar -->
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a data-target=".navbar-responsive-collapse" data-toggle="collapse" class="btn btn-navbar">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a href="${tenantPrefix}/" class="brand">Lemon <sub><small>1.6.1</small></sub></a>
          <div class="nav-collapse collapse navbar-responsive-collapse">
            <ul class="nav">
              <li class="divider-vertical"></li>

<tags:menuNav2 systemCode="bpm"/>

            </ul>

			<%@include file="_header_second.jsp"%>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <!-- end of header bar -->
