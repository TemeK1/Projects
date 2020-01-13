package sarjakuva;

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
     * Metodi sarjakuvan nimen asetukselle
     * @param nimi sarjakuvan nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
}
