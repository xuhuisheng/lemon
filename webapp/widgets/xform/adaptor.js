
Xf.IMAGE_ROOT = 'scripts/gef/images/activities/48/';

var xform = new Xf.XForm();
xform.init()

var selectedModel = null;

Xf.onReady(function() {
	var target = $('#m-main');
	target.height($(document.body).height() - target.offset().top);
	doContainerLayout(target.width() - 5, target.height() - 5, decorator);

	var targetElement = $('#xf-layer-form');
	$('#xf-layer-mask').offset(targetElement.offset());
	$('#xf-layer-mask').width(targetElement.width());
	$('#xf-layer-mask').height(target.height() - 180);

	var json = $('#__gef_content__').val();
	if (json != '') {
		var data = eval('(' + json + ')');

		xform.model.deserial(data);
	}

	xform.registerListener({
		onSelect: function(field) {
			if (field == null) {
				selectedModel = xform.model;
				var form = new App.form.XformForm();
				form.render(selectedModel);
			} else {
				selectedModel = field;
				switch (field.type) {
					case 'textfield':
						var form = new App.form.TextfieldForm();
						form.render(selectedModel);
						break;
					case 'select':
						var form = new App.form.SelectForm();
						form.render(selectedModel);
						break;
					case 'radio':
						var form = new App.form.RadioForm();
						form.render(selectedModel);
						break;
					case 'checkbox':
						var form = new App.form.CheckboxForm();
						form.render(selectedModel);
						break;
					case 'textarea':
						var form = new App.form.TextareaForm();
						form.render(selectedModel);
						break;
					case 'password':
						var form = new App.form.PasswordForm();
						form.render(selectedModel);
						break;
					case 'fileupload':
						var form = new App.form.FileuploadForm();
						form.render(selectedModel);
						break;
				}
			}
		}
	});
});

function openWindow() {
	var json = prompt();

	var data = eval('(' + json + ')');

	xform.model.changeTemplate(data.template);
	xform.model.template.init();
	xform.model.template.deserial(data);
}

function save() {
	$('#__gef_name__').val(xform.model.title);
	$('#__gef_content__').val(xform.model.serial());
	$('#f').submit();
}
