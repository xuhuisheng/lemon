
Note = function(id, onCreate, onUpdateContent, onUpdatePosition) {
	this.id = id;
	this.onCreate = onCreate;
	this.onUpdateContent = onUpdateContent;
	this.onUpdatePosition = onUpdatePosition;

	this.init();
};

Note.prototype.init = function() {
	var self = this;


	this.draggingItem = null;
	this.editMode = false;

	var draggingItem = this.draggingItem;
	var editMode = this.editMode;

	$(document).delegate('.note-item', 'mousedown', function(event) {
		if (editMode) {
			return;
		}

		draggingItem = $(this);
		draggingItem.css('cursor', 'move');
		var position = draggingItem.position();
		draggingItem.data('noteOffsetX', event.clientX - position.left);
		draggingItem.data('noteOffsetY', event.clientY - position.top);
	});

	$(document).delegate('', 'mousemove', function(event) {
		if (editMode) {
			return;
		}
		if (draggingItem == null) {
			return;
		}
		draggingItem.css('position', 'absolute');
		draggingItem.css({
			left: event.clientX - draggingItem.data('noteOffsetX'),
			top: event.clientY - draggingItem.data('noteOffsetY')
		});
	});

	$(document).delegate('', 'mouseup', function(event) {
		if (editMode) {
			return;
		}
		if (draggingItem == null) {
			return;
		}

		if (self.onUpdatePosition) {
			var newId = draggingItem.data('id');
			var clientX = draggingItem.position().left;
			var clientY = draggingItem.position().top;
			self.onUpdatePosition(newId, clientX, clientY);
		}

		draggingItem.css('cursor', 'default');
		draggingItem = null;
	});

	$(document).delegate('.note-item', 'dblclick', function(event) {
		if (editMode) {
			return;
		}
		var panelBody = $(this).find('.panel-body');
		var notePre = panelBody.find('.note-pre');
		panelBody.append("<textarea class='note-textarea'>" + notePre.text() + "</textarea><button class='note-button'>保存</button>");
		$('.note-textarea').focus();
		editMode = true;
	});

	$(document).delegate('.note-item .panel-body .note-button', 'click', function(event) {
		var panelBody = $(this).parent('.panel-body');
		var notePre = panelBody.find('.note-pre');
		notePre.html($('.note-textarea').val());

		$('.note-textarea').remove();
		$(this).remove();
		editMode = false;

		if (self.onUpdateContent) {
			var newId = panelBody.parent('.note-item').data('id');
			var content = notePre.html();
			self.onUpdateContent(newId, content);
		}
	});
};

Note.prototype.addNewNote = function() {
	var sed = "_sed:" + new Date().getTime();

	$('#' + this.id).append('<div class="panel panel-default note-item note-yellow" style="width:150px;height:200px;" id="' + sed + '">'
		+ '<div class="panel-body">'
		+ '<pre class="note-pre">'
		+ '</pre>'
		+ '</div>'
		+ '</div>'
	);

	var self = this;
	if (self.onCreate) {
		self.onCreate(function(newId) {
			$('#' + sed).data('id', newId);
		});
	}
};
