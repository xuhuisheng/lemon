<%@ page language="java" pageEncoding="UTF-8" %>

<%@include file="_header_first.jsp"%>

<div class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <%@include file="_header_title.jsp"%>

    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav" id="navbar-menu">
		<tags:menuNav3 systemCode="pim"/>
      </ul>

      <ul class="nav navbar-nav navbar-right">
	    <%@include file="_header_tool.jsp"%>

		<li><button class="btn btn-default btn-sm navbar-btn" onclick="insertWidget()"><i class="glyphicon glyphicon-plus"></i></button></li>
      </ul>
    </div>

  </div>
</div>
