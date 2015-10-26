<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.activiti.engine.ProcessEngine"%>
<%@page import="com.mossle.bpm.cmd.SyncProcessCmd"%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	ProcessEngine processEngine = (ProcessEngine) ctx.getBean("processEngine");

	String processDefinitionId = request.getParameter("id");
	processEngine.getManagementService().executeCommand(new SyncProcessCmd(processDefinitionId));
%>
