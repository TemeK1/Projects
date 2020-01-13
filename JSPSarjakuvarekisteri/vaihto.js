var xmlHttp;

function otaVastaan() {
  if (xmlHttp.readyState != 4 ) return;
  if (xmlHttp.status != 200) return;
  var tulosRaha = xmlHttp.responseText;
  var tulos = document.getElementById('<%=editTulos.getNimi()%>');
  tulos.value = tulosRaha;
}

function laheta(url,vastaaja) {
  url = url + "&ts=" + new Date().getTime();
  //var xmlHttp;
  if (window.XMLHttpRequest) {
    xmlHttp = new XMLHttpRequest();
  } else if (window.ActiveXObject) {
    try { httpRequest = new ActiveXObject("Msxml2.XMLHTTP"); }
    catch (e) { try { httpRequest = new ActiveXObject("Microsoft.XMLHTTP"); }
                catch (e) {}
    }
  }
  if ( !xmlHttp ) {
    alert('Giving up :( Cannot create an XMLHTTP instance');
    return false;
  }
  xmlHttp.open('GET', url, true);
  xmlHttp.onreadystatechange = vastaaja;
  xmlHttp.send(null);
}

function hae() {
  var id = document.getElementById('5');
  // var tulos = document.getElementById('<%=editTulos.getNimi()%>');
  //laheta("AjaxVaihtajaServlet?raha=" + raha.value + "&val=" + val.value,otaVastaan);
  laheta("AjaxVaihtaja.jsp?id=" + id.value,otaVastaan);
}