<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "form");%>
<%pageContext.setAttribute("currentMenu", "form");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>designer</title>
    <%@include file="/common/s3.jsp"%>
	<link href="${tenantPrefix}/widgets/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${tenantPrefix}/widgets/xform3/xform-all.js"></script>
    <script type="text/javascript" src="${tenantPrefix}/widgets/xform3/adaptor.js"></script>
  </head>

  <body>
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/form.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

	  <div id="__gef_container__" style="padding-left:5px;">
	    <div id="__gef_palette__" style="float:left;width:260px;">
		  <ul class="nav nav-tabs" id="myTab">
            <li class="active"><a href="#operation" data-toggle="tab">操作</a></li>
			<li><a href="#form" data-toggle="tab">表单</a></li>
			<li><a href="#prop" data-toggle="tab">属性</a></li>
		  </ul> 
		  <div class="tab-content">
			<div class="tab-pane active" id="operation">
			  <div>
				<div class="xf-pallete" title="label">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_label.png">
				  label
				</div>
				<div class="xf-pallete" title="textfield">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_input.png">
				  textfield
				</div>
				<div class="xf-pallete" title="password">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_secret.png">
				  password
				</div>
				<div class="xf-pallete" title="textarea">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_textarea.png">
				  textarea
				</div>
				<div class="xf-pallete" title="select">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_select.png">
				  select
				</div>
				<div class="xf-pallete" title="radio">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_item.png">
				  radio
				</div>
				<div class="xf-pallete" title="checkbox">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_itemset.png">
				  checkbox
				</div>
				<div class="xf-pallete" title="fileupload">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_upload.png">
				  fileupload
				</div>
				<div class="xf-pallete" title="datepicker">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/new_range.png">
				  datepicker
				</div>
				<div class="xf-pallete" title="userpicker">
				  <img src="${tenantPrefix}/widgets/xform/images/xform/userpicker.png">
				  userpicker
				</div>
			  </div>
			</div>
			<div class="tab-pane" id="form">
			  <div class="popover" style="display:block;position:relative;">
				<h3 class="popover-title">title</h3>
				<div class="popover-content">
				  <div id="xf-form-attribute" class="controls"></div>
				</div>
			  </div>
			</div>
			<div class="tab-pane" id="prop">
			  <div class="popover" style="display:block;position:relative;">
				<h3 class="popover-title">属性</h3>
				<div class="popover-content">
				  <div id="xf-form-attribute" class="controls">
				    <label>
					  名称
				      <input id="xFormName" type="text">
					</label>
				    <label>
					  标识
				      <input id="xFormCode" type="text">
                    </label>
				  </div>
				</div>
			  </div>
			</div>
		  </div>
	    </div>

		<div class="__gef_center__">
		<div id="__gef_toolbar__">
		  <div style="width:50px;float:left;">&nbsp;</div>
		  <div class="btn-group">
			<button class="btn" onclick="doSave()">save</button>
<!--
			<button class="btn" onclick="alert(xform.doExport())">export</button>
			<button class="btn" onclick="doImport()">import</button>
-->
			<button class="btn" onclick="xform.addRow()">add row</button>
			<button class="btn" onclick="xform.removeRow()">remove row</button>
			<button class="btn" onclick="doChangeMode(this)">change to merge mode</button>
			<button class="btn" onclick="doMerge()">merge</button>
			<button class="btn" onclick="doSplit()">split</button>
		  </div>
		</div>


		<div id="__gef_canvas__" style="overflow:auto;">
		  <div id="xf-center" class="xf-center" unselectable="on">
			<div id="xf-layer-form" class="xf-layer-form">
			  <form id="xf-form" action="#" method="post" class="controls">
			  </form>
			</div>
			<div id="xf-layer-mask" class="xf-layer-mask">
			</div>
		  </div>
		</div>
	  </div>

		</div>

    </div>
	<!-- end of main -->
	</div>

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	<c:if test="${model != null}">
	  <input id="__gef_id__" name="id" value="${model.id}">
    </c:if>
	  <input id="__gef_name__" name="name" value="${model.name}">
	  <input id="__gef_code__" name="code" value="${model.code}">
	  <textarea id="__gef_content__" name="content">${model.content}</textarea>
	</form>
	</div>
  </body>

</html>

