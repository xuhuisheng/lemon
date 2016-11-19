<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim-schedule");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>

	<script type="text/javascript" src="${ctx}/s/jquery-ui/ui/minified/jquery-ui.min.js"></script>

	<link rel='stylesheet' type='text/css' href='${ctx}/s/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='${ctx}/s/fullcalendar/fullcalendar.print.css' media='print' />
	<script type="text/javascript" src="${ctx}/s/fullcalendar/moment.min.js"></script>
	<script type='text/javascript' src='${ctx}/s/fullcalendar/fullcalendar.js'></script>
	<script type="text/javascript" src="${ctx}/s/fullcalendar/zh-CN.js"></script>

	<script type="text/javascript">
var Schedule = function() {
};

Schedule.prototype = {
	create: function(data) {
		$('#calendarId').val('');
		$('#calendarTitle').val('');
		$('#calendarStart').val(data.start.format());
		$('#calendarEnd').val(data.end.format());
		$('#calendarContent').val('');
		$('#calendarModal').modal('show');
	},
	save: function(data) {
		$.post(
			'${tenantPrefix}/rs/schedule/save',
			data,
			function(result) {
				$('#calendar').fullCalendar('renderEvent', result.data, true); // stick? = true
			}
		);
	},
	edit: function(eventObject) {
		this.eventObject = eventObject;

		$('#calendarId').val(eventObject.id);
		$('#calendarTitle').val(eventObject.title);
		$('#calendarStart').val(eventObject.start.format());
		if (eventObject.end) {
			$('#calendarEnd').val(eventObject.end.format());
		} else {
			$('#calendarEnd').val('');
		}
		$('#calendarContent').val(eventObject.content);
		$('#calendarModal').modal('show');
	},
	update: function(data) {
		var eventObject = this.eventObject;

		$.post(
			'${tenantPrefix}/rs/schedule/update',
			data,
			function(result) {
				eventObject.title = result.data.title;
				eventObject.content = result.data.content;
				eventObject.start = moment(result.data.start);
				eventObject.end = moment(result.data.end);
				
				$('#calendar').fullCalendar('updateEvent', eventObject);
				// $('#calendar').fullCalendar('refetchEvents');
			}
		);
	},
	updateDirect: function(eventObject) {
		var data = {
			id: eventObject.id,
			title: eventObject.title,
			start: eventObject.start.format(),
			end: eventObject.end.format(),
			content: eventObject.content
		};
		$.post(
			'${tenantPrefix}/rs/schedule/update',
			data,
			function(result) {
			}
		);
	},
	remove: function(id) {
		$.post(
			'${tenantPrefix}/rs/schedule/remove',
			{id: id},
			function(result) {
				$('#calendar').fullCalendar('removeEvents', id);
			}
		);
	}
};

var schedule = new Schedule();

$(function () {

		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			selectable: true,
			selectHelper: true,
			select: function(start, end) {
				schedule.create({
					start: start,
					end: end
				});
			},
			eventClick: function(eventObject) {
				schedule.edit(eventObject);
			},
			eventDrop: function(eventObject) {
				schedule.updateDirect(eventObject);
			},
			eventResizeStop: function(eventObject) {
				schedule.updateDirect(eventObject);
				return true;
			},
			editable: true,
			events: function(start, end, isStrict, callback) {
		        $.ajax({
		            url: '${tenantPrefix}/rs/schedule/get',
		            dataType: 'json',
		            data: {
		                //start: start.format(),
						//end: end.format()
		            },
		            success: function(doc) {
		                callback(doc.data);
		            }
		        });
		    },
		});


    $(document).delegate('#calendarSave', 'click', function() {
		var id = $('#calendarId').val();
		var title = $('#calendarTitle').val();
		var start = $('#calendarStart').val();
		var end = $('#calendarEnd').val();
		var content = $('#calendarContent').val();

		var eventData;
		if (title) {
			eventData = {
				id: id,
				title: title,
				start: start,
				end: end,
				content: content
			};
			if (id == '') {
				schedule.save(eventData);
			} else {
				schedule.update(eventData);
			}
		}
		$('#calendar').fullCalendar('unselect');

		$('#calendarModal').modal('hide');
	});

    $(document).delegate('#calendarRemove', 'click', function() {
		var id = $('#calendarId').val();
		schedule.remove(id);

		$('#calendar').fullCalendar('unselect');

		$('#calendarModal').modal('hide');
	});
});
    </script>
  </head>

  <body>
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  &nbsp;
		</div>

		<div class="panel-body">

		  <div id='calendar'></div>

        </div>
      </div>

      </section>
	  <!-- end of main -->
	</div>

<div id="calendarModal" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">&nbsp;</h4>
      </div>
      <div class="modal-body">
		<input id="calendarId" type="hidden" name="id" value="">
        <label>
		  标题：
		  <input id="calendarTitle" type="text" name="title" value="" class="form-control">
		</label>
        <label>
		  开始：
		  <input id="calendarStart" type="text" name="start" value="" class="form-control">
		</label>
        <label>
		  结束：
		  <input id="calendarEnd" type="text" name="end" value="" class="form-control">
		</label>
        <label>
		  备注：
		  <textarea id="calendarContent" name="end" class="form-control"></textarea>
		</label>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" id="calendarRemove">关闭</button>
        <button type="button" class="btn btn-primary" id="calendarSave">保存</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

  </body>

</html>
