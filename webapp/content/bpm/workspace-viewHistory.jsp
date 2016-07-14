<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s3.jsp"%>
	<script>
$(function () {
  $('#processGraphMask').css($('#processGraphWrapper').position());
  $('#processGraphMask').width($('#processGraphWrapper').width());
  $('#processGraphMask').height($('#processGraphWrapper').height());

  $('[data-toggle="popover"]').popover();
});
	</script>

    <script type="text/javascript" src="${tenantPrefix}/widgets/replay/scripts/JobExecutor.js"></script>
    <script type="text/javascript" src="${tenantPrefix}/widgets/replay/scripts/Node.js"></script>
    <script type="text/javascript" src="${tenantPrefix}/widgets/replay/scripts/Token.js"></script>
    <script type="text/javascript" src="${tenantPrefix}/widgets/replay/scripts/Replay.js"></script>
    <script type="text/javascript">
REPLAY_TOKEN_IMAGE = '${tenantPrefix}/widgets/replay/images/user.png';
REPLAY_TOKEN_IMAGE = '${tenantPrefix}/rs/avatar?id=${1}&width=64';
REPLAY_TOKEN_WIDTH = 32;
    </script>
    <script type="text/javascript">
var processDefinition = [
<c:forEach items="${nodeDtos}" var="item" varStatus="status">
{
    name: '${item.id}',
    type: '${item.type}',
    x: ${item.x},
    y: ${item.y},
    w: ${item.width},
    h: ${item.height},
    ts: [
	<c:forEach items="${item.outgoings}" var="outgoing" varStatus="outgoingStatus">
	{
        name: '${outgoing.id}',
        to: '${outgoing.id}',
		g: ${outgoing.g}
    }${outgoingStatus.last ? '' : ','}
	</c:forEach>
	]
}${status.last ? '' : ','}
</c:forEach>
];

var historyActivities = [
<c:forEach items="${graph.nodes}" var="node" varStatus="status">
<c:forEach items="${node.outgoingEdges}" var="edge">
{
    name: '${edge.src.name}',
    t: '${edge.dest.name}'
}${status.last ? '' : ','}
</c:forEach>
</c:forEach>
];

var currentActivities = [
<c:forEach items="${currentActivities}" var="item" varStatus="status">
'${item}'${status.last ? '' : ','}
</c:forEach>
];

var replay = new Replay(
    processDefinition,
    historyActivities,
    currentActivities
);
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace3.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  流程图 ${historicProcessInstance.name}
		  <div class="pull-right">
		    <button class="btn btn-xs" onclick="replay.prev()"><i class="glyphicon glyphicon-backward"></i></button>
		    <button class="btn btn-xs" onclick="replay.next()"><i class="glyphicon glyphicon-forward"></i></button>
		    <button class="btn btn-xs" onclick="replay.replay()"><i class="glyphicon glyphicon-play"></i></button>
		  </div>
		</div>

		<div class="panel-body">

        <div id="processGraphWrapper" class="content">

		  <img src="workspace-graphHistoryProcessInstance.do?processInstanceId=${param.processInstanceId}">

		  <div id="processGraphMask" style="position:absolute;">
		    <c:forEach items="${nodeDtos}" var="item">
		    <div style="position:absolute;left:${item.x}px;top:${item.y}px;width:${item.width}px;height:${item.height}px;" data-container="body" data-trigger="hover" data-toggle="popover" data-placement="bottom" data-html="true" data-content="<table><tr><td>节点类型:</td><td>${item.type}</td></tr><tr><td>节点名称:</td><td>${item.name}</td></tr><tr></table>"></div>
			</c:forEach>
		  </div>

		</div>
		</div>
      </div>

      <div class="panel panel-default">
        <div class="panel-heading">
		  列表
		</div>
  <table id="demoGrid" class="table table-hover">
    <thead>
      <tr>
	    <%--
        <th class="sorting" name="id">编号</th>
		--%>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="startTime">开始时间</th>
        <th class="sorting" name="endTime">结束时间</th>
        <th class="sorting" name="assignee">负责人</th>
        <th>状态</th>
        <th>意见</th>
	  </tr>
    </thead>

    <c:forEach items="${humanTasks}" var="item">
    <tbody>
      <tr>
	    <%--
	    <td>${item.id}</td>
		--%>
	    <td>${item.name}</td>
	    <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><fmt:formatDate value="${item.completeTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td>
		  <tags:user userId="${item.assignee}"/>
		  <c:if test="${not empty item.owner && item.assignee != item.owner}">
		  <b>(原执行人:<tags:user userId="${item.owner}"/>)</b>
		  </c:if>
		</td>
	    <td>${item.action}</td>
	    <td>${item.comment}</td>
      </tr>
	  <c:forEach items="${item.children}" var="child">
      <tr>
	    <td class="active text-muted">&nbsp;*&nbsp;${child.name}</td>
	    <td class="active text-muted"><fmt:formatDate value="${child.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td class="active text-muted"><fmt:formatDate value="${child.completeTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td class="active text-muted">
		  <tags:user userId="${child.assignee}"/>
		  <c:if test="${not empty child.owner && child.assignee != child.owner}">
		  <b>(原执行人:<tags:user userId="${child.owner}"/>)</b>
		  </c:if>
		</td>
	    <td class="active text-muted">
		  <c:if test="${child.catalog == 'communicate'}">
		    沟通
		  </c:if>
		  <c:if test="${child.catalog == 'copy'}">
		    抄送
		  </c:if>
		  <c:if test="${child.catalog == 'vote'}">
		    加签
		  </c:if>
		  (${child.action})
		</td>
	    <td class="active text-muted">${child.comment}</td>
      </tr>
	    <c:forEach items="${child.children}" var="third">
      <tr>
	    <td class="active text-muted">&nbsp;**&nbsp;${third.name}</td>
	    <td class="active text-muted"><fmt:formatDate value="${third.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td class="active text-muted"><fmt:formatDate value="${third.completeTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td class="active text-muted">
		  <tags:user userId="${third.assignee}"/>
		  <c:if test="${not empty third.owner && third.assignee != third.owner}">
		  <b>(原执行人:<tags:user userId="${third.owner}"/>)</b>
		  </c:if>
		</td>
	    <td class="active text-muted">
		  <c:if test="${third.catalog == 'vote'}">
		    加签
		  </c:if>
		  (${third.action})
		</td>
	    <td class="active text-muted">${third.comment}</td>
      </tr>
	    </c:forEach>
	  </c:forEach>
    </tbody>
    </c:forEach>
  </table>
      </div>

<!--
      <article class="m-widget">
        <header class="header">
		  <h4 class="title">表单</h4>
		</header>
		<div class="content">

  <table id="demoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="variableName">名称</th>
        <th class="sorting" name="value">值</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${historicVariableInstances}" var="item">
      <tr>
	    <td>${item.variableName}</td>
	    <td>${item.value}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
        </div>
      </article>
-->

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
