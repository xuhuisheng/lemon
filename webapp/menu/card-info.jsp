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
<div class="panel-group col-md-2" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        工卡管理
      </h4>
    </div>
    <ul class="nav nav-list">
      <li><a href="${tenantPrefix}/card/card-info-list.do"><i class="glyphicon glyphicon-list"></i> 工卡管理</a></li>
      <li><a href="${tenantPrefix}/card/card-avatar-list.do"><i class="glyphicon glyphicon-list"></i> 头像管理</a></li>
      <li><a href="${tenantPrefix}/card/door-info-list.do"><i class="glyphicon glyphicon-list"></i> 门禁管理</a></li>
    </ul>
  </div>

        <footer id="m-footer" class="text-center">
          <hr>
          &copy;Mossle
        </footer>

</div>
      <!-- end of sidebar -->

