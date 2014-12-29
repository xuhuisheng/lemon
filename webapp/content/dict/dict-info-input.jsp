<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dict");%>
<%pageContext.setAttribute("currentMenu", "dict");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dictInfo.dictInfo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#dictInfoForm").validate({
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
    <%@include file="/header/dict.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/dict.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="dictInfo.dictInfo.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="dictInfoForm" method="post" action="dict-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="dictInfo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="dictInfo_typeId">类型</label>
	<div class="controls">
	  <input type="hidden" name="typeId" value="${dictType.id}">
	  ${dictType.name}
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="dictInfo_name">名称</label>
	<div class="controls">
	  <input id="dictInfo_name" type="text" name="name" value="${model.name}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="dictInfo_value">数据</label>
	<div class="controls">
	  <input id="dictInfo_value" type="text" name="value" value="${model.value}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="dictInfo_priority">排序</label>
	<div class="controls">
	  <input id="dictInfo_priority" type="text" name="priority" value="${model.priority}" size="40" class="text number">
    </div>
  </div>
  <c:forEach var="item" items="${dictDto.data}">
  <div class="control-group">
    <label class="control-label" for="dictInfo_${item.value.name}">${item.value.name}</label>
	<div class="controls">
	  <input id="dictInfo_${item.value.name}" type="text" name="dictData_${item.value.name}" value="${item.value.value}" size="40" class="text">
    </div>
  </div>
  </c:forEach>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
