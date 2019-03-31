<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "vehicle");%>
<%pageContext.setAttribute("currentMenu", "vehicle");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#vehicle-infoForm").validate({
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
    <%@include file="/header/vehicle-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/vehicle-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="vehicle-infoForm" method="post" action="vehicle-task-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="vehicle-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_name">车辆</label>
	<div class="col-sm-5">
	  <select name="infoId" class="form-control">
	    <c:forEach var="item" items="${vehicleInfos}">
	    <option value="${item.id}" ${model.vehicleInfo.id==item.id ? 'selected' : ''}>${item.name} ${item.code}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_name">负责人</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_name" type="text" name="userId" value="${model.userId}" size="40" class="form-control required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_name"><spring:message code="vehicle-info.vehicle-info.input.name" text="名称"/></label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_code">地点</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_code" type="text" name="location" value="${model.location}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_bugDate">开始时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_startTime" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_insuranceDate">结束时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="endTime" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_status0">状态</label>
	<div class="col-sm-5">
	  <label>
	  <input id="vehicleInfo_status0" type="radio" name="status" value="0" class="" ${empty model || model.status == '0' ? 'checked' : ''}> 借出
	  </label>
	  <label>
	  <input id="vehicleInfo_status1" type="radio" name="status" value="1" class="" ${model.status == '1' ? 'checked' : ''}> 归还
	  </label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="vehicleInfo_description">备注</label>
	<div class="col-sm-5">
	  <textarea id="vehicleInfo_description" type="text" name="description" size="40" class="form-control">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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

