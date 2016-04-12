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
    <script type="text/javascript">
var config = {
    id: 'pimInfoGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'pimInfoGridForm',
	exportUrl: 'pim-info-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
  </head>

  <body>
    <%@include file="/header/plm.jsp"%>

    <div class="row-fluid">

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-controls="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        ${plmProject.name}
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/project.do?projectId=${plmProject.id}"><i class="glyphicon glyphicon-list"></i> 返回项目</a></li>
		  <tags:isUser>
		  <li><a href="${tenantPrefix}/plm/create.do?projectId=${plmProject.id}"><i class="glyphicon glyphicon-list"></i> 新建任务</a></li>
		  </tags:isUser>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-controls="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        &nbsp;
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/index.do"><i class="glyphicon glyphicon-list"></i> 返回首页</a></li>
		  <li><a href="${tenantPrefix}/plm/projects.do"><i class="glyphicon glyphicon-list"></i> 所有项目</a></li>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    详情
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
        <div class="panel-content">
		  <div class="pull-right">
			<c:if test="${plmIssue.reporterId == currentUserId}">
		    <a href="edit.do?id=${plmIssue.id}" class="btn btn-default">编辑</a>
			</c:if>
			<c:if test="${empty plmIssue.assigneeId}">
			  <a href="claim.do?id=${plmIssue.id}" class="btn btn-default">领取</a>
			</c:if>
			<c:if test="${plmIssue.assigneeId == currentUserId}">
			  <a href="#assignModal" role="button" class="btn btn-default" data-toggle="modal">分配</a>
			</c:if>
			<c:if test="${plmIssue.assigneeId == currentUserId && plmIssue.status == 'active'}">
			  <a href="complete.do?id=${plmIssue.id}" class="btn btn-default">完成</a>
			</c:if>
			<c:if test="${plmIssue.reporterId == currentUserId && plmIssue.status == 'complete'}">
			  <a href="reopen.do?id=${plmIssue.id}" class="btn btn-default">重开</a>
			</c:if>
		  </div>

		  <p>&nbsp; 名称：${plmIssue.name}</p>

		  <p>&nbsp; 项目：${plmProject.name}</p>

		  <p>&nbsp; 类型：${plmIssue.type}</p>

		  <p>&nbsp; 报告人：<tags:user userId="${plmIssue.reporterId}"/></p>

		  <p>&nbsp; 创建时间：<fmt:formatDate value="${plmIssue.createTime}" type="both"/></p>

		  <p>&nbsp; 负责人：<tags:user userId="${plmIssue.assigneeId}"/></p>

		  <p>&nbsp; 完成时间：<fmt:formatDate value="${plmIssue.completeTime}" type="both"/></p>

		  <p>&nbsp; 状态：${plmIssue.status}</p>

		  <p>&nbsp; 描述：</p>
		  
		  <p style="padding:5px;">${plmIssue.content}</p>

        </div>
</div>
        <div class="m-spacer"></div>



	  
  <ul class="nav nav-tabs" id="myTab">
    <li class="active"><a href="#plmCommentTab">评论</a></li>
    <li><a href="#plmLogTab">操作记录</a></li>
  </ul>
     
  <div class="tab-content">
    <div class="tab-pane active" id="plmCommentTab">
      <tags:isUser>
	  <article class="m-widget">
	    <form id="f" method="post" action="saveComment.do">
		  <input type="hidden" name="issueId" value="${plmIssue.id}">
		  <div class="form-group">
			<textarea id="content" class="form-control required" name="content" style="width:90%;"></textarea>
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
		  <hr>
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
		  <hr>
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


      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>


