package sarjakuva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Tietää yksittäisen tyypin asiat: id ja nimi.
 * Osaa tarkistaa niiden määrämuotoisuuden.
 * Osaa kääntää merkkijonon tyypin kentiksi ja kenttien sisällön merkkijonoksi.
 */
public class Tyyppi {
    private int tid;
    private String nimi;
    private static int seuraavaNro = 1;  
    
    /**
     * Oletusmuodostaja.
     */
    public Tyyppi() {
        this.tid = seuraavaNro;
        seuraavaNro++;
        this.nimi = "ei nimeä";
    }
    
    /**
     * Muodostaja.
     * @param nimi tyypin nimi.
     */
    public Tyyppi(String nimi) {
        this.tid = seuraavaNro;
        seuraavaNro++;
        this.nimi = nimi;
    }
    
    /**
     * @param tid sarjakuvan id-tunniste
     * @param nimi sarjakuvan nimi
     */
    public Tyyppi(int tid, String nimi) {
        this.tid = tid;
        this.nimi = nimi;
        seuraavaNro++;
    }
    
    /**
     * Antaa tyypin lisäyslausekkeen
     * @param con tietokantayhteys
     * @return jäsenen lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement getLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Tyypit" +
                "(tid, nimi) " +
                "VALUES (?, ?)");
        
        // Syötetään kentät näin välttääksemme SQL injektiot.
        // Käyttäjän syötteitä ei ikinä vain kirjoiteta kysely
        // merkkijonoon tarkistamatta niitä SQL injektioiden varalta!
        if ( tid != 0 ) sql.setInt(1, tid); else sql.setString(1, null);
        sql.setString(2, nimi);
        
        return sql;
    }
    
    /**
     * Antaa tietokannan luontilausekkeen tyyppitaululle
     * @return tyyppitaulun luontilauseke
     */
    public String getLuontilauseke() {
        return "CREATE TABLE Tyypit (\n" +
                "tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "nimi VARCHAR(100) NOT NULL\n" +
                ")";
    }
    
    /**
     * @return palauttaa tyypin nimen.
     * @example
     * <pre name="test">
     * Tyyppi $tyyppi = new Tyyppi($nimi);
     * $tyyppi.getNimi() === $nimi;
     * 
     * $tyyppi | $nimi
     * ---------------
     *  t1     | "Kovakantinen"
     *  t2     | "Pehmeäkantinen"
     *  t3     | "Albumi"
     *  t4     | "Liuska"
     *  t5     | "Manga"
     *  t6     | "Kirja"
     *  
     *  t6.setNimi("Book");
     *  t6.getNimi() === "Book";
     * </pre>
     */
    public String getNimi() {
        return this.nimi;
    }
    
    /**
     * @return palauttaa tyypin id:n
     */
    public int getTid() {
        return this.tid;
    }
       
    /**
     * @return palauttaa yksittäisen tyypin tiedot merkkijonona
     */
    public String getTiedot() {
        return this.tid + "|" + this.nimi;
    }
    
    
    /** 
     * Ottaa tyypin tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTid(tulokset.getInt("tid"));
        this.nimi = tulokset.getString("nimi");
    }
    
    /**
     * Metodi tyypin nimen asetukselle
     * @param nimi tyypin nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    /**
     * Metodi tyypin nimen asetukselle
     * @param id tyypille asetettava tid
     */
    public void setTid(int id) {
        this.tid = id;
    }
     
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == tid ) return;
        setTid(id);
    }
}
