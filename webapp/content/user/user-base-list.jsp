<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'userGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_username': '${param.filter_LIKES_username}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'userGridForm',
	exportUrl: 'user-base-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
    <script>
    var MouseEvent = function(e){
		this.x = e.clientX;
		this.y = e.clientY;
	}
    var Mouse = function(e){
		var kdheight = jQuery(document).scrollTop();
		mouse = new MouseEvent(e);
		leftpos = mouse.x - 200;
		var aTop = mouse.y;
		var aBottom = $(window).height() - mouse.y;
		if(aBottom > 240){
			toppos = mouse.y + kdheight;
		} else if(aTop >240){
			toppos = mouse.y +kdheight -240;
		} else {
			toppos = kdheight;
		}
	}
    var fix = false;
    	$(function(){
    		
    		jQuery(".e1").hover(
    			function(e){
    				fix = false;
    				Mouse(e);
    				var $cell = $(e.target).closest("tr");
    				var $childrens = $cell.children();
    				var $id = $childrens.eq(1);
    				
    				var src = "${scopePrefix}/user/qrcode.do?id="+$.trim($id.text());
        			$("#qrimg").attr("src",src);
        			$("#erweima").css({ top:toppos,left:leftpos }).fadeIn(100);
    			},function(){
    				if(!fix){
    				$("#erweima").hide();
    				}
    			})
    		
    			jQuery(".e1").click(function(){
    				fix = true;
    			})
    			jQuery("#closeImg").click(function(){
    				$("#erweima").hide();
    			})
    	})
    	
    	
    </script>
    <style type="text/css">
    	#erweima{
    		display:none;
    		left:1px;
    		top:1px;
    		width:190px;
    		color:#000000;
    		background:#ffffff;
    		position:absolute;
    		z-index:9999;
    		border:1px solid gray;
    		margin:0 auto;
    		min-height:160px;
    	}
    	#qrimg
    	{
    	width:170px;
    	height:150px;
    	padding:0 10px 2px 10px;
    	display:block;
    	}
    	#closeImg
    	{
    	width:15px;
    	height:15px;
    	padding:2px 2px 2px 180px;;
    	}
    	#divEwm p
    	{
    		margin:0;
    		padding-top:4px;
    		display:block;
    		padding-bottom:10px;
    	}
    </style>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="userSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userSearch" class="content content-inner">

		  <form name="userForm" method="post" action="user-base-list.do" class="form-inline">
		    <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
		    <input type="text" id="user_username" name="filter_LIKES_username" value="${param.filter_LIKES_username}">
		    <label for="user_enabled"><spring:message code='user.user.list.search.status' text='状态'/>:</label>
		    <select id="user_enabled" name="filter_EQI_status" class="input-mini">
			  <option value=""></option>
			  <option value="1" ${param.filter_EQI_status == 1 ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.true' text='启用'/></option>
			  <option value="0" ${param.filter_EQI_status == 0 ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="user:create">
		  <button class="btn btn-small a-insert" onclick="location.href='user-base-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="user:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
		</div>

	    <div class="m-clear"></div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.list.title" text="用户列表"/></h4>
		</header>
		<div class="content">

<form id="userGridForm" name="userGridForm" method='post' action="user-base-remove.do" class="m-form-blank">
  <table id="userGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
        <th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
        <th class="sorting" name="nickName">显示名</th>
        <th class="sorting" name="status"><spring:message code="user.user.list.status" text="状态"/></th>
        <th width="80">&nbsp;</th>
        <th>名片</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.username}</td>
        <td>${item.nickName}</td>
        <td>${item.status == 1 ? '启用' : '禁用'}</td>
        <td>
          <a href="user-avatar-input.do?id=${item.id}">头像</a>
          <a href="user-base-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
        <td><img class="e1" title="单击二维码固定" style="cursor:pointer;width:15px;height:15px;" src="${scopePrefix}/widgets/xform/images/visualpharm/th.png"></td>
      </tr>
      </c:forEach>
    </tbody>
    <div id="erweima">
    <span id="closeImg" style="cursor:pointer">X</span>
     <img id="qrimg" src=""/>
     <p style='text-align:center;'>
     	<font size="4pt" style="font-weight:bold;">二维码名片</font>
     	<br/>
     	<font size="2pt">（"扫一扫"如使用微信）</font>
     </p>
 
    </div>
  </table>
</form>
        </div>
      </article>

	  <article>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
