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
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-cms" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-cms" aria-expanded="true" aria-controls="collapse-body-cms">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        文章管理
      </h4>
    </div>
    <div id="collapse-body-cms" class="panel-collapse collapse ${currentMenu == 'cms' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-cms">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="list"><i class="glyphicon glyphicon-list"></i> 文章列表</a></li>
		  <li><a href="input"><i class="glyphicon glyphicon-list"></i> 编辑文章</a></li>
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

