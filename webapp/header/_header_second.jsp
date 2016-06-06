<%@ page language="java" pageEncoding="UTF-8" %>

      <ul class="nav navbar-nav navbar-right">
	    <li>
          <form class="navbar-form navbar-search" action="${tenantPrefix}/pim/address-list-list.do" role="search">
            <div class="form-group">
              <input type="text" class="form-control search-query" placeholder="搜索" name="username">
            </div>
          </form>
	    </li>
	  
		<tags:menuSystem3/>

        <li class="dropdown">
          <a data-toggle="dropdown" class="dropdown-toggle" href="#">
		    <img src="${tenantPrefix}/rs/avatar?id=<tags:currentUserId/>&width=16" style="width:16px;height:16px;" class="img-circle">
			<tags:currentUser/>
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu">
		    <li class="text-center">&nbsp;<img src="${tenantPrefix}/rs/avatar?id=<tags:currentUserId/>&width=64" style="width:64px;height:64px;" class="img-rounded"></li>
            <li><a href="${tenantPrefix}/user/my-info-input.do"><i class="glyphicon glyphicon-list"></i> 个人信息</a></li>
            <li class="divider"></li>
			<li><a href="${tenantPrefix}/j_spring_security_logout"><i class="glyphicon glyphicon-list"></i> 退出</a></li>
          </ul>
        </li>
		<li>
          <a href="${tenantPrefix}/msg/msg-info-listReceived.do">
            <i class="glyphicon glyphicon-bell"></i>
			<i id="unreadMsg" class="badge"></i>
	      </a>
		</li>
		<li>
          <a href="javascript:createFavorite();">
            <i class="glyphicon glyphicon-heart"></i>
	      </a>
		</li>

        <script type="text/javascript">
function createFavorite() {
	location.href = '${tenantPrefix}/pim/pim-favorite-input.do?title=' + document.title + '&content=' + encodeURIComponent(location.href);
}
		</script>
      </ul>
