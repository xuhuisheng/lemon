
var decorator = {
	layoutContainer: function(width, height) {
		$('#__gef_container__').width(width);
		$('#__gef_container__').height(height);
	},
	layoutPalette: function(width, height) {
		$('#__gef_palette__').width(width);
		$('#__gef_palette__').height(height);
	},
	layoutProperty: function(width, height) {
		$('#__gef_property__').width(width);
		$('#__gef_property__').height(height);
	},
	layoutCanvas: function(width, height) {
		$('#__gef_canvas__').width(width);
		$('#__gef_canvas__').height(height);
	},
	layoutToolbarBlank: function(width, height) {
		$('#__gef_toolbar_blank__').width(width);
		$('#__gef_toolbar_blank__').height(height);
	},
	getPaletteWidth: function() {
		return $('#__gef_palette__').width();
	},
	getPropertyHeight: function() {
		return 150;
	},
	getToolbarHeight: function() {
		return $('#__gef_toolbar__').height();
	}
}

function doContainerLayout(containerWidth, containerHeight, decorator) {
	decorator.layoutContainer(
		containerWidth,
		containerHeight
	);
	decorator.layoutPalette(
		decorator.getPaletteWidth(),
		containerHeight - decorator.getToolbarHeight() - decorator.getPropertyHeight()
	);
	decorator.layoutProperty(
		containerWidth,
		decorator.getPropertyHeight()
	);
	decorator.layoutCanvas(
		containerWidth - decorator.getPaletteWidth(),
		containerHeight - decorator.getToolbarHeight() - decorator.getPropertyHeight()
	);
	decorator.layoutToolbarBlank(
		decorator.getPaletteWidth(),
		decorator.getToolbarHeight()
	);
}
