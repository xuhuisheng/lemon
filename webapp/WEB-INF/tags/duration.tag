<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Date"%>
<%@attribute name="startTime" type="java.util.Date" required="true"%>
<%@attribute name="endTime" type="java.util.Date" required="true"%>
<%
  Date startTime = (Date) jspContext.getAttribute("startTime");
  Date endTime = (Date) jspContext.getAttribute("endTime");

  try {
    long duration = (endTime.getTime() - startTime.getTime()) / 1000;

    long day = duration / (24 * 60 * 60);
    long hour = duration / (60 * 60) % 24;
    long minute = duration / 60 % 60;
    long second = duration % 60;

    StringBuilder buff = new StringBuilder();
    if (day > 0) {
      buff.append(day).append("天");
    }
    if (hour > 0) {
      buff.append(hour).append("时");
    }
    if (minute > 0) {
      buff.append(minute).append("分");
    }
    if (second > 0) {
      buff.append(second).append("秒");
    }
    if (buff.length() > 0) {
      out.print(buff);
    } else {
      out.print("-");
    }
  } catch(Exception ex) {
    System.out.println(ex.toString());
  }
%>
