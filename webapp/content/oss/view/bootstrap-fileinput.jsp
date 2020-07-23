<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "oss");%>
<%pageContext.setAttribute("currentMenu", "oss");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <link rel='stylesheet' href='${cdnPrefix}/public/bootstrap-fileinput/4.5.1/css/fileinput.min.css' type='text/css' media='screen' />
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-fileinput/4.5.1/js/fileinput.min.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-fileinput/4.5.1/js/locales/zh.js"></script>

    <script type="text/javascript">
$(function() {
    $("#store-infoForm").validate({
        submitHandler: function(form) {
            bootbox.animate(false);
            var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

    $("#fileupload").fileinput({
      language:'zh',
      uploadUrl: function() {
        $.get('presign.do', {}, function(data) {
          console.info(data)
        });
        return '${tenantPrefix}/oss/rs/default'
      },
      uploadUrl: '${tenantPrefix}/oss/rs/default',
      uploadExtraData: {
        catalog: 'catalog',
        businessKey: 'bizKey'
      }
    })
})
    </script>
  </head>

  <body>
    <%@include file="/header/oss.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/oss.jsp"%>

    <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          编辑
        </div>

        <div class="panel-body">


<form id="storeInfoForm" method="post" action="${tenantPrefix}/store/store-info-upload.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1">模块</label>
    <div class="col-sm-5">
      <p class="form-control-static">catalog</p>
      <input type="hidden" name="catalog" value="catalog">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">业务主键</label>
    <div class="col-sm-5">
      <p class="form-control-static">bizKey</p>
      <input type="hidden" name="businessKey" value="bizKey">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">附件</label>
    <div class="col-md-11">
      <input id="fileupload" type="file" name="file" class="required" multiple>
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

