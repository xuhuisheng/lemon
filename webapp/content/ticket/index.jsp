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

	<link href="${cdnPrefix}/public/bootstrap-step/0.0.11/css/bs-is-fun.css" rel="stylesheet" />
	<link href="${cdnPrefix}/public/mossle/0.0.11/css/layim.css" rel="stylesheet" />
	<style>
.ticket .active, 
.ticket .active a {
	color: white;
}
	</style>

	<script>
function replyMessage() {
	// var message = $('#commentMessage').val();
	var message = CKEDITOR.instances.commentMessage.getData();
	location.href = 'replyMessage.do?type=${param.type}&id=${param.id}&message=' + message;
}

function markResolve() {
	// var message = $('#commentMessage').val();
	var message = CKEDITOR.instances.commentMessage.getData();
	location.href = 'markResolve.do?type=${param.type}&id=${param.id}&message=' + message;
}

function markClose() {
	// var message = $('#commentMessage').val();
	var message = CKEDITOR.instances.commentMessage.getData();
	location.href = 'markClose.do?type=${param.type}&id=${param.id}&message=' + message;
}
	</script>

    <script type="text/javascript">
$(function() {
	var editor = CKEDITOR.replace('commentMessage');
	editor.config.filebrowserImageUploadUrl = "${tenantPrefix}/cms/cms-article-uploadImage.do";
})
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
		    <a class="list-group-item ${param.type == 'all' ? 'active' : ''}" href="?type=all">
			  <span class="badge">${total}</span>
			  我的工单
		    </a>
		    <a class="list-group-item ${param.type == 'open' ? 'active' : ''}" href="?type=open">
			  <span class="badge">${totalOpen}</span>
			  待处理
			</a>
		    <a class="list-group-item ${param.type == 'new' ? 'active' : ''}" href="?type=new">
			  <span class="badge">${totalNew}</span>
			  未分配
			</a>
		    <a class="list-group-item ${param.type == 'pending' ? 'active' : ''}" href="?type=pending">
			  <span class="badge">${totalPending}</span>
			  等待回复
			</a>
		    <a class="list-group-item ${param.type == 'resolved' ? 'active' : ''}" href="?type=resolved">
			  <span class="badge">${totalResolved}</span>
			  已处理
			</a>
		    <a class="list-group-item ${param.type == 'closed' ? 'active' : ''}" href="?type=closed">
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
			  <p class="list-group-item-text">更新时间: <fmt:formatDate value="${item.updateTime}" type="both"/></p>
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
		    <a href="assign.do?type=${param.type}&id=${param.id}">转发</a>
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

		  <p>请求时间: <fmt:formatDate value="${ticketInfo.createTime}" type="both"/> </p>

		  <p>
		    更新时间: <fmt:formatDate value="${ticketInfo.updateTime}" type="both"/>
			受理人: <tags:user userId="${ticketInfo.assignee}"/>
			<c:if test="${ticketInfo.status == 'closed'}">
			  满意度: ${ticketInfo.survey} ${ticketInfo.surveyMessage}
			</c:if>
		  </p>

		  <p>${ticketInfo.content}</p>

          <c:if test="${not empty ticketInfo.ticketAttachments}">
		  <p>附件: </p>
          <ul>
		    <c:forEach var="item" items="${ticketInfo.ticketAttachments}">
		    <li><a href="download.do?key=${item.code}">${item.name}</a></li>
			</c:forEach>
		  </ul>
		  </c:if>
		</div>

        <c:if test="${ticketInfo.status != 'closed'}">
		<div>
		  <textarea class="form-control" name="message" id="commentMessage"></textarea>
		  <div style="margin-top:10px;" class="pull-left">
		    <select class="form-control" onchange="$('#commentMessage').val(this.value)">
			  <option value=""></option>
			  <option value="请问什么问题">请问什么问题</option>
			</select>
		  </div>
		  <div style="margin-top:10px;" class="pull-right">
		    <label class="checkbox-inline">
		      <input type="checkbox">内部回复
			</label>
			<button class="btn btn-primary" onclick="replyMessage()">发送</button>
		  </div>
		  <div style="clear:both;"></div>
		</div>
		</c:if>

<ul class="layim_chatview layim_chatthis" id="layim_areaone100001">
  <c:forEach var="item" items="${ticketComments}">
  <li class="${item.type == 'reply' ? 'layim_chateme' : ''}">
    <div class="layim_chatuser">
	  <c:if test="${item.type == 'reply'}">
      <span class="layim_chattime"><fmt:formatDate value="${item.createTime}" type="both"/></span>
	  <span class="layim_chatname"><tags:user userId="${item.creator}"/></span>
	  <img src="${cdnPrefix}/public/mossle/0.0.11/logo16.png">
	  </c:if>
	  <c:if test="${item.type != 'reply'}">
	  <img src="${ctx}/s/logo16.png">
	  <span class="layim_chatname"><tags:user userId="${item.creator}"/></span>
      <span class="layim_chattime"><fmt:formatDate value="${item.createTime}" type="both"/></span>
	  </c:if>
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

