<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>designer</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript" src="${tenantPrefix}/widgets/gef/designer-bpmn2-packed.js"></script>
    <script type='text/javascript' src='${tenantPrefix}/widgets/gef/container-layout.js'></script>
    <script type='text/javascript' src='${tenantPrefix}/widgets/gef/adaptor.js'></script>
    <script type="text/javascript">
Gef.IMAGE_ROOT = '${tenantPrefix}/widgets/gef/images/activities/48/';
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">
	  <div id="__gef_container__" style="padding-left:5px;">
	    <div id="__gef_toolbar__">
		  <div id="__gef_toolbar_blank__" style="float:left;">&nbsp;</div>
		  <div class="btn-group">
		    <button class="btn btn-small" onclick="Gef.activeEditor.reset()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/New-16x16.png">新建</button>
		    <button class="btn btn-small" onclick="doImport()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Properties_16x16.png">导入</button>
		    <button class="btn btn-small" onclick="doExport()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Copy_16x16.png">导出</button>
		    <button class="btn btn-small" onclick=""><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Save-16x16.png">保存</button>
		    <button class="btn btn-small" onclick="doDeploy()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Stock-Index-Up-16x16.png">发布</button>
		    <button class="btn btn-small" onclick="Gef.activeEditor.clear()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Cancel_16x16.png">清空</button>
		    <button class="btn btn-small" onclick="doUndo()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Undo_16x16.png">撤销</button>
		    <button class="btn btn-small" onclick="doRedo()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Redo_16x16.png">重做</button>
		    <button class="btn btn-small" onclick="doLayout()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Settings_16x16.png">布局</button>
		    <button class="btn btn-small" onclick=""><img src="${tenantPrefix}/widgets/gef/images/visualpharm/Delete_16x16.png">删除</button>
		    <button class="btn btn-small" onclick="alignVertical()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/New-16x16.png">竖直对齐</button>
		    <button class="btn btn-small" onclick="alignHorizontal()"><img src="${tenantPrefix}/widgets/gef/images/visualpharm/New-16x16.png">水平对齐</button>
		  </div>
		</div>
	    <div id="__gef_palette__" style="float:left;">
		  <div style="border: 1px solid #CCCCCC; border-radius: 4px;padding: 2px;">
		    <div id="startnone" class="paletteItem-startnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="startnone-img" class="paletteItem-startnone" src="${tenantPrefix}/widgets/gef/images/activities/32/start-event.png" unselectable="on">
		    </div>
		    <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="endnone-img" class="paletteItem-endnone" src="${tenantPrefix}/widgets/gef/images/activities/32/end-event.png" unselectable="on">
		    </div>
		    <div id="exclusive" class="paletteItem-exclusive" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="exclusive-img" class="paletteItem-exclusive" src="${tenantPrefix}/widgets/gef/images/activities/32/gateway.png" unselectable="on">
		    </div>
		    <div id="taskuser" class="paletteItem-taskuser" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="taskuser-img" class="paletteItem-taskuser" src="${tenantPrefix}/widgets/gef/images/activities/32/task.png" unselectable="on">
		    </div>
		    <div id="subprocess" class="paletteItem-subprocess" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="subprocess-img" class="paletteItem-subprocess" src="${tenantPrefix}/widgets/gef/images/activities/32/sub-process.png" unselectable="on">
		    </div>
		    <div id="pool" class="paletteItem-pool" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="pool-img" class="paletteItem-pool" src="${tenantPrefix}/widgets/gef/images/activities/32/pool.png" unselectable="on">
		    </div>
		    <div id="lane" class="paletteItem-lane" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="lane-img" class="paletteItem-lane" src="${tenantPrefix}/widgets/gef/images/activities/32/lane.png" unselectable="on">
		    </div>
		    <div id="catchtimer" class="paletteItem-catchtimer" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="catchtimer-img" class="paletteItem-catchtimer" src="${tenantPrefix}/widgets/gef/images/activities/32/catching-event.png" unselectable="on">
		    </div>
		    <div id="throwsignal" class="paletteItem-throwsignal" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="throwsignal-img" class="paletteItem-throwsignal" src="${tenantPrefix}/widgets/gef/images/activities/32/throwing-event.png" unselectable="on">
		    </div>
		    <div id="boundarycancel" class="paletteItem-boundarycancel" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		      <img id="boundarycancel-img" class="paletteItem-boundarycancel" src="${tenantPrefix}/widgets/gef/images/activities/32/boundary-event.png" unselectable="on">
		    </div>
		  </div>
	    </div>
		<div id="__gef_canvas_wrapper__" style="float:left;clear:right;overflow:auto;">
		  <div id="__gef_canvas__">
		  </div>
		</div>
	    <div id="__gef_property__" style="clear:left;background-color:white;overflow:auto;border:1px solid black;">
	    </div>
	  </div>

<!--
	  <div id="__gef_bpmn2_palette__" class="modal" style="left:480px;top:100px;width:70px;">
	    <div style="text-align:center;padding:5px;">
		  <button class="btn btn-small" onclick="$('#__gef_bpmn2_palette_content__').toggle();">折叠</button>
		  <button class="btn btn-small" onclick="var xml = Gef.activeEditor.serial();alert(xml);">发布</button>
		  <button class="btn btn-small" onclick="doLayout()">布局</button>
		  <button class="btn btn-small" onclick="doUndo()">撤销</button>
		  <button class="btn btn-small" onclick="doRedo()">重做</button>
		  <button class="btn btn-small" onclick="showForm()">表单</button>
		</div>
	    <div unselectable="on" id="__gef_bpmn2_palette_content__">
		  <div id="startnone" class="paletteItem-startnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		    <img id="startnone-img" class="paletteItem-startnone" src="${tenantPrefix}/widgets/gef/images/bpmn2/large/start_empty.png" unselectable="on">
			<br>start
		  </div>
		  <div id="endnone" class="paletteItem-endnone" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		    <img id="endnone-img" class="paletteItem-endnone" src="${tenantPrefix}/widgets/gef/images/bpmn2/large/end_empty.png" unselectable="on">
			<br>end
		  </div>
		  <div id="exclusive" class="paletteItem-exclusive" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		    <img id="exclusive-img" class="paletteItem-exclusive" src="${tenantPrefix}/widgets/gef/images/bpmn2/large/gateway_exclusive.png" unselectable="on">
			<br>exclusive
		  </div>
		  <div id="parallel" class="paletteItem-parallel" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		    <img id="parallel-img" class="paletteItem-parallel" src="${tenantPrefix}/widgets/gef/images/bpmn2/large/gateway_parallel.png" unselectable="on">
			<br>parallel
		  </div>
		  <div id="taskuser" class="paletteItem-taskuser" style="text-align:center;font-size:12px;cursor:pointer;" unselectable="on">
		    <img id="taskuser-img" class="paletteItem-taskuser" src="${tenantPrefix}/widgets/gef/images/bpmn2/large/task_empty.png" unselectable="on">
			<br>user task
		  </div>
		</div>
	  </div>

	  <div id="__gef_bpmn2_center__">
	  </div>

	  <div id="__gef_bpmn2_parameter__" class="modal" style="top:100px;left:1345px;width:200px;">
      </div>
-->
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
	  <div id="__gef_xml__" class="modal hide" style="width:50%">
	    <div class="modal-header">
		  导入
	    </div>
	    <div class="modal-body">
		  <textarea id="__gef_xml_content__" style="width:95%;height:150px;"></textarea>
	    </div>
	    <div class="modal-footer">
		  <a id="__gef_xml_close__" href="#" class="btn">关闭</a>
		  <a id="__gef_xml_save__" href="#" class="btn btn-primary">导入</a>
	    </div>
	  </div>

    </section>
	<!-- end of main -->
	</div>

    <form id="f" action="console-deploy.do" method="post" style="display:none;">
	  <textarea id="__gef_bpmn2_xml__" name="xml"></textarea>
	</form>

  </body>

</html>
