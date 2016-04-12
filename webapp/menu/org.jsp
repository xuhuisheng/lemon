<%@ page language="java" pageEncoding="UTF-8" %>
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-org" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-org" aria-expanded="true" aria-controls="collapse-body-org">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        组织机构
      </h4>
    </div>
    <div id="collapse-body-org" class="panel-collapse collapse ${currentMenu == 'org' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-org">
      <div class="panel-body">

		    <select style="width:100%" onchange="location.href='org-list.do?partyStructTypeId=' + this.value">
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${item.id == param.partyStructTypeId ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
			</select>
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
				url: "${tenantPrefix}/rs/party/tree?partyStructTypeId=${partyStructType.id}"
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					location.href = '${tenantPrefix}/party/org-list.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=' + treeNode.id;
				}
			}
		};

		var zNodes =[];

		$(function(){
			$.fn.zTree.init($("#treeMenu"), setting, zNodes);
		});
</script>
