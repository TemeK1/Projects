<%@ page language="Java" import="jspsarjis.*,sarjakuva.*,java.util.*,java.io.*" session="true" autoFlush="true"
  isThreadSafe="true" isErrorPage="false"
%>
<% Rekisteri rekisteri = SarjisBean.getRekisteri();
   int id = SarjisBean.getInt(request,"id",-2);
   int size = rekisteri.getMaara();
   String lisatiedot = "asd";
   for (int i = 0; i < size; i++) {
	   if (rekisteri.getNimike(i).getNimikeNro() == id) {
		   lisatiedot = rekisteri.getNimike(i).getLisatietoja();
		   break;
	   }
   }
%>
<%=lisatiedot%>