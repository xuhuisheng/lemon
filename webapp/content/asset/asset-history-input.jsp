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
    $("#asset-historyForm").validate({
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

<form id="asset-historyForm" method="post" action="asset-history-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="asset-history_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_address">资产</label>
	<div class="col-sm-5">
	  <input id="asset-history_address" type="text" name="assetInfoId" value="${model.assetInfo.id}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_address">before userId</label>
	<div class="col-sm-5">
	  <input id="asset-history_address" type="text" name="beforeUserId" value="${model.beforeUserId}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_address">after userId</label>
	<div class="col-sm-5">
	  <input id="asset-history_address" type="text" name="afterUserId" value="${model.afterUserId}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_description">更新时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0px;">
	    <input id="accountInfo_closeTime" type="text" name="updateTime" value="<fmt:formatDate value='${model.updateTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_description">原因</label>
	<div class="col-sm-5">
	  <input id="asset-history_description" type="text" name="reason" value="${model.reason}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_description">状态</label>
	<div class="col-sm-5">
	  <input id="asset-history_description" type="text" name="status" value="${model.status}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="assetInfo_location">操作人</label>
	<div class="col-sm-5">
	  <input id="assetInfo_location" type="text" name="operator" value="${model.operator}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-history_description">备注</label>
	<div class="col-sm-5">
	  <textarea name="description" class="form-control">${model.description}</textarea>
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

