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
	<style type="text/css">
#bpmGraph {
    left: 30%;
	width: 80%;
}
	</style>
	<script type="text/javascript">
function showGraph(id) {
	$('#bpmGraph img').attr('src', 'workspace!graphProcessDefinition.do?bpmProcessId=' + id);
	$('#bpmGraph img').css('max-width', '');
	$('#bpmGraph').modal();
}
	</script>
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
            <a class="btn btn-small" href="${scopePrefix}/form/form!viewStartForm.do?bpmProcessId=${bpmProcess.id}"><li class="icon-play"></li>发起</a>
            <button class="btn btn-small" onclick="showGraph('${bpmProcess.id}')"><li class="icon-picture"></li>图形</button>
          </div>
        </div>
      </s:iterator>

</s:iterator>

    </section>
    <!-- end of main -->
    </div>

    <div id="bpmGraph" class="modal hide fade">
	  <div class="modal-body">
	    <img src="#" style="max-width: none;">
	  </div>
	</div>
  </body>

</html>
