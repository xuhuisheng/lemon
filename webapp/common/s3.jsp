<%@page language="java" pageEncoding="UTF-8" %>
    <link rel="shortcut icon" type="image/x-icon" href="${tenantPrefix}/favicon.ico" />
    <!-- bootstrap -->
    <link rel='stylesheet' href='${tenantPrefix}/s/bootstrap/3.3.5/css/bootstrap.min.css' type='text/css' media='screen' />
	<style type="text/css">
.navbar-search .search-query {
  -webkit-border-radius: 15px;
     -moz-border-radius: 15px;
          border-radius: 15px;
}

.navbar-inverse .navbar-search .search-query {
    background: rgba(35, 43, 48, 0.83) none repeat scroll 0 0;
    border-color: #111111;
    border-radius: 15px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) inset, 0 1px 0 rgba(255, 255, 255, 0.15);
    color: gray;
    transition: width 0.3s ease 0s;
    width: 100px;
}

.navbar-inverse .navbar-search .search-query:focus {
    background: white none repeat scroll 0 0;
    transition: width 0.3s ease 0s;
    width: 150px;
}
	</style>

    <!-- html5 -->
    <!--[if lt IE 9]>
    <script type="text/javascript" src="${ctx}/s/html5/html5shiv.js"></script>
    <![endif]-->

    <!-- jquery -->
    <script type='text/javascript' src='${tenantPrefix}/s/jquery/1.11.3/jquery.min.js'></script>
    <script type="text/javascript" src="${ctx}/s/jquery/jquery-migrate-1.2.1.min.js"></script>
	<!-- bootstrap -->
    <script type='text/javascript' src='${tenantPrefix}/s/bootstrap/3.3.5/js/bootstrap.min.js'></script>

    <!-- message -->
    <script type="text/javascript" src="${ctx}/s/jquery-sliding-message/jquery.slidingmessage.min.js"></script>

    <!-- table and pager -->
    <script type="text/javascript" src="${ctx}/s/pagination/pagination.js"></script>
    <script type="text/javascript" src="${ctx}/s/table/table.js"></script>
    <script type="text/javascript" src="${ctx}/s/table/messages_${locale}.js"></script>

    <!-- validater -->
    <script type="text/javascript" src="${ctx}/s/jquery-validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/jquery-validation/additional-methods.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/jquery-validation/localization/messages_${locale}.js"></script>
    <link type="text/css" rel="stylesheet" href="${ctx}/s/jquery-validation/jquery.validate.css" />

    <!-- datepicker -->
    <link type="text/css" rel="stylesheet" href="${ctx}/s/bootstrap-datepicker/datepicker.css">
    <script type="text/javascript" src="${ctx}/s/bootstrap-datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="${ctx}/s/bootstrap-datepicker/locales/bootstrap-datepicker.${locale}.js"></script>
    <link href="${ctx}/s/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet">
    <script type="text/javascript" src="${ctx}/s/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="${ctx}/s/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.${locale}.js"></script>

	<!-- tree -->
    <link rel="stylesheet" href="${ctx}/s/ztree/zTreeStyle/zTreeStyle.css" type="text/css" />
    <script type="text/javascript" src="${ctx}/s/ztree/jquery.ztree.all-3.5.min.js"></script>

    <!-- ckeditor -->
    <script type="text/javascript" src="${ctx}/s/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${ctx}/s/ckfinder/ckfinder.js"></script>

	<!-- tablednd -->
    <script type="text/javascript" src="${ctx}/s/jquery-tablednd/jquery.tablednd.min.js"></script>

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
});
    </script>
