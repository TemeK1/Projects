package sarjakuva;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Huolehtii tyypeistä kokonaisuutena: kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii tyyppejä tid:n perusteella.
 * Avustajat: Tyyppi-luokka.
 */
public class Tyypit {
    private Kanta kanta;
    private static Tyyppi aputyyppi = new Tyyppi();
    private static ArrayList<Tyyppi> loytyneet = new ArrayList<Tyyppi>();
    
    /**
     * Oletusmuodostaja.
     */
    public Tyypit() {
        //
    }
    
    /**
     * Tarkistetaan että kannassa jäsenten tarvitsema taulu
     * @param nimi tietokannan nimi
     */
    public Tyypit(String nimi) {
        kanta = Kanta.alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tietokannan metadata ja tarkistetaan siitä onko
            // Jasenet nimistä taulua olemassa.
            // Jos ei ole, luodaan se. Ei puututa tässä siihen, onko
            // mahdollisesti olemassa olevalla taululla oikea rakenne,
            // käyttäjä saa kuulla siitä virheilmoituksen kautta
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Tyypit", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Tyypit taulu
                    try ( PreparedStatement sql = con.prepareStatement(aputyyppi.getLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            System.err.println("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    
    /**
     * @param tyyppi lisättävä tyyppi
     * @throws PoikkeusLuokka poikkeustilanteissa
     */
    public void lisaa(Tyyppi tyyppi) throws PoikkeusLuokka {
           try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = tyyppi.getLisayslauseke(con) ) {
               sql.executeUpdate();
               try ( ResultSet rs = sql.getGeneratedKeys() ) {
                  tyyppi.tarkistaId(rs);
               }   
               
           } catch (SQLException e) {
               throw new PoikkeusLuokka("Ongelmia tietokannan kanssa:" + e.getMessage());
           }
    }
    
    /**
     * @return löydetyt tyypit
     * @throws PoikkeusLuokka virhetilanteissa
     */
    public ArrayList<Tyyppi> hae() throws PoikkeusLuokka {
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Tyypit") ) {
            loytyneet = new ArrayList<Tyyppi>();
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                    Tyyppi j = new Tyyppi();
                    j.parse(tulokset);
                    loytyneet.add(j);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new PoikkeusLuokka("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Metodi nimen kysymiseen.
     * @param tid tyypin id
     * @return palauttaa tyypin nimen
     * @throws NullPointerException ei ole tällaista.
     */
    public String getNimi(int tid) throws NullPointerException {
        if (tid == -1 || tid == 0) return "-";
        int id = 0;
        for (int i = 0; i < loytyneet.size(); i++) {
            if ( loytyneet.get(i).getTid() == tid ) { 
                id = i;
                break;
            }
        }
        return loytyneet.get(id).getNimi();
    }
    
    /**
     * @return palauttaa kaikki tyypit.
     */
    public ArrayList<Tyyppi> getTyypit() {
        return loytyneet;
    }
    
    /**
     * @param id tietorakenteen indeksi
     * @return palauttaa kaikki tyypit.
     */
    public Tyyppi getTyyppi(int id) {
        return loytyneet.get(id);
        
    }
}
