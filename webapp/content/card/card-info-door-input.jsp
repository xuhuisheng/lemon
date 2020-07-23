<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "card");%>
<%pageContext.setAttribute("currentMenu", "card");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#card-infoForm").validate({
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
    <%@include file="/header/card-info.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/card-info.jsp"%>

    <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          编辑
        </div>

        <div class="panel-body">

<form id="card-infoForm" method="post" action="card-info-door-save.do" class="form-horizontal">
  <input id="card-info_id" type="hidden" name="cardId" value="${param.id}">
  <c:forEach var="item" items="${doorInfos}">
  <div class="form-group">
    <label class="control-label col-md-1" for="card-info_address">&nbsp;</label>
    <div class="col-sm-5">
      <label class="checkbox checkbox-inline">
        <input type="checkbox" name="doorIds" value="${item.id}" class="checkbox" ${fn:contains(ids, item.id) ? 'checked' : ''}>
        ${item.name}
      </label>
    </div>
  </div>
  </c:forEach>
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

