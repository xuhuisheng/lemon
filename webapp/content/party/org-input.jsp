<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "org");%>
<%pageContext.setAttribute("currentMenu", "org");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>添加下级</title>
    <%@include file="/common/s3.jsp"%>
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
})
    </script>

    <link type="text/css" rel="stylesheet" href="${tenantPrefix}/widgets/userpicker3/userpicker.css">
    <script type="text/javascript" src="${tenantPrefix}/widgets/userpicker3/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${tenantPrefix}/rs/user/search'
	});
})
    </script>
	<script>
var createOrgPicker = function(conf) {
	if (!conf) {
		conf = {
			modalId: 'orgPickerModel',
			multiple: false,
			url: '${tenantPrefix}/rs/party/entities?typeId=${partyType.id}'
		};
	}

	if ($('#' + conf.modalId).length == 0) {
		$(document.body).append(
'<div id="' + conf.modalId + '" class="modal fade">'
+'  <div class="modal-dialog">'
+'    <div class="modal-content">'
+'  <div class="modal-header">'
+'    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
+'    <h3>选择组织</h3>'
+'  </div>'
+'  <div class="modal-body">'
+'      <!--'
+'	  <article class="m-blank">'
+'	    <div class="pull-left">'
+'		  <form name="userForm" method="post" action="javascript:void(0);return false;" class="form-inline m-form-bottom">'
+'    	    <label for="user_username">账号:</label>'
+'			<input type="text" id="user_username" name="filter_LIKES_username" value="">'
+'			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>'
+'		  </form>'
+'		</div>'
+'	    <div class="m-clear"></div>'
+'	  </article>'
+'      -->'
+'      <article class="m-widget">'
+'        <header class="header">'
+'		  <h4 class="title">用户列表</h4>'
+'		</header>'
+'		<div class="content">'
+'<form id="userPickerForm" name="userPickerForm" method="post" action="#" class="m-form-blank">'
+'  <table id="userPickerGrid" class="m-table table-hover">'
+'    <tbody id="orgPickerModelBody">'
+'      <tr>'
+'        <td><input id="selectedItem1" type="checkbox" class="selectedItem" name="selectedItem" value="1"></td>'
+'        <td>admin</td>'
+'      </tr>'
+'      <tr>'
+'        <td><input id="selectedItem2" type="checkbox" class="selectedItem" name="selectedItem" value="2"></td>'
+'        <td>user</td>'
+'      </tr>'
+'    </tbody>'
+'  </table>'
+'</form>'
+'        </div>'
+'      </article>'
+'  </div>'
+'  <div class="modal-footer">'
+'    <span id="orgPickerModelResult"></span>'
+'    <a id="orgPickerModelBtnClose" href="#" class="btn" data-dismiss="modal">关闭</a>'
+'    <a id="orgPickerModelBtnSelect" href="#" class="btn btn-primary">选择</a>'
+'  </div>'
+'  </div>'
+'  </div>'
+'</div>');
	}

	$(document).delegate('#btnOpen', 'click', function(e) {
		$('#' + conf.modalId).modal();
		$.ajax({
			url: conf.url,
			success: function(data) {
				var html = '';
				for (var i = 0; i < data.length; i++) {
					var item = data[i];
					html +=
					  '<tr>'
						+'<td><input id="selectedItem' + i + '" type="radio" class="selectedItem" name="selectedItem" value="'
						+ item.id + '" title="' + item.name + '"></td>'
						+'<td><label for="selectedItem' + i + '">' + item.name + '</label></td>'
					  +'</tr>'
				}
				$('#' + conf.modalId + 'Body').html(html);
			}
		});
	});

	$(document).delegate('#' + conf.modalId + 'BtnSelect', 'click', function(e) {
		$('#' + conf.modalId).modal('hide');
		var orgPickerElement = $('#' + conf.modalId).data('orgPicker');
		$('#org_id').val($('.selectedItem:checked').val());
		$('#org_name').val($('.selectedItem:checked').attr('title'));

		$('#btnClean').show();
	});

	$(document).delegate('#btnClean', 'click', function(e) {
		$('#org_id').val('');
		$('#org_name').val('');

		$('#btnClean').hide();
	});
}

$(function() {
	createOrgPicker();
});
	</script>
  </head>

  <body>
    <%@include file="/header/org.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/org.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  添加下级
		</div>

		<div class="panel-body">


<form id="orgForm" method="post" action="org-save.do" class="form-horizontal">
  <input id="org_partyStructTypeId" type="hidden" name="partyStructTypeId" value="${partyStructTypeId}">
  <input id="org_partyEntityId" type="hidden" name="partyEntityId" value="${partyEntityId}">
  <input id="org_partyTypeId" type="hidden" name="partyTypeId" value="${partyType.id}">
<c:if test="${partyType.type == 1}">
  <div class="form-group">
    <label class="control-label col-md-1" for="org_orgname"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="col-sm-5">
	  <div class="input-group userPicker">
        <input id="_task_name_key" type="hidden" name="childEntityRef" value="">
        <input type="text" class="form-control" name="username" placeholder="" value="">
        <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
      </div>
	</div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_status">是否兼职</label>
	<div class="col-sm-5">
	  <label for="orgInputUser_status1" class="radio inline">
	    <input id="orgInputUser_status1" type="radio" name="status" value="1" class="required" checked>
		主职
	  </label>
	  <label for="orgInputUser_status2" class="radio inline">
	    <input id="orgInputUser_status2" type="radio" name="status" value="2" class="required">
		兼职
	  </label>
	  <label for="orgInputUser_status2" class="validate-error" generated="true" style="display:none;"></label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_priority">排序</label>
	<div class="col-sm-5">
	  <input id="orgInputUser_priority" type="text" name="priority" value="" size="40" class="form-control required number" minlength="1" maxlength="50" autocomplete="off">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_admin0">管理</label>
	<div class="col-sm-5">
	  <label for="orgInputUser_admin0" class="radio inline">
	    <input id="orgInputUser_admin0" type="radio" name="admin" value="0" class="required" checked>
		员工
	  </label>
	  <label for="orgInputUser_admin1" class="radio inline">
	    <input id="orgInputUser_admin1" type="radio" name="admin" value="1" class="required">
		负责人
	  </label>
	  <label for="orgInputUser_status2" class="validate-error" generated="true" style="display:none;"></label>
    </div>
  </div>
</c:if>
<c:if test="${partyType.type == 2}">
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_status">组织类型</label>
	<div class="col-sm-5">
	  ${partyType.name}
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="org_name"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="org_id" type="hidden" name="childEntityId" value="">
      <input id="org_name" type="text" name="childEntityName" value="" size="40" class="form-control required" minlength="1" maxlength="50" autocomplete="off">
	  <button id="btnClean" type="button" class="btn" style="display:none;">清空</button>
	  <button id="btnOpen" type="button" class="btn btn-default">选择已有组织</button>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_priority">排序</label>
	<div class="col-sm-5">
	  <input id="orgInputUser_priority" type="text" name="priority" value="" size="40" class="form-control required number" minlength="1" maxlength="50" autocomplete="off">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_admin0">管理</label>
	<div class="col-sm-5">
	  <label for="orgInputUser_admin0" class="radio inline">
	    <input id="orgInputUser_admin0" type="radio" name="admin" value="0" class="required" checked>
		普通
	  </label>
	  <label for="orgInputUser_admin1" class="radio inline">
	    <input id="orgInputUser_admin1" type="radio" name="admin" value="1" class="required">
		管理
	  </label>
	  <label for="orgInputUser_status2" class="validate-error" generated="true" style="display:none;"></label>
    </div>
  </div>
</c:if>
<c:if test="${partyType.type == 0}">
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_status">组织类型</label>
	<div class="col-sm-5">
	  ${partyType.name}
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="org_name"><spring:message code="org.org.input.orgname" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="org_id" type="hidden" name="childEntityId" value="">
      <input id="org_name" type="text" name="childEntityName" value="" size="40" class="form-control required" minlength="1" maxlength="50" autocomplete="off">
	  <button id="btnClean" type="button" class="btn" style="display:none;">清空</button>
	  <button id="btnOpen" type="button" class="btn">选择已有组织</button>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="orgInputUser_priority">排序</label>
	<div class="col-sm-5">
	  <input id="orgInputUser_priority" type="text" name="priority" value="" size="40" class="form-control required number" minlength="1" maxlength="50" autocomplete="off">
    </div>
  </div>
</c:if>
  <div class="form-group">
    <div class="col-md-offset-1 col-sm-5">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
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

