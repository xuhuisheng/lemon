<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.lang.annotation.*"%>
<%@page import="java.lang.reflect.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.annotation.*"%>
<%@page import="javax.ws.rs.*"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="com.mossle.core.util.ReflectUtils"%>
<%!
	String getRequestMethodColor(String methodName) {
		if ("POST".equals(methodName)) {
			return "#10A54A";
		} else if ("GET".equals(methodName)) {
			return "#0f6ab4";
		} else if ("DELETE".equals(methodName)) {
			return "#a41e22";
		} else if ("PUT".equals(methodName)) {
			return "#faf5ee";
		} else if ("PATCH".equals(methodName)) {
			return "#D38042";
		} else {
			return "black";
		}
	}

	String getRequestMethod(Method method) {
		if (method.isAnnotationPresent(Resource.class)) {
			return null;
		}
		if (method.isAnnotationPresent(POST.class)) {
			return "POST";
		} else if (method.isAnnotationPresent(GET.class)) {
			return "GET";
		} else if (method.isAnnotationPresent(DELETE.class)) {
			return "DELETE";
		} else {
			System.out.println("UNKNOW : " + method);
			return null;
		}
	}

	String getPath(Path subPath, String rootPathValue) {
		String subPathValue = null;
		if (subPath != null) {
			subPathValue = subPath.value();
			if (subPathValue.charAt(0) != '/') {
				subPathValue = "/" + subPathValue;
			}
			subPathValue = rootPathValue + subPathValue;
		} else {
			subPathValue = rootPathValue;
		}
		return subPathValue;
	}
%>
<%
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
	Map<String,Object> map = ctx.getBeansWithAnnotation(Path.class);

%>
<html>
  <head>
    <meta charset="utf-8">
	<title>jersey</title>
    <style>
tbody tr:nth-child(odd) td,
tbody tr:nth-child(odd) th {
  background-color: #f9f9f9;
}
    </style>
	<script type="text/javascript">
var data = {
<%
	int classIndex = 0;
	for (Object item : map.values()) {
		Class clz = item.getClass();
		Path rootPath = (Path) clz.getAnnotation(Path.class);
		String rootPathValue = rootPath.value();
		if (rootPathValue.charAt(0) != '/') {
			rootPathValue = "/" + rootPathValue;
		}
%>
	'<%=clz.getName()%>': {
<%
		int methodIndex = 0;
		for (Method method : clz.getDeclaredMethods()) {
			String subMethodValue = getRequestMethod(method);
			if (subMethodValue == null) {
				continue;
			}
			Path subPath = (Path) method.getAnnotation(Path.class);
%>
		'<%=method.getName()%>': {
			path: '<%=getPath(subPath, rootPathValue)%>',
			method: '<%=subMethodValue%>',
			params: [
<%
			Annotation[][] annotationArray = method.getParameterAnnotations();
			for (int i = 0; i < annotationArray.length; i++) {
				Annotation[] annotations = annotationArray[i];

				String name = null;
				String type = null;

				for (Annotation annotation : annotations) {
					if (annotation instanceof PathParam) {
						name = ((PathParam) annotation).value();
						type = "path";
					} else if (annotation instanceof QueryParam) {
						name = ((QueryParam) annotation).value();
						type = "query";
					} else if (annotation instanceof FormParam) {
						name = ((FormParam) annotation).value();
						type = "form";
					} else if (annotation instanceof HeaderParam) {
						name = ((HeaderParam) annotation).value();
						type = "header";
					}
				}
				if (name == null) {
					continue;
				}
%>
				{
					name: '<%=name%>',
					type: '<%=type%>'
				}<%=(i == annotationArray.length - 1 ? "" : ",")%>
<%
			}
%>
			]
		}<%=(methodIndex == clz.getDeclaredMethods().length - 1 ? "" : ",")%>
<%
			methodIndex++;
		}
%>
	}<%=(classIndex == map.values().size() - 1 ? "" : ",")%>
<%
		classIndex++;
	}
%>
};

function toggle(id) {
	var el = document.getElementById(id);
	if (el.style.display == 'none') {
		el.style.display = '';
	} else {
		el.style.display = 'none';
	}
}

function doTest(className, methodName) {
	var o = data[className][methodName];
	var id = className + '-' + methodName;
	var url = '<%=request.getContextPath()%>/rs' + o.path;

	var xmlhttp;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	var headerParams = {};
	var queryParams = '';
	var formParams = '';
	for (var i = 0; i < o.params.length; i++) {
		var item = o.params[i];
		if (item.type == 'path') {
			url = url.replace('{' + item.name + '}', document.getElementById(id + '-' + item.name).value);
		} else if (item.type == 'query') {
			if (queryParams != '') {
				queryParams += '&';
			}
			queryParams += item.name + '=' +  encodeURIComponent(document.getElementById(id + '-' + item.name).value);
		} else if (item.type == 'form') {
			if (formParams != '') {
				formParams += '&';
			}
			formParams += item.name + '=' +  document.getElementById(id + '-' + item.name).value;
		} else if (item.type == 'header') {
			headerParams[item.name] = document.getElementById(id + '-' + item.name).value;
		}
	}
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 1) {
			for (var key in headerParams) {
				var value = headerParams[key];
				xmlhttp.setRequestHeader(key, value);
			}
		}
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			document.getElementById(id + '-result').innerHTML = xmlhttp.responseText;
			document.getElementById(id + '-result').style.display = '';
		}
	}

	if (queryParams != '') {
		if (url.indexOf('?') == -1) {
			url += '?' + queryParams;
		} else {
			url += '&' + queryParams;
		}
	}

	xmlhttp.open(o.method, url, true);

	if (o.method == 'GET') {
		xmlhttp.send();
	} else {
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(formParams);
	}
}
	</script>
  </head>
  <body>
<%
	for (Object item : map.values()) {
		Class clz = item.getClass();
		Path rootPath = (Path) clz.getAnnotation(Path.class);
		String rootPathValue = rootPath.value();
		if (rootPathValue.charAt(0) != '/') {
			rootPathValue = "/" + rootPathValue;
		}
%>
  <div style="background-color:black;color:white;cursor:pointer;padding:5px;border:1px dotted gray;" onclick="toggle('<%=clz.getName()%>')">
    <%=rootPathValue%>
  </div>
  <div id="<%=clz.getName()%>" style="display:none;">
<%
		for (Method method : clz.getDeclaredMethods()) {
			String subMethodValue = getRequestMethod(method);
			if (subMethodValue == null) {
				continue;
			}

			Path subPath = (Path) method.getAnnotation(Path.class);
			String subPathValue = getPath(subPath, rootPathValue);

			Produces produces = method.getAnnotation(Produces.class);
%>
    <div style="border:1px solid gray;margin:5px;padding:5px;cursor:pointer;" onclick="toggle('<%=clz.getName()%>-<%=method.getName()%>')">
	  <span style="background-color:<%=getRequestMethodColor(subMethodValue)%>;color:white;display:inline-block;width:60px;text-align:center;"><%=subMethodValue%></span>
	  &nbsp;
	  <span style="display:inline-block;width:100px;"><%=subPathValue%></span>
	  &nbsp;
	  <span style="display:inline-block;float:right;"><%=produces == null ? "" : Arrays.asList(produces.value())%></span>
	</div>

	<div id="<%=clz.getName()%>-<%=method.getName()%>" style="display:none;border:1px solid gray;padding:5px;margin:5px;">
<%
			Annotation[][] annotationArray = method.getParameterAnnotations();
			Class[] parameterTypeArray = method.getParameterTypes();
			for (int i = 0; i < annotationArray.length; i++) {
				Annotation[] annotations = annotationArray[i];
				Class parameterType = parameterTypeArray[i];

				String name = null;
				String type = null;

				for (Annotation annotation : annotations) {
					if (annotation instanceof PathParam) {
						name = ((PathParam) annotation).value();
						type = "path";
					} else if (annotation instanceof QueryParam) {
						name = ((QueryParam) annotation).value();
						type = "query";
					} else if (annotation instanceof FormParam) {
						name = ((FormParam) annotation).value();
						type = "form";
					} else if (annotation instanceof HeaderParam) {
						name = ((HeaderParam) annotation).value();
						type = "header";
					}
				}
				if (name == null) {
					continue;
				}
%>
      <div>
	    <label for="<%=clz.getName()%>-<%=method.getName()%>-<%=name%>"><%=name%>:</label>
		<input type="text" id="<%=clz.getName()%>-<%=method.getName()%>-<%=name%>" class="<%=type%>">
	  </div>
<%
			}
%>
      <div>
	    <div id="<%=clz.getName()%>-<%=method.getName()%>-result" style="border:1px solid gray;margin:5px;padding:5px;display:none;"></div>
	    <button onclick="doTest('<%=clz.getName()%>','<%=method.getName()%>')">test</button>
	  </div>
	</div>
<%
		}
%>
  </div>
<%
	}
%>
  </body>
</html>
