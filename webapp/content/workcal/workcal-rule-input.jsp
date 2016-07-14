<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "workcal");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#workcal-ruleForm").validate({
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
    <%@include file="/header/workcal.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/workcal.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="scope-globalForm" method="post" action="workcal-rule-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="scope-global_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">年度</label>
	<div class="col-sm-5">
	  <input id="scope-global_name" type="text" name="year" value="${model.year}" size="40" class="form-control required number" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">周</label>
	<div class="col-sm-5">
	  <select name="week">
	    <option value="2">周一</option>
	    <option value="3">周二</option>
	    <option value="4">周三</option>
	    <option value="5">周四</option>
	    <option value="6">周五</option>
	    <option value="7">周六</option>
	    <option value="1">周日</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">名称</label>
	<div class="col-sm-5">
	  <input id="scope-global_name" type="text" name="name" value="${model.name}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">日期</label>
	<div class="col-sm-5">
	  <input id="scope-global_name" type="text" name="workDate" value="${model.workDate}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">状态</label>
	<div class="col-sm-5">
	  <select name="status">
	    <option value="0">规则</option>
	    <option value="1">节假日</option>
	    <option value="2">调休</option>
	    <option value="3">补休</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="scope-global_name">工作日历类型</label>
	<div class="col-sm-5">
	  <select name="workcalTypeId">
	    <c:forEach items="${workcalTypes}" var="item">
	    <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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

