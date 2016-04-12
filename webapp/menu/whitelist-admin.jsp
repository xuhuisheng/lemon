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
    <div class="panel-heading" role="tab" id="collapse-header-whitelist" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-whitelist" aria-expanded="true" aria-controls="collapse-body-whitelist">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        白名单
      </h4>
    </div>
    <div id="collapse-body-whitelist" class="panel-collapse collapse ${currentMenu == 'whitelist' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-whitelist">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${ctx}/whitelist/whitelist-type-list.do"><i class="glyphicon glyphicon-list"></i> 类型</a></li>
		  <li><a href="${ctx}/whitelist/whitelist-app-list.do"><i class="glyphicon glyphicon-list"></i> 应用</a></li>
		  <li><a href="${ctx}/whitelist/whitelist-host-list.do"><i class="glyphicon glyphicon-list"></i> 域名</a></li>
		  <li><a href="${ctx}/whitelist/whitelist-ip-list.do"><i class="glyphicon glyphicon-list"></i> IP</a></li>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

