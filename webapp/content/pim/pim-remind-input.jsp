<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "remind");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#pimRemindForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  编辑
		</div>

		<div class="panel-body">

<form id="pimRemindForm" method="post" action="pim-remind-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="pimRemind_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_type">类型</label>
	<div class="col-sm-5">
	  <select id="pimRemind_type" name="type" class="form-control required">
	    <option value="一次" ${model.type == '一次'}>一次</option>
	    <option value="每日" ${model.type == '每日'}>每日</option>
	    <option value="每周" ${model.type == '每周'}>每周</option>
	    <option value="每月" ${model.type == '每月'}>每月</option>
	    <option value="每年" ${model.type == '每年'}>每年</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_remindTime">时间</label>
	<div class="col-sm-5">
	  <input id="pimRemind_remindTime" name="remindTime" class="form-control required" minlength="2" maxlength="200" value="${model.remindTime}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_description">事项</label>
	<div class="col-sm-5">
	  <textarea id="pimRemind_description" name="description" class="form-control required" minlength="2" maxlength="200">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
