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
 * Huolehtii sarjakuvista kokonaisuutena: kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii sarjakuvia sid:n perusteella.
 * Avustajat: Sarjakuva-luokka.
 */
public class Sarjakuvat {
    private Kanta kanta;
    private static Sarjakuva apusarjakuva = new Sarjakuva();
    private static ArrayList<Sarjakuva> loytyneet = new ArrayList<Sarjakuva>();
   
    
    /**
     * Oletusmuodostaja.
     */
    public Sarjakuvat() {
        //
    }
    
    /**
     * Tarkistetaan että kannassa jäsenten tarvitsema taulu
     * @param nimi tietokannan nimi
     */
    public Sarjakuvat(String nimi) {
        kanta = Kanta.alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tietokannan metadata ja tarkistetaan siitä onko
            // Jasenet nimistä taulua olemassa.
            // Jos ei ole, luodaan se. Ei puututa tässä siihen, onko
            // mahdollisesti olemassa olevalla taululla oikea rakenne,
            // käyttäjä saa kuulla siitä virheilmoituksen kautta
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Sarjakuvat", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Sarjakuvat taulu
                    try ( PreparedStatement sql = con.prepareStatement(apusarjakuva.getLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            System.err.println("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * @param sarjakuva lisättävä sarjakuva
     * @throws PoikkeusLuokka poikkeustilanteissa
     */
    public void lisaa(Sarjakuva sarjakuva) throws PoikkeusLuokka {
           try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = sarjakuva.getLisayslauseke(con) ) {
               sql.executeUpdate();
               try ( ResultSet rs = sql.getGeneratedKeys() ) {
                  sarjakuva.tarkistaId(rs);
               }   
               
           } catch (SQLException e) {
               throw new PoikkeusLuokka("Ongelmia tietokannan kanssa:" + e.getMessage());
           }
    }
    
    /**
     * @return löydetyt sarjakuvat
     * @throws PoikkeusLuokka virhetilanteissa
     */
    public ArrayList<Sarjakuva> hae() throws PoikkeusLuokka {
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Sarjakuvat") ) {
            loytyneet = new ArrayList<Sarjakuva>();
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                    Sarjakuva j = new Sarjakuva();
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
     * @param sid sarjakuvan id
     * @return palauttaa sarjakuvan nimen
     * @throws NullPointerException ei ole tällaista.
     */
    public String getNimi(int sid) throws NullPointerException {
        if (sid == -1 || sid == 0) return "-";
        int id = 0;
        for (int i = 0; i < loytyneet.size(); i++) {
            if ( loytyneet.get(i).getSid() == sid ) { 
                id = i;
                break;
            }
        }
        return loytyneet.get(id).getNimi();
    }
    
    /**
     * @return palauttaa kaikki sarjakuvat.
     */
    public ArrayList<Sarjakuva> getSarjakuvat() {
        return loytyneet;
    }
    
    /**
     * @param id taulukon indeksi
     * @return palauttaa yhden sarjakuvan
     */
    public Sarjakuva getSarjakuva(int id) {
        return loytyneet.get(id);
    }
}