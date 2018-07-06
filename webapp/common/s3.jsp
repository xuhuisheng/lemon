<%@page language="java" pageEncoding="UTF-8" %>
    <!--[if lt IE 9]>
    <script type="text/javascript">
	//alert('您使用的浏览器版本太低，请使用IE9+，或者FireFox，Chrome浏览。');
	</script>
    <![endif]-->

    <link rel="shortcut icon" type="image/x-icon" href="${cdnPrefix}/public/mossle/0.0.11/favicon.ico" />
    <!-- bootstrap -->
    <link rel='stylesheet' href='${cdnPrefix}/public/bootstrap/3.3.7/css/bootstrap.min.css' type='text/css' media='screen' />
	<style type="text/css">
.navbar-search .search-query {
  -webkit-border-radius: 15px;
     -moz-border-radius: 15px;
          border-radius: 15px;
}

.navbar .navbar-search .search-query {
    border-radius: 15px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) inset, 0 1px 0 rgba(255, 255, 255, 0.15);
    color: gray;
    transition: width 0.3s ease 0s;
    width: 100px;
}

.navbar .navbar-search .search-query:focus {
    transition: width 0.3s ease 0s;
    width: 200px;
}
	</style>

    <!-- html5 -->
    <!--[if lt IE 9]>
	<script src="${cdnPrefix}/respond/1.4.2/respond.min.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/html5/html5shiv.js"></script>
    <![endif]-->

    <!-- jquery -->
    <script type='text/javascript' src='${cdnPrefix}/public/jquery/1.12.4/jquery.min.js'></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-migrate/1.2.1/jquery-migrate.min.js"></script>
	<!-- bootstrap -->
    <script type='text/javascript' src='${cdnPrefix}/public/bootstrap/3.3.7/js/bootstrap.min.js'></script>
	<!-- bootbox -->
    <script type="text/javascript" src="${cdnPrefix}/public/bootbox/3.3.0/bootbox.min.js"></script>

    <!-- message -->
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-sliding-message/0.92/jquery.slidingmessage.min.js"></script>

    <!-- table and pager -->
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-pagination/1.2/pagination.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-table/0.0.20170322/table.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-table/0.0.20170322/messages_${locale}.js"></script>
	<script type="text/javascript" src="${cdnPrefix}/public/mossle/0.0.11/js/table.js"></script>

    <!-- validater -->
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-validation/1.16.0/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-validation/1.16.0/additional-methods.min.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-validation/1.16.0/localization/messages_${locale}.js"></script>
    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/jquery-validation/1.16.0/jquery.validate.css" />

    <!-- datepicker -->
    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.min.css">
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-datepicker/1.6.4/locales/bootstrap-datepicker.${locale}.min.js"></script>
    <link href="${cdnPrefix}/public/bootstrap-datetimepicker/2.4.4/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-datetimepicker/2.4.4/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/bootstrap-datetimepicker/2.4.4/js/locales/bootstrap-datetimepicker.${locale}.js"></script>

	<!-- tree -->
    <link rel="stylesheet" href="${cdnPrefix}/public/ztree/3.5.28/css/zTreeStyle/zTreeStyle.css" type="text/css" />
    <script type="text/javascript" src="${cdnPrefix}/public/ztree/3.5.28/jquery.ztree.all.min.js"></script>

    <!-- ckeditor -->
    <script type="text/javascript" src="${cdnPrefix}/public/ckeditor/4.6.2/ckeditor.js"></script>

	<!-- tablednd -->
    <script type="text/javascript" src="${cdnPrefix}/public/jquery-tablednd/0.9.1/jquery.tablednd.min.js"></script>

    <style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
    </style>
    <script type="text/javascript">
$(function() {
    $.showMessage($('#m-success-message').html(), {
        position: 'top',
        size: '55',
        fontSize: '20px'
    });

    $('.datepicker').datepicker({
		language: '${locale}',
		format: 'yyyy-mm-dd',
        autoclose: true
	});

    $('.datetimepicker').datetimepicker({
		language: '${locale}',
        format: "yyyy-mm-dd hh:ii",
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left"
    });

    function widgetToggleContent() {
        var self = $(this);
        self.toggleClass('glyphicon-chevron-up');
        self.toggleClass('glyphicon-chevron-down');
        var widget = self.parents('.panel');
        var content = widget.find('.panel-body');
        content.toggle(200);
    }

    $(document).delegate('.panel .panel-heading .ctrl .glyphicon-chevron-up', 'click', widgetToggleContent);
    $(document).delegate('.panel .panel-heading .ctrl .glyphicon-chevron-down', 'click', widgetToggleContent);
});
    </script>
