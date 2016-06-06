<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "address-list");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>通讯录</title>
    <%@include file="/common/s3.jsp"%>
	<script type="text/javascript" src="${ctx}/s/jquery.qrcode.min.js"></script>

    <script type="text/javascript">
function utf16to8(str) {  
	var out, i, len, c;  
	out = "";  
	len = str.length;  
	for (i = 0; i < len; i++) {  
		c = str.charCodeAt(i);  
		if ((c >= 0x0001) && (c <= 0x007F)) {  
			out += str.charAt(i);  
		} else if (c > 0x07FF) {  
			out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));  
			out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));  
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));  
		} else {  
			out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));  
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));  
		}  
	}  
	return out;  
}  
function showModal(displayName, mobile, email) {
	$('#modal').modal('show');
	$('#qr').html('');
	$('#qr').qrcode(
		"BEGIN:VCARD\n"
		+ "VERSION:2.1\n"
		+ "FN:" + utf16to8(displayName) + "\n"
		+ "TEL;WORK;VOICE:" + mobile + "\n"
		+ "EMAIL;PREF;INTERNET:" + email+ "\n"
		+ "END:VCARD");

}
    </script>
  </head>

  <body>
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="addressListSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">
    <form name="addressListForm" method="post" action="address-list-list.do" class="form-inline">
	  <label for="addressList_username">账号:</label>
	  <input type="text" id="addressList_username" name="username" value="${param.username}" class="form-control">
	  <button class="btn btn-default a-search" onclick="document.addressListForm.submit()">查询</button>&nbsp;
    </form>
  </div>
</div>

      <div style="margin-bottom: 20px;">
	  
      <div class="panel panel-default">
        <div class="panel-heading">
		  用户列表
		</div>

		<div class="panel-body">

      <c:forEach items="${list}" var="item">

	    <table style="padding-bottom:20px;">
		  <tr>
		    <td width="100" rowspan="2"><img src="${tenantPrefix}/rs/avatar?id=${item.id}&width=80" style="width:80px;height:80px;margin-left:10px;"/></td>
			<td width="20">&nbsp;</td>
			<td width="40" align="right">账号: </td>
			<td width="180">
			  ${item.username}
			  <a href="javascript:void(0);showModal('${item.displayName}', '${item.mobile}', '${item.email}');"><i class="glyphicon glyphicon-qrcode"></i></a>
			</td>
			<td width="20">&nbsp;</td>
			<td align="right">显示名: </td>
			<td>${item.displayName}</td>
		  </tr>
		  <tr>
			<td width="20">&nbsp;</td>
			<td align="right">邮箱: </td>
			<td><a href="mailto:${item.email}">${item.email}</a></td>
			<td width="20">&nbsp;</td>
			<td align="right">电话: </td>
			<td><a href="tel:${item.mobile}">${item.mobile}</a></td>
		  </tr>
		</table>

		<div style="height:20px;"></div>

      </c:forEach>
		</div>

      </div>

      </section>
	  <!-- end of main -->
	</div>

<div class="modal fade" id="modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
      </div>
      <div class="modal-body text-center">
        <div id="qr"></div>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

  </body>

</html>
