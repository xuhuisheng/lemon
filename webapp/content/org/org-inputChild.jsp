<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.org.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });


    var options = {
		source: function(query, process) {
			$.getJSON('${tenantPrefix}/rs/party/search', {
				name: query,
				partyTypeId: $('#org_partyTypeId').val()
			}, function(result) {
				process(result);
			})
		},
		items: 5
	};

	var callback = function(event, data, formatted) {
		console.info(event, data, formatted);
	};

    $("#org_name").typeahead(options);
})
    </script>
  </head>

  <body>
    <%@include file="/header/group.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/group.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="org.org.input.title" text="编辑用户"/></h4>
		</header>

		<div class="content content-inner">

<form id="orgForm" method="post" action="org-saveChild.do" class="form-horizontal">
  <input id="group-base_groupBaseId" type="hidden" name="partyStructTypeId" value="${partyStructTypeId}">
  <input id="group-base_groupBaseId" type="hidden" name="partyEntityId" value="${partyEntityId}">
  <c:if test="${model != null}">
  <input id="org_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="org_name"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="controls">
	  <input id="org_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="2" maxlength="50" autocomplete="off">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="scope-local_global">类型</label>
	<div class="controls">
	  <select id="org_partyTypeId" name="partyTypeId">
      <c:forEach items="${partyTypes}" var="item">
	    <option value="${item.id}" ${item.id==model.partyType.id ? 'selected' : ''}>${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
