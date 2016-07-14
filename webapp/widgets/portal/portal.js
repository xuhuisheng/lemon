
$(function() {
	$('.dashboard').dashboard({
		afterPanelRemoved: function(panelId) {
			location.href = 'remove.do?id=' + panelId;
		},
		afterOrdered: function(panelOrders) {
			var ids = ''
			for (var key in panelOrders) {
				var id = key;
				var priority = panelOrders[key];
				ids += 'ids=' + id.replace('panel', '') + '&priorities=' + priority + '&';
			}
			location.href = 'updateOrder.do?' + ids;
		}
	});
});

function insertWidget() {
	$('#portalItemId').prop('disabled', true);
	$('#portalItemName').val('');
	$('#widgetModal').modal('show');
}

function updateWidget(id, widgetId, name) {
	$('#portalItemId').prop('disabled', false);
	$('#portalItemId').val(id);
	$('#portalWidgetId').val(widgetId);
	$('#portalItemName').val(name);
	$('#widgetModal').modal('show');
}

