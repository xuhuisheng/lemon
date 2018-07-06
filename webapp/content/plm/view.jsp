<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "plm");%>
<%pageContext.setAttribute("currentMenu", "plm");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
	<style>
.module {
	padding: 10px;
}

.module-header {
	background: rgba(0, 0, 0, 0) url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAADCAYAAABS3WWCAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NEQ5RDgxQzc2RjQ5MTFFMjhEMUNENzFGRUMwRjhBRTciIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NEQ5RDgxQzg2RjQ5MTFFMjhEMUNENzFGRUMwRjhBRTciPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo0RDlEODFDNTZGNDkxMUUyOEQxQ0Q3MUZFQzBGOEFFNyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo0RDlEODFDNjZGNDkxMUUyOEQxQ0Q3MUZFQzBGOEFFNyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PvXFWFAAAAAYSURBVHjaYvj//z8D0/Pnz/8zgFgAAQYAS5UJscReGMIAAAAASUVORK5CYII=") repeat-x scroll 0 10px;
	padding-top: 2px;
	padding-bottom: 2px;
}

.module-title {
	background-color: white;
	display: inline-block;
	padding-right: 5px;
	font-weight: bold;
}

.module-list {
	list-style: outside none none;
	padding: 0px;
	margin-left: 10px;
}

.module-item {
	float: left;
	padding: 2px;
}

.module-list-1 .module-item {
	width: 100%;
}

.module-list-2 .module-item {
	width: 50%;
}

.module-label {
	width: 150px;
	display: inline-block;
	float: left;
}

.module-content {
	padding: 5px;
	margin-left: 10px;
}
	</style>

    <link type="text/css" rel="stylesheet" href="${ctx}/widgets/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="${ctx}/widgets/userpicker3-v2/userpicker.js"></script>
  </head>

  <body>
    <%@include file="/header/plm.jsp"%>

	<div class="row-fluid" style="padding-top:65px;">
      <div class="media col-md-12" style="padding:10px;padding-left:25px;">
        <div class="media-left">
          <a href="#">
            <img class="media-object img-circle" src="${ctx}/s/logo48.png" alt="...">
          </a>
        </div>
        <div class="media-body">
		  <div><a href="project.do?projectId=${plmIssue.plmProject.id}">${plmIssue.plmProject.code}</a> / PROJECT-1</div>
          <h3 class="media-heading">${plmIssue.name}</h3>
        </div>
      </div>

      <div class="col-md-12" style="padding:10px;padding-left:25px;">
			<c:if test="${plmIssue.reporterId == currentUserId}">
		    <a href="edit.do?id=${plmIssue.id}" class="btn btn-default">编辑</a>
			</c:if>
			<c:if test="${empty plmIssue.assigneeId}">
			  <a href="claim.do?id=${plmIssue.id}" class="btn btn-default">领取</a>
			</c:if>
			<c:if test="${plmIssue.assigneeId == currentUserId}">
			  <button type="button" class="btn btn-default" data-toggle="modal" data-target="#assignModal">分配</button>
			</c:if>
			<c:if test="${plmIssue.assigneeId == currentUserId && plmIssue.status == 'active'}">
			  <a href="complete.do?id=${plmIssue.id}" class="btn btn-default">完成</a>
			</c:if>
			<c:if test="${plmIssue.reporterId == currentUserId && plmIssue.status == 'complete'}">
			  <a href="reopen.do?id=${plmIssue.id}" class="btn btn-default">重开</a>
			</c:if>
      </div>
	</div>

    <div class="row-fluid">
	  <!-- left start -->
	  <div class="col-md-8">
	    <!-- details start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Details</div>
		  </div>
		  <ul class="module-list module-list-2">
		    <li class="module-item">
		      <b class="module-label">Type</b>
			  <span>${plmIssue.type}</span>
		    </li>
		    <li class="module-item">
		      <b class="module-label">Status</b>
			  <span>${plmIssue.status}</span>
		    </li>
		    <li class="module-item">
		      <b class="module-label">Priority</b>
			  <span>${plmIssue.severity}</span>
		    </li>
		    <li class="module-item">
		      <b class="module-label">Resolution</b>
			  <span>${plmIssue.resolution}</span>
		    </li>
		  </ul>
		  <div style="clear:both;"></div>
		</div>
	    <!-- details end -->
	    <!-- description start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Description</div>
		  </div>
		  <div class="module-content">
		    ${plmIssue.content}
		  </div>
		  <div style="clear:both;"></div>
		</div>
	    <!-- description end -->
	    <!-- attachment start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Attachment</div>
		  </div>
		  <div class="module-content">
		    ...
		  </div>
		  <div style="clear:both;"></div>
		</div>
	    <!-- attachment end -->
	    <!-- link start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Links</div>
		  </div>
		  <div class="module-content">
		    ...
		  </div>
		  <div style="clear:both;"></div>
		</div>
	    <!-- link end -->
	    <!-- activity start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Activity</div>
		  </div>
		  <div class="module-content">
	  
  <ul class="nav nav-tabs" id="myTab" style="margin-left:-15px;margin-top:10px;margin-right:-5px;padding-right:0px;">
    <li>&nbsp;&nbsp;&nbsp;&nbsp;</li>
    <li class="active"><a href="#plmCommentTab">评论</a></li>
    <li><a href="#plmLogTab">操作记录</a></li>
  </ul>
     
  <div class="tab-content">
    <div class="tab-pane active" id="plmCommentTab" style="margin-left:-15px;margin-top:10px;margin-right:-5px;">
      <tags:isUser>
	  <article class="m-widget">
	    <form id="f" method="post" action="saveComment.do">
		  <input type="hidden" name="issueId" value="${plmIssue.id}">
		  <div class="form-group">
			<textarea id="content" class="form-control required" name="content" style="width:100%;"></textarea>
		  </div>
		  <button type="submit" class="btn btn-default">发表回复</button>
	    </form>
      </article>
	  </tags:isUser>

      <article class="m-widget">
        <div class="content">
<c:forEach var="item" items="${plmComments}">
		  <p>
		    &nbsp; <tags:user userId="${item.userId}"/>
		    &nbsp; <fmt:formatDate value="${item.createTime}" type="both"/>
			<c:if test="${item.userId == currentUserId}">
			&nbsp; <button href="#commentModal" role="button" class="btn" data-toggle="modal" onclick="showModal(${item.id})">修改</button>
			</c:if>
		  </p>

		  <p>&nbsp; <span id="commentContent${item.id}"><c:out value="${item.content}"/></span></p>
		  <hr style="margin-left:-15px;">
</c:forEach>
        </div>
      </article>
	</div>
    <div class="tab-pane" id="plmLogTab">

      <article class="m-widget">
        <div class="content">
<c:forEach var="item" items="${plmLogs}">
		  <p>
		    &nbsp; <tags:user userId="${item.userId}"/>
		    &nbsp; <fmt:formatDate value="${item.logTime}" type="both"/>
			&nbsp; ${item.type}
		  </p>
		  <p>
		    ${item.content}
		  </p>
		  <hr style="margin-left:-15px;">
</c:forEach>
        </div>
      </article>
	</div>
  </div>
     
  <script>
  $(function () {
    $('#myTab a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    })
  })
  </script>
		  </div>
		  <div style="clear:both;"></div>
		</div>
	    <!-- activity end -->
	  </div>
	  <!-- left end -->
	  <!-- right start -->
	  <div class="col-md-4">
	    <!-- people start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">People</div>
		  </div>
		  <ul class="module-list module-list-1">
		    <li class="module-item">
		      <b class="module-label">Reportor</b>
			  <span><tags:user userId="${plmIssue.reporterId}"/></span>
		    </li>
		    <li class="module-item">
		      <b class="module-label">Assignee</b>
			  <span><tags:user userId="${plmIssue.assigneeId}"/></span>
		    </li>
		  </ul>
		  <div style="clear:both;"></div>
		</div>
	    <!-- people end -->
	    <!-- date start -->
	    <div class="module">
	      <div class="module-header">
		    <div class="module-title">Dates</div>
		  </div>
		  <ul class="module-list module-list-1">
		    <li class="module-item">
		      <b class="module-label">Create</b>
			  <span><fmt:formatDate value="${plmIssue.createTime}" type="both"/></span>
		    </li>
		    <li class="module-item">
		      <b class="module-label">Update</b>
			  <span><fmt:formatDate value="${plmIssue.createTime}" type="both"/></span>
		    </li>
		  </ul>
		  <div style="clear:both;"></div>
		</div>
	    <!-- date end -->
	  </div>
	  <!-- right end -->
	  <div style="clear:both;"></div>
	</div>

<div id="assignModal" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">分配</h4>
      </div>
      <div class="modal-body">
		<form method="post" action="assign.do" id="assignForm">
		  <input id="assignIssueId" type="hidden" name="id" value="${plmIssue.id}">
		  <div class="input-group userPicker" style="width:200px;" data-multiple="false">
			<input id="_task_name_key" type="hidden" name="userId" value="">
			<input type="text" class="form-control" name="username" placeholder="" value="">
			<div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
		  </div>
		</form>
      </div>
      <div class="modal-footer">
		<button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
		<button class="btn btn-primary" onclick="updateAssignee()">保存</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<script>

	createUserPicker({
		multiple: false,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});

function updateAssignee() {
	$('#assignForm').submit();
}
</script>

  </body>

</html>


