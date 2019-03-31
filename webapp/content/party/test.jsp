<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>test</title>
    <%@include file="/common/s3.jsp"%>
	<script type="text/javascript">
function doGetSuperiorId() {
	var userId = $('#getSuperiorId_userId').val();
	$.get('getSuperiorId.do', {
		userId: userId
	},
	function(data) {
		$('#getSuperiorId').text(data);
	});
}

function doGetPositionUserIds() {
	var userId = $('#getPositionUserIds_userId').val();
	var positionName = $('#getPositionUserIds_positionName').val();
	$.post('getPositionUserIds.do', {
		userId: userId,
		positionName: positionName
	},
	function(data) {
		$('#getPositionUserIds').text(data);
	});
}
	</script>
  </head>

  <body>
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/party.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

        <div class="panel panel-default">
          <div class="panel-heading">
		    <i class="glyphicon glyphicon-list"></i>
		    test
		  </div>


    <table id="orgTypeGrid" class="table table-hover">
      <thead>
        <tr>
          <th>#</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
		    getSuperiorId(<input id="getSuperiorId_userId" type="text" value="" placeholder="userId">)
			<button onclick="doGetSuperiorId()">do</button>
		  </td>
          <td><span id="getSuperiorId"></span></td>
        </tr>
        <tr>
          <td>
		    getPositionUserIds(
			<input id="getPositionUserIds_userId" type="text" value="" placeholder="userId">
			,
			<input id="getPositionUserIds_positionName" type="text" value="" placeholder="positionName">
			)
			<button onclick="doGetPositionUserIds()">do</button>
		  </td>
          <td><span id="getPositionUserIds"></span></td>
        </tr>
      </tbody>
    </table>

        </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

