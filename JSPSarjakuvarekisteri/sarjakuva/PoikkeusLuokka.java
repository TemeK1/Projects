package sarjakuva;

/**
 * Poikkeusluokka tietorakenteesta johtuville poikkeuksille.
 * @author Teemu Käpylä
 * @version 16.07.2019
 */
public class PoikkeusLuokka extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Poikkeuksen muodostaja jolle tuodaan poikkeuksessa
     * käytettävä viesti
     * @param viesti Poikkeuksen viesti
     */
    public PoikkeusLuokka(String viesti) {
        super(viesti);
    }
}