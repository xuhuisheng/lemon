<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "address-list");%>
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
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-schedule" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-schedule" aria-expanded="true" aria-controls="collapse-body-delegate">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        个人任务
      </h4>
    </div>
    <div id="collapse-body-schedule" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="collapse-header-schedule">
      <div class="panel-body">
        <ul class="nav nav-list">
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 最近任务</a></li>
		  <!--
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 所有任务</a></li>
		  -->
        </ul>
      </div>
    </div>
  </div>

  <!--
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-schedule" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-schedule" aria-expanded="true" aria-controls="collapse-body-delegate">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        分类
      </h4>
    </div>
    <div id="collapse-body-schedule" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="collapse-header-schedule">
      <div class="panel-body">
        <ul class="nav nav-list">
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 工作</a></li>
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 学习</a></li>
        </ul>
      </div>
    </div>
  </div>
  -->

</div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">

<form id="pimRemindForm" method="post" action="pim-task-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="pimInfo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimInfo_name">标题</label>
	<div class="col-sm-5">
	  <input id="pimInfo_name" type="text" name="name" value="${model.name}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimInfo_content">描述</label>
	<div class="col-sm-5">
	  <textarea id="pimInfo_org" type="text" name="content" class="form-control required"  maxlength="200">${model.content}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimInfo_startTime">时间</label>
	<div class="col-sm-5">
	  <div class="input-group datetimepicker date">
	    <input id="pimInfo_startTime" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd HH:mm'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimInfo_priority">优先级</label>
	<div class="col-sm-5">
	  <input id="pimInfo_title" type="text" name="priority" value="${model.priority}" class="form-control required number">
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
