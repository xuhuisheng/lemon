<%@page contentType="text/html;charset=UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="${ctx}/s/bootstrap/3.3.5/css/bootstrap.css" rel="stylesheet" type="text/css">
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="${ctx}/s/html5/html5shiv.min.js"></script>
    <script src="${ctx}/s/respond/respond.min.js"></script>
    <![endif]-->
    <!--[if lt IE 8]>
    <script>alert("您的IE版本过低，请升级到IE8+或者使用谷歌、火狐浏览器，谢谢。");</script>
    <![endif]-->
    <link rel="shortcut icon" href="${ctx}/favicon.ico">
    <style type="text/css">
html, body {
    font: 12px/1.5 arial,"Microsoft Yahei","Hiragino Sans GB",sans-serif;
    height: 100%;
}

.navbar-nav > li > a {
    font-size: 14px;
}

body {
    padding-top: 50px;
}

.logo-img {
    border-radius: 4px;
    box-shadow: none;
    height: 40px;
    margin-top: -5px;
    width: 40px;
}

.pagination-sm .goto-sm {
    background: #fff none repeat scroll 0 0;
    border: 1px solid #dadada;
    height: 17px;
    padding: 0;
    width: 38px;
}
    </style>
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

    <script type="text/javascript" src="${ctx}/s/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/bootstrap/3.3.5/js/bootstrap.js"></script>

	<!-- tree -->
    <link rel="stylesheet" href="${ctx}/s/ztree/zTreeStyle/zTreeStyle.css" type="text/css" />
    <script type="text/javascript" src="${ctx}/s/ztree/jquery.ztree.all-3.5.min.js"></script>

    <script type="text/javascript" src="${ctx}/s/bootstrap/twitter-bootstrap-hover-dropdown.min.js"></script>
    <script type="text/javascript">
    $(function(){
      $('.dropdown-toggle').dropdownHover();
    });
    </script>
