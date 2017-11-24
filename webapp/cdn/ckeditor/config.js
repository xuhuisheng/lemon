/*
Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';

	config.toolbar = 'Custom';

	config.toolbar_Custom =
	[
		{ name: 'document', items : [ 'NewPage' ] },
		{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
		{ name: 'basicstyles', items : [ 'Bold','TextColor','BGColor' ] },
		{ name: 'insert', items : [ 'Link','Unlink','Image','Flash','Table' ] },
		{ name: 'tools', items : [ 'Source','Preview','Maximize' ] }
	];
};
