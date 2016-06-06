<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.bpm-conf-user.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
/*
var config = {
    id: 'bpm-conf-userGrid',
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
	gridFormId: 'bpm-conf-userGridForm',
	exportUrl: 'bpm-conf-user-export.do'
};
*/
var config = {};
var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
    <link type="text/css" rel="stylesheet" href="${tenantPrefix}/widgets/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="${tenantPrefix}/widgets/userpicker3-v2/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		showExpression: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-console.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

	  <ul class="breadcrumb">
	    <li><a href="bpm-process-list.do">流程配置</a></li>
	    <li><a href="bpm-conf-node-list.do?bpmConfBaseId=${bpmConfBaseId}">${bpmConfBase.processDefinitionKey}</a></li>
	    <li class="active">${bpmConfNode.name}</li>
	  </ul>
	  
	  <ul class="nav nav-tabs">
        <li class="active"><a href="#tab-user" data-toggle="tab">参与者</a></li>
        <li><a href="#tab-assign" data-toggle="tab">分配策略</a></li>
<c:if test="${not empty bpmConfCountersign}">
        <li><a href="#tab-countersign" data-toggle="tab">会签</a></li>
</c:if>
      </ul>

      <div class="tab-content">
        <div class="tab-pane active" id="tab-user">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    添加
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="bpm-conf-userSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="bpmCategoryForm" method="post" action="bpm-conf-user-save.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="_task_name_key">参与者:</label>
		    <div class="input-group userPicker" style="display:block-inline;">
			  <input id="_task_name_key" type="hidden" name="value" class="input-medium" value="">
			  <input type="text" name="taskAssigneeNames" style="width: 175px;" value="" class="form-control">
			  <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
		    </div>
		    <label for="type">类型</label>
			<select name="type" class="form-control">
			  <option value="0">负责人</option>
			  <option value="1">候选人</option>
			  <option value="2">候选组</option>
			  <option value="3">抄送人</option>
			</select>
			<button class="btn btn-default" onclick="document.bpmCategoryForm.submit()">提交</button>
		  </form>

		</div>
	  </div>
<%--
	  <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='bpm-conf-user-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size form-control" style="display:inline;width:auto;">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
        </div>

	    <div class="clearfix"></div>
	  </div>
--%>

<form id="bpm-conf-userGridForm" name="bpm-conf-userGridForm" method='post' action="bpm-conf-user-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


    <input type="hidden" name="bpmConfNodeId" value="${bpmConfNodeId}">
    <table id="bpmCategoryGrid" class="table table-hover">
      <thead>
        <tr>
          <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="user.bpmCategory.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="user.bpmCategory.list.name" text="名称"/></th>
          <th class="sorting" name="type">类型</th>
          <th class="sorting" name="priority">状态</th>
          <th width="100">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfUsers}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>
		    <c:if test="${item.type==0}">
			  <tags:user userId="${item.value}"/>
			</c:if>
		    <c:if test="${item.type==1}">
			  <tags:user userId="${item.value}"/>
			</c:if>
		    <c:if test="${item.type==2}">
			  <tags:user userId="${item.value}"/>
			</c:if>
		    <c:if test="${item.type==3}">
			  <tags:user userId="${item.value}"/>
			</c:if>
		  </td>
          <td>
		    <c:if test="${item.type==0}">
			  负责人
			</c:if>
			<c:if test="${item.type==1}">
			  候选人
			</c:if>
			<c:if test="${item.type==2}">
			  候选组
			</c:if>
			<c:if test="${item.type==3}">
			  抄送人
			</c:if>
		  </td>
          <td>
		    <c:if test="${item.status==0}">
			  默认
			</c:if>
			<c:if test="${item.status==1}">
			  添加
			</c:if>
			<c:if test="${item.status==2}">
			  删除
			</c:if>
		  </td>
          <td>
		    <a href="bpm-conf-user-remove.do?id=${item.id}">删除</a>
          </td>
        </tr>
        </c:forEach>
      </tbody>
    </table>


      </div>
</form>

<%--
	  <div>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-default">&lt;</button>
		  <button class="btn btn-default">1</button>
		  <button class="btn btn-default">&gt;</button>
		</div>

	    <div class="clearfix"></div>
      </div>
--%>
      <div class="m-spacer"></div>

</div>





        <div class="tab-pane" id="tab-assign">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    分配策略
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="bpm-conf-userSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">


		  <form name="bpmConfAssignForm" method="post" action="bpm-conf-assign-save.do" class="form-inline">
		    <input type="hidden" name="id" value="${bpmConfAssign.id}">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="bpmConfAssignName">分配策略:</label>
			<select id="bpmConfAssignName" name="name" class="form-control">
			  <option value="无" ${bpmConfAssign.name=='无' ? 'selected' : ''}>无</option>
			  <option value="当只有一人时采用独占策略" ${bpmConfAssign.name=='当只有一人时采用独占策略' ? 'selected' : ''}>当只有一人时采用独占策略</option>
			  <option value="资源中任务最少者" ${bpmConfAssign.name=='资源中任务最少者' ? 'selected' : ''}>资源中任务最少者 </option>
			  <option value="资源中随机分配" ${bpmConfAssign.name=='资源中随机分配' ? 'selected' : ''}>资源中随机分配</option>
			</select>
		    
			<button class="btn btn-small" onclick="document.bpmConfAssignForm.submit()">提交</button>
		  </form>

		</div>
	  </div>
		</div>
<c:if test="${not empty bpmConfCountersign}">
        <div class="tab-pane" id="tab-countersign">


<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    会签
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="bpm-conf-userSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">


		  <form name="bpmConfCountersignForm" method="post" action="bpm-conf-countersign-save.do" class="form-inline">
		    <input type="hidden" name="id" value="${bpmConfCountersign.id}">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="type">会签类型:</label>
			<select name="type">
			  <option value="0" ${bpmConfCountersign.type==0 ? 'selected' : ''}>全票通过</option>
			  <option value="1" ${bpmConfCountersign.type==1 ? 'selected' : ''}>比例通过</option>
			</select>
		    <label for="bpmConfCountersign_rate">通过率:</label>
            <div class="input-append">
              <input id="bpmConfCountersign_rate" type="text" name="rate" class="input-medium number" value="${bpmConfCountersign.rate}">
              <span class="add-on" style="padding:2px;">%</span>
            </div>
		    
			<button class="btn btn-small" onclick="document.bpmConfCountersignForm.submit()">提交</button>
		  </form>

		</div>
	  </div>
		</div>
</c:if>
      </div>




</div>
      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

