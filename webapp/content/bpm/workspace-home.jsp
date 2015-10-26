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

<c:forEach items="${bpmCategories}" var="bpmCategory">
      <div class="row-fluid">
      <div class="page-header">
        <h3>${bpmCategory.name}</h3>
      </div>

      <ul class="thumbnails">
      <c:forEach items="${bpmCategory.bpmProcesses}" var="bpmProcess">
        <li class="span2">
		  <div class="caption">
		    <h3>${bpmProcess.name}&nbsp;</h3>
            <p>${bpmProcess.descn}&nbsp;</p>
            <div class="btn-group" style="margin-bottom:10px;">
              <a class="btn btn-small" href="${tenantPrefix}/operation/process-operation-viewStartForm.do?bpmProcessId=${bpmProcess.id}"><i class="icon-play"></i>发起</a>
              <a class="btn btn-small" href="workspace-graphProcessDefinition.do?bpmProcessId=${bpmProcess.id}" target="_blank"><i class="icon-picture"></i>图形</a>
            </div>
		    <div style="width:100%;height:100px;">
              <a href="#" class="thumbnail">
                <img src="workspace-graphProcessDefinition.do?bpmProcessId=${bpmProcess.id}">
              </a>
		    </div>
		  </div>
        </li>
      </c:forEach>
      </ul>

</c:forEach>

    </section>
    <!-- end of main -->
    </div>

  </body>

</html>
