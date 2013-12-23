<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "form");%>
<%pageContext.setAttribute("currentMenu", "form");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>designer</title>
    <%@include file="/common/s.jsp"%>
	<link href="${ctx}/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${ctx}/xform/designer-xform-packed.js"></script>
    <script type="text/javascript" src="${ctx}/xform/container-layout.js"></script>
    <script type="text/javascript" src="${ctx}/xform/adaptor.js"></script>
  </head>

  <body>
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/form.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">
	  <div id="__gef_container__" style="padding-left:5px;">
	    <div id="__gef_toolbar__">
		  <div id="__gef_toolbar_blank__" style="float:left;">&nbsp;</div>
		  <div class="btn-group">
		    <button class="btn btn-small" onclick="openWindow()"><img src="${ctx}/gef/images/visualpharm/Properties_16x16.png">导入</button>
		    <button class="btn btn-small" onclick="var json = xform.model.serial();alert(json);"><img src="${ctx}/gef/images/visualpharm/Copy_16x16.png">导出</button>
		    <button class="btn btn-small" onclick="save()"><img src="${ctx}/gef/images/visualpharm/Save-16x16.png">保存</button>
		  </div>
		  <div class="btn-group">
		    <button class="btn btn-small" onclick="xform.model.changeTemplate('oneColumn')"><img src="${ctx}/gef/images/visualpharm/New-16x16.png">一列</button>
		    <button class="btn btn-small" onclick="xform.model.changeTemplate('twoColumn')"><img src="${ctx}/gef/images/visualpharm/New-16x16.png">两列</button>
		    <button class="btn btn-small" onclick="xform.model.changeTemplate('threeColumn')"><img src="${ctx}/gef/images/visualpharm/New-16x16.png">三列</button>
	      </div>
		</div>
	    <div id="__gef_palette__" style="float:left;">
		  <div style="border: 1px solid #CCCCCC; border-radius: 4px;padding: 2px;">
		    <div id="startnone" class="paletteItem-startnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-textfield" src="${ctx}/xform/images/xform/new_input.png" unselectable="on" title="文本">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-radio" src="${ctx}/xform/images/xform/new_item.png" unselectable="on" title="单选">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-checkbox" src="${ctx}/xform/images/xform/new_itemset.png" unselectable="on" title="多选">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-password" src="${ctx}/xform/images/xform/new_secret.png" unselectable="on" title="密码">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-select" src="${ctx}/xform/images/xform/new_select1.png" unselectable="on" title="下拉">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-textarea" src="${ctx}/xform/images/xform/new_textarea.png" unselectable="on" title="多行文本">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img class="xf-fileupload" src="${ctx}/xform/images/xform/new_upload.png" unselectable="on" title="上传">
		    </div>
		  </div>
	    </div>
		<div id="__gef_canvas__" style="float:left;clear:right;overflow:auto;">
		  <div id="xf-center" class="xf-center" unselectable="on">
			<div id="xf-layer-form" class="xf-layer-form">
			  <form id="xf-form" method="post" action="#" class="xf-form">
				<table id="xf-form-table" class="xf-form-table">
				  <thead id="xf-form-table-head"><tr><th>Title</th></tr></thead>
				  <tbody id="xf-form-table-body"><tr><td>Body</td></tr></tbody>
				  <tfoot id="xf-form-table-foot"><tr><td>Footer</td></tr></tfoot>
				</table>
			  </form>
			</div>
			<div id="xf-layer-mask" class="xf-layer-mask">
			</div>
		  </div>
		</div>
	    <div id="__gef_property__" style="clear:left;background-color:white;overflow:auto;">
	    </div>
	  </div>

	  <div id="__gef_form__" class="modal hide" style="width:900px;">
	    <div class="modal-body">
		  <table class="table">
		    <thead>
			  <tr>
			    <th>&nbsp;</th>
			    <th>id</th>
			    <th>name</th>
			    <th>type</th>
			    <th>required</th>
			    <th>writable</th>
			  </tr>
			</thead>
		    <tbody id="__gef_form_tbody__">
			  <tr>
			    <td>&nbsp;</td>
			    <td>&nbsp;</td>
			    <td>&nbsp;</td>
			    <td>&nbsp;</td>
			    <td>&nbsp;</td>
			    <td>&nbsp;</td>
			  </tr>
			</tbody>
		  </table>
	    </div>
	    <div class="modal-footer">
		  <a id="__gef_bpmn2_form_add__" href="#" class="btn">添加一行</a>
		  <a id="__gef_bpmn2_form_close__" href="#" class="btn">关闭</a>
		  <a id="__gef_bpmn2_form_save__" href="#" class="btn btn-primary">保存</a>
	    </div>
	  </div>
      <div id="__gef_menu__">
	    <ul class="dropdown-menu" style="min-width:auto;">
		  <li><a href="#">a</a></li>
		  <li><a href="#">b</a></li>
		</ul>
	  </div>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template!save.do" method="post" style="display:none;">
	<s:if test="model != null">
	  <input id="__gef_id__" name="id" value="${model.id}">
    </s:if>
	  <input id="__gef_name__" name="name" value="${model.name}">
	  <textarea id="__gef_content__" name="content">${model.content}</textarea>
	</form>
	</div>
  </body>

</html>
