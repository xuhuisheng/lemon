
Table = function(config) {
	this.init(config);
};

Table.prototype = {
	init: function(config) {
		this.config = config;

		this.configSorting();
	},

	configSorting: function() {
		var self = this;

		$('#' + self.config.id + ' .sorting').each(function(index, element) {
			var elem = $(element);
			var column = elem.attr('name');

			$(element).click(function() {
				if (elem.hasClass('sorting-asc')) {
					self.changeOrder(column, 'DESC');
				} else {
					self.changeOrder(column, 'ASC');
				}
			});

			if (column == self.config.orderBy) {
				elem.addClass('sorting-' + (self.config.asc ? 'asc' : 'desc'));
			}
		});
	},

	changeOrder: function (orderBy, order) {
		var params = {
			'pageNo': this.config.pageNo,
			'pageSize': this.config.pageSize,
			'orderBy': orderBy,
			'order': order
		};
		var targetParams = {};
		$.extend(targetParams, this.config.params, params);

		var url = this.buildUrl(targetParams);
		location.href = url;
	},

	changePageNo: function(pageNo) {
		if (pageNo != this.config.pageNo) {
			var params = {
				'pageNo': pageNo,
				'pageSize': this.config.pageSize,
				'orderBy': this.config.orderBy,
				'order': this.config.asc ? 'ASC' : 'DESC'
			};

			var targetParams = {};
			$.extend(targetParams, this.config.params, params);
			var url = this.buildUrl(targetParams);
			location.href = url;
		}
		return false;
	},

	changePageSize: function(pageSize) {
		if (pageSize != this.config.pageSize) {
			var params = {
				'pageNo': this.config.pageNo,
				'pageSize': pageSize,
				'orderBy': this.config.orderBy,
				'order': this.config.asc ? 'ASC' : 'DESC'
			};

			var targetParams = {};
			$.extend(targetParams, this.config.params, params);
			var url = this.buildUrl(targetParams);
			location.href = url;
		}
		return false;
	},

	buildUrl: function (params) {
		var url = location.pathname;

		var separator = url.indexOf('?') == -1 ? '?' : '&';
		for (var key in params) {
			var value = params[key];
			if (typeof value == 'undefined' || value == null || value == '') {
				continue;
			}
			url += separator + key + '=' + encodeURIComponent(value);
			if (separator == '?') {
				separator = '&';
			}
		}
		return url;
	},

	configPagination: function(expr) {
		var self = this;

		$(expr).pagination(this.config.totalCount, {
			callback: function(pageIndex, jq) {
				self.changePageNo(pageIndex + 1);
			}, // 点击页数执行的回调函数
			current_page: this.config.pageNo - 1, // 当前页码
			items_per_page: this.config.pageSize //每页显示几项
		});
	},

	configPageInfo: function(expr) {
		var start = (this.config.pageNo - 1) * this.config.pageSize + 1;
		var end = start + this.config.resultSize - 1;
		// var html = "共" + this.config.totalCount + "条记录 显示" + start + "到" + end + '条记录';
		var html = '';
		if (this.config.totalCount == 0) {
			html = this.messages['page.empty'];
		} else {
			html = this.messages['page.info'].call(this, this.config.totalCount, start, end);
		}
		$(expr).html(html);
	},

	configPageSize: function(expr) {
		var self = this;

		$(expr).val(this.config.pageSize);

		$(expr).bind('change', function() {
			self.changePageSize($(expr).val());
		});
	},

	removeAll: function() {
		var len = $('.' + this.config.selectedItemClass + ':checked').length;
		if (len == 0) {
			$.showMessage(this.messages['select.record'], {
				 position: 'top',
				 size: '36',
				 fontSize: '20px'
			});
			return false;
		}
		if (confirm(this.messages['confirm.delete'])) {
			$('#' + this.config.gridFormId).submit();
			return true;
		} else {
			return false;
		}
	},

	exportExcel: function() {
		var url = this.config.exportUrl;

		var params = {
			'pageNo': this.config.pageNo,
			'pageSize': this.config.pageSize,
			'orderBy': this.config.orderBy,
			'order': this.config.asc ? 'ASC' : 'DESC'
		};

		var targetParams = {};
		$.extend(targetParams, this.config.params, params);

		var separator = url.indexOf('?') == -1 ? '?' : '&';
		for (var key in params) {
			var value = params[key];
			if (typeof value == 'undefined' || value == null || value == '') {
				continue;
			}
			url += separator + key + '=' + encodeURIComponent(value);
			if (separator == '?') {
				separator = '&';
			}
		}
		location.href = url;
	},

	messages: {
		'page.info': function() {
			return arguments[0] + " records, display: " + arguments[1] + " to " + arguments[2];
		},
		'page.empty': 'No data to display',
		'select.record': 'please select record to delete',
		'confirm.delete': 'are you sure to delete these records?'
	}
}
