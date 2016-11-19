<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
    <%@include file="/menu/bpm-workspace3.jsp"%>

    <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<c:forEach items="${bpmCategories}" var="bpmCategory">
        <div class="row">

		    <div class="panel panel-default">
		      <div class="panel-heading">
			    <h3 class="panel-title">
				  <i class="glyphicon glyphicon-list"></i>
				  ${bpmCategory.name}
				</h3>
		      </div>
			  <div class="panel-body">
      <c:forEach items="${bpmCategory.bpmProcesses}" var="bpmProcess">

        <div class="col-md-2">
		  <div class="caption">
		    <h3>${bpmProcess.name}&nbsp;</h3>
            <p>${bpmProcess.descn}&nbsp;</p>
            <div class="btn-group" style="margin-bottom:10px;">
              <a class="btn btn-default btn-sm" href="${tenantPrefix}/operation/process-operation-viewStartForm.do?bpmProcessId=${bpmProcess.id}"><i class="glyphicon glyphicon-play"></i> 发起</a>
              <a class="btn btn-default btn-sm" href="workspace-graphProcessDefinition.do?bpmProcessId=${bpmProcess.id}" target="_blank"><i class="glyphicon glyphicon-picture"></i> 图形</a>
            </div>
		  </div>
        </div>

      </c:forEach>
			  </div>
		    </div>

        </div>
</c:forEach>

    </section>
    <!-- end of main -->
    </div>

  </body>

</html>
