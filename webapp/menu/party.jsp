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
        应用
      </h4>
    </div>
    <div id="collapse-body-scope" class="panel-collapse collapse ${currentMenu == 'scope' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/scope/scope-info-list.do"><i class="glyphicon glyphicon-list"></i> 应用</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user-admin" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-scpoe" aria-expanded="true" aria-controls="collapse-body-user-admin">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        账号体系
      </h4>
    </div>
    <div id="collapse-body-user-admin" class="panel-collapse collapse ${currentMenu == 'user-admin' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/user/user-repo-list.do"><i class="glyphicon glyphicon-list"></i> 账号体系</a></li>
        </ul>
      </div>
    </div>
  </div>
--%>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-party" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-party" aria-expanded="true" aria-controls="collapse-body-party">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        组织机构
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
        模板
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
        附件
      </h4>
    </div>
    <div id="collapse-body-store" class="panel-collapse collapse ${currentMenu == 'store' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-store">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/store/store-info-list.do"><i class="glyphicon glyphicon-list"></i> 附件</a></li>
        </ul>
      </div>
    </div>
  </div>

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
		  <li><a href="${tenantPrefix}/whitelist/whitelist-admin-list.do"><i class="glyphicon glyphicon-list"></i> 白名单</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-dict" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-dict" aria-expanded="true" aria-controls="collapse-body-dict">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        数据字典
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
    <div class="panel-heading" role="tab" id="collapse-header-sequence" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-sequence" aria-expanded="true" aria-controls="collapse-body-sequence">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        流水号
      </h4>
    </div>
    <div id="collapse-body-sequence" class="panel-collapse collapse ${currentMenu == 'sequence' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-sequence">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/sequence/sequence-info-list.do"><i class="glyphicon glyphicon-list"></i> 流水号</a></li>
        </ul>
      </div>
    </div>
  </div>

<!--
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-link" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-link" aria-expanded="true" aria-controls="collapse-body-link">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        短链
      </h4>
    </div>
    <div id="collapse-body-link" class="panel-collapse collapse ${currentMenu == 'link' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-link">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/link/link-info-list.do"><i class="glyphicon glyphicon-list"></i> 短链</a></li>
        </ul>
      </div>
    </div>
  </div>
-->

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

