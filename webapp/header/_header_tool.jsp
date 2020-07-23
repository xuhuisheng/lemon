<%@ page language="java" pageEncoding="UTF-8" %>

      <li>
          <form class="navbar-form navbar-search" action="${ctx}/pim/address-list-list.do" role="search">
            <div class="form-group">
              <input type="text" class="form-control search-query" placeholder="搜索" name="username">
            </div>
          </form>
      </li>
      <li>
        <a href="${tenantPrefix}/content/oa/index.jsp">
          <i class="glyphicon glyphicon-cloud"></i>
          应用
        </a>
      </li>
    
      <tags:menuSystem3/>

      <li class="dropdown">
        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
          <i class="glyphicon glyphicon-download-alt"></i>
          Android
          <b class="caret"></b>
        </a>
        <ul class="dropdown-menu">
          <li class="text-center"><img src="${ctx}/common/android-app.png" style="margin:0px;"></li>
        </ul>
      </li>
      <li class="dropdown">
        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
          <i class="glyphicon glyphicon-bell"></i>
          <i class="badge" id="msg-unread-count" style="display:none;"></i>
          <b class="caret" id="msg-unread-caret"></b>
        </a>
        <ul class="dropdown-menu" id="msg-unread-content">
          <li><a href="${ctx}/msg/msg-info-listReceived.do">更多消息</a></li>
        </ul>
      </li>
      <li class="dropdown">
        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
          <img src="${ctx}/avatar/api/<tags:currentUsername/>?width=32" style="width:32px;height:32px;margin-top:-16px;margin-bottom:-16px;" class="img-circle">
          <tags:currentUser/>
          <b class="caret"></b>
        </a>
        <ul class="dropdown-menu">
          <li class="text-center">&nbsp;<img src="${ctx}/avatar/api/<tags:currentUsername/>?width=64" style="width:64px;height:64px;" class="img-rounded"></li>
          <li><a href="${ctx}/user/my/my-info-input.do"><i class="glyphicon glyphicon-list"></i> 个人信息</a></li>
          <li class="divider"></li>
          <li><a href="${ctx}/j_spring_security_logout"><i class="glyphicon glyphicon-list"></i> 退出</a></li>
        </ul>
      </li>


