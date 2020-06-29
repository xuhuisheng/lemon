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

function addRow() {
  $('#table-body').append('<tr>'
    + "<td><input name='category' class='form-control'></td>"
    + "<td><input name='name' class='form-control'></td>"
    + "<td><input name='num' class='form-control'></td>"
    + "<td><button type='button' class='btn btn-default' onclick='removeRow(this)'>-</button></td>"
    + "</tr>"
  )
}

function removeRow(btn) {
  var tr = $(btn).parent().parent();
  tr.remove();
}
    </script>
  </head>

  <body>
    <%@include file="/header/asset-user.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/asset-user.jsp"%>

    <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          编辑
        </div>

        <div class="panel-body">

<form id="asset-infoForm" method="post" action="request-save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-12" for="asset-info_description" style="text-align:left;">申请原因</label>
    <div class="col-md-12">
      <textarea name="description" class="form-control">${model.description}</textarea>
    </div>
  </div>
    <table class="table">
      <thead>
        <tr>
          <th>分类</th>
          <th>资产</th>
          <th>数量</th>
          <th><button class="btn btn-default" type="button" onclick="addRow()">+</button>
        </tr>
      </thead>
      <tbody id="table-body">
        <tr>
          <td><input name='category' class="form-control"></td>
          <td><input name='name' class="form-control"></td>
          <td><input name='num' class="form-control"></td>
          <th><button class="btn btn-default" type="button" onclick="removeRow(this)">-</button>
        </tr>
      </tbody>
    </table>

  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.in
        put.back' text='返回'/></button>
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

