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


<form id="vehicle-infoForm" method="post" action="vehicle-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="vehicle-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_code">车牌号</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_code" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_name">名称</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_weight">载重</label>
	<div class="col-sm-5">
      <div class="input-group">
        <input type="text" class="form-control required number" id="vehicleInfo_weight" name="weight" value="${model.weight}">
        <div class="input-group-addon">吨</div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_seat">座位</label>
	<div class="col-sm-5">
      <div class="input-group">
        <input type="text" class="form-control required number" id="vehicleInfo_seat" name="seat" value="${model.seat}">
        <div class="input-group-addon">个</div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_type">类型</label>
	<div class="col-sm-5">
	  <select id="vehicleInfo_type" name="type" class="form-control">
	    <option value="小型车">小型车</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_color">颜色</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_color" type="text" name="color" value="${model.color}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_price">价格</label>
	<div class="col-sm-5">
      <div class="input-group">
        <input type="text" class="form-control required number" id="vehicleInfo_price" name="price" value="${model.price}">
        <div class="input-group-addon">元</div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_engineNumber">发动机编号</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_engineNumber" type="text" name="engineNumber" value="${model.engineNumber}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_vin">出厂编号</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_vin" type="text" name="vin" value="${model.vin}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_bugDate">购买时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="buyDate" value="<fmt:formatDate value='${model.buyDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_clivtaDate">交强险时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_clivtaDate" type="text" name="clivtaDate" value="<fmt:formatDate value='${model.clivtaDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_viDate">商业险时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_viDate" type="text" name="viDate" value="<fmt:formatDate value='${model.viDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_usingUnit">使用单位</label>
	<div class="col-sm-5">
	  <input id="vehicleInfo_usingUnit" type="text" name="usingUnit" value="${model.usingUnit}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="vehicleInfo_status0">状态</label>
	<div class="col-sm-5">
	  <label>
	    <input id="vehicleInfo_status0" type="radio" name="status" value="0" class="" ${empty model || model.status == '0' ? 'checked' : ''}> 在库
	  </label>
	  <label>
	    <input id="vehicleInfo_status1" type="radio" name="status" value="1" class="" ${model.status == '1' ? 'checked' : ''}> 使用中
	  </label>
	  <label>
	    <input id="vehicleInfo_status2" type="radio" name="status" value="2" class="" ${model.status == '2' ? 'checked' : ''}> 维修
	  </label>
	  <label>
	    <input id="vehicleInfo_status3" type="radio" name="status" value="3" class="" ${model.status == '3' ? 'checked' : ''}> 报废
	  </label>
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

