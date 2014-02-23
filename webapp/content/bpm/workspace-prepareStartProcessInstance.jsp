<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
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
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="workspace-startProcessInstance.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_id" type="hidden" name="processDefinitionId" value="${processDefinitionId}">
  <c:forEach items="${startFormData.formProperties}" var="item">
  <div class="control-group">
    <label class="control-label">${item.name}</label>
	<div class="controls">
	  <c:if test="${item.type.name=='enum'}">
	  <c:forEach items="${type.getInformation('values')}" var="item">
	    <label class="checkbox inline">
	    <input type="radio" name="${item.id}" value="${key}" ${item.value == key ? 'checked' : ''}>
		${item.value}
		</label>
	  </c:forEach>
	  </select>
	  </c:if>
	  <c:if test="${item.type.name != 'enum'}">
	  <input type="text" name="${id}" value="${item.value}" size="40" class="text ${item.required ? 'required' : ''}" ${item.writable ? '' : 'readonly'}>
	  </c:if>
    </div>
  </div>
  </c:forEach>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn">保存</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn">返回</button>
    </div>
  </div>
</form>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
