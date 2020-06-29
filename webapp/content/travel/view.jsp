<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "travel");%>
<%pageContext.setAttribute("currentMenu", "travel");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <style>
fieldset {
    padding: .35em .625em .75em;
    margin: 0 2px;
    border: 1px solid silver;
 }

legend {
    padding: .5em;
    border: 0;
    width: auto;
    margin-bottom: 0px;
 }
    </style>
    <script type="text/javascript">
$(function() {
    $("#travel-infoForm").validate({
        submitHandler: function(form) {
            bootbox.animate(false);
            var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})

function addRow() {
  $('#body').append(

'    <fieldset style="margin-bottom:20px;">'
+'    <i class="glyphicon glyphicon-remove pull-right" style="cursor:pointer;" onclick="removeRow(this)"></i>'
+'  <div class="form-group">'
+'    <label class="control-label col-md-1" for="travel-info_name">&nbsp;</label>'
+'    <div class="col-sm-5">'
+'      <label class="radio radio-inline">'
+'        <input type="radio" name="type" value="single">'
+'        单程'
+'      </label>'
+'      <label class="radio radio-inline">'
+'        <input type="radio" name="type" value="shuttle">'
+'        往返'
+'      </label>'
+'    </div>'
+'  </div>'
+'  <div class="form-group">'
+'    <label class="control-label col-md-1" for="travel-info_name">&nbsp;</label>'
+'    <div class="col-sm-5">'
+'      <label class="radio radio-inline">'
+'        <input type="radio" name="vehicle" value="plane">'
+'        飞机'
+'      </label>'
+'      <label class="radio radio-inline">'
+'        <input type="radio" name="vehicle" value="train">'
+'        火车'
+'      </label>'
+'      <label class="radio radio-inline">'
+'        <input type="radio" name="vehicle" value="bus">'
+'        汽车'
+'      </label>'
+'    </div>'
+'  </div>'
+'  <div class="form-group">'
+'    <label class="control-label col-md-1" for="travel-info_name">行程时间</label>'
+'    <div class="col-sm-2">'
+'      <div class="input-group datepicker date" style="padding:0;">'
+'        <input id="visitorInfo_visitTime" type="text" name="startDate" value="" readonly+ style="background-color:white;cursor:default;" class="form-control">'
+'        <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>'
+'      </div>'
+'    </div>'
+'    <div class="col-sm-2">'
+'      <div class="input-group datepicker date" style="padding:0;">'
+'        <input id="visitorInfo_visitTime" type="text" name="endDate" value="" readonly style="background-color:white;cursor:default;" class="form-control">'
+'        <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>'
+'      </div>'
+'    </div>'
+'  </div>'
+'  <div class="form-group">'
+'    <label class="control-label col-md-1" for="travel-info_name">行程地点</label>'
+'    <div class="col-sm-2">'
+'      <input id="travel-info_name" type="text" name="startCity" value="${model.startCity}" size="40" class="form-control required" minlength="2" maxlength="10">'
+'    </div>'
+'    <div class="col-sm-2">'
+'      <input id="travel-info_name" type="text" name="endCity" value="" size="40" class="form-control required" minlength="2" maxlength="10">'
+'    </div>'
+'  </div>'
+'</fieldset>'
)

}

function removeRow(el) {
  $(el).parent().remove()
}
    </script>
  </head>

  <body>
    <%@include file="/header/travel-info.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/travel-info.jsp"%>

    <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          查看
        </div>

        <div class="panel-body">


<form id="travel-infoForm" method="post" action="save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="travel-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="travelInfo_project">关联项目</label>
    <div class="col-sm-5">
      <p class="form-control-static">${travelInfo.project}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="travelInfo_description">出差事由</label>
    <div class="col-sm-5">
      <p class="form-control-static">${travelInfo.description}</p>
    </div>
  </div>
  <div id="body">
    <c:forEach var="item" items="${travelInfo.travelItems}">
  <fieldset style="margin-bottom:20px;">
  <div class="form-group">
    <label class="control-label col-md-1" for="travel-info_name">&nbsp;</label>
    <div class="col-sm-2">
      <p class="form-control-static">${item.type}</p>
    </div>
    <div class="col-sm-2">
      <p class="form-control-static">${item.vehicle}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="travel-info_name">行程时间</label>
    <div class="col-sm-2">
      <p class="form-control-static">${item.startDate}</p>
    </div>
    <div class="col-sm-2">
      <p class="form-control-static">${item.endDate}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="travel-info_name">行程地点</label>
    <div class="col-sm-2">
      <p class="form-control-static">${item.startCity}</p>
    </div>
    <div class="col-sm-2">
      <p class="form-control-static">${item.endCity}</p>
    </div>
  </div>
</fieldset>
</c:forEach>
</div>
</form>

        </div>
      </article>

    </section>
    <!-- end of main -->
    </div>

  </body>

</html>

