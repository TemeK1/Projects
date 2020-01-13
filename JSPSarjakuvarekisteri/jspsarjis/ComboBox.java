package jspsarjis	;

import javax.servlet.http.*;

@SuppressWarnings("javadoc")
public class ComboBox {
    private String valittu = "";
    private String nimi = "";
    private HttpSession session = null;

    private int lkm = 0;
    private static final int MAXLKM = 20;
    private String alkiot[];
    private String js;

    public ComboBox(String nimi) {
        this.nimi = nimi;
        lkm = 0;
        alkiot = new String[MAXLKM];
    }

    public ComboBox(String nimi, HttpSession session) {
        this(nimi);
        this.session = session;
        setValittu(session);
    }

    public ComboBox(String nimi,String alkiot[]) {
        this(nimi,alkiot,null,null,0);
    }

    public ComboBox(String nimi,String alkiot[], HttpSession session) {
        this(nimi,alkiot,session,null,0);
    }

    public ComboBox(String nimi,String alkiot[], HttpServletRequest request) {
        this(nimi,alkiot,null,request,0);
    }

    public ComboBox(String nimi,String alkiot[], HttpSession session, String def) {
        this(nimi,alkiot,session,null,def);
    }

    public ComboBox(String nimi,String alkiot[], HttpSession session, HttpServletRequest request, String def) {
        this.session = session;
        this.nimi = nimi;
        this.alkiot = alkiot;
        lkm = alkiot.length;
        setValittu(session,def);
        setValittu(request);
    }

    public ComboBox(String nimi,String alkiot[], HttpSession session, HttpServletRequest request,  int index) {
        this.session = session;
        this.nimi = nimi;
        this.alkiot = alkiot;
        lkm = alkiot.length;
        String def = ""; 
        if ( lkm > 0 ) def = alkiot[0];
        if ( 0 <= index && index <lkm ) def = alkiot[index];
        setValittu(session,def);
        setValittu(request);
    }

    public void setValittu(String valittu) {
        if ( valittu == null ) return;
        this.valittu = valittu;
        save();
    }

    public String getValittu() {
        return valittu;
    }

    public void setValittu(HttpServletRequest request) {
        if ( request == null ) return;  
        String valinta = request.getParameter(nimi);
        if (valinta == null) return;
        setValittu(valinta);
    }

    public void setValittu(HttpSession session) {
        String text = null;
        if ( session != null ) text = (String)session.getAttribute(nimi);
        if (text == null) return;
        setValittu(text);
    }

    public void setValittu(HttpSession session, String def) {
        String text = def;
        if ( session != null ) text = (String)session.getAttribute(nimi);
        if (text == null) text = def;
        if (text == null) return;
        setValittu(text);
    }

    public void save(HttpSession session1) {
        if ( session1 == null ) return;
        session1.setAttribute(nimi,getValittu());
    }

    public void save() {
        save(session);
    }


    public void lisaa(String valinta) {
        if ( lkm >= alkiot.length ) return;
        alkiot[lkm++] = valinta;
        if ( lkm == 1 && valittu.equals("") ) setValittu(alkiot[0]);
    }

    public void setJs(String js) {
        this.js = js;   
    }

    public String getNimi() {
        return nimi;  
    }

    public String asHTML() {
        if ( js == null ) js = "";  
        StringBuffer sb = new StringBuffer("<select name=\""+getNimi()+"\" id=\""+getNimi()+"\" " + js + ">\n");
        for (int i=0; i<lkm; i++) {
            String selected = "";
            if ( alkiot[i].equals(getValittu()) ) selected = " selected=\"selected\" ";
            sb.append("<option value=\"" + alkiot[i] +  "\"" + selected + ">" + alkiot[i] + "</option>\n");
        }
        sb.append("</select>");
        return sb.toString();
    }

    @Override
    public String toString() {
        return asHTML();
    }

    public void setSelectedIndex(int index) {
        int i = index;
        if ( i < 0 ) i = 0;
        if ( lkm <= i ) i = lkm -1;
        if ( i < 0 ) return;
        setValittu(alkiot[i]);
    }

    public int getSelectedIndex() {
        for (int i=0; i<lkm; i++) {
            if ( alkiot[i].equals(getValittu()) ) return i;
        }
        return -1;
    }



}