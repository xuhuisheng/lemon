// make sure the $ is pointing to JQuery and not some other library
(function($){

	// add a new method to JQuery
	$.fn.equalHeight = function(defaultHeight) {
		// find the tallest height in the collection
		// that was passed in (.column)
		var tallest = typeof defaultHeight == 'undefined' ? 0 : defaultHeight;

		this.each(function(){
			thisHeight = $(this).height();
			if( thisHeight > tallest)
				tallest = thisHeight;
		});

		// set each items height to use the tallest value found
		this.each(function(){
			if (this.id == 'm-sidebar') {
				$(this).height(tallest + 4);
			} else {
				$(this).height(tallest + 2);
			}
		});
	}
})(jQuery);
