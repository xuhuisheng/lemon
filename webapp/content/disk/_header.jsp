<%@page contentType="text/html;charset=UTF-8"%>
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <a class="navbar-brand" href="${ctx}" style="margin-top:-5px;">
	  <!--
        <img alt="" src="${ctx}/site-logo.png" class="logo-img" style="display:inline;">
		-->
        <strong>网盘</strong>
      </a>
    </div>

    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav" id="navbar-menu">
        <li><a href="${tenantPrefix}/disk/disk-info-list.do">我的文件</a></li>
        <li><a href="${tenantPrefix}/disk/disk-share-list.do">我的分享</a></li>
        <li><a href="${tenantPrefix}/disk/disk-home.do">分享首页</a></li>
		<!--
        <li class="" id="costreimbuse">
          <a id="dropcostreimburse" href="${ctx}/finance/expense/expense-index.do">个人报销</a>
        </li>
        <li class="" id="costreimbuse">
          <a id="dropcostreimburse" href="${ctx}/finance/expense/expense-list.do">报销审批</a>
        </li>

        <li class="dropdown">
          <a id="drop1" href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown">基础信息维护 <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
            <li><a href="${ctx}/finance/expense/expense-catalog-list.do">分类管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-catalog-acl-list.do">分类权限</a></li>
            <li><a href="${ctx}/finance/expense/expense-assist-list.do">部门助理管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-delegation-list.do">代理人管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-limit-list.do">规则管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-company-list.do">公司管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-cost-center-list.do">成本中心管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-currency-list.do">币种管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-destination-list.do">目的地管理</a></li>
            <li><a href="${ctx}/finance/expense/expense-traffic-list.do">交通工具管理</a></li>
          </ul>
        </li>
	  -->
      </ul>

      <ul class="nav navbar-nav navbar-right">
        <li><a href="###"><tags:currentUser/></a></li>
        <li>
          <a href="javascript:formSubmit()">注销</a>
        </li>
      </ul>
    </div>

    <!-- csrt for log out-->
    <form action="${ctx}/j_spring_security_logout" method="post" id="logoutForm">
      <input name="_csrf" value="86faba16-7103-47e3-b41e-b0e7bb79a614" type="hidden">
    </form>

    <script>
       function formSubmit() {
           document.getElementById("logoutForm").submit();
       }
    </script>

  </div>
</div>