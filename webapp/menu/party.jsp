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
<%--
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-scope" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-scpoe" aria-expanded="true" aria-controls="collapse-body-scope">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        应用管理
      </h4>
    </div>
    <div id="collapse-body-scope" class="panel-collapse collapse ${currentMenu == 'scope' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/scope/scope-info-list.do"><i class="glyphicon glyphicon-list"></i> 应用管理</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user-admin" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-scpoe" aria-expanded="true" aria-controls="collapse-body-user-admin">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        用户管理
      </h4>
    </div>
    <div id="collapse-body-user-admin" class="panel-collapse collapse ${currentMenu == 'user-admin' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/user/user-repo-list.do"><i class="glyphicon glyphicon-list"></i> 用户库列表</a></li>
        </ul>
      </div>
    </div>
  </div>
--%>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-party" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-party" aria-expanded="true" aria-controls="collapse-body-party">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        组织机构管理
      </h4>
    </div>
    <div id="collapse-body-party" class="panel-collapse collapse ${currentMenu == 'party' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-party">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/party/tree-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.tree" text="组织机构图"/></a></li>
		  <li><a href="${tenantPrefix}/party/party-entity-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.org" text="组织机构"/></a></li>
		  <li><a href="${tenantPrefix}/party/party-struct-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.struct" text="组织机构结构"/></a></li>
		  <li><a href="${tenantPrefix}/party/party-type-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.type" text="组织机构类型"/></a></li>
		  <li><a href="${tenantPrefix}/party/party-struct-type-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.struct.type" text="组织机构结构类型"/></a></li>
		  <li><a href="${tenantPrefix}/party/party-struct-rule-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.struct.rule" text="组织机构结构规则"/></a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-template" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-template" aria-expanded="true" aria-controls="collapse-body-template">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        模板管理
      </h4>
    </div>
    <div id="collapse-body-template" class="panel-collapse collapse ${currentMenu == 'template' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-template">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/template/template-info-list.do"><i class="glyphicon glyphicon-list"></i> 模板管理</a></li>
		  <li><a href="${tenantPrefix}/template/template-field-list.do"><i class="glyphicon glyphicon-list"></i> 模板项管理</a></li>
        </ul>
      </div>
    </div>
  </div>

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
		  <li><a href="${tenantPrefix}/sendmail/sendmail-config-list.do"><i class="glyphicon glyphicon-list"></i> 邮件配置</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-template-list.do"><i class="glyphicon glyphicon-list"></i> 邮件模板</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-queue-list.do"><i class="glyphicon glyphicon-list"></i> 邮件队列</a></li>
		  <li><a href="${tenantPrefix}/sendmail/sendmail-history-list.do"><i class="glyphicon glyphicon-list"></i> 邮件历史</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-sendsms" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-sendsms" aria-expanded="true" aria-controls="collapse-body-sendsms">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        发送短信
      </h4>
    </div>
    <div id="collapse-body-sendsms" class="panel-collapse collapse ${currentMenu == 'sendsms' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-sendsms">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/sendsms/sendsms-config-list.do"><i class="glyphicon glyphicon-list"></i> 短信配置</a></li>
		  <li><a href="${tenantPrefix}/sendsms/sendsms-queue-list.do"><i class="glyphicon glyphicon-list"></i> 短信队列</a></li>
		  <li><a href="${tenantPrefix}/sendsms/sendsms-history-list.do"><i class="glyphicon glyphicon-list"></i> 短信历史</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-audit" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-audit" aria-expanded="true" aria-controls="collapse-body-audit">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        审计日志
      </h4>
    </div>
    <div id="collapse-body-audit" class="panel-collapse collapse ${currentMenu == 'audit' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-audit">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/audit/audit-base-list.do"><i class="glyphicon glyphicon-list"></i> 审计日志</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-store" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-store" aria-expanded="true" aria-controls="collapse-body-store">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        附件管理
      </h4>
    </div>
    <div id="collapse-body-store" class="panel-collapse collapse ${currentMenu == 'store' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-store">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/store/store-info-list.do"><i class="glyphicon glyphicon-list"></i> 附件管理</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-whitelist" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-whitelist" aria-expanded="true" aria-controls="collapse-body-whitelist">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        白名单管理
      </h4>
    </div>
    <div id="collapse-body-whitelist" class="panel-collapse collapse ${currentMenu == 'whitelist' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-whitelist">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/whitelist/whitelist-admin-list.do"><i class="glyphicon glyphicon-list"></i> 白名单管理</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-dict" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-dict" aria-expanded="true" aria-controls="collapse-body-dict">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        数据字典管理
      </h4>
    </div>
    <div id="collapse-body-dict" class="panel-collapse collapse ${currentMenu == 'dict' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-dict">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/dict/dict-type-list.do"><i class="glyphicon glyphicon-list"></i> 数据字典</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-ticket" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-ticket" aria-expanded="true" aria-controls="collapse-body-ticket">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        桌面支持
      </h4>
    </div>
    <div id="collapse-body-ticket" class="panel-collapse collapse ${currentMenu == 'ticket' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/ticket/ticket-catalog-list.do"><i class="glyphicon glyphicon-list"></i> 分类</a></li>
		  <li><a href="${tenantPrefix}/ticket/ticket-group-list.do"><i class="glyphicon glyphicon-list"></i> 小组</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-plm" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-plm" aria-expanded="true" aria-controls="collapse-body-plm">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        项目管理
      </h4>
    </div>
    <div id="collapse-body-plm" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/index.do"><i class="glyphicon glyphicon-list"></i> 项目</a></li>
		  <li><a href="${tenantPrefix}/plm/plm-project-list.do"><i class="glyphicon glyphicon-list"></i> 产品管理</a></li>
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

