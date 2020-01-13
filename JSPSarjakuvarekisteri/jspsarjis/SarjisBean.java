package jspsarjis;
import sarjakuva.*;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: jsp-kerho</p>
 * <p>Description: Kerho-ohjelman WWW-versio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: jyu</p>
 * @author vesal
 * @version 1.0
 */
 
public class SarjisBean {

  static private Rekisteri rekisteri = new Rekisteri();
  static private Tyyppi[] tyypit;
  static private Sarjakuva[] sarjakuvat;

  static  {
//    public KerhoBean() throws SailoException {
     File f = new File(".");
     String s = f.getAbsolutePath();
     System.out.println(s);
    try {
      rekisteri.lueTyypit();
      tyypit = rekisteri.getTyypit();
      rekisteri.lueSarjakuvat();
      sarjakuvat = rekisteri.getSarjakuvat();
      rekisteri.lueNimikkeet();
    } catch (PoikkeusLuokka ex) {
      System.err.println(ex);
    }
  }

  
  /**
   * Tulostetaan harrastukset rivinvaihdoilla erotettuna.
   * @param out    tietovirta johon tulostetaan
   * @param kerho1  kerho josta harrastuksia etsit��n
   * @param jasen  j�sen jonka harrastuksia tulostetaan
   */
  public static void tulostaNimikkeet(PrintWriter out, Rekisteri rekisteri1 , Nimike nimike) {
    ArrayList<Nimike> nimikkeet = rekisteri1.haku(rekisteri1.getVuosiMin(), rekisteri1.getVuosiMax(), "");
    for (Nimike nim : nimikkeet ) {
      System.out.println(nim);
    }
  }


  /**
   * Tulostetaan j�sen ja harrastukset.  RIvit erotellaan rivinvaihdoilla.
   * @param out    tietovirta johon tulostetaan
   * @param kerho1  kerho josta harrastuksia etsit��n
   * @param jasen  j�sen jonka harrastuksia tulostetaan
   */
  public static void tulostaNimike(PrintWriter out, Rekisteri rekisteri1, Nimike nimike1) {
    System.out.println(nimike1);
  }

  
  /**
   * Palautetaan singleton mallin mukainen kerho
   * @return k�yt�ss� oleva kerho
   */
  public static Rekisteri getRekisteri() {
    return rekisteri;
  }
  
  public static Tyyppi[] getTyypit() {
	  return tyypit;
  }
  
  public static Sarjakuva[] getSarjakuvat() {
	  return sarjakuvat;
  }
  
  /**
   * Muutetaan request parametri kokonaisluvuksi.
   * @param request pyynt� josta parametria etsit��n
   * @param name parametin nimi
   * @param def oletusarvo jos parametri� ei ole tai se ei ole kokonaisluku
   * @return parametrin arvo kokonaislukuna
   */
  public static int getInt(HttpServletRequest request, String name, int def) {
        try {
          return Integer.parseInt(request.getParameter(name));
        } catch (Exception ex) {
          return def;
        }
      } 
}