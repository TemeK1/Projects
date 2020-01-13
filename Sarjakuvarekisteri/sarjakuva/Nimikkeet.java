package sarjakuva;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Huolehtii kaikista nimikkeistä kokonaisuutena, kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii ja lajittele nimikkeitä (paljon töitä).
 * ns. nimike-rekisteri eli sanan varsinaisessa mielessä the sarjakuvarekisteri.
 */
public class Nimikkeet {
    private Kanta kanta;
    private static Nimike apunimike = new Nimike();
    private static ArrayList<Nimike> nimikkeet = new ArrayList<Nimike>();
    
    /**
     * Tarkistetaan että kannassa jäsenten tarvitsema taulu
     * @param nimi tietokannan nimi
     */
    public Nimikkeet(String nimi) {
        kanta = Kanta.alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tietokannan metadata ja tarkistetaan siitä onko
            // Jasenet nimistä taulua olemassa.
            // Jos ei ole, luodaan se. Ei puututa tässä siihen, onko
            // mahdollisesti olemassa olevalla taululla oikea rakenne,
            // käyttäjä saa kuulla siitä virheilmoituksen kautta
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Nimikkeet", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Nimikkeet taulu
                    try ( PreparedStatement sql = con.prepareStatement(apunimike.getLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            System.err.println("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * @param nimike lisättävä nimike
     * @throws PoikkeusLuokka poikkeustilanteissa
     */
    public void lisaa(Nimike nimike) throws PoikkeusLuokka {
           try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = nimike.getLisayslauseke(con) ) {
               sql.executeUpdate();
               try ( ResultSet rs = sql.getGeneratedKeys() ) {
                  nimike.tarkistaId(rs);
               }   
               
           } catch (SQLException e) {
               throw new PoikkeusLuokka("Ongelmia tietokannan kanssa:" + e.getMessage());
           }
    }
    
    /**
     * @return löydetyt nimikkeet
     * @throws PoikkeusLuokka virhetilanteissa
     */
    public ArrayList<Nimike> hae() throws PoikkeusLuokka {
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Nimikkeet") ) {
            nimikkeet = new ArrayList<Nimike>();
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                    Nimike j = new Nimike();
                    j.parse(tulokset);
                    nimikkeet.add(j);
                }
            }
            return nimikkeet;
        } catch ( SQLException e ) {
            throw new PoikkeusLuokka("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * Välittäjämetodi arvon kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen arvo
     */
    public double getArvo(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getArvo();
    }
    
    /**
     * @return palauttaa rahallisen arvon keskiarvon.
     */
    public double getArvoTotal() {
        if (nimikkeet.size() == 0) return 0;
        double summa = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            summa += nimikkeet.get(i).getArvo();
        }
        return summa;
    }
    
    /**
     * @return palauttaa tiedon siitä mitä sarjakuvia on kaikista eniten.
     */
    public int getEnitenSarjakuvia() {
        int montako = 0;
        int mita = 0;
        
        var kartta = new HashMap<Integer, Integer>();
        for (Nimike lehti : nimikkeet) {
            Integer key = lehti.getSid();
            Integer value = kartta.get(key);
            
            if (value != null) value += 1;
            else value = 1;
            kartta.put(key, value);
            
            if (montako < value) {
                montako = value;
                mita = key;
            }      
        }
        return mita;
    }
    
    /**
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen id:n
     */
    public int getId(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getId(); 
    }
    
    /**
     * Välittäjämetodi kunnon kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen kunnon
     */
    public int getKunto(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getKunto();
    }
    
    /**
     * @return palauttaa kunto-luokituksen keskiarvon.
     */
    public double getKuntoKa() {
        if (nimikkeet.size() == 0) return 0;
        double summa = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            summa += nimikkeet.get(i).getKunto();
        }
        return summa / nimikkeet.size();
    }
    
    /**
     * Välittäjämetodi lisätietojen kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen lisätiedot
     */
    public String getLisatietoja(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getLisatietoja();
    }
    
    /**
     * @return palauttaa nimikkeiden määrän.
     */
    public int getMaara() {
        return nimikkeet.size();
    }
    
    /**
     * Välittäjämetodi nimen kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen nimen
     */
    public String getNimi(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getNimi();
    }
    
    /**
     * @param tunniste nimikkeen tunniste
     * @return palauttaa nimikkeen numeron
     */
    public int getNimikeNro(int tunniste) {
        return nimikkeet.get(tunniste).getNimikeNro();
    }
    
    /**
     * Välittäjämetodi nimikkeen julkaisunumeron kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen numeron
     */
    public int getNumero(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getNumero();
    }
    
    /**
     * Välittäjämetodi nimikkeen sarjakuva-id:n kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen sarjakuva-id:n
     */
    public int getSid(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getSid();
    }
    
    /**
     * Välittäjämetodi nimikkeen tyyppi-id:n kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen tyyppi-id:n
     */
    public int getTid(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getTid();
    }
    
    /**
     * @param tunniste nimikkeen id-tunnisteen.
     * @return nimikekohtaiset tiedot yhtenä pakettina
     */
    public String getTiedot(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getTiedot();
    }
    
    /**
     * @param tunniste mikä nimike palautetaan.
     * @return palauttaa olion.
     * @throws IndexOutOfBoundsException laiton i
     */
    public Nimike getNimike(int tunniste) throws IndexOutOfBoundsException {
        return nimikkeet.get(tunniste);  
    }
    
    /**
     * Välittäjämetodi nimikkeen julkaisuvuoden kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen julkaisuvuoden
     */
    public int getVuosi(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return nimikkeet.get(id).getVuosi();
    }
    
    /**
     * @return palauttaa vanhimman vuosikerran.
     */
    public int getVuosiMin() {
        int vanhin = Integer.MAX_VALUE;
        for (int i = 0; i < nimikkeet.size(); i++) {
           if (vanhin > nimikkeet.get(i).getVuosi()) 
             vanhin = nimikkeet.get(i).getVuosi();
        }
        return vanhin;
    }
    
    /**
     * @return palauttaa tuoreimman vuosikerran.
     */
    public int getVuosiMax() {
        int tuorein = Integer.MIN_VALUE;
        for (int i = 0; i < nimikkeet.size(); i++) {
           if (tuorein < nimikkeet.get(i).getVuosi()) 
             tuorein = nimikkeet.get(i).getVuosi();
        }
        return tuorein;
    }
    
    /**
     * Poistaa nimikkeen tietorakenteesta.
     * @param poistettava poistettava nimike
     */
    public void poista(int poistettava) {
        int nimike = -1;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (nimikkeet.get(i).getNimikeNro() == poistettava) nimike = i;
        }
        if (nimike > -1) nimikkeet.remove(nimike);
    }

    /**
     * @param os outputstream
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
}
