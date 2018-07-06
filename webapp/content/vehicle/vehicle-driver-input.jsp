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


<form id="vehicle-infoForm" method="post" action="vehicle-driver-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="vehicle-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_name">姓名</label>
	<div class="col-sm-5">
	  <input id="vehicleDriver_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_name">性别</label>
	<div class="col-sm-5">
	  <label>
	    <input id="vehicleDriver_gender0" type="radio" name="gender" value="M" class="" ${empty model || model.gender == 'M' ? 'checked' : ''}> 男
	  </label>
	  <label>
	    <input id="vehicleDriver_gender1" type="radio" name="gender" value="F" class="" ${model.status == 'F' ? 'checked' : ''}> 女
	  </label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_weight">驾驶证号</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required" id="vehicleDriver_weight" name="code" value="${model.code}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_seat">出生日期</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="birthday" value="<fmt:formatDate value='${model.birthday}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_seat">领证日期</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="licenseDate" value="<fmt:formatDate value='${model.licenseDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_seat">证件到期时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="expireDate" value="<fmt:formatDate value='${model.expireDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_type">驾龄</label>
	<div class="col-sm-5">
      <div class="input-group">
        <input type="text" class="form-control required number" id="vehicleDriver_year" name="year" value="${model.year}">
        <div class="input-group-addon">年</div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_color">联系电话</label>
	<div class="col-sm-5">
	  <input id="vehicleDriver_color" type="text" name="mobile" value="${model.mobile}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_color">准驾车型</label>
	<div class="col-sm-5">
	  <input id="vehicleDriver_color" type="text" name="type" value="${model.type}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_price">地址</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required" id="vehicleDriver_price" name="location" value="${model.location}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_description">年检记录</label>
	<div class="col-sm-5">
	  <textarea id="vehicleDriver_description" type="text" name="annualInspect" size="40" class="form-control">${model.annualInspect}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleDriver_description">备注</label>
	<div class="col-sm-5">
	  <textarea id="vehicleDriver_description" type="text" name="description" size="40" class="form-control">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-2">
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

