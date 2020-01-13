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
   Tyyppi[] tyypit = SarjisBean.getTyypit();
   Sarjakuva[] sarjakuvat = SarjisBean.getSarjakuvat();
   ComboBox ctyypit = new ComboBox("kentta1",session);
   ComboBox csarjakuvat = new ComboBox("kentta2",session);
   int id = SarjisBean.getInt(request,"id",-2);
   boolean lisays = true;
	Nimike nimike = new Nimike();
	nimike.rekisteroi();
	int size = rekisteri.getMaara();
	if ( id > 0 ) { 
		for (int i = 1; i < size; i++) {
			if (rekisteri.getNimike(i).getNimikeNro() == id) {
				nimike = rekisteri.getNimike(i);
				lisays = false;
				break;
			}
		}
	}
	Boolean[] tarkistus = new Boolean[4];
	String[] kentat = { "Numero (kokonaisluku)", "Vuosi (kokonaisluku)", "Kunto (0-10)", "Arvo (esim. 2.5)" };
	String numero = request.getParameter("numero");
	String onumero = "" + nimike.getNumero();
	String vuosi = request.getParameter("vuosi");
	String ovuosi = "" + nimike.getVuosi();
	String kunto = request.getParameter("kunto");
	String okunto = "" + nimike.getKunto();
	String arvo = request.getParameter("arvo");
	String oarvo = "" + nimike.getArvo();
	String nimi = request.getParameter("nimi");
	String onimi = nimike.getNimi();
	String tiedot = request.getParameter("tiedot");
	String otiedot = nimike.getLisatietoja();

	String styyppi = "";
	int tid = nimike.getTid();
	int tcount = 0;
	int scount = 0;
	for (int i = 0; i < tyypit.length; i++) {
		if (tyypit[i] == null) break;
		if (tyypit[i].getTid() == nimike.getTid()) styyppi = tyypit[i].getNimi();	
		ctyypit.lisaa(tyypit[i].getNimi());
		tcount++;
	}
	
	String ssarjakuva = "";
	int sid = nimike.getSid();
	for (int i = 0; i < sarjakuvat.length; i++) {
		if (sarjakuvat[i] == null) break;
		if (sarjakuvat[i].getSid() == nimike.getSid()) ssarjakuva = sarjakuvat[i].getNimi();
		csarjakuvat.lisaa(sarjakuvat[i].getNimi());
		scount++;
	}
	
	String virhe = "";
	
	ctyypit.setValittu(styyppi);
	csarjakuvat.setValittu(ssarjakuva);
	
	
	if ( "Talleta".equals(request.getParameter("Talleta"))) { 
		tarkistus[0] = nimike.setNumero(numero);
		tarkistus[1] = nimike.setVuosi(vuosi);
		tarkistus[2] = nimike.setKunto(kunto);
		tarkistus[3] = nimike.setArvo(arvo);
 		nimike.setNimi(nimi);
		nimike.setLisatietoja(tiedot);
		boolean tallennetaanko = false;
		for (int i = 0; i < tarkistus.length; i++) {
			tallennetaanko = true;
			if (tarkistus[i] == false) {
				tallennetaanko = false;
				virhe = "syötteessä virhe ainakin kentässä " + kentat[i];
				break;
			}
		}
		
		csarjakuvat.setValittu(request);
		
		for (int i = 0; i < scount; i++) 
			if (csarjakuvat.getValittu() == sarjakuvat[i].getNimi()) nimike.setSid(sarjakuvat[i].getSid());
		
		
		if (lisays && tallennetaanko) rekisteri.lisaa(nimike);
		if (tallennetaanko) rekisteri.tallenna();
	}

%>
<h1><%=nimike.getNimi() + " | " + rekisteri.getTyyppiNimi(nimike.getTid()) + " | " + rekisteri.getSaNimi(nimike.getSid())%></h1>
<form action="nimike.jsp?id=<%=id%>" method="post">
<table>
<tr><td>Numero</td><td><input type="text" name="numero" value="<%=onumero%>" /></td></tr>
<tr><td>Tyyppi</td><td><%=ctyypit%></td></tr>
<tr><td>Sarjakuva</td><td><%=csarjakuvat%></td></tr>
<tr><td>Vuosi</td><td><input type="text" name="vuosi" value="<%=ovuosi%>" /></td></tr>
<tr><td>Kunto</td><td><input type="text" name="kunto" value="<%=okunto%>" /></td></tr>
<tr><td>Arvo</td><td><input type="text" name="arvo" value="<%=oarvo%>" /></td></tr>
<tr><td>Nimi</td><td><input type="text" name="nimi" value="<%=onimi%>" /></td></tr>
<tr><td>Lisätiedot</td><td><input name="tiedot" value="<%=otiedot%>" /></td></tr>
</table>
<input type="submit" name="Talleta" value="Talleta" />
<%=virhe%>
</form>
<br />
<a href="rekisteri.jsp?id=<%=id%>">Takaisin etusivulle</a>
<br />
</body>
</html>