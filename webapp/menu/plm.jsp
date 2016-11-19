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
    <div class="panel-heading" role="tab" id="collapse-header-plm" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-plm" aria-expanded="true" aria-controls="collapse-body-plm">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        项目跟踪
      </h4>
    </div>
    <div id="collapse-body-plm" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-plm">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${scopePrefix}/plm/plm-project-list.do"><i class="glyphicon glyphicon-list"></i> 产品</a></li>
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

