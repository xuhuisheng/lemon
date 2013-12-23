<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentMenu", "user");%>
<%pageContext.setAttribute("HEADER_MODEL", "bpm-admin");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

	  <button class="btn" onclick="location.href='identity!inputUser.do'">添加</button>

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
        <th class="sorting" name="name">名</th>
        <th class="sorting" name="name">姓</th>
        <th class="sorting" name="name">邮箱</th>
        <th width="170">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="users" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td>${item.firstName}</td>
	    <td>${item.lastName}</td>
	    <td>${item.email}</td>
        <td>
		  <a href="identity!inputUser.do?userId=${item.id}">修改</a>
		  <a href="identity!removeUser.do?userId=${item.id}">删除</a>
		  <a href="identity!inputUserMembership.do?userId=${item.id}">设置群组</a>
        </td>
      </tr>
      </s:iterator>
    </tbody>
  </table>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
