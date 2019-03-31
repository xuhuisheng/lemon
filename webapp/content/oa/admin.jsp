<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "index");%>
<%pageContext.setAttribute("currentMenu", "index");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.asset-lend.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <style type="text/css">
a:hover {
	text-decoration: none;
}    
    </style>
    <script src="${cdnPrefix}/public/bootstrap-typeahead/4.0.2/bootstrap3-typeahead.min.js"></script>
    <script type="text/javascript">
$(function() {
    var urlMap = {
        '车辆申请': '${ctx}/vehicle/index.do',
        '会议室预定': '${ctx}/meeting/index.do',
        '印章申请': '${ctx}/stamp/index.do',
        '个人资产': '${ctx}/asset/index.do',
        '差旅申请': '${ctx}/travel/index.do',
        '休假申请': '${ctx}/leave/index.do',
        '考勤': '${ctx}/attendance/index.do',
        '报销': '${ctx}/expense/index.do',
        '个人事务': '${ctx}/pim/index.do',
        '网盘': '${ctx}/disk/index.do',
        '项目管理': '${ctx}/plm/index.do',
        '工单': '${ctx}/ticket/index.do'
    };

    var $input = $(".typeahead");
    $input.typeahead({
        source: function(a, process) {
            var urls = [];
            for (var key in urlMap) {
                urls.push(key);
            }
            process(urls);
        },
        updater: function (item) {
            var url = urlMap[item];
            if (!!url) {
                location.href = url;
            }
            return item;
        }
    });
});
    </script>
  </head>

  <body>
    <%@include file="/header/_pim3.jsp"%>

    <div class="container" style="padding-top:65px;">
      <div class="row">
      </div>
    </div>

    <div class="container">
      <div class="row" style="padding-bottom:20px;">
        <div class="col-md-6 col-md-offset-3">
          <div class="input-group">
            <input type="text" class="form-control typeahead" placeholder="Search" data-provide="typeahead">
            <span class="input-group-btn">
              <button class="btn btn-default" type="button"><i class="glyphicon glyphicon-search"></i></button>
            </span>
          </div>
        </div>
      </div>

      <h4>用户</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/user/account-info-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">账号管理</h4>
                  账号管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/auth/user-connector-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">权限管理</h4>
                  权限管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>流程管理</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/bpm/bpm-process-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">流程管理</h4>
                  流程管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>内容管理</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/cms/cms-article-list.do.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">内容管理</h4>
                  内容管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>行政</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/vehicle/vehicle-info-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">车辆管理</h4>
                  车辆管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>人事</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/party/org-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">组织结构</h4>
                  组织结构
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>财务</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/expense/expense-info-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">个人报销</h4>
                  个人报销
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>

      <h4>系统</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/template/template-info-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">模板管理</h4>
                  模板管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/sendmail/sendmail-queue-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">发送邮件</h4>
                  发送邮件
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/store/store-info-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">附件管理</h4>
                  附件管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/dict/dict-type-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">数据字典</h4>
                  数据字典
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/ticket/ticket-catalog-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">工单管理</h4>
                  工单管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/plm/index.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">项目管理</h4>
                  项目管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="${ctx}/report/report-subject-list.do">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">报表管理</h4>
                  报表管理
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

      </div>
    </div>

  </body>

</html>
