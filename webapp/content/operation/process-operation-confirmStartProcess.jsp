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
		  <h4 class="title">确认发起流程</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="process-operation-startProcessInstance.do" class="form-horizontal">
  <input id="demo_id" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
  <input type="hidden" name="businessKey" value="${businessKey}">
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn">发起流程</button>
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
