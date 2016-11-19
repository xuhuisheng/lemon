/*
 * Compressed by JSA(www.xidea.org)
 */

xf = {
};

xf.field = {
};

xf.$ = function(id) {
	return document.getElementById(id);
}

xf.id = function() {
	if (typeof xf.sed == 'undefined') {
		xf.sed = 1;
	}
	return 'xf-' + xf.sed++;
}

xf.addClass = function(el, className) {
	if ((' ' + el.className).indexOf(' ' + className) == -1) {
		el.className += ' ' + className;
	}
}

xf.removeClass = function(el, className) {
	var cls = el.className;
	while (cls.indexOf(' ' + className) != -1) {
		var index = cls.indexOf(' ' + className);
		cls = cls.substring(0, index) + cls.substring(index + className.length + 1);
	}
	while (cls.indexOf(className + ' ') != -1) {
		var index = cls.indexOf(className + ' ');
		cls = cls.substring(0, index) + cls.substring(index + className.length + 1);
	}
	el.className = cls;
}

xf.getTarget = function(e) {
	var ev = window.event ? window.event : e;
	var x = ev.clientX;
	var y = ev.clientY;
	var target = ev.srcElement ? ev.srcElement : ev.target;
	if (target.tagName == 'IMG' && target.parentNode.className == 'xf-pallete') {
		target = target.parentNode;
	}
	return target;
}

xf.getHandler = function(el) {
	while (el) {
		if (el.className && el.className == 'xf-handler') {
			return el;
		}
		el = el.parentNode;
	}
	return null;
}

xf.getPosition = function(e) {
	var ev = window.event ? window.event : e;
	var x = ev.clientX;
	var y = ev.clientY;
	return {
		x: x,
		y: y
	};
}

xf.insertAfter = function(newElement, targetElement) {
    var parent = targetElement.parentNode;
    if(parent.lastChild == targetElement) {
        parent.appendChild(newElement);
    }else {
        parent.insertBefore(newElement, targetElement.nextSibling);
    }
}

xf.createField = function(label, value, callback, self, formNode) {

	var labelNode = document.createElement('label');
	labelNode.innerHTML = label + ':';
	labelNode.className = 'control-label';

	var input = document.createElement('input');
	input.type = 'text';
	input.value = value;
	input.onblur = function() {
		callback.call(self, this.value);
	}

	formNode.appendChild(labelNode);
	formNode.appendChild(input);
}

xf.createBooleanField = function(label, value, callback, self, formNode) {
	var labelNode = document.createElement('label');
	labelNode.className = 'checkbox control-group';

	var input = document.createElement('input');
	input.type = 'checkbox';
	input.value = 'true';
	if (value) {
		input.checked = true;
	}
	input.onclick = function() {
		callback.call(self, this.checked);
	}
	input.style.marginLeft = '-20px';

	labelNode.appendChild(input);
	labelNode.appendChild(document.createTextNode(' ' + label));

	formNode.appendChild(labelNode);
}

;

xf.Xform = function(id) {
	this.id = id;

	// 默认模式是拖拽编辑添加组件
	// 可以切换成合并单元格的模式
	this.mode = 'EDIT';

	this.sections = [];

	this.sed = 0;
	this.proxy = new xf.Proxy();
	this.fieldFactory = new xf.field.FieldFactory();
}

xf.Xform.prototype.addSection = function(section) {
	section.id = xf.id();
	section.xform = this;
	this.sections.push(section);
}

xf.Xform.prototype.initEvents = function() {
	var self = this;
	document.onmousedown = function(e) {
		self.mouseDown(e);
	}
	document.onmousemove = function(e) {
		self.mouseMove(e);
	}
	document.onmouseup = function(e) {
		self.mouseUp(e);
	}
}

xf.Xform.prototype.mouseDown = function(e) {
	var target = xf.getTarget(e);
	var handler = xf.getHandler(target);
	if (handler || target.className == 'xf-pallete') {
		e.preventDefault();
	}

	if (this.mode == 'EDIT') {
		if (target.className == 'xf-pallete') {
			this.request = {
				type: 'add',
				fieldType: target.title
			}
		} else if (handler) {
			var section = this.findSection(e);
			if (section) {
				var field = section.findField(target);
				if (field) {
					this.request = {
						type: 'move',
						section: section,
						field: field
					};
				}
			}
		}
	} else if (this.mode == 'MERGE') {
		var section = this.findSection(e);
		if (section) {
			section.mergeStart(e);
			this.request = {
				status: 'merge'
			};
		}
	}

	var section = this.findSection(e);
	if (section) {
		section.selectSomething(e);
	}
}

xf.Xform.prototype.mouseMove = function(e) {
	if (!this.request) {
		return;
	}

	if (this.mode == 'EDIT') {
		var position = xf.getPosition(e);
		this.proxy.move(position.x + 5, position.y + 5);
	} else if (this.mode == 'MERGE') {
		var section = this.findSection(e);
		if (section) {
			section.mergeMove(e);
		}
	}
}

xf.Xform.prototype.mouseUp = function(e) {
	if (!this.request) {
		return;
	}

	if (this.mode == 'EDIT') {
		this.proxy.hide();

		var target = xf.getTarget(e);
		var section = this.findSection(e);
		if (section) {
			if (this.request.type == 'add') {
				section.addField(this.request, target);
			} else if (this.request.type == 'move') {
				section.moveTo(this.request.field, target);
			}
		}
		this.request = null;
	} else if (this.mode == 'MERGE') {
		var section = this.findSection(e);
		if (section) {
			section.mergeEnd(e);
		}
		this.request = null;
	}
}

xf.Xform.prototype.findSection = function(e) {
	var target = xf.getTarget(e);
	var parent = target;
	while (true) {
		if (parent.className != null && parent.className.indexOf('xf-section') != -1) {
			for (var i = 0; i < this.sections.length; i++) {
				var section = this.sections[i];
				if (section.id == parent.id) {
					return section;
				}
			}
		}

		parent = parent.parentNode;
		if (!parent) {
			return null;
		}
	}
}

xf.Xform.prototype.addRow = function() {
	var section = this.sections[1];
	section.addRow();
}

xf.Xform.prototype.removeRow = function() {
	var section = this.sections[1];
	section.removeRow();
}

xf.Xform.prototype.render = function() {
	for (var i = 0; i < this.sections.length; i++) {
		var section = this.sections[i];
		section.render();
	}
}

xf.Xform.prototype.doExport = function() {
	var text = '{"name":"' + this.name + '","code":"' + this.code + '","sections":[';
	for (var i = 0; i < this.sections.length; i++) {
		var sectionText = this.sections[i].doExport();
		text += sectionText;
		if (i != this.sections.length - 1) {
			text += ',';
		}
	}
	text += ']}';
	return text;
}

xf.Xform.prototype.doImport = function(text) {
	var o = eval('(' + text + ')');

	this.name = o.name;
	this.code = o.code;

	xf.$(this.id).innerHTML = '';
	this.sections = [];

	for (var i = 0; i < o.sections.length; i++) {
		var sectionData = o.sections[i];
		switch (sectionData.type) {
		case 'text':
			var section = new xf.TextSection(sectionData.tag, sectionData.text);
			xform.addSection(section);
			break;
		case 'grid':
			var section = new xf.GridSection(sectionData.row, sectionData.col);
			xform.addSection(section);
			section.doImport(sectionData);
			break;
		}
	}

	this.render();
}

xf.Xform.prototype.doMerge = function() {
	for (var i = 0; i < this.sections.length; i++) {
		this.sections[i].merge();
	}
}

xf.Xform.prototype.doSplit = function() {
	for (var i = 0; i < this.sections.length; i++) {
		this.sections[i].split();
	}
}

xf.Xform.prototype.setValue = function(data) {
	for (var i = 0; i < this.sections.length; i++) {
		var section = this.sections[i];
		section.setValue(data);
	}
}

;

xf.TextSection = function(tag, text) {
	this.tag = tag;
	this.text = text;
}

xf.TextSection.prototype.render = function() {	
	var el = document.createElement('div');
	el.id = this.id;
	el.className = 'xf-section';
	el.innerHTML = '<' + this.tag + ' style="text-align:center;">'
		+ this.text
		+ '</' + this.tag + '>';

	var xformEl = xf.$(this.xform.id);

	xformEl.appendChild(el);
}

xf.TextSection.prototype.addField = function(request, target) {
}

xf.TextSection.prototype.doExport = function() {
	return '{"type":"text","tag":"' + this.tag + '","text":"' + this.text + '"}';
}

xf.TextSection.prototype.selectSomething = function(e) {
	this.xform.selectionListener.select(this);
}

xf.TextSection.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('text', this.text, this.updateText, this, formNode);
}

xf.TextSection.prototype.updateText = function(text) {
	this.text = text;
	var el = xf.$(this.id);
	el.innerHTML = '<' + this.tag + ' style="text-align:center;">'
		+ this.text
		+ '</' + this.tag + '>';
}

xf.TextSection.prototype.mergeStart = function(e) {
}

xf.TextSection.prototype.mergeMove = function(e) {
}

xf.TextSection.prototype.mergeEnd = function(e) {
}

xf.TextSection.prototype.merge = function() {
}

xf.TextSection.prototype.split = function() {
}

xf.TextSection.prototype.setValue = function(data) {
}

;

xf.GridSection = function(row, col) {
	this.row = row;
	this.col = col;
	this.fieldMap = {};
	this.mergeMap = {};

	this.selectedItems = [];
	this.startCell = [];
	this.endCell = [];
}

xf.GridSection.prototype.findCell = function(el) {
	while (el) {
		if (el.className && el.className.indexOf('xf-cell') != -1) {
			return el;
		}
		el = el.parentNode;
	}
	return false;
}

xf.GridSection.prototype.merge = function() {
	if (this.selectedItems.length == 0) {
		return false;
	}
	var minRow = this.minRow;
	var minCol = this.minCol;
	var maxRow = this.maxRow;
	var maxCol = this.maxCol;

	var startId = this.id + '-' + minRow + '-' + minCol;
	var el = xf.$(startId);
	if (!el) {
		return false;
	}

	var colSpan = el.colSpan + maxCol - minCol;
	var rowSpan = el.rowSpan + maxRow - minRow;
	el.setAttribute("colspan", colSpan);
	el.setAttribute("rowspan", rowSpan);
	el.setAttribute("width", (100 * colSpan / this.col) + '%');
	var els = this.selectedItems.slice(1);
	for (var i = 0; i < els.length; i++) {
		var el = els[i];
		el.parentNode.removeChild(el);
	}

	this.mergeMap[minRow + '-' + minCol] = {
		minRow: minRow,
		minCol: minCol,
		maxRow: maxRow,
		maxCol: maxCol
	};

	this.selectedItems = [];
	var tds = document.getElementsByTagName('TD');
	for (var i = 0; i < tds.length; i++) {
		xf.removeClass(tds[i], 'active');
	}
}

xf.GridSection.prototype.split = function() {
	if (this.selectedItems.length == 0) {
		return false;
	}
	var el = this.selectedItems[0];
	var position = this.getPosition(el);
	var minRow = position.row;
	var minCol = position.col;

	if (position.width == 1 && position.height == 1) {
		return false;
	}
	el.setAttribute("colspan", 1);
	el.setAttribute("rowspan", 1);
	el.setAttribute("width", (100 / this.col) + '%');

	for (var i = 0; i < position.width; i++) {

		for (var j = 0; j < position.height; j++) {
			if (i == 0 && j == 0) {
				continue;
			}
			var targetRow = position.row + j;
			var targetCol = position.col + i;

			var td = document.createElement('td');
			td.setAttribute("width", (100 / this.col) + '%');
			td.innerHTML = '<div>&nbsp;</div>';
			td.id = this.id + '-' + targetRow + '-' + targetCol;

			td.className = 'xf-cell xf-cell-right xf-cell-bottom';
			if (targetCol == 0) {
				td.className += ' xf-cell-left';
			}
			if (targetRow == 0) {
				td.className += ' xf-cell-top';
			}

			var preTd = xf.$(this.id + '-' + targetRow + '-' + (targetCol - 1));

			if (preTd == null) {
				var tr = xf.$(this.id + '-' + targetRow);
				if (tr.children.length == 0) {
					tr.appendChild(td);
				} else {
					tr.insertBefore(td, tr.firstChild);
				}
			} else {
				xf.insertAfter(td, preTd);
			}
		}
	}

	delete this.mergeMap[minRow + '-' + minCol];

	this.selectedItems = [];
	var tds = document.getElementsByTagName('TD');
	for (var i = 0; i < tds.length; i++) {
		xf.removeClass(tds[i], 'active');
	}
}

xf.GridSection.prototype.render = function() {
	// table
	var el = document.createElement('div');
	el.id = this.id;
	el.className = 'xf-section';

	var html = '<table class="xf-table" cellspacing="0" cellpadding="0" width="90%" align="center" border="0">'
		+ '<tbody>';

	for (var i = 0; i < this.row; i++) {
		var rowId = this.id + '-' + i;

		html += '<tr id="' + rowId + '">'
		for (var j = 0; j < this.col; j++) {
			var cellId = rowId + '-' + j;
			var cellClassName = 'xf-cell-right xf-cell-bottom';
			if (i == 0) {
				cellClassName += ' xf-cell-top';
			}
			if (j == 0) {
				cellClassName += ' xf-cell-left';
			}
			html += '<td id="' + cellId + '" class="' + cellClassName + '" width="' + (100 / this.col) + '%">&nbsp;'
				+ '</td>';
		}
		html += '</tr>';
	}

	html += '</tbody>'
		+ '</table>';

	el.innerHTML = html;

	var xformEl = xf.$(this.xform.id);
	xformEl.appendChild(el);

	// field
	for (var key in this.fieldMap) {
		var field = this.fieldMap[key];
		field.render();
	}

	// merge
	for (var key in this.mergeMap) {
		var info = this.mergeMap[key];
		this.minRow = info.minRow;
		this.minCol = info.minCol;
		this.maxRow = info.maxRow;
		this.maxCol = info.maxCol;
		for (var i = this.minRow; i <= this.maxRow; i++) {
			for (var j = this.minCol; j <= this.maxCol; j++) {
				var node = xf.$(this.id + '-' + i + '-' + j);
				this.selectedItems.push(node);
			}
		}
		this.merge();
	}
}

xf.GridSection.prototype.addField = function(request, target) {
	var el = this.findCell(target);
	if (el) {
		var fieldFactory = this.xform.fieldFactory;
		var field = fieldFactory.create(request.fieldType, el);
		field.render();
		this.fieldMap[el.id] = field;
	}
}

xf.GridSection.prototype.doExport = function() {
	var text = '{"type":"grid","row":"' + this.row + '","col":"' + this.col + '","merge":[';

	var mergeExists = false;
	for (var key in this.mergeMap) {
		mergeExists = true;

		var startId = key;
		var mergeInfo = this.mergeMap[startId];
		text += '{"startId":"' + startId
			+ '","minRow":' + mergeInfo.minRow
			+ ',"minCol":' + mergeInfo.minCol
			+ ',"maxRow":' + mergeInfo.maxRow
			+ ',"maxCol":' + mergeInfo.maxCol
			+ '},';
	}
	if (mergeExists) {
		text = text.substring(0, text.length - 1);
	}

	var fieldExists = false;
	text += '],"fields":[';
	for (var key in this.fieldMap) {
		fieldExists = true;

		var fieldId = key;
		var fieldValue = this.fieldMap[fieldId];
		text += fieldValue.doExport() + ',';
	}
	if (fieldExists) {
		text = text.substring(0, text.length - 1);
	}

	text += ']}';
	return text;
}

xf.GridSection.prototype.addRow = function() {
	var tbody = this.findTbody();
	var tr = document.createElement('tr');
	tr.id = this.id + '-' + this.row;
	tbody.appendChild(tr);
	for (var i = 0; i < this.col; i++) {
		var td = document.createElement('td');
		td.id = tr.id + '-' + i;
		td.className = 'xf-cell-right xf-cell-bottom';
		if (i == 0) {
			td.className += ' xf-cell-left';
		}
		td.width = (100 / this.col) + '%'
		td.innerHTML = '&nbsp;';
		tr.appendChild(td);
	}
	this.row++;
}

xf.GridSection.prototype.removeRow = function() {
	if (this.selectedItems.length == 0) {
		return false;
	}

	var td = this.selectedItems[0];
	var tr = td.parentNode;
	var tbody = tr.parentNode;

	var rowIndex = 0;
	
	for (var i = 0; i < tbody.children.length; i++) {
		var child = tbody.children[i];
		if (child == tr) {
			rowIndex = i;
			break;
		}
	}

	try {
		if (tbody.children[0] == tr) {
			for (var i = 0; i < tbody.children[1].children.length; i++) {
				var td = tbody.children[1].children[i];
				td.className += ' xf-cell-top';
			}
		}
	} catch(e) {
	}

	try {
		if (tbody.children[tbody.children.length - 1] == tr) {
			for (var i = 0; i < tbody.children[tbody.children.length - 2].children.length; i++) {
				var td = tbody.children[tbody.children.length - 2].children[i];
				td.className += ' xf-cell-bottom';
			}
		}
	} catch(e) {
	}

	tr.parentNode.removeChild(tr);

	for (var i = rowIndex; i < this.row - 1; i++) {
		var targetRowElement = tbody.children[i];
		var array = targetRowElement.getAttribute("id").split("-");
		var prefix = array[0] + '-' + array[1] + '-';
		targetRowElement.setAttribute("id", prefix + rowIndex);
		for (var j = 0; j < this.col; j++) {
			var targetColElement = targetRowElement.children[j];
			targetColElement.setAttribute("id", prefix + rowIndex + "-" + j);
		}
	}

	for (var key in this.fieldMap) {
		var field = this.fieldMap[key];
		if (field.row == rowIndex) {
			delete this.fieldMap[key];
			continue;
		}
		if (field.row > rowIndex) {
			field.row -= 1;
		}
	}

	this.row--;

	this.selectedItems = [];

};

xf.GridSection.prototype.findTbody = function() {
	var el = xf.$(this.id);

	for (var i = 0; i < el.childNodes.length; i++) {
		var childNode = el.childNodes[i];
		if (childNode.tagName == 'TABLE') {
			el = childNode;
		}
	}

	for (var i = 0; i < el.childNodes.length; i++) {
		var childNode = el.childNodes[i];
		if (childNode.tagName == 'TBODY') {
			el = childNode;
		}
	}
	return el;
}

xf.GridSection.prototype.selectSomething = function(e) {
	var target = xf.getTarget(e);
	var cell = this.findCell(target);
	var field = this.fieldMap[cell.id];
	this.xform.selectionListener.select(field);
}

xf.GridSection.prototype.doImport = function(sectionData) {
	this.fieldMap = {};
	
	for (var i = 0; i < sectionData.fields.length; i++) {
		var fieldData = sectionData.fields[i];
		var field = this.xform.fieldFactory.create(fieldData.type);
		field.parentId = this.id + '-' + fieldData.row + '-' + fieldData.col;
		for (var key in fieldData) {
			field[key] = fieldData[key];
		}
		this.fieldMap[field.parentId] = field;
	}

	this.mergeMap = {};
	if (sectionData.merge) {
		for (var i = 0; i < sectionData.merge.length; i++) {
			var mergeData = sectionData.merge[i];
			this.mergeMap[mergeData.startId] = mergeData;
		}
	}
}

xf.GridSection.prototype.getPosition = function(el) {
	if (!el.id) {
		return false;
	}
	var array = el.id.split('-');
	var p = {};
	p.row = parseInt(array[2]);
	p.col = parseInt(array[3]);
	p.width = parseInt(el.colSpan);
	if (p.width == 0) {
		p.width = 1;
	}
	p.height = parseInt(el.rowSpan);
	if (p.height == 0) {
		p.height = 1;
	}
	return p;
}

xf.GridSection.prototype.mergeStart = function(e) {
	var target = xf.getTarget(e);
	var cell = this.findCell(target);

	if (!cell) {
		return;
	}

	for (var i = 0; i < this.selectedItems.length; i++) {
		xf.removeClass(this.selectedItems[i], 'active');
	}
	xf.addClass(cell, 'active');

	this.status = 'DRAG';
	this.startCell = cell;
	this.selectedItems.push(cell);
}

xf.GridSection.prototype.mergeMove = function(e) {

	if (this.status == 'DRAG') {
		var target = xf.getTarget(e);
		var cell = this.findCell(target);

		if (!cell) {
			return;
		}
		var endCell = cell;
		var startCell = this.startCell;

		var startPosition = this.getPosition(startCell);
		var endPosition = this.getPosition(endCell);

		if (startCell.id == endCell.id) {
			return;
		}

		var minRow = Math.min(startPosition.row, endPosition.row);
		var minCol = Math.min(startPosition.col, endPosition.col);
		var maxRow = Math.max(startPosition.row, endPosition.row);
		var maxCol = Math.max(startPosition.col, endPosition.col);

		this.selectedItems = [];
		this.minRow = minRow;
		this.minCol = minCol;
		this.maxRow = maxRow;
		this.maxCol = maxCol;
		for (var i = minRow; i <= maxRow; i++) {
			for (var j = minCol; j <= maxCol; j++) {
				var el = xf.$(this.id + '-' + i + '-' + j);
				xf.addClass(el, 'active');
				this.selectedItems.push(el);
			}
		}

	}
}

xf.GridSection.prototype.mergeEnd = function(e) {
	if (this.status == 'DRAG') {
		this.status = 'DROP';
	}
}

xf.GridSection.prototype.findField = function(target) {
	var cellEl = this.findCell(target);
	return this.fieldMap[cellEl.id];
}

xf.GridSection.prototype.moveTo = function(field, target) {
	var cellEl = this.findCell(target);
	var position = this.getPosition(cellEl);

	if (field.row == position.row && field.col == position.col) {
		return;
	}

	var fieldId = this.id + '-' + field.row + '-' + field.col;
	
	delete this.fieldMap[fieldId];
	xf.$(field.parentId).innerHTML = '';

	var row = position.row;
	var col = position.col;
	field.row = row;
	field.col = col;

	var fieldId = this.id + '-' + field.row + '-' + field.col;
	this.fieldMap[this.id + '-' + field.row + '-' + field.col] = field;

	field.parentId = cellEl.id;
	field.render();
}

xf.GridSection.prototype.setValue = function(data) {
	for (var key in this.fieldMap) {
		var field = this.fieldMap[key];
		var value = data[field.name];
		if (value) {
			field.setValue(value);
		}
	}
}

;

xf.Proxy = function() {
	this.id = 'xf-proxy';
	this.status = 'uninitialized';
	this.init();
}

xf.Proxy.prototype.init = function() {
	if (this.status == 'uninitialized') {
		var el = document.createElement('div');
		el.id = this.id;
		el.innerHTML = '&nbsp;';
		el.style.position = 'absolute';
		el.style.top = -100 + 'px';
		el.style.left = -100 + 'px';
		el.style.zIndex = 10000;
		el.style.width = '50px';
		el.style.backgroundColor = '#DDDDDD';
		el.style.border = 'dotted 1px gray';
		document.body.appendChild(el);

		this.status = 'initialized';
	}
}

xf.Proxy.prototype.move = function(x, y) {
	var el = xf.$(this.id);
	el.style.top = y + 'px';
	el.style.left = x + 'px';
}

xf.Proxy.prototype.hide = function() {
	this.move(-100, -100);
}



;

xf.field.FieldFactory = function() {
	this.fieldTypeMap = {
		label: xf.field.Label,
		textfield: xf.field.TextField,
		password: xf.field.Password,
		textarea: xf.field.TextArea,
		select: xf.field.Select,
		radio: xf.field.Radio,
		checkbox: xf.field.Checkbox,
		fileupload: xf.field.FileUpload,
		datepicker: xf.field.DatePicker,
		userpicker: xf.field.UserPicker
	};
}

xf.field.FieldFactory.prototype.create = function(type, parentNode) {
	var constructor = this.fieldTypeMap[type];
	var field = new constructor(parentNode);
	return field;
}

;

xf.field.Label = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'label-' + this.row + '-' + this.col; 
	this.text = "text";
}

xf.field.Label.prototype.render = function() {
	this.updateText(this.text);
}

xf.field.Label.prototype.doExport = function() {
	return '{"type":"label","row":' + this.row
		+ ',"col":' + this.col
		+ ',"text":"' + this.text
		+ '"}';
}

xf.field.Label.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('text', this.text, this.updateText, this, formNode);
}

xf.field.Label.prototype.updateText = function(text) {
	this.text = text;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		+ '<label style="display:block;text-align:right;margin-bottom:0px;">' + this.text + '</label>'
		+ '</div>';
}

;

xf.field.TextField = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'textfield-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.TextField.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.TextField.prototype.doExport = function() {
	return '{"type":"textfield","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.TextField.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.TextField.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		
		+ '<input type="text" name="' + this.name + '" ' + (this.readOnly ? 'readOnly' : '')
			+ ' value="' + (this.value ? this.value : '') + '"'
			+ (this.required ? ' required="true" class="required"' : '')
			+ ' style="margin-bottom:0px;" maxlength="200">'
		
		+ '</div>';
}

xf.field.TextField.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.TextField.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.TextField.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.Password = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'password-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.Password.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.Password.prototype.doExport = function() {
	return '{"type":"password","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.Password.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.Password.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		+ '<input type="password" name="' + this.name + '" '
		+ (this.readOnly ? 'readOnly' : '')
		+ (this.required ? ' required="true" class="required"' : '')
		+ ' style="margin-bottom:0px;" maxlength="200">'
		+ '</div>';
}

xf.field.Password.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.Password.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.Password.prototype.setValue = function(value) {
}

;

xf.field.TextArea = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'textarea-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.TextArea.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.TextArea.prototype.doExport = function() {
	return '{"type":"textarea","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.TextArea.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.TextArea.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		+ '<textarea name="' + this.name + '" ' + (this.readOnly ? 'readOnly' : '')
		+ (this.required ? ' required="true" class="required"' : '')
		+ ' style="margin-bottom:0px;" maxlength="200">' + (this.value ? this.value : '') + '</textarea>'
		+ '</div>';
}

xf.field.TextArea.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.TextArea.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.TextArea.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.Select = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'select-' + this.row + '-' + this.col;
	this.items = '';
	this.required = false;
	this.readOnly = false;
}

xf.field.Select.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.Select.prototype.doExport = function() {
	return '{"type":"select","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","items":"' + this.items
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.Select.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createField('items', this.items, this.updateItems, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.Select.prototype.updateName = function(value) {
	this.name = value;
	this.updateItems(this.items);
}

xf.field.Select.prototype.updateItems = function(value) {
	this.items = value;
	var parentNode = xf.$(this.parentId);
	var html = 
		'<div class="xf-handler">'
		+ '<select name="' + this.name + '" ' + (this.readOnly ? 'disabled' : '')
		+ (this.required ? ' required="true" class="required"' : '') + ' style="margin-bottom:0px;">';
	var array = this.items.split(',');
	for (var i = 0; i < array.length; i++) {
		var item = array[i];
		html += '<option value="' + item + '" ' + (this.value == item ? 'selected' : '') + '>' + item + '</option>';
	}
	html += '</select>'
		+ '</div>';
	parentNode.innerHTML = html;
}

xf.field.Select.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.Select.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.Select.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.Radio = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'radio-' + this.row + '-' + this.col;
	this.items = '';
	this.required = false;
	this.readOnly = false;
}

xf.field.Radio.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.Radio.prototype.doExport = function() {
	return '{"type":"radio","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","items":"' + this.items
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.Radio.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createField('items', this.items, this.updateItems, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.Radio.prototype.updateName = function(value) {
	this.name = value;
	this.updateItems(this.items);
}

xf.field.Radio.prototype.updateItems = function(value) {
	this.items = value;
	var parentNode = xf.$(this.parentId);
	var html = '<div class="xf-handler">';
	var array = this.items.split(',');
	for (var i = 0; i < array.length; i++) {
		var item = array[i];
		html += '<label class="radio inline">';
		html += '<input type="radio" name="' + this.name + '" value="' + item + '" '
			+ (this.readOnly ? 'readOnly' : '') + ' '
			+ (this.value == item ? 'checked' : '') + ' '
			+ (this.required ? ' required="true" class="required"' : '') + ' style="margin:1px;">';
		html += item;
		html += '</label>';
		html += '<label for="' + this.name + '" class="validate-error" generated="true" style="display:none;"></label>';
	}
	parentNode.innerHTML = html + '</div>';
}

xf.field.Radio.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.Radio.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.Radio.prototype.setValue = function(value) {
	this.value = value;
	this.updateItems(this.items);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.Checkbox = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'checkbox-' + this.row + '-' + this.col;
	this.items = '';
	this.required = false;
	this.readOnly = false;
}

xf.field.Checkbox.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.Checkbox.prototype.doExport = function() {
	return '{"type":"checkbox","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","items":"' + this.items
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.Checkbox.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createField('items', this.items, this.updateItems, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.Checkbox.prototype.updateName = function(value) {
	this.name = value;
	this.updateItems(this.items);
}

xf.field.Checkbox.prototype.updateItems = function(value) {
	this.items = value;
	var parentNode = xf.$(this.parentId);
	var html = '<div class="xf-handler">';
	var array = this.items.split(',');

	var valueArray = [];
	if (this.value != null) {
		valueArray = this.value.split(',');
	}

	for (var i = 0; i < array.length; i++) {
		var item = array[i];
		html += '<label class="checkbox inline">';
		html += '<input type="checkbox" name="' + this.name + '" value="' + item + '" '
			+ (this.readOnly ? 'readOnly' : '') + ' '
			+ (this.value == item ? 'checked' : '') + ' '
			+ (this.required ? ' required="true" class="required"' : '') + ' style="margin:1px;">';
		html += item;
		html += '</label>';
		html += '<label for="' + this.name + '" class="validate-error" generated="true" style="display:none;"></label>';
	}
	parentNode.innerHTML = html + '</div>';
}

xf.field.Checkbox.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.Checkbox.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.Checkbox.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.FileUpload = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'fileupload-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.FileUpload.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.FileUpload.prototype.doExport = function() {
	return '{"type":"fileupload","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.FileUpload.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.FileUpload.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		+ '<input type="file" name="' + this.name + '" ' + (this.readOnly ? 'readOnly' : '') + '>'
		+ '</div>';
}

xf.field.FileUpload.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.FileUpload.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.FileUpload.prototype.setValue = function(value) {
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler"><a href="../rs/store/view?model=form&key=' + value.key + '">' + value.label + '</a></div>';
	} else {
		var parentNode = xf.$(this.parentId);
		if (parentNode.children.length == 1) {
			var span = document.createElement('span');
			span.innerHTML = '<div class="xf-handler"><a href="../rs/store/view?model=form&key=' + value.key + '">' + value.label + '</a></div>';
			parentNode.appendChild(span);
		} else {
			var span = parentNode.children[1];
			span.innerHTML = '<div class="xf-handler"><a href="../rs/store/view?model=form&key=' + value.key + '">' + value.label + '</a></div>';
		}
	}
}

;

xf.field.DatePicker = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'datepicker-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.DatePicker.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.DatePicker.prototype.doExport = function() {
	return '{"type":"datepicker","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.DatePicker.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.DatePicker.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
	    + '<div style="padding-left: 0px;margin-bottom:0px;" class="input-append datepicker date">'
	    + '<input type="text" name="' + this.name + '" style="background-color:white;cursor:default; width: 175px;" '
		+ (this.readOnly ? 'readOnly' : '') + ' value="' + (this.value ? this.value : '') + '"'
		+ (this.required ? ' required="true" class="required"' : '') + '>'
	    + '<span style="padding-top: 2px; padding-bottom: 2px;" class="add-on"><i class="icon-calendar"></i></span>'
	    + '<label for="' + this.name + '" class="validate-error" generated="true" style="display:none;"></label>'
	    + '</div>'
		+ '</div>';
}

xf.field.DatePicker.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.DatePicker.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.DatePicker.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}

;

xf.field.UserPicker = function(parentNode) {
	if (!parentNode) {
		return;
	}
	var parentId = parentNode.id;
	var array = parentId.split('-');

	this.parentId = parentId;
	this.row = array[2];
	this.col = array[3];
	this.name = 'userpicker-' + this.row + '-' + this.col;
	this.required = false;
	this.readOnly = false;
}

xf.field.UserPicker.prototype.render = function() {
	this.updateName(this.name);
}

xf.field.UserPicker.prototype.doExport = function() {
	return '{"type":"userpicker","row":' + this.row
		+ ',"col":' + this.col
		+ ',"name":"' + this.name
		+ '","required":' + this.required
		+ ',"readOnly":' + this.readOnly
		+ '}';
}

xf.field.UserPicker.prototype.viewForm = function(formNode) {
	formNode.innerHTML = '';
	xf.createField('name', this.name, this.updateName, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('required', this.required, this.updateRequired, this, formNode);
	formNode.appendChild(document.createElement('br'));
	xf.createBooleanField('readOnly', this.readOnly, this.updateReadOnly, this, formNode);
}

xf.field.UserPicker.prototype.updateName = function(value) {
	this.name = value;
	var parentNode = xf.$(this.parentId);
	parentNode.innerHTML = 
		'<div class="xf-handler">'
		+'	<div class="input-append userPicker" style="padding:0px;margin:0px;">'
		+'      <input type="hidden" name="' + this.name + '" class="input-medium" value="">'
		+'      <input type="text" name="' + this.name + '_name" class="input-medium" value=""'
		+ (this.required ? ' required="true" class="required"' : '') + ' style="width:175px;">'
		+'      <span class="add-on" style="height:20px;padding:2px 5px;"><i class="icon-user"></i></span>'
		+'	</div>'
		+ '</div>';
}

xf.field.UserPicker.prototype.updateRequired = function(value) {
	this.required = value;
}

xf.field.UserPicker.prototype.updateReadOnly = function(value) {
	this.readOnly = value;
}

xf.field.UserPicker.prototype.setValue = function(value) {
	this.value = value;
	this.updateName(this.name);
	if (this.readOnly) {
		var parentNode = xf.$(this.parentId);
		parentNode.innerHTML = '<div class="xf-handler">' + value + '</div>';
	}
}
