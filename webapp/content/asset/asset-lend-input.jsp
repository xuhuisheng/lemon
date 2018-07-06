<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "asset");%>
<%pageContext.setAttribute("currentMenu", "asset");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#asset-infoForm").validate({
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
    <%@include file="/header/asset-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/asset-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">

<form id="asset-infoForm" method="post" action="asset-lend-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="asset-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">资产</label>
	<div class="col-sm-5">
	  <select id="assetInfo_category" name="infoId" class="form-control">
	    <c:forEach var="item" items="${assetInfos}">
	    <option value="${item.id}" ${model.assetInfo.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">领用人</label>
	<div class="col-sm-5">
	  <input id="asset-info_address" type="text" name="userId" value="${model.userId}" size="40" class="form-control required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">领用时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0px;">
	    <input id="accountInfo_closeTime" type="text" name="lendDate" value="<fmt:formatDate value='${model.lendDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">归还时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0px;">
	    <input id="accountInfo_closeTime" type="text" name="returnDate" value="<fmt:formatDate value='${model.returnDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">备注</label>
	<div class="col-sm-5">
	  <textarea name="description" class="form-control">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">状态</label>
	<div class="col-sm-5">
	  <label class="radio-inline">
	    <input id="carInfo_status0" type="radio" name="status" value="0" class="" ${empty model || model.status == '0' ? 'checked' : ''}> 领用
	  </label>
	  <label class="radio-inline">
	    <input id="carInfo_status1" type="radio" name="status" value="1" class="" ${model.status == '1' ? 'checked' : ''}> 归还
	  </label>
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

