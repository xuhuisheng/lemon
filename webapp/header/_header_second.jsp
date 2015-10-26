<%@ page language="java" pageEncoding="UTF-8" %>

            <ul class="nav pull-right">
			  <li>
			    <form class="navbar-search" action="${ctx}/pim/address-list-list.do">
                  <input type="text" class="search-query" placeholder="搜索" name="username">
                </form>
			  </li>
			  <tags:menuSystem2/>
              <li class="dropdown">
                <a data-toggle="dropdown" class="dropdown-toggle" href="#">
			      <img src="${tenantPrefix}/rs/avatar?id=<tags:currentUserId/>&width=16" style="width:16px;height:16px;" class="img-circle">
				  <tags:currentUser/>
                  <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                  <li><a href="${ctx}/user/my-info-input.do"><i class="icon-user"></i>个人信息</a></li>
                  <li class="divider"></li>
				  <li><a href="${ctx}/j_spring_security_logout"><i class="icon-user"></i>退出</a></li>
                </ul>
              </li>
			  <li>
                <a href="${tenantPrefix}/msg/msg-info-listReceived.do">
                  <i id="unreadMsg" class="badge"></i>
				</a>
			  </li>
            </ul>