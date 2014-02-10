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
	exportUrl: 'bpm-category!exportExcel.do'
};

var table;

$(function() {
    table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
	<script type="text/javascript">
$(function() {
var taskDefinitionId = null;

	$(document).delegate('.userPickerBtn', 'click', function(e) {
		taskDefinitionId = $(this).attr("id");
		$('#userPicker').modal();
		$.ajax({
			url: '${scopePrefix}/rs/user/search',
			data: {
				username: ''
			},
			success: function(data) {
				var html = '';
				for (var i = 0; i < data.length; i++) {
					var item = data[i];
					html +=
					  '<tr>'
						+'<td><input id="selectedItem' + i + '" type="radio" class="selectedItem" name="selectedItem" value="'
						+ item.id + '" title="' + item.displayName + '"></td>'
						+'<td><label for="selectedItem' + i + '">' + item.displayName + '</label></td>'
					  +'</tr>'
				}
				$('#userPickerBody').html(html);
			}
		});
	});

	$(document).delegate('#userPickerBtnSelect', 'click', function(e) {
		$('#_task' + taskDefinitionId).val($('.selectedItem:checked').val());
		$('#_task_name' + taskDefinitionId).val($('.selectedItem:checked').attr('title'));
		$('#userPicker').modal('hide');
	});
});
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

			<a class="btn btn-small" href="bpm-conf-node.do?bpmConfBaseId=${bpmConfBaseId}">返回</a>

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

		  <form name="bpmCategoryForm" method="post" action="bpm-conf-user!save.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${bpmConfNodeId}">
		    <label for="_task_name_key">参与者:</label>
		    <input type="hidden" name="value" class="input-medium userPicker" id="_task_key" value="">
		    <input type="text" name="taskAssigneeNames" class="input-medium userPicker" id="_task_name_key" value="">
		    <span style="padding:2px;" class="add-on"><i id='_key' style="cursor:pointer;" class="icon-user userPickerBtn"></i></span>
		    <label for="type">类型</label>
			<select name="type">
			  <option value="0">负责人</option>
			  <option value="1">候选人</option>
			  <option value="2">候选组</option>
			</select>
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">提交</button>
		  </form>

		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">参与者</h4>
		</header>
		<div class="content">

  <form id="bpmCategoryGridForm" name="bpmCategoryGridForm" method='post' action="bpm-conf-user!remove.do" style="margin:0px;">
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
        <s:iterator value="bpmConfUsers" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.value}</td>
          <td>
		    <s:if test="type==0">
			  负责人
			</s:if>
			<s:if test="type==1">
			  候选人
			</s:if>
			<s:if test="type==2">
			  候选组
			</s:if>
		  </td>
          <td>
		    <s:if test="status==0">
			  默认
			</s:if>
			<s:if test="status==1">
			  添加
			</s:if>
			<s:if test="status==2">
			  删除
			</s:if>
		  </td>
          <td>
		    <a href="bpm-conf-user!remove.do?id=${item.id}">删除</a>
          </td>
        </tr>
        </s:iterator>
      </tbody>
    </table>
  </form>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>



<div id="userPicker" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>选择用户</h3>
  </div>
  <div class="modal-body">



      <!--
	  <article class="m-blank">
	    <div class="pull-left">
		  <form name="userForm" method="post" action="javascript:void(0);return false;" class="form-inline m-form-bottom">
    	    <label for="user_username">账号:</label>
			<input type="text" id="user_username" name="filter_LIKES_username" value="">
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>
		</div>
	    <div class="m-clear"></div>
	  </article>
      -->

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">用户列表</h4>
		</header>
		<div class="content">

<form id="userPickerForm" name="userPickerForm" method='post' action="#" class="m-form-blank">
  <table id="userPickerGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check">&nbsp;</th>
        <th>账号</th>
      </tr>
    </thead>

    <tbody id="userPickerBody">

      <tr>
        <td><input id="selectedItem1" type="checkbox" class="selectedItem" name="selectedItem" value="1"></td>
        <td>admin</td>
      </tr>

      <tr>
        <td><input id="selectedItem2" type="checkbox" class="selectedItem" name="selectedItem" value="2"></td>
        <td>user</td>
      </tr>

    </tbody>
  </table>
</form>

        </div>
      </article>



  </div>
  <div class="modal-footer">
    <span id="userPickerResult"></span>
    <a id="userPickerBtnClose" href="#" class="btn">关闭</a>
    <a id="userPickerBtnSelect" href="#" class="btn btn-primary">选择</a>
  </div>
</div>

  </body>

</html>
