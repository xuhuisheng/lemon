<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "note");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>


    <link rel='stylesheet' href='${tenantPrefix}/widgets/note/note.css' type='text/css' media='screen' />
    <script src='${tenantPrefix}/widgets/note/note.js' type='text/javascript'></script>
    <script type="text/javascript">

$(function() {
	var note = new Note('note', function(callback) {
		$.get('${tenantPrefix}/pim/pim-note-create.do', {}, function(data) {
			callback(data);
		});
	}, function(id, content) {
		$.get('${tenantPrefix}/pim/pim-note-update-content.do', {id:id, content:content});
	}, function(id, clientX, clientY) {
		$.get('${tenantPrefix}/pim/pim-note-update-position.do', {id:id, clientX:clientX, clientY:clientY});
	});

	$('#addNewNote').click(function() {
		note.addNewNote();
	});
});

	</script>
  </head>

  <body>
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid" style="padding-top:60px;">
      <button class="btn btn-default" id="addNewNote">Add</button>

	  <div id="note">
	    <c:forEach items="${pimNotes}" var="item">
		  <div class="panel panel-default note-item note-yellow" style="width:150px;height:200px;position:absolute;left:${item.clientX}px;top:${item.clientY}px" data-id="${item.id}">
		    <div class="panel-body">
			  <pre class="note-pre">${item.content}</pre>
			</div>
		  </div>
		</c:forEach>
	  </div>

	  <!-- end of main -->
	</div>

  </body>

</html>
