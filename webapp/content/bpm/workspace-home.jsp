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

<s:iterator value="bpmCategories" var="bpmCategory">
      <div class="page-header">
        <h3>${bpmCategory.name}</h3>
      </div>

      <s:iterator value="#bpmCategory.bpmProcesses" var="bpmProcess">
        <div class="well span2">
          <h4>${bpmProcess.name}</h4>
          <p>${bpmProcess.descn}</p>
          <div class="btn-group">
		    <s:if test="#bpmProcess.startStrategy=='id'">
            <a class="btn btn-small" href="${scopePrefix}/form/form!viewStartForm.do?processDefinitionId=${bpmProcess.processDefinitionId}"><li class="icon-play"></li>发起</a>
			</s:if>
		    <s:if test="#bpmProcess.startStrategy=='key'">
            <a class="btn btn-small" href="${scopePrefix}/form/form!viewStartForm.do?processDefinitionKey=${bpmProcess.processDefinitionKey}"><li class="icon-play"></li>发起</a>
			</s:if>
            <a class="btn btn-small" href="console!graphProcessDefinition.do?processDefinitionId=${bpmProcess.processDefinitionId}"><li class="icon-picture"></li>图形</a>
          </div>
        </div>
      </s:iterator>

</s:iterator>

    </section>
    <!-- end of main -->
    </div>

  </body>

</html>
