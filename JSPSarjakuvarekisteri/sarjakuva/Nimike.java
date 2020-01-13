package sarjakuva;

import java.io.OutputStream;
import java.io.PrintStream;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Tietää nimikkeen asiat: id, sarjakuva/tyyppi-id, nimi, vuosi, numero, kunto, hinta, lisätiedot..
 * Ei tiedä käyttöliittymästä eikä koko kirjaston sisällöstä mitään.
 * Osaa muuntaa merkkijonon nimikkeen tiedoiksi ja nimikkeen tiedot merkkijonoksi.
 * Osaa tarkistaa nimikkeen tietojen määrämuotoisuuden.
 */
public class Nimike implements Cloneable {
    private int         sid = 1;
    private int         tid = 1;
    private int         numero = 0;
    private int         vuosi = 0;
    private int         kunto = 0;
    private int         tarkistin = 0;
    private double      arvo = 0;
    private String      nimi = "-";
    private String      lisatietoja = "-";
    
    private int         nimikeNro = 0;
    private static int  seuraavaNro = 1;
    
    /**
     * Oletusmuodostaja
     */
    public Nimike() {
        this.nimikeNro = seuraavaNro++;
        this.nimi = "-";
    }
    
    /**
     * Muodostaja pelkällä nimi-parametrillä.
     * @param nimi nimikkeen nimi
     */
    public Nimike(String nimi) {
        this.nimi = nimi;
    }
    
    /**
     * Kun käyttöliittymässä lisätään uutta nimikettä suoraan kloonista niin tätä 
     * täytyy kutsua tai muuten menee hommat päin prinkkalaa.
     */
    public void rekisteroi() {
        this.nimikeNro = seuraavaNro++;
    }
    
    
    /**
     * @return nimikkeen arvo
     */
    public double getArvo() {
        return this.arvo;
    }
    
    /**
     * @return yksittäisen nimikkeen id-tunnisteen
     */
    public int getId() {
        return this.nimikeNro;
    }
    
    /**
     * @return nimikkeen kunto
     */
    public int getKunto() {
        return this.kunto;
    }
    
    /**
     * @return nimikkeen lisätiedot
     */
    public String getLisatietoja() {
        return this.lisatietoja;
    }

    /**
     * @return palauttaa nimikkeen nimen.
     */
    public String getNimi() {
        return this.nimi;
    }
    
    /**
     * @return palauttaa nimikkeen numeron.
     */
    public int getNimikeNro() {
        return this.nimikeNro;
    }
    
    /**
     * @return nimikkeen numerot
     */
    public int getNumero() {
        return this.numero;
    }
    
    /**
     * @return tarkastaa että onko merkintä aito ja palauttaa sen mukaan totuusarvon.
     */
    public boolean getOnkoAito() {
        if (this.tarkistin == 1) return true;
        return false;
    }
    
    /**
     * @return nimikkeen sarjakuva-id
     */
    public int getSid() {
        return this.sid;
    }
    
    /**
     * @return nimikkeen tyyppi-id
     */
    public int getTid() {
        return this.tid;
    }
    
    /**
     * @return nimikkeen vuosi
     */
    public int getVuosi() {
        return this.vuosi;
    }
    
    /**
     * @return nimikkeen kaikki tiedot yhtenä merkkijonona
     */
    public String getTiedot() {
        StringBuilder tiedot = new StringBuilder();
        tiedot.append(this.nimikeNro);
        tiedot.append("|");
        tiedot.append(this.sid);
        tiedot.append("|");
        tiedot.append(this.tid);
        tiedot.append("|");
        tiedot.append(this.numero);
        tiedot.append("|");
        tiedot.append(this.vuosi);
        tiedot.append("|");
        tiedot.append(this.kunto);
        tiedot.append("|");
        tiedot.append(this.arvo);
        tiedot.append("|");
        tiedot.append(this.nimi);
        tiedot.append("|");
        tiedot.append(this.lisatietoja);
        
        return tiedot.toString();     
    }
    
    @Override
    public Nimike clone() throws CloneNotSupportedException {
        Nimike klooni;
        klooni = (Nimike) super.clone();
        return klooni;
    }
    
    /**
     * Parsii merkkijonosta yksittäisen olion tiedot esille ja settailee ne 
     * kohdilleen oikeille attribuuteille.
     * @param rivi merkkijono joka sisältää olion tarvitsemat tiedot.
     * @example
     * <pre name="test">
     * Nimike $nimike = new Nimike();
     * $nimike.parse($rivi);
     * 
     * $nimike | $rivi
     * -----------------------------
     * n1      | "1|1|1|9|2003|6|2.0|-|normaali julkaisu"
     * n2      | "3|1|1|8|1987|7|2.0|Aavesotilaat|erikoisjulkaisu" 
     * n3      | "5|9|2|8|1981|5|1.0|-|normaali julkaisu, mustekynäsotkua kannessa"
     * n4      | "10|8|2|11|1988|8|1.0|-|normaali julkaisu" 
     * n5      | "11|1|1|14|1986|8|2.0|-|normaali julkaisu" 
     * n6      | "12|1|1|16|1986|7|2.0|-|normaali julkaisu"
     * n7      | " "
     * 
     * n1.getNimi() === "-";
     * n2.getNimi() === "Aavesotilaat";
     * n6.getSid() === 1;
     * n4.getTid() === 2; 
     * n1.getTiedot() === "1|1|1|9|2003|6|2.0|-|normaali julkaisu";
     * n7.getTiedot() === "13|1|1|0|0|0|0.0|-|-";
     * n2.setNimi("Kummitussotilaat");
     * n2.getTiedot() === "3|1|1|8|1987|7|2.0|Kummitussotilaat|erikoisjulkaisu";
     * n2.setLisatietoja("normaalijulkaisu");
     * n2.getTiedot() === "3|1|1|8|1987|7|2.0|Kummitussotilaat|normaalijulkaisu";
     * </pre>
     */
    public void parse(String rivi) {
        try {
            StringBuilder erotettavat = new StringBuilder(rivi);
            if (erotettavat.length() < 16) { 
                return;
            }
            this.setNimikeNro(Integer.parseInt(Mjonot.erota(erotettavat, '|', false)));
            this.setSid((Mjonot.erota(erotettavat, '|', false)));
            this.setTid((Mjonot.erota(erotettavat, '|', false)));
            this.setNumero((Mjonot.erota(erotettavat, '|', false)));
            this.setVuosi((Mjonot.erota(erotettavat, '|', false)));
            this.setKunto((Mjonot.erota(erotettavat, '|', false)));
            this.setArvo((Mjonot.erota(erotettavat, '|', false)));
            this.setNimi((Mjonot.erota(erotettavat, '|', false)));
            this.setLisatietoja((Mjonot.erota(erotettavat, '|', false)));
        }   catch (NumberFormatException ex) {
               System.err.println("Virhe " + ex.getMessage());
        }
    }
   
    /**
     * @param arvo nimikkeen arvon asetus
     * @return totuusarvo operaation onnistumisesta
     */
    public boolean setArvo(String arvo) {
        try {
          this.arvo = Double.parseDouble(arvo);
          if (this.arvo < 0.0) return false;
          return true;
        } catch (NumberFormatException ex) {
             return false;
        }
    }
    
    /**
     * @param kunto nimikkeen kunnon asetus
     * @return palauttaa totuusarvon operaation onnistumisesta
     */
    public boolean setKunto(String kunto) {
        try {
          this.kunto = Integer.parseInt(kunto);
          if (this.kunto > 10 || this.kunto < 0) return false;
          return true;
        } catch (NumberFormatException ex) {
             return false;
        }
    }
    
    /**
     * @param lisatietoja nimikkeen lisätietojen asetus
     */
    public void setLisatietoja(String lisatietoja) {
        this.lisatietoja = lisatietoja;
        if (lisatietoja.length() == 0) this.lisatietoja = "-";
    }
    
    /**
     * Metodi nimikkeen nimen asetukselle
     * @param nimi nimikkeen nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi.trim();
    }
    
    /**
     * Metodi nimikkeen numeron asetukselle
     * @param numero nimikkeen numero.
     */
    public void setNimikeNro(int numero) {
        this.nimikeNro = numero;
        if (seuraavaNro <= this.nimikeNro) seuraavaNro = nimikeNro + 1;
    }
    
    /**
     * @param numero nimikkeen numeron asetus
     * @return totuusarvo operaation onnistumisesta
     */
    public boolean setNumero(String numero) {
        try {
            this.numero = Integer.parseInt(numero);
            if (this.numero < 0) { 
                this.numero = 0;
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * @param sid nimikkeen sarjakuva-id:n asetus
     * @return palauttaa totuusarvon operaation onnistumisesta.
     */
    public boolean setSid(String sid) {
        try {
            this.sid = Integer.parseInt(sid);
            return true;
           } catch (NumberFormatException ex) {
               return false;
           }
    }
    
    /**
     * Tällä merkataan käytön aikana, että onko nimike säilyttämisen arvoinen vai ei.
     * @param arvo tarkastimeen asetettava numero.
     */
    public void setTarkistin(int arvo) {
        this.tarkistin = arvo;
    }
    
    /**
     * @param tid nimikkeen tyyppi-id:n asetus
     * @return totuusarvo operaation onnistumisesta
     */
    public boolean setTid(String tid) {
        try {
         this.tid = Integer.parseInt(tid);
         return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * @param sid nimikkeen sarjakuva-id:n asetus
     */
    public void setSid(int sid) {
        this.sid = sid;
    }
    
    
    /**
     * @param tid nimikkeen tyyppi-id:n asetus
     */
    public void setTid(int tid) {
        this.tid = tid;
    }
    
    
    /**
     * @param vuosi nimikkeen vuoden asetus
     * @return totuusarvo operaation onnistumisesta.
     */
    public boolean setVuosi(String vuosi) {
        try {
            this.vuosi = Integer.parseInt(vuosi);
            if (this.vuosi > 0) return true;
            this.vuosi = 0;
            return false;
           } catch (NumberFormatException ex) {
               return false;
        }
    }
    
    /**
     * Tulostetaan nimikkeen tiedot. ns. alkeellinen tietovirta.
     * @param os tietovirta johon tulostetaan
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
    
    /**
     * Tulostetaan nimikkeen tiedot.
     * @param out tietovirta johon tulostetaan
     */
    public void tulosta(PrintStream out) {
        out.println(this.nimi + ", " + this.numero + ", " + this.vuosi + ", " + this.kunto + ", " + this.arvo + " €, " + this.lisatietoja);
    }
    
    /**
     * @param args ei mitään
     */
    public static void main(String[] args) {
        Nimike nimike1 = new Nimike();
        nimike1.setNumero("52");
        nimike1.setVuosi("1999");
        nimike1.setKunto("10");
        nimike1.setLisatietoja("Hyvä tarina");
        nimike1.setArvo("1000");
        nimike1.tulosta(System.out);
    }
}
