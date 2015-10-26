<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.lang.reflect.*"%>
<%@page import="java.text.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.concurrent.*"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor"%>
<%@page import="org.springframework.scheduling.config.ScheduledTaskRegistrar"%>
<%@page import="org.springframework.scheduling.config.CronTask"%>
<%@page import="org.springframework.scheduling.config.TriggerTask"%>
<%@page import="org.springframework.scheduling.config.IntervalTask"%>
<%@page import="org.springframework.scheduling.config.Task"%>
<%@page import="org.springframework.scheduling.support.ScheduledMethodRunnable"%>
<%@page import="org.springframework.scheduling.support.DelegatingErrorHandlingRunnable"%>
<%@page import="org.springframework.scheduling.TaskScheduler"%>
<%!
	Object forceGetField(Object instance, String fieldName) throws Exception {
		return forceGetField(instance.getClass(), instance, fieldName);
	}

	Object forceGetField(Class clz, Object instance, String fieldName) throws Exception {
		Field field = clz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	Runnable findRunnable(Object instance) throws Exception {
		Runnable runnable = null;
		if (instance instanceof FutureTask) {
			Callable callable = null;
			try {
				// jdk6是这样，jdk7不要获得sync，直接反射callable
				Object sync = forceGetField(FutureTask.class, instance, "sync");
				
				callable = (Callable) forceGetField(sync, "callable");
			} catch(Exception ex) {
				// jdk7直接反射callable
				callable = (Callable) forceGetField(FutureTask.class, instance, "callable");
			}
			runnable = (Runnable) forceGetField(callable, "task");
			instance = runnable;
		}
		if (instance instanceof DelegatingErrorHandlingRunnable) {
			runnable = (Runnable) forceGetField(DelegatingErrorHandlingRunnable.class, instance, "delegate");
		}
		return runnable;
	}

	Object findInstance(Runnable runnable) {
		if (runnable instanceof ScheduledMethodRunnable) {
			ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) runnable;
			Method method = scheduledMethodRunnable.getMethod();
            Class clz = method.getDeclaringClass();
			return clz.getCanonicalName() + "." + method.getName();
		} else {
			return runnable;
		}
	}

	Task findTask(ScheduledTaskRegistrar scheduledTaskRegistrar, Runnable runnable) throws Exception {
		List<Task> tasks = null;
		
		tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, "triggerTasks");
		if (tasks != null) {
			for (Task task : tasks) {
				if (task.getRunnable() == runnable) {
					return task;
				}
			}
		}
		
		tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, "cronTasks");
		if (tasks != null) {
			for (Task task : tasks) {
				if (task.getRunnable() == runnable) {
					return task;
				}
			}
		}
		
		tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, "fixedRateTasks");
		if (tasks != null) {
			for (Task task : tasks) {
				if (task.getRunnable() == runnable) {
					return task;
				}
			}
		}
		
		tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, "fixedDelayTasks");
		if (tasks != null) {
			for (Task task : tasks) {
				if (task.getRunnable() == runnable) {
					return task;
				}
			}
		}

		return null;
	}

	boolean isFixedRate(ScheduledTaskRegistrar scheduledTaskRegistrar, Task task) throws Exception {
		List<Task> tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, "fixedRateTasks");
		return tasks.contains(task);
	}

	void scheduleTask(ScheduledTaskRegistrar scheduledTaskRegistrar, Set<ScheduledFuture<?>> scheduledFutures, Task task) throws Exception {
		if (task instanceof TriggerTask) {
			TriggerTask triggerTask = (TriggerTask) task;
			scheduledFutures.add(scheduledTaskRegistrar.getScheduler().schedule(
				triggerTask.getRunnable(), triggerTask.getTrigger()));
		} else if (task instanceof CronTask) {
			CronTask cronTask = (CronTask) task;
			scheduledFutures.add(scheduledTaskRegistrar.getScheduler().schedule(
				cronTask.getRunnable(), cronTask.getTrigger()));
		} else if (task instanceof IntervalTask) {
			IntervalTask intervalTask = (IntervalTask) task;
			long now = System.currentTimeMillis();
			TaskScheduler taskScheduler = scheduledTaskRegistrar.getScheduler();
			if (isFixedRate(scheduledTaskRegistrar, task)) {
				if (intervalTask.getInitialDelay() > 0) {
					Date startTime = new Date(now + intervalTask.getInitialDelay());
					scheduledFutures.add(taskScheduler.scheduleAtFixedRate(
							intervalTask.getRunnable(), startTime, intervalTask.getInterval()));
				} else {
				    scheduledFutures.add(taskScheduler.scheduleAtFixedRate(
							intervalTask.getRunnable(), intervalTask.getInterval()));
				}
			} else {
				if (intervalTask.getInitialDelay() > 0) {
					Date startTime = new Date(now + intervalTask.getInitialDelay());
					scheduledFutures.add(taskScheduler.scheduleAtFixedRate(
							intervalTask.getRunnable(), startTime, intervalTask.getInterval()));
				} else {
				    scheduledFutures.add(taskScheduler.scheduleAtFixedRate(
							intervalTask.getRunnable(), intervalTask.getInterval()));
				}
			}
		}
	}
%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);

	ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor
		= (ScheduledAnnotationBeanPostProcessor) ctx.getBean(
		"org.springframework.context.annotation.internalScheduledAnnotationProcessor");

	ScheduledTaskRegistrar scheduledTaskRegistrar = (ScheduledTaskRegistrar)
		forceGetField(scheduledAnnotationBeanPostProcessor, "registrar");

	String action = request.getParameter("action");
	if ("execute".equals(action)) {
		String group = request.getParameter("group");
		int index = Integer.parseInt(request.getParameter("index"));
		List<Task> tasks = (List<Task>) forceGetField(scheduledTaskRegistrar, group);
		Task task = tasks.get(index);
		task.getRunnable().run();
		
		response.sendRedirect("scheduler.jsp");
		return;

	} else if ("cancel".equals(action)) {
		String group = request.getParameter("group");
		int index = Integer.parseInt(request.getParameter("index"));
		Set<ScheduledFuture<?>> scheduledFutures = (Set<ScheduledFuture<?>>) forceGetField(scheduledTaskRegistrar, "scheduledFutures");
		int i = 0;
		for (ScheduledFuture scheduledFuture : scheduledFutures) {
			if (i == index) {
				scheduledFuture.cancel(false);
				break;
			}
			i++;
		}

		response.sendRedirect("scheduler.jsp");
		return;

	} else if ("active".equals(action)) {
		String group = request.getParameter("group");
		int index = Integer.parseInt(request.getParameter("index"));
		Set<ScheduledFuture<?>> scheduledFutures = (Set<ScheduledFuture<?>>) forceGetField(scheduledTaskRegistrar, "scheduledFutures");
		int i = 0;
		ScheduledFuture future = null;
		for (ScheduledFuture scheduledFuture : scheduledFutures) {
			if (i == index) {
				future = scheduledFuture;
				break;
			}
			i++;
		}

		Runnable runnable = findRunnable(future);

		Task task = findTask(scheduledTaskRegistrar, runnable);
		scheduleTask(scheduledTaskRegistrar, scheduledFutures, task);
		scheduledFutures.remove(future);

		response.sendRedirect("scheduler.jsp");
		return;

	}
%>
<html>
  <head>
    <meta charset="utf-8">
	<title>scheduler</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
  </head>
  <body>

	<!-- ################################################## -->
	<h5>TriggerTask</h5>
	<table border="1">
	  <thead>
		<tr>
		  <th>runnable</th>
		  <th>action</th>
		</tr>
	  </thead>
	  <tbody>
<%
	// out.println(forceGetField(scheduledTaskRegistrar, "triggerTasks"));
	// out.println(forceGetField(scheduledTaskRegistrar, "cronTasks"));
	// out.println(forceGetField(scheduledTaskRegistrar, "fixedRateTasks"));
	// out.println(forceGetField(scheduledTaskRegistrar, "fixedDelayTasks"));

	List<TriggerTask> triggerTasks = (List<TriggerTask>) forceGetField(scheduledTaskRegistrar, "triggerTasks");

	if (triggerTasks != null) {
		int i = 0;
		for (TriggerTask triggerTask : triggerTasks) {
			pageContext.setAttribute("index", i);

			Runnable runnable = triggerTask.getRunnable();
			pageContext.setAttribute("instance", findInstance(runnable));

			pageContext.setAttribute("triggerTask", triggerTask);
%>
		<tr>
		  <td>${instance}</td>
		  <td><a href="scheduler.jsp?action=execute&group=triggerTasks&index=${index}">execute</a></td>
		</tr>
<%
			i++;
		}
	}
%>
	  </tbody>
	</table>

	<!-- ################################################## -->
	<h5>CronTask</h5>
	<table border="1">
	  <thead>
		<tr>
		  <th>runnable</th>
		  <th>expression</th>
		  <th>action</th>
		</tr>
	  </thead>
	  <tbody>
<%

	List<CronTask> cronTasks = (List<CronTask>) forceGetField(scheduledTaskRegistrar, "cronTasks");

	if (cronTasks != null) {
		int i = 0;
		for (CronTask cronTask : cronTasks) {
			pageContext.setAttribute("index", i);

			Runnable runnable = cronTask.getRunnable();
			pageContext.setAttribute("instance", findInstance(runnable));
;
			pageContext.setAttribute("cronTask", cronTask);
%>
		<tr>
		  <td>${instance}</td>
		  <td>${cronTask.expression}</td>
		  <td><a href="scheduler.jsp?action=execute&group=cronTasks&index=${index}">execute</a></td>
		</tr>
<%
			i++;
		}
	}
%>
	  </tbody>
	</table>

	<!-- ################################################## -->
	<h5>fixedRateTasks</h5>
	<table border="1">
	  <thead>
		<tr>
		  <th>runnable</th>
		  <th>action</th>
		</tr>
	  </thead>
	  <tbody>
<%

	List<IntervalTask> fixedRateTasks = (List<IntervalTask>) forceGetField(scheduledTaskRegistrar, "fixedRateTasks");

	if (fixedRateTasks != null) {
		int i = 0;
		for (IntervalTask fixedRateTask : fixedRateTasks) {
			pageContext.setAttribute("index", i);

			Runnable runnable = fixedRateTask.getRunnable();
			pageContext.setAttribute("instance", findInstance(runnable));
;
			pageContext.setAttribute("fixedRateTask", fixedRateTask);
%>
		<tr>
		  <td>${instance}</td>
		  <td><a href="scheduler.jsp?action=execute&group=fixedRateTasks&index=${index}">execute</a></td>
		</tr>
<%
			i++;
		}
	}
%>
	  </tbody>
	</table>

	<!-- ################################################## -->
	<h5>fixedDelayTasks</h5>
	<table border="1">
	  <thead>
		<tr>
		  <th>runnable</th>
		  <th>action</th>
		</tr>
	  </thead>
	  <tbody>
<%

	List<IntervalTask> fixedDelayTasks = (List<IntervalTask>) forceGetField(scheduledTaskRegistrar, "fixedDelayTasks");

	if (fixedDelayTasks != null) {
		int i = 0;
		for (IntervalTask fixedDelayTask : fixedDelayTasks) {
			pageContext.setAttribute("index", i);

			Runnable runnable = fixedDelayTask.getRunnable();
			pageContext.setAttribute("instance", findInstance(runnable));
;
			pageContext.setAttribute("fixedDelayTask", fixedDelayTask);
%>
		<tr>
		  <td>${instance}</td>
		  <td>${cronTask.expression}</td>
		  <td><a href="scheduler.jsp?action=execute&group=fixedDelayTasks&index=${index}">execute</a></td>
		</tr>
<%
			i++;
		}
	}
%>
	  </tbody>
	</table>

	<h5>ScheduledFuture</h5>
	<table border="1">
	  <thead>
		<tr>
		  <th>future</th>
		  <th>next time</th>
		  <th>cancel</th>
		  <th>done</th>
		</tr>
	  </thead>
	  <tbody>
<%

	Set<ScheduledFuture<?>> scheduledFutures = (Set<ScheduledFuture<?>>) forceGetField(scheduledTaskRegistrar, "scheduledFutures");

	if (scheduledFutures != null) {
		int i = 0;
		long now = System.currentTimeMillis();

		for (ScheduledFuture scheduledFuture : scheduledFutures) {
			pageContext.setAttribute("index", i);
			pageContext.setAttribute("scheduledFuture", scheduledFuture);

			if (scheduledFuture instanceof DelegatingErrorHandlingRunnable
					|| scheduledFuture instanceof FutureTask) {
				Runnable runnable = findRunnable(scheduledFuture);
				pageContext.setAttribute("instance", findInstance(runnable));
			} else {
				pageContext.setAttribute("instance", scheduledFuture);
			}

			long time = now + scheduledFuture.getDelay(TimeUnit.MILLISECONDS);
			Date date = new Date(time);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			pageContext.setAttribute("dateText", dateFormat.format(date));
%>
		<tr>
		  <td>${instance}</td>
		  <td>${dateText}</td>
		  <td>
		    ${scheduledFuture.cancelled}&nbsp;
		    <%if(!scheduledFuture.isCancelled()){%>
              <a href="scheduler.jsp?action=cancel&group=scheduledFutures&index=${index}">cancel</a>
			<%}else{%>
              <a href="scheduler.jsp?action=active&group=scheduledFutures&index=${index}">active</a>
			<%}%>
		  </td>
		  <td>${scheduledFuture.done}</td>
		</tr>
<%
			i++;
		}
	}
%>
	  </tbody>
	</table>

  </body>
</html>
