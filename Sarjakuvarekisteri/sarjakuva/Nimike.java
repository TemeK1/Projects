package sarjakuva;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
     * Antaa nimikkeen lisäyslausekkeen
     * @param con tietokantayhteys
     * @return nimikkeen lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement getLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Nimikkeet" +
                "(nimikeNro, sid, tid, numero, vuosi, kunto, arvo, nimi, lisatietoja) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        // Syötetään kentät näin välttääksemme SQL injektiot.
        // Käyttäjän syötteitä ei ikinä vain kirjoiteta kysely
        // merkkijonoon tarkistamatta niitä SQL injektioiden varalta!
        if ( nimikeNro != 0 ) sql.setInt(1, nimikeNro); else sql.setString(1, null);
        sql.setInt(2, sid);
        sql.setInt(3, tid);
        sql.setInt(4, numero);
        sql.setInt(5, vuosi);
        sql.setInt(6, kunto);
        sql.setDouble(7, arvo);
        sql.setString(8, nimi);
        sql.setString(9, lisatietoja);
        
        return sql;
    }
    
    /**
     * Antaa tietokannan luontilausekkeen nimiketaululle
     * @return nimiketaulun luontilauseke
     */
    public String getLuontilauseke() {
        return "CREATE TABLE Nimikkeet (" +
                "nimikeNro INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "sid INTEGER, " +
                "tid INTEGER, " +
                "numero INTEGER, " +
                "vuosi INTEGER, " +
                "kunto INTEGER, " +
                "arvo DECIMAL(10, 2), " +
                "nimi VARCHAR(100), " +
                "lisatietoja VARCHAR(255)" +
                ")";
    }
    
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == nimikeNro ) return;
        setNimikeNro(id);
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
     * Ottaa jäsenen tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setNimikeNro(tulokset.getInt("nimikeNro"));
        this.sid = tulokset.getInt("sid");
        this.tid = tulokset.getInt("tid");
        this.numero = tulokset.getInt("numero");        
        this.vuosi = tulokset.getInt("vuosi");
        this.kunto = tulokset.getInt("kunto");
        this.arvo = tulokset.getDouble("arvo");
        this.nimi = tulokset.getString("nimi");
        this.lisatietoja = tulokset.getString("lisatietoja");
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
