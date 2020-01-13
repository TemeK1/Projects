package jspsarjis;

import javax.servlet.http.*;

@SuppressWarnings("javadoc")
public class EditBox {
  private String text = "";
  private String nimi = "";
  private int columns = -1;
  private HttpSession session = null;
  private String js;

  public EditBox(String nimi) {
    this.nimi = nimi;
  }

  public EditBox(String nimi,HttpSession session,HttpServletRequest request, String def) {
    this(nimi);
    this.session = session;
    setText(session,def);
    setText(request);
  }

  public EditBox(String nimi,HttpServletRequest request, String def) {
    this(nimi,null,request,def);
  }

  public EditBox(String nimi,HttpServletRequest request) {
    this(nimi,request,null);
  }


  public void setText(String text) {
    if ( text == null ) return;
    this.text = text;
    save();
  }

  public void setText(String text, String def) {
    if ( text == null ) { if ( def != null ) this.text = def; }
    else this.text = text;
    save();
  }

  public void save(HttpSession session1) {
    if ( session1 == null ) return;
    session1.setAttribute(nimi,getText());
  }

  public void save() {
    save(session);
  }

  public String getText() {
    return text;
  }

  public void setText(HttpSession session) {
    setText(session,null);
  }

  public void setText(HttpSession session, String def) {
    String text1 = null;
    if ( session != null ) text1 = (String)session.getAttribute(nimi);
    if (text1 == null) { if ( def != null ) text1 = def; }
    setText(text1);
  }

  public void setText(HttpServletRequest request) {
    setText(request,null);
  }

  public void setText(HttpServletRequest request, String def) {
    String text1 = request.getParameter(nimi);
    if (text1 == null) text1 = def;
    setText(text1);
  }

  public void setJs(String js) {
    this.js = js;   
  }
      
  public String getNimi() {
    return nimi;  
  }
      
  
  public String asHTML() {
    String size = "";
    if ( js == null ) js = "";  
    if ( columns >= 0 ) size = "\" size=\"" + columns + "\" ";
    return "<input type=\"text\" name=\"" + getNimi() +"\" id=\"" + getNimi() +"\" value=\""+ getText() + "\"" + size + " " + js + "/>";
  }

  @Override
  public String toString() {
    return asHTML();
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public int getColumns() {
    return columns;
  }

}