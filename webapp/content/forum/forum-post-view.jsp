<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "forum");%>
<%pageContext.setAttribute("currentMenu", "forum");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.cal-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cal-infoGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'cal-infoGridForm',
	exportUrl: 'cal-info-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
  </head>

  <body>
    <%@include file="/header/forum.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/forum.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="cal-info.cal-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">

        <table class="m-table">
            <tbody style="background-color:#FFFEEE;">
                <tr>
                    <td>&nbsp;</td>
                    <td>${forumTopic.title} ${forumTopic.createTime}</td>
                </tr>
                <tr>
                    <td>${forumTopic.userId}</td>
                    <td><pre>${forumTopic.content}</pre></td>
                </tr>
            </tbody>
<c:forEach var="item" items="${forumTopic.forumPosts}" varStatus="status">
            <tbody class="${status.index % 2 != 0 ? 'odd' : 'even'}">
                <tr>
                    <td>${status.index + 1}楼</td>
                    <td>${item.createTime}</td>
                </tr>
                <tr>
                    <td>${item.userId}</td>
                    <td><pre>${item.content}</pre></td>
                </tr>
            </tbody>
</c:forEach>
        </table>
        <br />
        <fieldset>
            <legend>回复</legend>
            <form name="f" action="${scopePrefix}/forum/forum-post-createPost.do?method=post" method="post" onsubmit="return beforeSubmit();">
                <input type="hidden" name="forumTopicId" value="${forumTopic.id}" />
                <table>
                    <tbody>
                        <tr>
                            <td align="right">内容：</td>
                            <td><textarea name="content" cols="15" rows="3"></textarea></td>
                        </tr>
                        <tr>
                            <td align="center" colspan="2">
                                <input type="submit" value="提交" />
                                <input type="reset" value="取消" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </form>
        </fieldset>
        <br />

        </div>
      </article>

      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
