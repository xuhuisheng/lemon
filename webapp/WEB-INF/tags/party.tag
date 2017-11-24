<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.api.party.PartyConnector"%>
<%@attribute name="partyId" type="java.lang.Object" required="true"%>
<%
  Object partyId = jspContext.getAttribute("partyId");
  if (partyId == null) {
    out.print("");
  } else {

    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(application);
    PartyConnector partyConnector = ctx.getBean(PartyConnector.class);
    try {
      out.print(partyConnector.findById(partyId.toString()).getName());
    } catch(Exception ex) {
      out.print(partyId);
      System.out.println("cannot find party : " + partyId);
    }
  }
%>
