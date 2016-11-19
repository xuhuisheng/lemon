<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace3.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  确认发起流程
		</div>

		<div class="panel-body">

<form id="demoForm" method="post" action="process-operation-startProcessInstance.do" class="form-horizontal">
  <input id="demo_id" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
  <input type="hidden" name="businessKey" value="${businessKey}">
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn btn-default">发起流程</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link">返回</button>
    </div>
  </div>
</form>

		</div>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
