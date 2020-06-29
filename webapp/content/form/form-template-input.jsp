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
	<link href="${cdnPrefix}/public/mossle-xform/0.0.11/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-xform/0.0.11/xform-all.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-xform/0.0.11/adaptor.js"></script>

	<style type="text/css">
.xf-palette {
	border: dotted 2px gray;
	width: 45%;
	margin: 5px;
	padding: 5px;
	padding-left: 10px;
	text-align: left;
	background-color: #F8F8F8;
	float: left;
}

.xf-palette img {
	float: right;
}

.xf-table td {
	height: 45px;
}

.tab-pane {
	margin-right: 20px;
}
	</style>
  </head>

  <body>
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">

	<!-- start of main -->
      <section id="m-main" class="col-md-12" style="padding-top:65px;">

	  <div id="__gef_container__" style="padding-left:5px;">
	    <div id="__gef_palette__" style="float:left;width:260px;">
		  <ul class="nav nav-tabs" id="myTab">
            <li class="active"><a href="#operation" data-toggle="tab">控件</a></li>
			<li><a href="#form" data-toggle="tab">控件属性</a></li>
			<li><a href="#prop" data-toggle="tab">表单属性</a></li>
		  </ul> 
		  <div class="tab-content">
			<div class="tab-pane active" id="operation">
			  <div style="padding-top:5px;">
				<div class="xf-palette" title="label">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_label.png">
				  标签
				</div>
				<div class="xf-palette" title="textfield">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_input.png">
				  单行文本
				</div>
				<div class="xf-palette" title="password">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_secret.png">
				  密码
				</div>
				<div class="xf-palette" title="textarea">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_textarea.png">
				  多行文本
				</div>
				<div class="xf-palette" title="radio">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_item.png">
				  单选
				</div>
				<div class="xf-palette" title="select">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_select.png">
				  下拉选择
				</div>
				<div class="xf-palette" title="checkbox">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_itemset.png">
				  多选
				</div>
				<div class="xf-palette" title="fileupload">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_upload.png">
				  文件上传
				</div>
				<div class="xf-palette" title="datepicker">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_range.png">
				  日期
				</div>
				<div class="xf-palette" title="datetimepicker">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_range.png">
				  日期时间
				</div>

				<div class="xf-palette" title="userpicker">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/userpicker.png">
				  选择用户
				</div>

				<div class="xf-palette" title="departmentpicker">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/userpicker.png">
				  选择部门
				</div>

				<div style="clear:both"></div>

				<div style="border-top:2px dotted gray;margin-top:5px;margin-bottom:5px;margin-left:5px;margin-right:10px;"></div>
				<!--
				<div class="xf-palette" title="columnSection2">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.13/images/xform/new_label.png">
				  二列布局
				</div>
				<div class="xf-palette" title="columnSection3">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.13/images/xform/new_label.png">
				  三列布局
				</div>
				-->
				<div class="xf-palette" title="tableSection">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_label.png">
				  表格布局
				</div>
				<div class="xf-palette" title="gridSection">
				  <img src="${cdnPrefix}/public/mossle-xform/0.0.11/images/xform/new_label.png">
				  子表布局
				</div>
			  </div>
			</div>
			<div class="tab-pane" id="form">
			  <div class="panel panel-default" style="display:block;position:relative;">
				<div class="panel-heading">控件属性</div>
				<div class="panel-body">
				  <div id="xf-form-attribute" class="controls"></div>
				</div>
			  </div>
			</div>
			<div class="tab-pane" id="prop">
			  <div class="panel panel-default" style="display:block;position:relative;">
				<div class="panel-heading">表单属性</div>
				<div class="panel-body">
				  <div id="xf-form-attribute" class="controls">
				    <label>
					  名称
				      <input id="xFormName" type="text" class="form-control">
					</label>
				    <label>
					  标识
				      <input id="xFormCode" type="text" class="form-control">
                    </label>
				  </div>
				</div>
			  </div>
			</div>
		  </div>
	    </div>

		<div class="__gef_center__">
		<div id="__gef_toolbar__">
		  <div class="btn-group">
			<button class="btn btn-default" onclick="doSave()">保存</button>
<!--
			<button class="btn btn-default" onclick="alert(xform.doExport())">export</button>
			<button class="btn btn-default" onclick="doImport()">import</button>
-->
			<button class="btn btn-default" onclick="xform.addRow()">添加行</button>
			<button class="btn btn-default" onclick="xform.removeRow()">删除行</button>
			<button class="btn btn-default" onclick="doChangeMode(this)">切换为合并模式</button>
			<button class="btn btn-default" onclick="doMerge()" id="mergeCell" style="display:none;">合并单元格</button>
			<button class="btn btn-default" onclick="doSplit()" id="splitCell" style="display:none;">拆分单元格</button>
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

