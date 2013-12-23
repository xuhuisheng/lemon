<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-task");%>
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

<form id="demoForm" method="post" action="workspace!completeTask.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_id" type="hidden" name="taskId" value="${taskId}">
  <s:iterator value="taskFormData.formProperties" var="item">
  <div class="control-group">
    <label class="control-label">${item.name}</label>
	<div class="controls">
	  <s:if test="type.name=='enum'">
	  <s:iterator value="type.getInformation('values')">
	    <label class="checkbox inline">
	    <input type="radio" name="${item.id}" value="${key}" ${item.value == key ? 'checked' : ''}>
		${value}
		</label>
	  </s:iterator>
	  </select>
	  </s:if>
	  <s:else>
	  <input type="text" name="${id}" value="${item.value}" size="40" class="text ${item.required ? 'required' : ''}" ${item.writable ? '' : 'readonly'}>
	  </s:else>
    </div>
  </div>
  </s:iterator>
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
