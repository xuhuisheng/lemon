
Table.prototype.messages = {
	'page.info': function() {
		return arguments[0] + " records, display: " + arguments[1] + " to " + arguments[2];
	},
	'page.empty': 'No data to display',
	'select.record': 'please select record to delete',
	'confirm.delete': 'are you sure to delete these records?'
}
