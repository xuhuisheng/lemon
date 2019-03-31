<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "meeting");%>
<%pageContext.setAttribute("currentMenu", "meeting");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.meeting-room.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
	<script>
var building = [{
	code: 'TS',
	name: '唐山办公区',
	floors: [
		'A-F1',
		'A-F2'
	]
}, {
	code: 'BJ',
	name: '北京办公区',
	floors: [
		'F10'
	]
}];

var currentBuilding = 'BJ';
var currentFloor = 'F10';
var calendarDate = '${calendarDate}';

var prefix = calendarDate.substring(5).replace('-', '月') + '日';

function refreshRooms() {
	$.get('rs/rooms', {
		building: currentBuilding,
		floor: currentFloor,
		calendarDate: calendarDate,
	}, function(response) {
		$('#room-container').empty();
		for (var i = 0; i < response.data.length; i++) {
			var room = response.data[i];
			var deviceItem = '';
			if (room.devices.indexOf('白板') != -1) {
				deviceItem += '<i class="glyphicon glyphicon-blackboard" style="padding-right:10px;" title="白板"></i>';
			}
			if (room.devices.indexOf('IP电话') != -1) {
				deviceItem += '<i class="glyphicon glyphicon-phone-alt" style="padding-right:10px;" title="IP电话"></i>';
			}
			if (room.devices.indexOf('投影仪') != -1) {
				deviceItem += '<i class="glyphicon glyphicon-facetime-video" style="padding-right:10px;" title="投影仪"></i>';
			}

			var body = '';
			for (var j = 0; j < room.infos.length; j++) {
				var info = room.infos[j];

				body += '<li class="list-group-item">'
					+ prefix
					+ ' ' + info.startTime + ' ~ ' + info.endTime;
				if (info.status == 'idle') {
					body += ' <a href="create?roomId=' + room.id
					+ '&calendarDate=' + calendarDate
					+ '&startTime=' + info.startTime
					+ '&endTime=' + info.endTime
					+ '" class="btn btn-xs btn-default">预定</a>';
				} else {
					body += ' ' + info.userId;
				}
				body += '</li>';
			}

			$('#room-container').append(
'      <div class="col-md-4">'
+'	    <div class="panel panel-default">'
+'	      <div class="panel-heading">'
+'		    <div class="panel-title">'
+'		      ' + room.name
+'			  <div class="pull-right">'
+'				  ' + deviceItem
+'				  <span>'
+'					' + room.num + '<i class="glyphicon glyphicon-user"></i>'
+'				  </span>'
+'			  </div>'
+'		    </div>'
+'		  </div>'
+'        <ul class="list-group" style="height:200px;overflow:auto;">'
+           body
+'        </ul>'
+'	    </div>'
+'	  </div>');
		}
	});
}

function updateBuilding(buildingCode) {
	currentBuilding = buildingCode;

	$('#select-floor').empty();
	var floors = [];
	for (var i = 0; i < building.length; i++) {
		if (building[i].code == buildingCode) {
			floors = building[i].floors;
			break;
		}
	}
	$.each(floors, function(index, item) {
		$('#select-floor').append('<option value="' + item + '">' + item + '</option>');
	});

	updateFloor($('#select-floor').val());
}

function updateFloor(floorCode) {
	currentFloor = floorCode;

	this.refreshRooms();
}

function updateCalendarDate(calendarDateText) {
	calendarDate = calendarDateText;

	var shortURL = 'index.do?calendarDate=' + calendarDateText;
	history.replaceState(null, null, shortURL)

	prefix = calendarDate.substring(5).replace('-', '月') + '日';

	this.refreshRooms();
}

$(function() {
	updateBuilding($('#select-building').val());

	$('#select-building').change(function() {
		var value = $(this).val();
		updateBuilding(value);
	})
	
	$('#select-floor').change(function() {
		var value = $(this).val();
		updateFloor(value);
	});

	$('#meeting-calendarDate').change(function() {
		var value = $(this).val();
		updateCalendarDate(value);
	});
})
	</script>
  </head>

  <body>
    <%@include file="/header/meeting-user.jsp"%>

	<div class="row-fluid" style="padding-top:65px;margin-bottom:50px;">
	  <div class="col-md-11">
	    <form class="form-inline">
          <div class="form-group">
            <label for="exampleInputName2">办公区</label>
            <select class="form-control" id="select-building">
			  <option value="TS">唐山办公区</option>
			  <option value="BJ">北京办公区</option>
			</select>
          </div>
          <div class="form-group">
            <label for="exampleInputEmail2">楼层</label>
            <select class="form-control" id="select-floor">
			</select>
          </div>
          <div class="form-group">
            <label for="exampleInputEmail2">日期</label>
			<div class="input-group date datepicker" style="padding-left:15px;padding-right:15px;">
	          <input id="meeting-calendarDate" name="calendarDate" size="16" type="text" value="${calendarDate}" readonly style="background-color:white;cursor:default;" class="form-control required">
              <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
            </div> 
          </div>
        </form>
	  </div>
	  <div class="col-md-1">
        <a href="list.do" class="pull-right btn btn-default">预定列表</a>
	  </div>
	</div>

    <div class="row-fluid" id="room-container">

      <div class="col-md-4">
	    <div class="panel panel-default">
	      <div class="panel-heading">
		    <div class="panel-title">
		      title
			  <div class="pull-right">
				  <i class="glyphicon glyphicon-facetime-video" style="padding-right:10px;" title="投影仪"></i>
				  <i class="glyphicon glyphicon-phone-alt" style="padding-right:10px;" title="电话"></i>
				  <i class="glyphicon glyphicon-blackboard" style="padding-right:10px;" title="白板"></i>
				  <span>
					10<i class="glyphicon glyphicon-user"></i>
				  </span>
			  </div>
		    </div>
		  </div>
		  <div class="panel-body" style="height:200px;overflow:auto;">
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		  </div>
	    </div>
	  </div>

      <div class="col-md-4">
	    <div class="panel panel-default">
	      <div class="panel-heading">
		    <div class="panel-title">
		      title
		    </div>
		  </div>
		  <div class="panel-body" style="height:200px;overflow:auto;">
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		  </div>
	    </div>
	  </div>

      <div class="col-md-4">
	    <div class="panel panel-default">
	      <div class="panel-heading">
		    <div class="panel-title">
		      title
		    </div>
		  </div>
		  <div class="panel-body" style="height:200px;overflow:auto;">
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		  </div>
	    </div>
	  </div>

      <div class="col-md-4">
	    <div class="panel panel-default">
	      <div class="panel-heading">
		    <div class="panel-title">
		      title
		    </div>
		  </div>
		  <div class="panel-body" style="height:200px;overflow:auto;">
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		  </div>
	    </div>
	  </div>

      <div class="col-md-4">
	    <div class="panel panel-default">
	      <div class="panel-heading">
		    <div class="panel-title">
		      title
		    </div>
		  </div>
		  <div class="panel-body" style="height:200px;overflow:auto;">
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		    1<br>
		  </div>
	    </div>
	  </div>

	</div>

  </body>

</html>

