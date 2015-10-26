<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">列表</h4>
		</header>
		<div class="content">

  <table id="demoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="key">代码</th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="category">分类</th>
        <th class="sorting" name="version">版本</th>
        <th class="sorting" name="description">描述</th>
        <th class="sorting" name="suspended">状态</th>
        <th width="150">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${processDefinitions}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td>${item.key}</td>
	    <td>${item.name}</td>
	    <td>${item.category}</td>
	    <td>${item.version}</td>
	    <td>${item.description}</td>
	    <td>${item.suspended ? '挂起' : '激活'}</td>
        <td>
          <a href="${tenantPrefix}/form/form-viewStartForm.do?processDefinitionId=${item.id}">发起流程</a>
          <a href="workspace-graphProcessDefinition.do?processDefinitionId=${item.id}" target="_blank">流程图</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
