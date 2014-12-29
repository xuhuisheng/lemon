<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scheduler");%>
<%pageContext.setAttribute("currentMenu", "scheduler");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.pimScheduler.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>

	<script type="text/javascript" src="${ctx}/s/jquery-ui/ui/minified/jquery-ui.min.js"></script>

	<link rel='stylesheet' type='text/css' href='${ctx}/s/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='${ctx}/s/fullcalendar/fullcalendar.print.css' media='print' />
	<script type="text/javascript" src="${ctx}/s/fullcalendar/moment.min.js"></script>
	<script type='text/javascript' src='${ctx}/s/fullcalendar/fullcalendar.js'></script>
	<script type="text/javascript" src="${ctx}/s/fullcalendar/zh-CN.js"></script>

	<script type="text/javascript">
var Scheduler = function() {
};

Scheduler.prototype = {
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
			'${scopePrefix}/rs/scheduler/save',
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
			'${scopePrefix}/rs/scheduler/update',
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
			'${scopePrefix}/rs/scheduler/update',
			data,
			function(result) {
			}
		);
	},
	remove: function(id) {
		$.post(
			'${scopePrefix}/rs/scheduler/remove',
			{id: id},
			function(result) {
				$('#calendar').fullCalendar('removeEvents', id);
			}
		);
	}
};

var scheduler = new Scheduler();

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
				scheduler.create({
					start: start,
					end: end
				});
			},
			eventClick: function(eventObject) {
				scheduler.edit(eventObject);
			},
			eventDrop: function(eventObject) {
				scheduler.updateDirect(eventObject);
			},
			eventResizeStop: function(eventObject) {
				scheduler.updateDirect(eventObject);
				return true;
			},
			editable: true,
			events: function(start, end, isStrict, callback) {
		        $.ajax({
		            url: '${scopePrefix}/rs/scheduler/get',
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
				scheduler.save(eventData);
			} else {
				scheduler.update(eventData);
			}
		}
		$('#calendar').fullCalendar('unselect');

		$('#calendarModal').modal('hide');
	});

    $(document).delegate('#calendarRemove', 'click', function() {
		var id = $('#calendarId').val();
		scheduler.remove(id);

		$('#calendar').fullCalendar('unselect');

		$('#calendarModal').modal('hide');
	});
});
    </script>
  </head>

  <body>
    <%@include file="/header/pim.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header"><h4 class="title">&nbsp;</h4></header>

        <div id="search" class="content">

		  <div id='calendar'></div>
		</div>
	  </article>

      </section>
	  <!-- end of main -->
	</div>

    <div id="calendarModal" class="modal hide fade">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>&nbsp;</h3>
      </div>
      <div class="modal-body">
		<input id="calendarId" type="hidden" name="id" value="">
        <label>
		  标题：
		  <input id="calendarTitle" type="text" name="title" value="">
		</label>
        <label>
		  开始：
		  <input id="calendarStart" type="text" name="start" value="">
		</label>
        <label>
		  结束：
		  <input id="calendarEnd" type="text" name="end" value="">
		</label>
        <label>
		  备注：
		  <textarea id="calendarContent" name="end"></textarea>
		</label>
      </div>
      <div class="modal-footer">
        <a href="#" class="btn" id="calendarRemove">删除</a>
        <a href="#" class="btn btn-primary" id="calendarSave">保存</a>
      </div>
    </div>

  </body>

</html>
