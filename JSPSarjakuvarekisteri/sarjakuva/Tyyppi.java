package sarjakuva;

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
        this.nimi = "ei nime�";
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
     * Metodi tyypin nimen asetukselle
     * @param nimi tyypin nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
}
