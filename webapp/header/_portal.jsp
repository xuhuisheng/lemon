<%@ page language="java" pageEncoding="UTF-8" %>

<%@include file="_header_first.jsp"%>

<div class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="${tenantPrefix}/portal/index.do">
	    <img src="${cdnPrefix}/logo32.png" class="img-responsive pull-left" style="margin-top:-5px;margin-right:5px;">
	    Lemon <sub><small>1.8.0</small></sub>
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
		    <li class="text-center">&nbsp;<img src="${tenantPrefix}/rs/avatar?id=<tags:currentUserId/>&width=64" style="width:64px;height:64px;" class="img-rounded"></li>
            <li><a href="${tenantPrefix}/user/my-info-input.do"><i class="glyphicon glyphicon-list"></i> 个人信息</a></li>
            <li class="divider"></li>
			<li><a href="${tenantPrefix}/j_spring_security_logout"><i class="glyphicon glyphicon-list"></i> 退出</a></li>
          </ul>
        </li>
		<li>
          <a href="${tenantPrefix}/msg/msg-info-listReceived.do">
            <i class="glyphicon glyphicon-bell"></i>
			<i id="unreadMsg" class="badge"></i>
	      </a>
		</li>

		<li><button class="btn btn-default btn-sm navbar-btn" onclick="insertWidget()"><i class="glyphicon glyphicon-plus"></i></button></li>
      </ul>
    </div>

  </div>
</div>

	  <style>
.feedback {
    bottom: 10px;
    height: 54px;
    left: 100%;
    margin-left: -60px;
    position: fixed;
    width: 54px;
    z-index: 998;
}

.feedback a {
	display: inline-block;
	border-radius:50%;
	height: 48px;
	width: 48px;
	background-color:gray;
	padding: 10px;
	text-align:center;
	color: white;
	line-height: 14px;
	font-size: 12px;
}
	  </style>

	  <div class="feedback" data-toggle="modal" data-target="#feedbackModal">
	    <a href="javascript:void(0);">
		  <i class="glyphicon glyphicon-envelope"></i>
		  <br>
		  反馈
		</a>
	  </div>

<div class="modal fade" tabindex="-1" role="dialog" id="feedbackModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">反馈</h4>
      </div>
      <div class="modal-body">
		<textarea id="feedbackContent" class="form-control"></textarea>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" onclick="submitFeedback()">提交</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script>
function submitFeedback() {
	$.post('${tenantPrefix}/feedback/submit.do', {
		content: $('#feedbackContent').val(),
		contact: ''
	}, function() {
		$('#feedbackContent').val('');
		$('#feedbackModal').modal("hide");
	});
}
</script>


