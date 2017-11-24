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
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-disk" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-disk" aria-expanded="true" aria-controls="collapse-body-disk">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        &nbsp;
      </h4>
    </div>
    <div id="collapse-body-bpm-process" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="collapse-header-disk">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="disk-info-list.do"><i class="glyphicon glyphicon-list"></i> 全部文件</a></li>
		  <li><a href="disk-share-list.do"><i class="glyphicon glyphicon-list"></i> 我的分享</a></li>
		  <li><a href="disk-info-list.do"><i class="glyphicon glyphicon-list"></i> 回收站</a></li>
        </ul>
      </div>
    </div>
  </div>

</div>

      <!-- end of sidebar -->
