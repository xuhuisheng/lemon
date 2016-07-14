
QueryBuilder = function(id) {
	this.id = id;
	this.sed = 0;
};

QueryBuilder.prototype.setFields = function(fields) {
	this.fields = fields;
};

QueryBuilder.prototype.setData = function(data) {
	this.data = data;
};

QueryBuilder.prototype.setUrl = function(url) {
	this.url = url;
};

QueryBuilder.prototype.genId = function() {
	return this.sed++;
};

QueryBuilder.prototype.render = function() {
	this.renderMenu();
	this.renderFields();

	var self = this;

	$(document).delegate('.queryBuilderAdd', 'click', function(e) {
		self.addField(e.target.title);
	});

	$(document).delegate('.queryBuilderDelete', 'click', function(e) {
		var el = e.target.parentNode.parentNode;
		el.parentNode.removeChild(el);
	});

	$(document).delegate('', 'hide.bs.dropdown', function(e) {
		if ($(e.relatedTarget).parent().hasClass('queryBuilderField')) {
			e.preventDefault();
		}
	});

	$(document).on('click.bs.dropdown.data-api', function(e) {
		if ($(e.target).parents('.queryBuilderField').length == 0) {
			$('.queryBuilderField.open').each(function(index, item) {
				$(item).removeClass('open');
				var name = item.getAttribute('title');
				$('#' + item.id + ' .queryBuilderValue').html(
					$('#' + item.id + ' .queryBuilderName').val()
				);
			});
		}
	});

	$(document).delegate('#queryBuilderButton', 'click', function() {
		self.submit();
	});
};

QueryBuilder.prototype.renderMenu = function() {
	var el = document.getElementById(this.id + 'Menu');

	var html = '';
	for (var key in this.fields) {
		var field = this.fields[key];
		html += '<li><a href="#" title="' + field.name + '" class="queryBuilderAdd">' + field.label + '</a></li>'
	}

	el.innerHTML = html;
};

QueryBuilder.prototype.renderFields = function() {
	var el = document.getElementById(this.id + 'Fields');
	el.innerHTML = '';

	for (var i = 0; i < this.data.length; i++) {
		var item = this.data[i];
		this.addField(item.name, item.value);
	}
};

QueryBuilder.prototype.addField = function(fieldName, fieldValue) {
	var field = this.fields[fieldName];
	fieldValue = fieldValue ? fieldValue : '';

	var el = document.createElement('li');
	el.id = this.genId();
	el.className = 'queryBuilderField';
	el.title = field.name;
	el.innerHTML = '<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">'
		+ '<span class="queryBuilderLabel">' + field.label + '</span>'
		+ '<span class="queryBuilderOperator">' + field.operator + ':&nbsp;</span>'
		+ '<span class="queryBuilderValue">' + fieldValue + '</span>'
		+ '&nbsp;'
		+ '<span aria-hidden="true" class="queryBuilderDelete">&times;</span>'
		+ '</a>'
        + '<ul class="dropdown-menu" role="menu">'
        + '<li><form><input type="text" class="queryBuilderName" name="' + fieldName + '" value="' + fieldValue + '"></a></li>'
        + '</ul>';
	
	document.getElementById(this.id + 'Fields').appendChild(el);
};

QueryBuilder.prototype.submit = function() {
	var text = '';

	var expr = '#' + this.id + ' input';
	$(expr).each(function(index, item) {
		var name = item.getAttribute('name');
		var value = item.value;

		if (value != '') {
			text += '|' + name + '=' + value;
		}
	});

	var url = this.url;
	if (url.indexOf('?') == -1) {
		url += '?q=' + text.substring(1);
	} else {
		url += '&q=' + text.substring(1);
	}
	location.href = url;
};


