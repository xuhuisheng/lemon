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
	<script src="${cdnPrefix}/public/holder/2.9.4/holder.min.js"></script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/bpm-workspace3.jsp"%>

      <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

        <c:forEach items="${bpmCategories}" var="bpmCategory">
        <div class="row">
          <div class="col-md-12">
            <div class="panel panel-default">
              <div class="panel-heading">
                <h3 class="panel-title">
                  <i class="glyphicon glyphicon-list"></i>
                  ${bpmCategory.name}
                </h3>
              </div>
			  <div class="panel-body">
                <c:forEach items="${bpmCategory.bpmProcesses}" var="bpmProcess">
				  <a href="${ctx}/operation/process-operation-viewStartForm.do?bpmProcessId=${bpmProcess.id}" class="thumbnail col-md-1" style="margin-bottom:0px;border:0px;">
                    <img data-src="holder.js/80x80?random=yes&text=${bpmProcess.name}" alt="" class="img-circle" style="width: 64px; height: 32px;">
				    <div class="caption">
					  <h5 class="text-center">${bpmProcess.name}</h5>
				    </div>
                  </a>
                </c:forEach>
			  </div>
            </div>
          </div>
        </div>
        </c:forEach>

      </section>
      <!-- end of main -->
    </div>

  </body>

</html>
