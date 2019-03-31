<%@ page language="java" pageEncoding="UTF-8" %>

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="text-center">
    <div class="btn-group" style="margin-bottom:10px;">
      <a class="btn btn-default ${partyType == 'user' ? 'active' : ''}" href="${tenantPrefix}/party/index.do">人员</a>
      <a class="btn btn-default ${partyType == 'org' ? 'active' : ''}" href="${tenantPrefix}/party/index-org.do">组织</a>
      <!--
      <a class="btn btn-default ${partyType == 'position' ? 'active' : ''}" href="${tenantPrefix}/org/job-list.do">岗位</a>
      -->
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        组织结构
      </h4>
    </div>
    <!--
    <div class="panel-heading" style="padding: 3px 10px 4px 10px">
      <h4 class="panel-title">
        <select onchange="location.href='org-list.do?partyStructTypeId=' + this.value" class="form-control">
          <c:forEach items="${partyStructTypes}" var="item">
          <option value="${item.id}" ${item.id == param.partyStructTypeId ? 'selected' : ''}>${item.name}</option>
          </c:forEach>
        </select>
      </h4>
    </div>
    -->
    <div id="collapse-body-org" class="panel-collapse collapse ${currentMenu == 'org' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-org">
      <div class="panel-body">
	      <ul id="treeMenu" class="ztree"></ul>
      </div>
    </div>
  </div>

  <footer id="m-footer" class="text-center">
    <hr>
	  &copy;Mossle
  </footer>

</div>
      <!-- end of sidebar -->

<script type="text/javascript">
		var setting = {
			async: {
				enable: true,
				url: "${tenantPrefix}/party/rs/tree?partyStructTypeId=${partyStructType.id}"
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					location.href = '${partyBaseUrl}?partyEntityId=' + treeNode.id;
				}
			}
		};

		var zNodes =[];

		$(function(){
			$.fn.zTree.init($("#treeMenu"), setting, zNodes);
		});
</script>
