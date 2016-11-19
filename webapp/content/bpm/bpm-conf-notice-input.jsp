<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#bpm-conf-noticeForm").validate({
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
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="noticeForm" method="post" action="bpm-conf-notice-save.do" class="form-horizontal">

  <input id="bpm-process_id" type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
  <div class="form-group">
    <label class="control-label col-md-1">类型</label>
	<div class="col-sm-5">
	  <select name="type" class="form-control">
	    <option value="0">到达</option>
	    <option value="1">完成</option>
	    <option value="2">超时</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">提醒人</label>
	<div class="col-sm-5">
	  <input type="text" name="receiver" value="" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">提醒时间</label>
	<div class="col-sm-5">
	  <input type="text" name="dueDate" value="" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">提醒方式</label>
	<div class="col-sm-5">
	  <c:forEach items="${types}" var="item">
	  <label><input type="checkbox" name="notificationTypes" value="${item}"> ${item}</label>
	  </c:forEach>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">模板</label>
	<div class="col-sm-5">
	  <select name="templateCode" class="form-control">
	    <c:forEach items="${templateDtos}" var="item">
	    <option value="${item.code}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" type="submit" class="btn btn-default">保存</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link">返回</button>
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

