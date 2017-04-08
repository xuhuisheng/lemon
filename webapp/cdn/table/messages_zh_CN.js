
Table.prototype.messages = {
	'page.info': function() {
		return '共' + arguments[0] + "条记录 显示" + arguments[1] + "到" + arguments[2] + '条记录';
	},
	'page.empty': '没有数据',
	'select.record': '请选择需要删除的记录',
	'confirm.delete': '确认要删除选择的记录吗？'
}
