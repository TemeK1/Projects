<%@ page language="Java" import="jspsarjis.*,sarjakuva.*,java.util.*,java.io.*" session="true" autoFlush="true"
  isThreadSafe="true" isErrorPage="false"
%><!DOCTYPE html>
<html>
<head>
<title>WebSarjisRekisteri</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="StyleSheet" href="tyyli.css" type="text/css" media="all">
</head>
<% Rekisteri rekisteri = SarjisBean.getRekisteri();
   int maara = rekisteri.getMaara();
   double arvo = rekisteri.getArvoTotal();
   double kunto = rekisteri.getKuntoKa();
   int id = SarjisBean.getInt(request,"id",-2);
   String haku = "";
   haku = request.getParameter("haku");
   if (haku == null) haku = "";
%>
<h1>Sarjakuvarekisteri</h1>
Nimikkeitä: <%=maara%> | Arvo yhteensä: <%=arvo%> euroa | Kunto ka. <%=kunto%><br /><br />
<form action="rekisteri.jsp" method="get">
Hakukenttä: <input type="text" name="haku" value="<%=haku%>">
<input type="submit" name="Submit" value="Hae" />
</form>
<a href ="nimike.jsp">Uusi nimike</a>
<table>
<tr><td>Tyyppi</td><td>Sarjakuva</td><td>Nimi</td><td>Numero</td><td>Kunto</td><td>Arvo</td><td>Vuosi</td></tr>
<% ArrayList<Nimike> nimikkeet = rekisteri.haku(rekisteri.getVuosiMin(), rekisteri.getVuosiMax(), haku); 
   for (Nimike nim : nimikkeet) {
	   out.println("<tr><td><a href=\"nimike.jsp?id=" + nim.getId() + "\">" + rekisteri.getTyyppiNimi(nim.getTid()) + "</a><td>" + rekisteri.getSaNimi(nim.getSid()) + "</td><td>"  + nim.getNimi() + "</td><td>" + nim.getNumero() + "</td><td>" + nim.getKunto() + "</td><td>" + nim.getArvo() + "</td><td>" + nim.getVuosi() + "</td></tr>");
	   if (nim.getNimikeNro() == id) out.println("<tr><td>" + nim.getLisatietoja() + "</td></tr>");
   }
%>
</table>
<br>
</body>
</html>
