<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>


<div class="col-md-2 full-height" style="padding-top:65px;">

  <div class="panel panel-default" style="height:100%">
    <div class="panel-heading" style="padding:2px 15px;">
	  <div class="btn-group" style="width:100%;">
		<button onclick="location.href='create.do'" class="btn btn-default" style="width:50%;">新邮件</button>
		<button onclick="location.href='config.do'" class="btn btn-default" style="width:50%;">配置</button>
	  </div>
    </div>
    <div class="panel-body" style="padding:0px;">
	  <ul id="treeMenu" class="ztree"></ul>
    </div>
  </div>

</div>

      <!-- end of sidebar -->

<script type="text/javascript">
		var setting = {
			async: {
				enable: true,
				url: "${tenantPrefix}/rs/javamail/tree"
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					location.href = '${tenantPrefix}/javamail/index.do?folder=' + treeNode.ref;
				}
			}
		};

		var zNodes = [];

		$(function(){
			$.fn.zTree.init($("#treeMenu"), setting, zNodes);
		});
</script>
