<%@ page language="java" pageEncoding="UTF-8" %>

      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-org">
              <i class="icon-user"></i>
              <span class="title">组织管理</span>
            </a>
          </div>
          <div id="collapse-org" class="accordion-body collapse ${currentMenu == 'org' ? 'in' : ''}">
		    <select style="width:100%" onchange="location.href='org-list.do?partyStructTypeId=' + this.value">
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${item.id == param.partyStructTypeId ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
			</select>
            <ul id="treeMenu" class="ztree"></ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->

<script type="text/javascript">
		var setting = {
			async: {
				enable: true,
				url: "${scopePrefix}/rs/party/tree?partyStructTypeId=${partyStructType.id}"
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					location.href = '${scopePrefix}/party/org-list.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=' + treeNode.id;
				}
			}
		};

		var zNodes =[];

		$(function(){
			$.fn.zTree.init($("#treeMenu"), setting, zNodes);
		});
</script>
