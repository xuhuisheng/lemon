<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dashboard");%>
<%pageContext.setAttribute("currentMenu", "dashboard");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>portal</title>
	<%@include file="/common/s3.jsp"%>

    <script src='${tenantPrefix}/widgets/portal/dashboard.js' type='text/javascript'></script>
    <link rel='stylesheet' href='${tenantPrefix}/widgets/portal/dashboard.css' type='text/css' media='screen' />
    <script type="text/javascript" src="${tenantPrefix}/widgets/portal/portal.js"></script>

  </head>

  <body>
    <%@include file="/header/portal.jsp"%>
  
    <div data-height="300" class="container-fluid dashboard dashboard-draggable" id="dashboard" style="margin-top:70px;">
      <header></header>
      <section class="row">
<c:forEach items="${map}" var="entry">
        <div class="portal-col col-md-4 col-sm-6" data-id="${entry.key}" data-order="${entry.key}">
  <c:forEach items="${entry.value}" var="item">
		<div data-id="${item.id}" class="portlet" data-order="${item.rowIndex}">
          <div data-url="${tenantPrefix}${item.portalWidget.url}" class="panel panel-default" id="panel${item.id}" data-id="${item.id}">
            <div class="panel-heading">
              <div class="panel-actions">
                <button class="btn btn-sm refresh-panel"><i class="glyphicon glyphicon-refresh"></i></button>
                <div class="dropdown">
                  <button data-toggle="dropdown" class="btn btn-sm" role="button"><span class="caret"></span></button>
                  <ul aria-labelledby="dropdownMenu1" role="menu" class="dropdown-menu">
                    <li><a href="javascript:void(0);updateWidget(${item.id}, ${item.portalWidget.id}, '${item.name}')"><i class="glyphicon glyphicon-pencil"></i> 编辑</a></li>
                    <li><a class="remove-panel" href="#"><i class="glyphicon glyphicon-remove"></i> 移除</a></li>
                  </ul>
                </div>
              </div>
		      <i class="glyphicon glyphicon-list"></i> ${item.name}
            </div>
            <div class="panel-body">
		      <table class="table table-hover">
			    <thead>
			      <tr>
				    <th>编号</th>
				    <th>名称</th>
				    <th>创建时间</th>
				    <th>&nbsp;</th>
			      </tr>
			    </thead>
			    <tbody>
			    <c:forEach items="${personalTasks.result}" var="item">
			      <tr>
				    <td>${item.id}</td>
				    <td>${item.name}</td>
				    <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				    <td>
				      <a href="${tenantPrefix}/operation/task-operation-viewTaskForm.do?humanTaskId=${item.id}" class="btn btn-xs btn-primary">处理</a>
				    </td>
			      </tr>
			    </c:forEach>
			    </tbody>
		      </table>
            </div>
          </div>
        </div>
  </c:forEach>
		</div>
</c:forEach>

      </section>
    </div>

<div id="widgetModal" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">编辑组件</h4>
      </div>
      <div class="modal-body">
	    <form id="widgetForm" action="save.do" method="post">
		  <input id="portalItemId" type="hidden" name="id" value="">
		  <div class="form-group">
			<label for="portalWidgetId">组件</label>
		    <select id="portalWidgetId" class="form-control" name="portalWidgetId">
		      <c:forEach items="${portalWidgets}" var="item">
			  <option value="${item.id}">${item.name}</option>
			  </c:forEach>
		    </select>
		  </div>
		  <div class="form-group">
			<label for="portalItemName">标题</label>
		    <input id="portalItemName" class="form-control" type="text" value="" name="portalItemName">
		  </div>
		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" onclick="$('#widgetForm').submit();">保存</button>
      </div>
    </div>
  </div>
</div>

    <div class="text-center">
	  &copy;mossle
    </div>
  </body>

</html>
