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
    <div class="panel-heading" role="tab" id="collapse-header-sendmail" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-sendmail" aria-expanded="true" aria-controls="collapse-body-sendmail">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        发送邮件
      </h4>
    </div>
    <div id="collapse-body-sendmail" class="panel-collapse collapse ${currentMenu == 'sendmail' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-sendmail">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/sendmail/sendmail-queue-list.do"><i class="glyphicon glyphicon-list"></i> 邮件队列</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-history-list.do"><i class="glyphicon glyphicon-list"></i> 邮件历史</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-template-list.do"><i class="glyphicon glyphicon-list"></i> 邮件模板</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-app-list.do"><i class="glyphicon glyphicon-list"></i> 邮件应用</a></li>
 		  <li><a href="${tenantPrefix}/sendmail/sendmail-config-list.do"><i class="glyphicon glyphicon-list"></i> 邮件配置</a></li>
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

