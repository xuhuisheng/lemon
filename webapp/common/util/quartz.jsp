<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.context.support.ClassPathXmlApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="java.net.*"%>
<%@page import="java.text.*"%>
<%@page import="java.util.*"%>
<%@page import="org.quartz.*"%>
<%@page import="org.quartz.impl.matchers.*"%>
<%
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ApplicationContext ctx = null;
    ctx = WebApplicationContextUtils.getWebApplicationContext(application);
    Scheduler scheduler = (Scheduler) ctx.getBeansOfType(Scheduler.class).values().iterator().next();

	String action = request.getParameter("action");

	if ("trigger".equals(action)) {
		//System.out.println(scheduler);
		String jobName = request.getParameter("jobName");
		String jobGroupName = request.getParameter("jobGroupName");
		System.out.println("jobName : " + jobName + ", jobGroupName : " + jobGroupName);
		scheduler.triggerJob(new JobKey(jobName, jobGroupName));

		response.sendRedirect("quartz.jsp");
		return;
	} else if ("standby".equals(action)) {
		scheduler.standby();

		response.sendRedirect("quartz.jsp");
		return;
	} else if ("start".equals(action)) {
		scheduler.start();

		response.sendRedirect("quartz.jsp");
		return;
	}

	pageContext.setAttribute("scheduler", scheduler);
	pageContext.setAttribute("metaData", scheduler.getMetaData());
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>quartz</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
    <script>
function $(id) {
    return document.getElementById(id);
}

function toggle(body) {
    if ($(body).style.display == '') {
        $(body).style.display = 'none';
    } else {
        $(body).style.display = '';
    }
}
    </script>
  </head>
  <body>
	<button onclick='location.href="quartz.jsp"'>refresh</button>
	&nbsp;
	<button onclick='location.href="quartz.jsp?action=${scheduler.inStandbyMode ? "start" : "standby"}"'>${scheduler.inStandbyMode ? 'start' : 'pause'}</button>

	<br>

	<table border="1" onclick="toggle('statusBody')" style="cursor:pointer;">
	  <thead>
		<tr>
		  <th colspan="2">meta data</th>
		</tr>
	  </thead>
	  <tbody id="statusBody" style="display:none;">
		<tr>
		  <th>name</th>
		  <td>${metaData.schedulerName}</td>
		</tr>
		<tr>
		  <th>instance id</th>
		  <td>${metaData.schedulerInstanceId}</td>
		</tr>
		<tr>
		  <th>scheduler class</th>
		  <td>${metaData.schedulerClass.name}</td>
		</tr>
		<tr>
		  <th>version</th>
		  <td>${metaData.version}</td>
		</tr>
		<tr>
		  <th>started</th>
		  <td>${metaData.started}</td>
		</tr>
		<tr>
		  <th>in standby mode</th>
		  <td>${metaData.inStandbyMode}</td>
		</tr>
		<tr>
		  <th>shutdown</th>
		  <td>${metaData.shutdown}</td>
		</tr>
		<tr>
		  <th>remote</th>
		  <td>${metaData.schedulerRemote}</td>
		</tr>
		<tr>
		  <th>running since</th>
		  <td>${metaData.runningSince}</td>
		</tr>
		<tr>
		  <th>number of jobs executed</th>
		  <td>${metaData.numberOfJobsExecuted}</td>
		</tr>
		<tr>
		  <th>job store class</th>
		  <td>${metaData.jobStoreClass.name}</td>
		</tr>
		<tr>
		  <th>job store suppoorts persistence</th>
		  <td>${metaData.jobStoreSupportsPersistence}</td>
		</tr>
		<tr>
		  <th>job store clustered</th>
		  <td>${metaData.jobStoreClustered}</td>
		</tr>
		<tr>
		  <th>thread pool class</th>
		  <td>${metaData.threadPoolClass.name}</td>
		</tr>
		<tr>
		  <th>thread pool size</th>
		  <td>${metaData.threadPoolSize}</td>
		</tr>
	  </tbody>
	</table>

	<br>

	<table border='1' width='100%'>
	  <thead>
		<tr>
		  <th>trigger name</th><th>previous fire time</th><th>start time</th><th>end time</th><th>operation</th>
		</tr>
	  </thead>
	<tbody>
<%
    List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
    for (String triggerGroupName : triggerGroupNames) {
        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
%>
<tr><td><%=trigger.getKey().getName()%></td><td>
<%
            if (trigger.getPreviousFireTime() != null) {
                out.println(dateFormat.format(trigger.getPreviousFireTime()));
            } else {
                out.println("-");
            }
%></td><td>
<%
            if (trigger.getStartTime() != null) {
                out.println(dateFormat.format(trigger.getStartTime()));
            } else {
                out.println("-");
            }
%></td><td>
<%
            if (trigger.getEndTime() != null) {
                out.println(dateFormat.format(trigger.getEndTime()));
            } else {
                out.println("-");
            }
%>
	</td><td>
<a href='quartz.jsp?action=trigger&jobName=<%=URLEncoder.encode(trigger.getJobKey().getName())%>&jobGroupName=<%=URLEncoder.encode(trigger.getJobKey().getGroup())%>'>trigger</a></td></tr>
<%
        }
    }
%>
      </tbody>
    </table>
  </body>
</html>
