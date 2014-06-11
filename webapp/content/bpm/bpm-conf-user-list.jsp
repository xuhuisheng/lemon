<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.bpmCategory.list.title" text="用户库列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'bpmCategoryGrid',
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
	gridFormId: 'bpmCategoryGridForm',
	exportUrl: 'bpm-category-export.do'
};

var table;

$(function() {
    table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>

    <link type="text/css" rel="stylesheet" href="${scopePrefix}/widgets/userpicker/userpicker.css">
    <script type="text/javascript" src="${scopePrefix}/widgets/userpicker/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${scopePrefix}/rs/user/search'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">返回</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="bpmCategorySearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmCategorySearch" class="content" style="padding:10px;">

			<a class="btn btn-small" href="bpm-conf-node-list.do?bpmConfBaseId=${bpmConfBaseId}">返回</a>

		</div>
	  </article>

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">添加</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="bpmCategorySearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmCategorySearch" class="content content-inner">

		  <form name="bpmCategoryForm" method="post" action="bpm-conf-user-save.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="_task_name_key">参与者:</label>
		    <div class="input-append userPicker">
			  <input id="_task_name_key" type="hidden" name="value" class="input-medium" value="">
			  <input type="text" name="taskAssigneeNames" style="width: 175px;" value="">
			  <span class="add-on"><i class="icon-user"></i></span>
		    </div>
		    <label for="type">类型</label>
			<select name="type">
			  <option value="0">负责人</option>
			  <option value="1">候选人</option>
			  <option value="2">候选组</option>
			  <option value="3">抄送人</option>
			</select>
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">提交</button>
		  </form>

		</div>
	  </article>

<c:if test="${not empty bpmConfCountersign}">
	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">会签</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="bpmConfCountersignSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmConfCountersignSearchIcon" class="content content-inner">

		  <form name="bpmConfCountersignForm" method="post" action="bpm-conf-countersign-save.do" class="form-inline">
		    <input type="hidden" name="id" value="${bpmConfCountersign.id}">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="type">会签类型:</label>
			<select name="type">
			  <option value="0" ${bpmConfCountersign.type==0 ? 'selected' : ''}>全票通过</option>
			  <option value="1" ${bpmConfCountersign.type==1 ? 'selected' : ''}>比例通过</option>
			</select>
		    <label for="bpmConfCountersign_rate">通过率:</label>
		    <input id="bpmConfCountersign_rate" type="text" name="rate" class="input-medium number" value="${bpmConfCountersign.rate}">
			<button class="btn btn-small" onclick="document.bpmConfCountersignForm.submit()">提交</button>
		  </form>

		</div>
	  </article>
</c:if>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">参与者</h4>
		</header>
		<div class="content">

  <form id="bpmCategoryGridForm" name="bpmCategoryGridForm" method='post' action="bpm-conf-user-remove.do" style="margin:0px;">
    <input type="hidden" name="bpmConfNodeId" value="${bpmConfNodeId}">
    <table id="bpmCategoryGrid" class="m-table table-hover">
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
			  ${item.value}
			</c:if>
		    <c:if test="${item.type==1}">
			  ${item.value}
			</c:if>
		    <c:if test="${item.type==2}">
			  ${item.value}
			</c:if>
		    <c:if test="${item.type==3}">
			  ${item.value}
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
  </form>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
