<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "ticket");%>
<%pageContext.setAttribute("currentMenu", "ticket");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.ticket-group.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>

	<link href="${ctx}/s/bootstrap-step/css/bs-is-fun.css" rel="stylesheet" />
	<link href="${ctx}/s/layim.css" rel="stylesheet" />

	<style>
.ticket .active, 
.ticket .active a {
	color: white;
}
	</style>

	<script>
function replyMessage() {
	var message = $('#commentMessage').val();
	location.href = 'replyMessage.do?type=${param.type}&id=${param.id}&message=' + message;
}

function markResolve() {
	var message = $('#commentMessage').val();
	location.href = 'markResolve.do?type=${param.type}&id=${param.id}&message=' + message;
}

function markClose() {
	var message = $('#commentMessage').val();
	location.href = 'markClose.do?type=${param.type}&id=${param.id}&message=' + message;
}
	</script>
  </head>

  <body>
    <%@include file="/header/ticket-admin.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">

	  <div class="col-md-2">
	    <div class="panel panel-default">
		  <div class="panel-heading">
		    # 工单中心
		  </div>
		  <ul class="list-group">
		    <a class="list-group-item ${param.type == 'all' ? 'active' : ''}" href="index.do?type=all">
			  <span class="badge">${total}</span>
			  我的工单
		    </a>
		    <a class="list-group-item ${param.type == 'open' ? 'active' : ''}" href="index.do?type=open">
			  <span class="badge">${totalOpen}</span>
			  待处理
			</a>
		    <a class="list-group-item ${param.type == 'new' ? 'active' : ''}" href="index.do?type=new">
			  <span class="badge">${totalNew}</span>
			  未分配
			</a>
		    <a class="list-group-item ${param.type == 'pending' ? 'active' : ''}" href="index.do?type=pending">
			  <span class="badge">${totalPending}</span>
			  等待回复
			</a>
		    <a class="list-group-item ${param.type == 'resolved' ? 'active' : ''}" href="index.do?type=resolved">
			  <span class="badge">${totalResolved}</span>
			  已处理
			</a>
		    <a class="list-group-item ${param.type == 'closed' ? 'active' : ''}" href="index.do?type=closed">
			  <span class="badge">${totalClosed}</span>
			  已关闭
			</a>
		  </ul>
		</div>
	  </div>

	  <div class="col-md-3">
	    <c:if test="${not empty param.type}">
	    <div class="panel panel-default">
		  <div class="panel-heading">
		    <input type="text" class="form-control">
		  </div>
		  <ul class="list-group ticket">
		    <c:forEach var="item" items="${page.result}">
		    <li class="list-group-item ${item.id == param.id ? 'active' : ''}">
			  <h4 class="list-group-item-heading">
			    <a href="?type=${param.type}&id=${item.id}">${item.name}</a>
			    <c:if test="${empty item.assignee}">
			    <a class="btn btn-default btn-xs" href="claim.do?id=${item.id}">领取</a>
			    </c:if>
			  </h4>
			  <p class="list-group-item-text">客户: <tags:user userId="${item.creator}"/></p>
			  <p class="list-group-item-text">处理状态: ${item.status}</p>
			  <p class="list-group-item-text">更新时间: ${item.createTime}</p>
		    </li>
			</c:forEach>
		  </ul>
		</div>
		</c:if>
	  </div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-7">
	    <c:if test="${not empty param.id}">

	    <ul class="nav nav-pills">
		  <li class="active">
		    <a>详情</a>
		  </li>
		  <li>
		    <a href="edit.do?id=${param.id}">编辑</a>
		  </li>
	      <c:if test="${ticketInfo.status != 'resolved' && ticketInfo.status != 'closed'}">
		  <li>
		    <a>转发</a>
		  </li>
	      </c:if>
		  <!--
		  <li>
		    <a>日志</a>
		  </li>
		  -->
	      <c:if test="${ticketInfo.status != 'resolved' && ticketInfo.status != 'closed'}">
		  <li>
		    <a onclick="markResolve()">标记处理</a>
		  </li>
	      </c:if>
	      <c:if test="${ticketInfo.status != 'closed'}">
          <li>
		    <a onclick="markClose()">标记关闭</a>
		  </li>
	      </c:if>
		  <!--
		  <li>
		    <a>转为知识库</a>
		  </li>
		  -->
		</ul>

		<ul class="nav nav-pills nav-justified step step-arrow" style="margin-top:10px;">
			<li class="${ticketInfo.status=='new' || ticketInfo.status=='open' || ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>新发起</a>
			</li>
			<li class="${ticketInfo.status=='open' || ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>受理中</a>
			</li>
			<li class="${ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>已回复</a>
			</li>
			<li class="${ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>已处理</a>
			</li>
			<li class="${ticketInfo.status=='closed' ? 'active' : ''}">
				<a>已关闭</a>
			</li>
		</ul>

		<div style="background-color:#eee;padding:10px;margin-bottom: 20px;">
		  <h3 style="margin-top:0px;">${ticketInfo.name}</h3>

		  <p>客户: <tags:user userId="${ticketInfo.creator}"/> 问题类型:  优先级:  状态: ${ticketInfo.status} </p>

		  <p>请求时间: ${ticketInfo.createTime} </p>

		  <p>更新时间:  受理人: <tags:user userId="${ticketInfo.assignee}"/> </p>

		  <p>${ticketInfo.content}</p>
		</div>

		<div>
		  <form action="doAssign.do" method="post">
		  <input type="hidden" name="id" value="${param.id}">
		  <input type="text" name="username" value="" class="form-control" style="width:200px;float:left;">
		  <button class="btn btn-default" style="margin-left:10px;">转发</button>
		  </form>
		</div>

<ul class="layim_chatview layim_chatthis" id="layim_areaone100001">
  <c:forEach var="item" items="${ticketComments}">
  <li class="${item.type == 'reply' ? 'layim_chateme' : ''}">
    <div class="layim_chatuser">
      <span class="layim_chattime">${item.createTime}</span>
	  <span class="layim_chatname">${item.creator}</span>
	  <img src="${ctx}/s/logo16.png">
	</div>
	<div class="layim_chatsay">${item.content}
      <em class="layim_zero"></em>
	</div>
  </li>
  </c:forEach>
</ul>

        </c:if>
      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

