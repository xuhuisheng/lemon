
var WorkCalendar = function(year) {
	if (!year) {
		this.year = new Date().getFullYear();
	} else {
		this.year = year;
	}
}

WorkCalendar.prototype = {
	render: function(id) {
		var html = '<div class="row" style="padding-bottom:10px;"><div style="width:20px;float:left;">&nbsp;</div>';
		for (var i = 0; i < 12; i++) {
			if (i == 4 || i == 8) {
				html += '</div><div class="row" style="padding-bottom:10px;"><div style="width:20px;float:left;">&nbsp;</div>';
			}
			html += this.generateMonth(i);
		}
		html += '</div>';
		$(id).append(html);
	},

	generateMonth: function(month) {
		var html = '<div class="datepicker dropdown-menu span3" style="display: block; position: relative;">'
+'  <div class="datepicker-days" style="display: block;">'
+'    <table class="table-condensed">'
+'	  <thead>'
+'	    <tr>'
+'		  <th class="prev"></th>'
+'		  <th class="switch" colspan="5">' + this.year + '年' + (month + 1) + '月</th>'
+'		  <th class="next"></th>'
+'		</tr>'
+'		<tr>'
+'		  <th class="dow">日</th>'
+'		  <th class="dow">一</th>'
+'		  <th class="dow">二</th>'
+'		  <th class="dow">三</th>'
+'		  <th class="dow">四</th>'
+'		  <th class="dow">五</th>'
+'		  <th class="dow">六</th>'
+'		</tr>'
+'	  </thead>'
+'	  <tbody>'
+'      <tr>';

		var date = new Date(this.year, month, 1);
		var day = date.getDay();

		var lastDayOfMonth = this.getLastDayOfMonth(month);

		for (var j = 0; j < day; j++) {
			html += '<td>&nbsp;</td>';
		}

		for (var j = 0; j < lastDayOfMonth; j++) {
			var week = (j + day) %7;
			if (week == 0) {
				html += '</tr><tr>';
			}

			var date = this.year + '' + (month + 1) + '' + (j + 1);
			html += '<td class="week' + week + ' date' + date + '">' + (j + 1) + '</td>';
		}

		if ((lastDayOfMonth + day) % 7 != 0) {
			for (var j = (lastDayOfMonth + day) % 7; j < 7; j++) {
				html += '<td>&nbsp;</td>';
			}
		}
		html += '</tr>';

		if (lastDayOfMonth + day < 36) {
			html += "<tr><td>&nbsp;</td></tr>"
		}

			html += '</tbody>'
+'	</table>'
+'  </div>'
+'</div>';
		return html;
	},

	getLastDayOfMonth: function(month) {
		switch (month) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			return 31;
		case 3:
		case 5:
		case 8:
		case 10:
			return 30;
		case 1:
			if (((this.year % 100 != 0) && (this.year % 4 == 0)) || (this.year % 400 == 0)) {
				return 29;
			} else {
				return 28;
			}
		default:
			throw new Error('invalid month : ' + month);

		}
	},

	activeByWeek: function(weeks) {
		for (var i = 0; i < weeks.length; i++) {
			$('.week' + weeks[i]).css({
				backgroundColor: '#ADD8E6'
			});
		}
	},

	markHolidays: function(holidays) {
		for (var i = 0; i < holidays.length; i++) {
			var item = holidays[i];
			$('.date' + item.date).css({
				backgroundColor: 'red'
			});
			$('.date' + item.date).attr('title', item.name);
		}
	},

	markWorkdays: function(workdays) {
		for (var i = 0; i < workdays.length; i++) {
			var item = workdays[i];
			$('.date' + item.date).css({
				backgroundColor: '#ADD8E6'
			});
			$('.date' + item.date).attr('title', item.name);
		}
	},

	markExtrdays: function(extrdays) {
		for (var i = 0; i < extrdays.length; i++) {
			var item = extrdays[i];
			$('.date' + item.date).css({
				backgroundColor: 'red'
			});
			$('.date' + item.date).attr('title', item.name);
		}
	}
};
