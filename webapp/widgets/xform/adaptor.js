var xform;

window.onload = function() {
	xform = new xf.Xform('xf-form');
	xform.addSection(new xf.TextSection('h1', 'title'));
	xform.addSection(new xf.GridSection(2, 4));

	xform.initEvents();

	xform.selectionListener = {
		select: function(field) {
			if (!field) {
				return;
			}
			var el = xf.$('xf-form-attribute');
			field.viewForm(el);
		}
	};

	xform.render();

	if ($('#__gef_content__').val() != '') {
		try {
			xform.doImport($('#__gef_content__').val());
		} catch(e) {
			console.error(e);
			xform.sections = [];
			xform.addSection(new xf.TextSection('h1', 'title'));
			xform.addSection(new xf.GridSection(2, 4));
			xform.render();
		}
	}
}

function doImport() {
	var value = prompt('text', '{"sections":[{"type":"text","tag":"h1",text:"title"},{"type":"grid","row":"2",col:"4","fields":[{"type":"label","row":0,"col":0,"text":"11111111111"},{"type":"textfield","row":0,"col":1,"name":"test","required":true}]}]}');
	if (value != '') {
		xform.doImport(value);
	}
}

function doChangeMode(button) {
	if (xform.mode == 'EDIT') {
		xform.mode = 'MERGE';
		button.innerHTML = 'change to edit mode';
	} else {
		xform.mode = 'EDIT';
		button.innerHTML = 'change to merge mode';
	}
}

function doMerge() {
	if (xform.mode == 'MERGE') {
		xform.doMerge();
	}
}

function doSplit() {
	if (xform.mode == 'MERGE') {
		xform.doSplit();
	}
}

function doSave() {
	$('#__gef_name__').val(xform.sections[0].text);
	$('#__gef_content__').val(xform.doExport());
	$('#f').submit();
}
