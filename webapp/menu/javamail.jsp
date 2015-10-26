<%@ page language="java" pageEncoding="UTF-8" %>

      <!-- start of sidebar -->
      <aside id="m-sidebar" class="span2" style="height:100%">

        <div class="accordion-group" style="height:100%">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-javamail">
              <i class="icon-user"></i>
              <span class="title">邮箱</span>
            </a>
          </div>
          <div id="collapse-javamail" class="accordion-body collapse ${currentMenu == 'javamail' ? 'in' : ''} full-height" style="overflow:auto;">
		    <button onclick="location.href='create.do'" class="btn">新邮件</button>
		    <button onclick="location.href='config.do'" class="btn">配置</button>
            <ul id="treeMenu" class="ztree"></ul>
          </div>
        </div>

      </aside>
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
