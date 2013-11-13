
Gef.ns('App.form');

App.form.UserTaskForm = Gef.extend(App.form.AbstractForm, {
    decorate: function(model) {
        this.clearItem();
        this.render(model);
    },

	render: function(model) {
try{
		var html = "UserTask"
			+"<div class='modal-body'>"
				+"<label class='span1'>name:</label>"
				+"<input id='f_usertask_name' type='text' name='name' value='" + model.dom.getAttribute('name') + "' class='span2'>"
				+"<br>"
				+"<label class='span1'>assignee:</label>";
		if (window.userDatas) {
			html += "<select id='f_assignee_name' class='span2'><option value=''></option>";
			for (var i = 0; i < window.userDatas.length; i++) {
				var item = window.userDatas[i];
				html += "<option value='" + item + "'";
				if ($.trim(model.assignee) == item) {
					html += " selected";
				}
				html +=">" + item + "</option>";
			}
			html += "</select>";
		} else {
			html += "<input id='f_assignee_name' type='text' name='name' value='" + $.trim(model.assignee) + "' class='span2'>";
		}
		html += "<br>";
		html += "<label class='span1'>participant:</label>";
		if (window.jobDatas) {
			html += "<select id='f_participant_name' class='span2'><option value=''></option>";
			for (var i = 0; i < window.jobDatas.length; i++) {
				var item = window.jobDatas[i];
				html += "<option value='" + item + "'";
				if ($.trim(model.participant) == item) {
					html += " selected";
				}
				html +=">" + item + "</option>";
			}
			html += "</select>";
		} else {
			html += "<input id='f_participant_name' type='text' name='name' value='" + $.trim(model.participant) + "' class='span2'>";
		}
		html += "<br>";
		html += "<label class='span1'>form:</label>";
		if (window.formDatas) {
			html += "<select id='f_usertask_form' class='span2'><option value=''></option>";
			for (var i = 0; i < window.formDatas.length; i++) {
				var item = window.formDatas[i];
				html += "<option value='" + item + "'";
				if ($.trim(model.dom.getAttribute('activiti:formKey')) == item) {
					html += " selected";
				}
				html +=">" + item + "</option>";
			}
			html += "</select>";
		} else {
			html += "<input id='f_usertask_form' type='text' name='name' value='" + $.trim(model.dom.getAttribute('activiti:formKey')) + "' class='span2'>";
		}

		html += "</div>";

		$('#__gef_property__').html(html);

		$('#f_usertask_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.dom.setAttribute('name', newValue);
			model.editPart.figure.updateAndShowText(newValue);
		});

		$('#f_assignee_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.assignee = newValue;
			if (newValue != '') {
				model.dom
					.getOrCreateElement('humanPerformer')
					.getOrCreateElement('resourceAssignmentExpression')
					.setElementContent('formalExpression', newValue);
			} else {
				var dom = model.dom;
				dom.removeElement(dom.getOrCreateElement('humanPerformer'));
			}
		});

		$('#f_participant_name').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.participant = newValue;
			if (newValue != null) {
				model.dom
					.getOrCreateElement('potentialOwner')
					.getOrCreateElement('resourceAssignmentExpression')
					.setElementContent('formalExpression', newValue);
			} else {
				var dom = model.dom;
				dom.removeElement(dom.getOrCreateElement('potentialOwner'));
			}
		});

		$('#f_usertask_form').blur(function() {
			var newValue = this.value;
			// FIXME: use command
			model.dom.setAttribute('activiti:formKey', newValue);
		});
}catch(e) {
	console.error(e);
}
	},

	dispose: function(modal) {
	}
});