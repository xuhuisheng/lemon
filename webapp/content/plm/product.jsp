<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "plm");%>
<%pageContext.setAttribute("currentMenu", "plm");%>
<%@page import="java.util.*"%>
<%@page import="com.mossle.plm.persistence.domain.*"%>
<%!
	public String generatePlmRequirements(List<PlmRequirement> plmRequirements, String projectId) {
		if (plmRequirements == null || plmRequirements.isEmpty()) {
			return "";
		}
		try {
			String text = "<ul>";
			for (PlmRequirement plmRequirement : plmRequirements) {
				text += generatePlmRequirement(plmRequirement, projectId);
			}
			text += "</ul>";
			return text;
		} catch(Exception ex) {
			System.out.println("20 : " + ex);
			// ex.printStackTrace();
			return "";
		}
	}

	public String generatePlmRequirement(PlmRequirement plmRequirement, String projectId) {
		if (plmRequirement == null) {
			return "";
		}
		try {
			String text = "<li>";
			if (plmRequirement.getPlmIssue() != null) {
				text += "<a href='product.do?id=" + projectId + "&issueId=" + plmRequirement.getPlmIssue().getId() + "'>";
			}
			text += plmRequirement.getName();
			if (plmRequirement.getPlmIssue() != null) {
				text += "</a>";
			}

			text += "<a href='requirement-input.do?parentId=" + plmRequirement.getId() + "'><i class='glyphicon glyphicon-plus'></i></a>";
			if (plmRequirement.getPlmRequirement() != null && plmRequirement.getPlmRequirements().isEmpty() && plmRequirement.getPlmIssue() == null) {
				text += "<a href='requirement-remove.do?id=" + plmRequirement.getId() + "'><i class='glyphicon glyphicon-remove'></i></a>";
			}
			if (plmRequirement.getPlmIssue() == null) {
				text += "<a href='requirement-link.do?id=" + plmRequirement.getId() + "'><i class='glyphicon glyphicon-link'></i></a>";
			}

			List<PlmRequirement> plmRequirements = new ArrayList<PlmRequirement>();
			plmRequirements.addAll(plmRequirement.getPlmRequirements());
			text += generatePlmRequirements(plmRequirements, projectId);
			text += "</li>";
			return text;
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("41 : " + ex);
			return "";
		}
	}
%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'pimInfoGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'pimInfoGridForm',
	exportUrl: 'pim-info-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
  </head>

  <body>
    <%@include file="/header/plm.jsp"%>

    <div class="row-fluid">

      <!-- start of sidebar -->
<div class="col-md-3" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        <a href="${tenantPrefix}/plm/project.do?projectId=${plmProject.id}">${plmProject.name}</a>
		<!--
	    <div class="pull-right ctrl">
	      <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
        </div>
		-->
      </h4>
    </div>
    <div class="panel-body">
<c:set var="plmRequirement" value="${plmRequirement}"/>
<%
PlmRequirement plmRequirement = (PlmRequirement) pageContext.getAttribute("plmRequirement");
out.print(generatePlmRequirement(plmRequirement, request.getParameter("id")));
%>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-9" style="padding-top:65px;">
<c:if test="${not empty plmIssue}">
        <div class="panel panel-default">
		  <div class="panel-heading">
		    ${plmIssue.name}
			<div class="pull-right ctrl">
			  <a class="btn btn-default btn-xs" href="edit.do?id=${plmIssue.id}">编辑</i></a>
			</div>
		  </div>
		  <div class="panel-body">
		    ${plmIssue.content}
		  </div>
		</div>
</c:if>
      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>


