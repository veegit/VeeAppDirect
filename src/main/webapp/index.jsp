<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="VeeAppDirect.css">
<title>Application</title>
<script language="JavaScript" type="text/javascript">
<!--
function getopenid ( selectedtype )
{
  document.openidform.openid.value = selectedtype ;
  document.openidform.submit() ;
}
-->
</script>
</head>
<body>
<%
    if (request.getParameter("logout")!=null)
    {
        session.removeAttribute("openid");
        session.removeAttribute("openid-claimed");
%>
    Logged out!<p>
<%
    }
	if (session.getAttribute("openid")==null) {
%>
<strong>Sign with</strong> <br>
<form name="openidform" method="POST" action="login_redirect.jsp">
<input type="hidden" name="openid" />
<a href="javascript:getopenid('https://www.google.com/accounts/o8/id')">Google</a>
<br>
<a href="javascript:getopenid('https://me.yahoo.com/')">Yahoo</a>
</form>
<%	
} else {

%>
Logged in as <%= session.getAttribute("openid") %><p>
<a href="?logout=true">Log out</a>
<script type="text/javascript" language="javascript" src="veeappdirect/veeappdirect.nocache.js"></script>
	
<% } %>
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>

	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>
</body>
</html>
