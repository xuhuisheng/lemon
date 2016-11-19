
window.alert = function(){};

$(function () {
	var kanbanCol = $('.panel-body');
	kanbanCol.css('height', (window.innerHeight - 150) + 'px');

	var kanbanColCount = parseInt(kanbanCol.length);
	// $('.container-fluid').css('min-width', (kanbanColCount * 350) + 'px');

	if (logined) {
		draggableInit();
	}

	//$('.panel-heading').click(function() {
	//	var $panelBody = $(this).parent().children('.panel-body');
	//	$panelBody.slideToggle();
	//});
});

function draggableInit() {
	var sourceId;

	$('[draggable=true]').bind('dragstart', function (event) {
		sourceId = $(this).parent().attr('id');
		event.originalEvent.dataTransfer.setData("text/plain", event.target.getAttribute('id'));
	});

	$('.panel-body').bind('dragover', function (event) {
		event.preventDefault();
		return false;
	});

	$('.panel-body').bind('drop', function (event) {
		var children = $(this).children();
		var targetId = children.attr('id');

		if (sourceId != targetId) {
			var elementId = event.originalEvent.dataTransfer.getData("text/plain");

			$('#processing-modal').modal('toggle'); //before post

			$.post('../rs/plm/kanbanChangeStep', {
				issueId: elementId,
				step: targetId
			}, function() {
				var element = document.getElementById(elementId);
				children.prepend(element);
				$('#processing-modal').modal('toggle'); // after post
			});

			/*
			// Post data 
			setTimeout(function () {
				var element = document.getElementById(elementId);
				children.prepend(element);
				$('#processing-modal').modal('toggle'); // after post
			}, 500);
			*/
		}

		event.preventDefault();
		return false;
	});
}


