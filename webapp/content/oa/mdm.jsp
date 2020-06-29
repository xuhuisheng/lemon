<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "index");%>
<%pageContext.setAttribute("currentMenu", "index");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>主数据</title>
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
        '人员': '#',
        '组织架构': '#',
        '空间': '#',
        '工作日历': '#',
        '数据字典': '#',
        '商品': '#',
        '客户': '#',
        '供应商': '#'
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

      <!--
      <div class="row">

        <div class="col-md-3 col-sm-3">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">行政</h3>
            </div>
            <ul class="nav nav-list">
              <li><a href="${ctx}/meeting/index.do">会议室预定</a></li>
              <li><a href="${ctx}/stamp/index.do">印章申请</a></li>
              <li><a href="${ctx}/asset/index.do">个人资产</a></li>
              <li><a href="${ctx}/travel/index.do">差旅申请</a></li>
              <li><a href="${ctx}/vehicle/index.do">车辆申请</a></li>
            </ul>
          </div>
        </div>

        <div class="col-md-3 col-sm-3">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">人事</h3>
            </div>
            <ul class="nav nav-list">
              <li><a href="${ctx}/leave/index.do">休假申请</a></li>
              <li><a href="${ctx}/attendance/index.do">考勤</a></li>
          </div>
        </div>

        <div class="col-md-3 col-sm-3">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">财务</h3>
            </div>
            <ul class="nav nav-list">
              <li><a href="${ctx}/expense/index.do">报销</a></li>
              <!- -
              <li><a href="${ctx}/fee/fee-info-list.do">费用</a></li>
              - ->
            </ul>
          </div>
        </div>

        <div class="col-md-3 col-sm-3">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">服务</h3>
            </div>
            <ul class="nav nav-list">
              <li><a href="${ctx}/pim/index.do">个人事务</a></li>
              <li><a href="${ctx}/disk/index.do">网盘</a></li>
              <li><a href="${ctx}/plm/index.do">项目管理</a></li>
              <li><a href="${ctx}/ticket/index.do">工单</a></li>
              <li><a href="${ctx}/bpm/workspace-home.do">流程审批</a></li>
            </ul>
          </div>
        </div>

      </div>
      -->

      <h4>主数据</h4>

      <div class="row">

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">人员</h4>
                  人员
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">组织架构</h4>
                  组织架构
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">空间</h4>
                  空间
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">工作日历</h4>
                  工作日历
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
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
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">商品</h4>
                  商品
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">客户</h4>
                  客户
                </div>
              </div>
              </a>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="panel panel-default">
            <div class="panel-body">
              <a href="#">
              <div class="media">
                <div class="media-left media-middle">
                  <img class="media-object" src="${cdnPrefix}/public/mossle/0.0.11/logo32.png" alt="">
                </div>
                <div class="media-body">
                  <h4 class="media-heading">供应商</h4>
                  供应商
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
