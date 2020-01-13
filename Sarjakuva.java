package sarjakuva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Tietää yksittäisen sarjakuvan asiat: id, nimi ja lyhenne.
 * Osaa tarkistaa niiden määrämuotoisuuden.
 * Osaa kääntää merkkijonon sarjakuvan kentiksi ja kenttien sisällön merkkijonoksi.
 */
public class Sarjakuva {
    private int sid;
    private String nimi;
    private static int seuraavaNro = 1;
    
    /**
     * Oletusmuodostaja.
     */
    public Sarjakuva() {
        this.sid = seuraavaNro;
        seuraavaNro++;
        this.nimi = "ei nimeä";
    }
    
    /**
     * Muodostaja.
     * @param nimi sarjakuvan nimi.
     */
    public Sarjakuva(String nimi) {
        this.sid = seuraavaNro;
        seuraavaNro++;
        this.nimi = nimi;
    }
    
    
    /**
     * @param sid sarjakuvan id-tunniste
     * @param nimi sarjakuvan nimi
     */
    public Sarjakuva(int sid, String nimi) {
        this.sid = sid;
        this.nimi = nimi;
        seuraavaNro++;
    }
    
    /**
     * Antaa sarjakuvan lisäyslausekkeen
     * @param con tietokantayhteys
     * @return jäsenen lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement getLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Sarjakuvat" +
                "(sid, nimi) " +
                "VALUES (?, ?)");
        
        // Syötetään kentät näin välttääksemme SQL injektiot.
        // Käyttäjän syötteitä ei ikinä vain kirjoiteta kysely
        // merkkijonoon tarkistamatta niitä SQL injektioiden varalta!
        if ( sid != 0 ) sql.setInt(1, sid); else sql.setString(1, null);
        sql.setString(2, nimi);
        
        return sql;
    }
    
    /**
     * Antaa tietokannan luontilausekkeen sarjakuvataululle
     * @return sarjakuvataulun luontilauseke
     */
    public String getLuontilauseke() {
        return "CREATE TABLE Sarjakuvat (\n" +
                "sid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "nimi VARCHAR(100) NOT NULL\n" +
                ")";
    }
    
    /**
     * @return palauttaa sarjakuvan nimen.
     * @example
     * <pre name="test">
     * Sarjakuva $sarjakuva = new Sarjakuva($nimi);
     * $sarjakuva.getNimi() === $nimi;
     * 
     * $sarjakuva | $nimi
     * ---------------
     * s1     | "Tintti"
     * s2     | "Aku Ankka"
     * s3     | "Asterix"
     * s4     | "Tex Willer"
     * s5     | "Tarzan"
     * s6     | "Turtles"
     *  
     * s6.setNimi(s1.getNimi());
     * s6.getNimi() === "Tintti";
     *  
     * s1.getTiedot() === "1|Tintti";
     * s2.getTiedot() === "2|Aku Ankka";
     * </pre>
     */
    public String getNimi() {
        return this.nimi;
    }
    
    /**
     * @return sarjakuvan id:n
     */
    public int getSid() {
        return this.sid;
    }
    
    /**
     * @return sarjakuvan tiedot merkkijonona
     */
    public String getTiedot() {
        return this.sid + "|" + this.nimi;
    }
    
    /** 
     * Ottaa sarjakuvan tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setSid(tulokset.getInt("sid"));
        this.nimi = tulokset.getString("nimi");
    }    
    
    /**
     * Metodi sarjakuvan nimen asetukselle
     * @param nimi sarjakuvan nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    /**
     * Metodi sarjakuvan nimen asetukselle
     * @param id sarjakuvalle asetettava sid
     */
    public void setSid(int id) {
        this.sid = id;
    }
     
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == sid ) return;
        setSid(id);
    }
}
