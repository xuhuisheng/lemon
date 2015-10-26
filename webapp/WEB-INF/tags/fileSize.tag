<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@attribute name="fileSize" type="java.lang.Long" required="true"%>
<%
  Long fileSize = (Long) jspContext.getAttribute("fileSize");

  String showSize = "";
  
  if (fileSize == 0) {
    showSize = "-";
  } else if (fileSize > 0 && fileSize < 1024) {
    showSize = Long.toString(fileSize) + "B";
  } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
    showSize = Long.toString(fileSize / 1024) + "KB";
  } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
    showSize = Long.toString(fileSize / (1024 * 1024)) + "MB";
  } else if (fileSize >= (1024 * 1024 * 1024)) {
    showSize = Long.toString(fileSize / (1024 * 1024 * 1024)) + "GB";
  }
  out.println(showSize);
%>
