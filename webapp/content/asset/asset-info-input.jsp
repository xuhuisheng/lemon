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

  <c:if test="${model != null}">
    refreshSubCategories('${model.assetCategoryByCategoryId.id}', '${model.assetCategoryBySubCategoryId.id}');
  </c:if>

  <c:if test="${model == null}">
    refreshSubCategories($('#assetInfo_category').val());
  </c:if>
})

function refreshSubCategories(categoryId, defaultValue) {
    $.getJSON('asset-category-children.do', {
        parentId: categoryId
    }, function(data) {
        $("#assetInfo_sub").empty();
        $.each(data.data, function(index, item) {
            $("#assetInfo_sub").append("<option value='" + item.id + "'>" + item.name + "</option>");
        });
        if (!!defaultValue) {
            $("#assetInfo_sub").find("option[value='" + defaultValue + "']").attr("selected", true);
        }
    });
}
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

<form id="asset-infoForm" method="post" action="asset-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="asset-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">编码</label>
    <div class="col-sm-5">
      <input id="asset-info_address" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">名称</label>
    <div class="col-sm-5">
      <input id="asset-info_address" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">所属大类</label>
    <div class="col-sm-5">
      <select id="assetInfo_category" name="categoryId" class="form-control" onchange="refreshSubCategories(this.value)">
        <c:forEach var="item" items="${assetCategories}">
        <option value="${item.id}" ${model.assetCategoryByCategoryId.id==item.id ? 'selected' : ''}>${item.name}</option>
        </c:forEach>
      </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_address">所属小类</label>
    <div class="col-sm-5">
      <select id="assetInfo_sub" name="subCategoryId" class="form-control required">
      </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">型号</label>
    <div class="col-sm-5">
      <input id="asset-info_description" type="text" name="model" value="${model.model}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">SN</label>
    <div class="col-sm-5">
      <input id="asset-info_description" type="text" name="sn" value="${model.sn}" size="40" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">价格</label>
    <div class="col-sm-5">
      <input id="asset-info_description" type="text" name="price" value="${model.price}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">购入时间</label>
    <div class="col-sm-5">
      <div class="input-group datepicker date" style="padding:0px;">
        <input id="accountInfo_closeTime" type="text" name="buyDate" value="<fmt:formatDate value='${model.buyDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
        <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="assetInfo_location">位置</label>
    <div class="col-sm-5">
      <input id="assetInfo_location" type="text" name="location" value="${model.location}" size="40" class="form-control number">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="asset-info_description">状态</label>
    <div class="col-sm-5">
      <label class="radio-inline">
        <input id="carInfo_status0" type="radio" name="status" value="0" class="" ${empty model || model.status == '0' ? 'checked' : ''}> 在库
      </label>
      <label class="radio-inline">
        <input id="carInfo_status1" type="radio" name="status" value="1" class="" ${model.status == '1' ? 'checked' : ''}> 借出
      </label>
      <label class="radio-inline">
        <input id="carInfo_status2" type="radio" name="status" value="2" class="" ${model.status == '2' ? 'checked' : ''}> 维修
      </label>
      <label class="radio-inline">
        <input id="carInfo_status3" type="radio" name="status" value="3" class="" ${model.status == '3' ? 'checked' : ''}> 报废
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

