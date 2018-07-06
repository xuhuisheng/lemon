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


<form id="vehicle-infoForm" method="post" action="vehicle-accident-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="vehicle-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_code">车辆</label>
	<div class="col-sm-5">
	  <select id="vehicleInfo_code" type="text" name="infoId" class="form-control required">
	    <c:forEach items="${vehicleInfos}" var="item">
		<option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_code">驾驶员</label>
	<div class="col-sm-5">
	  <select id="vehicleInfo_code" type="text" name="driverId" class="form-control required">
	    <c:forEach items="${vehicleDrivers}" var="item">
		<option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_bugDate">事故时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="createTime" value="<fmt:formatDate value='${model.createTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_name">事故地点</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_name" type="text" name="location" value="${model.location}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_name">事故确认人</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_name" type="text" name="confirmer" value="${model.confirmer}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">保险理赔金额</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required number" id="vehicleInfo_weight" name="price" value="${model.price}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">对方姓名</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required" id="vehicleInfo_weight" name="otherName" value="${model.otherName}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">对方住址</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required" id="vehicleInfo_weight" name="otherAddress" value="${model.otherAddress}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">对方电话</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required" id="vehicleInfo_weight" name="otherMobile" value="${model.otherMobile}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">公司暂付金额</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required number" id="vehicleInfo_weight" name="companyPrice" value="${model.companyPrice}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">本人担负金额</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required number" id="vehicleInfo_weight" name="personPrice" value="${model.personPrice}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">对方车牌号码</label>
	<div class="col-sm-5">
      <input type="text" class="form-control required number" id="vehicleInfo_weight" name="otherCode" value="${model.otherCode}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_description">事故概要</label>
	<div class="col-sm-5">
	  <textarea id="vehicleInfo_description" type="text" name="summary" size="40" class="form-control">${model.summary}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_description">和解内容</label>
	<div class="col-sm-5">
	  <textarea id="vehicleInfo_description" type="text" name="reconciliation" size="40" class="form-control">${model.reconciliation}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_description">备注</label>
	<div class="col-sm-5">
	  <textarea id="vehicleInfo_description" type="text" name="description" size="40" class="form-control">${model.description}</textarea>
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

