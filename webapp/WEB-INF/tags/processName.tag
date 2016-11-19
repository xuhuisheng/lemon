<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="org.activiti.engine.ProcessEngine"%>
<%@attribute name="processDefinitionId" type="java.lang.String" required="true"%>
<%
  String processDefinitionId = (String) jspContext.getAttribute("processDefinitionId");
  ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
  ProcessEngine processEngine = ctx.getBean(ProcessEngine.class);
  try {
    String processDefinitionName = processEngine
      .getRepositoryService()
      .createProcessDefinitionQuery()
      .processDefinitionId(processDefinitionId)
      .singleResult()
      .getName();
    out.print(processDefinitionName);
  } catch(Exception ex) {
    System.out.println("cannot find processDefinition : " + processDefinitionId);
  }
%>
