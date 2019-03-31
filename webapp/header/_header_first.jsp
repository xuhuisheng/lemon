<%@ page language="java" pageEncoding="UTF-8" %>

    <c:if test="${not empty flashMessages}">
	<div id="m-success-message" style="display:none;">
	  <ul>
	  <c:forEach items="${flashMessages}" var="item">
	    <li>${item}</li>
	  </c:forEach>
	  </ul>
	</div>
	</c:if>
	<script type="text/javascript">
function unreadCount() {
	$.getJSON('${ctx}/msg/rs/unreadCount', {}, function(data) {
		var count = data.data.totalCount;
		var list = data.data.result;
		if (count == 0) {
			$('#msg-unread-count').hide();
		} else {
			$('#msg-unread-count').html(count);
			$('#msg-unread-count').show();
			var html = '';
			for (var i = 0; i < list.length; i++) {
				var item = list[i];
				html += '<li><a href="${ctx}/msg/msg-info-view.do?id=' + item.id + '">' + item.name + '</a></li>';
			}
			html += '<li><a href="${ctx}/msg/msg-info-listReceived.do">更多消息</a></li>';
			$('#msg-unread-content').html(html);

		}
	});
}

unreadCount();
setInterval(unreadCount, 10000);
	</script>
