<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>添加下属</title>
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
			$.getJSON('${tenantPrefix}/rs/user/search', {
				username: query
			}, function(result) {
				process(result);
			})
		},
		items: 5
	};

	var callback = function(event, data, formatted) {
		console.info(event, data, formatted);
	};

    $("#group-base_name").typeahead(options);
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
		  <h4 class="title">添加用户</h4>
		</header>

		<div class="content content-inner">

<form id="orgForm" method="post" action="org-saveUser.do" class="form-horizontal">
  <input id="group-base_groupBaseId" type="hidden" name="partyStructTypeId" value="${partyStructTypeId}">
  <input id="group-base_groupBaseId" type="hidden" name="partyEntityId" value="${partyEntityId}">
  <div class="control-group">
    <label class="control-label" for="org_orgname"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="controls">
	  <input id="group-base_name" type="text" name="name" value="" size="40" class="text required" minlength="1" maxlength="50" autocomplete="off">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="orgInputUser_status">单选</label>
	<div class="controls">
	  <label for="orgInputUser_status1" class="radio inline">
	    <input id="orgInputUser_status1" type="radio" name="status" value="1" class="required">
		主职
	  </label>
	  <label for="orgInputUser_status2" class="radio inline">
	    <input id="orgInputUser_status2" type="radio" name="status" value="2" class="required">
		兼职
	  </label>
	  <label for="orgInputUser_status2" class="validate-error" generated="true" style="display:none;"></label>
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
