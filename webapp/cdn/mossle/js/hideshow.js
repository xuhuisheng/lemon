//  Andy Langton's show/hide/mini-accordion @ http://andylangton.co.uk/jquery-show-hide

// this tells jquery to run the function below once the DOM is ready
$(document).ready(function() {

	// append show/hide links to the element directly preceding the element with a class of "toggle"
	$('.m-toggle').not('.current').prev().append('<a href="#" class="m-toggle-link icon-chevron-down" style="text-decoration:none;">&nbsp;</a>');
	$('.m-toggle.current').prev().append('<a href="#" class="m-toggle-link icon-chevron-up" style="text-decoration:none;">&nbsp;</a>');

	// hide all of the elements with a class of 'toggle'
	$('.m-toggle').not('.current').hide();

	// capture clicks on the toggle links
	$('h3.m-toggle-link').click(function() {

		// change the link text depending on whether the element is shown or hidden
		if ($(this).hasClass('current')) {
			//$(this).toggleClass("icon-chevron-up").toggleClass("icon-chevron-down");
			$(this).next('.m-toggle').slideUp('slow');
			$(this).toggleClass('current');
		}
		else {
			var previousCurrent = $(this).parent().find('.m-toggle-link.current');
			previousCurrent.next('.m-toggle').slideUp('slow');
			previousCurrent.toggleClass('current');

			//$(this).toggleClass("icon-chevron-up").toggleClass("icon-chevron-down");
			$(this).next('.m-toggle').slideDown('slow');
			$(this).toggleClass('current');
		}

		// return false so any link destination is not followed
		return false;

	});
});