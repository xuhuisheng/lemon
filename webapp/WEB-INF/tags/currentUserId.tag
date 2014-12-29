<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="org.springframework.context.ApplicationContext"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="com.mossle.api.user.UserConnector"%>
<%@tag import="org.springframework.security.core.Authentication"%>
<%@tag import="org.springframework.security.core.context.SecurityContext"%>
<%@tag import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@tag import="com.mossle.api.userauth.UserAuthDTO"%>
<%@tag import="org.springframework.security.authentication.AnonymousAuthenticationToken"%>
<%@tag import="org.springframework.security.core.userdetails.UserDetails"%>
<%
	if ((SecurityContextHolder.getContext() == null)
		|| !(SecurityContextHolder.getContext() instanceof SecurityContext)
		|| (SecurityContextHolder.getContext().getAuthentication() == null)
		|| (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null)) {
		return;
	}
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	Object principal = authentication.getPrincipal();

	if (principal instanceof UserAuthDTO) {
		UserAuthDTO userAuthDto = (UserAuthDTO) principal;
		out.print(userAuthDto.getId());
	} else {
		out.print("0");
	}
%>
