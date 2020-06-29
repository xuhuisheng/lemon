<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.cms-article.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">

var setting = {
    async: {
        enable: true,
        url: "${tenantPrefix}/cms/rs/template/tree.do"
    },
    callback: {
        onClick: function(event, treeId, treeNode) {
      var url = 'index.do?catalogId=' + treeNode.id;
      if (treeNode.type == 'content') {
        url = 'file-input.do?id=' + treeNode.id + '&catalogId=' + treeNode.categoryId;
      }
            location.href = url;
        }
    }
};

var zNodes = [];

$(function(){
    $.fn.zTree.init($("#treeMenu"), setting, zNodes);
});
    </script>
    <style type="text/css">
      .ztree * {
          font-size: 14px;
      }
    </style>
  </head>

  <body>
    <%@include file="/header/cms.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/cms.jsp"%>

      <div class="col-md-2" style="padding-top:65px;">
        <div class="panel panel-default">
          <div class="panel-heading">
            <i class="glyphicon glyphicon-list"></i>
            目录
          </div>
          <div class="panel-body" style="padding:15px 0px;">
            <ul id="treeMenu" class="ztree"></ul>
          </div>
        </div>
      </div>

      <!-- start of main -->
      <section id="m-main" class="col-md-8" style="padding-top:65px;">

      <div style="margin-bottom: 15px;">

        <div class="btn-group">
          <c:if test="${not empty param.catalogId}">
            <a type="button" class="btn btn-default" href="dir-input.do?catalogId=${param.catalogId}">新建目录</a>
            <a type="button" class="btn btn-default" href="file-input.do?catalogId=${param.catalogId}">新建文件</a>
          </c:if>
        </div>

        <div class="clearfix"></div>
      </div>

<form id="cms-articleGridForm" name="cms-articleGridForm" method='post' action="" class="m-form-blank">
      <div class="panel panel-default">

  <table id="cmsArticleGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting">名称</th>
        <th width="110">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${cmsTemplateCatalogs}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.name}</td>
        <td>
          <a href="dir-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </c:forEach>
      <c:forEach items="${cmsTemplateContents}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.name}</td>
        <td>
          <a href="file-input.do?id=${item.id}&catalogId=${item.cmsTemplateCatalog.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>


      </div>
</form>

      <div>
        <div class="m-page-info pull-left">
          &nbsp;
          总 ${fn:length(cmsTemplateCatalogs) + fn:length(cmsTemplateContents)} 条
        </div>
      </div>

      <div class="m-spacer"></div>

      </section>
      <!-- end of main -->
    </div>

  </body>

</html>

