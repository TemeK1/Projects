package graafinenKomento;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import fi.jyu.mit.ohj2.Help;
import fi.jyu.mit.ohj2.Mjonot;
import fi.jyu.mit.ohj2.Syotto;

/**
 * 
 */
public class Komentorivi {

    /**
     *  Suorittaa yhden komennon.
     */
    public interface KomentoRajapinta {
        /**
         * @param parametrit
         * @return palauttaa merkkijonon.
         */
        String suorita(String parametrit);
    }

    /**
     * @author Teemu Käpylä
     * @version 19.7.2019
     * Ynnaa-luokka paloittelee merkkijonon numeroiksi ja 
     * laskee ne yhteen ja lopuksi palauttaa summan merkkijonona.
     */
    public static class Ynnaa implements KomentoRajapinta {
        @Override
        public String suorita(String parametrit) {
            StringBuilder data = new StringBuilder(parametrit);
            
            double summa = 0;
            try {
                while (!(data.equals(""))) {
                    summa += Double.parseDouble(Mjonot.erota(data, ' '));  
                }
                return "" + summa;
            } catch (NumberFormatException ex) {
                //
            }
            return "Total is " + summa;
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 22.7.2019
     * Tulostaa vinkit ohjelman käyttöön liittyen.
     */
    public static class Apua implements KomentoRajapinta {
        private Help help;
        private ByteArrayOutputStream bos = new ByteArrayOutputStream();
       
        /**
         * @param tiedosto tiedoston nimi
         */
        public Apua(String tiedosto) {
            try {
              this.help = new Help(tiedosto);
            } catch (IOException ex) {
                System.err.println("Tiedostoa ei saada luettua " + ex.getMessage());
            }
        }
           
        @Override
        public String suorita(String parametrit) {
            help.helpTopic(parametrit + "*");    
            help.setOut(new PrintStream(bos));
            return bos.toString();
        }        
    }
    
    /**
     * @author Teemu Käpylä
     * @version 23.7.2019
     * Ft to m.
     */
    public static class Yksikot implements KomentoRajapinta {     
        private static String muistissa = "ft";
        
        @Override
        public String suorita(String parametrit) {
            StringBuilder b = new StringBuilder(parametrit);
            double oletus = 10;
            try {
            String yksikko = Mjonot.erota(b, ' ', muistissa);
            double kaannettava = Mjonot.erota(b, ' ', oletus);
            muistissa = yksikko;
            if (yksikko.equals("ft")) return "" + kaannettava + " ft. is " + String.format("%.2f", (kaannettava * 0.3048)) + " m.";
            if (yksikko.equals("m")) return "" + kaannettava + " m. is " + String.format("%.2f", (kaannettava * 3.2808399)) + " ft.";
            if (yksikko.equals("lb")) return "" + kaannettava + " lb. is " + String.format("%.2f", (kaannettava * 0.45359237)) + " kg.";
            if (yksikko.equals("kg")) return "" + kaannettava + " kg is " + String.format("%.2f", (kaannettava * 2.20462262)) + " lb.";
            } catch (Exception ex) {
                return "Not valid unit.";
            }
            return "0.0";
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 23.7.2019
     * DnD 5 esineet-hakukone
     */
    public static class Termit implements KomentoRajapinta {
        private ArrayList<Termi> termit = new ArrayList<Termi>();       
        
        /**
         * Lukee esineet tiedostosta.
         */
        public Termit() {
            lueTiedosto();
        }
        
        @Override
        public String suorita(String parametrit) {
            ArrayList<Termi> haku = new ArrayList<Termi>();
            
            //String termi2 = Mjonot.erota(b, ' ', oletus);
            
            for (int i = 0; i < termit.size(); i++) {
                Termi termi2 = termit.get(i);
                if (termi2.getSisalto().toLowerCase().matches(parametrit + "(.*)") ||
                    termi2.getSisalto().toLowerCase().matches("(.*)" + parametrit + "(.*)")) {
                    haku.add(termi2);
                }
                if (haku.size() == 5) break;
            }
           
            if (haku.size() == 0) return "no results found";
            StringBuilder palautettava = new StringBuilder();
            for (int i = 0; i < haku.size(); i++) {
                palautettava.append(haku.get(i) + "\n" + "---------------------------" + "\n");
            }
            return palautettava.toString();
        }
        
        private void lueTiedosto() {
            String tiedosto = "knowledge.txt";
            
            try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
               StringBuilder b = new StringBuilder();
               Termi termi1 = new Termi();
               while (tutka.hasNext()) {
                   try {
                       b.append(tutka.nextLine() + "&&");
                       if (b.indexOf("|") > -1) { 
                           termi1.parse(b);
                           this.lisaa(termi1);
                           termi1 = new Termi();
                           b = new StringBuilder("");
                           continue;
                       }    
                   } catch (NumberFormatException ex) {
                       System.err.println("Wrong format " + ex.getMessage());
                   } catch (IndexOutOfBoundsException ex) {
                       System.err.println("There is a format error in the data file: " + ex.getMessage());
                   }
               }
            } catch (FileNotFoundException ex) {
                System.err.println("Can not open the file " + ex.getMessage());
            } 
        }
        
        private void lisaa(Termi termi1) {
            termit.add(termi1);
        }
        
        /**
         * @author Teemu Käpylä
         * @version 23.7.2019
         * Yksittäiset termit
         */
        public static class Termi {
            private String sisalto;
            
            /**
             * Oletusmuodostaja.
             */
            public Termi() {
                //
            }
            
            /**
             * @return palauttaa esineen nimen.
             */
            public String getSisalto() {
                return this.sisalto;
            }
            
            @Override
            public String toString() {
                return getSisalto();
            }
            
            private void parse(StringBuilder rivi) {

                this.sisalto = Mjonot.erota(rivi, '|', false);
            }     
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 23.7.2019
     * DnD 5 esineet-hakukone
     */
    public static class Items implements KomentoRajapinta {
        private ArrayList<Item> esineet = new ArrayList<Item>();       
        
        /**
         * Lukee esineet tiedostosta.
         */
        public Items() {
            lueTiedosto();
        }
        
        @Override
        public String suorita(String parametrit) {
            ArrayList<Item> haku = new ArrayList<Item>();
            
            //String termi2 = Mjonot.erota(b, ' ', oletus);
            
            for (int i = 0; i < esineet.size(); i++) {
                Item item2 = esineet.get(i);
                if (item2.getNimi().toLowerCase().matches("(.*)" + parametrit + "(.*)") ||
                    item2.getLisatietoja().toLowerCase().matches("(.*)" + parametrit + "(.*)")) {
                    haku.add(item2);
                }
            }
            if (haku.size() == 0) return "no results found";
            StringBuilder palautettava = new StringBuilder();
            for (int i = 0; i < haku.size(); i++) {
                palautettava.append(haku.get(i) + "\n");
            }
            return palautettava.toString();
        }
        
        private void lueTiedosto() {
            String tiedosto = "items.txt";
            
            try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
               String rivi;
               while (tutka.hasNext()) {
                   try {
                       rivi = tutka.nextLine();
                       Item esine = new Item(); 
                       esine.parse(rivi);
                       this.lisaa(esine);      
                   } catch (NumberFormatException ex) {
                       System.err.println("Wrong format " + ex.getMessage());
                   } catch (IndexOutOfBoundsException ex) {
                       System.err.println("There is a format error in the data file: " + ex.getMessage());
                   }
               }
            } catch (FileNotFoundException ex) {
                System.err.println("Can not open the file " + ex.getMessage());
            } 
        }
        
        private void lisaa(Item esine) {
            esineet.add(esine);
        }
        
        /**
         * @author Teemu Käpylä
         * @version 23.7.2019
         * Yksittäiset esineet
         */
        public static class Item {
            private String name;
            private String price;
            private String weight;
            private String information;
            
            /**
             * Oletusmuodostaja.
             */
            public Item() {
                //
            }
            
            /**
             * @return palauttaa esineen nimen.
             */
            public String getNimi() {
                return this.name;
            }
            
            /**
             * @return palauttaa esineen lisätiedot.
             */
            public String getLisatietoja() {
                return this.information;
            }
            
            @Override
            public String toString() {
                StringBuilder b = new StringBuilder();
                b.append(this.name + " | ");
                b.append(this.price + " | ");
                b.append(this.weight + " | ");
                b.append(this.information);
                
                return b.toString();
            }
            
            private void parse(String rivi) {
                String rivi2 = rivi.replaceAll("  ", "");
                StringBuilder b = new StringBuilder(rivi2);
                this.name = Mjonot.erota(b, '|', false);
                this.price = Mjonot.erota(b, '|', false);
                this.weight = Mjonot.erota(b, '|', false);
                this.information = Mjonot.erota(b, '|', false);
            }     
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 23.7.2019
     * DnD 5 taiat-hakukone
     */
    public static class Taiat implements KomentoRajapinta {
        private ArrayList<Taika> taiat = new ArrayList<Taika>();       
        private static int hakuMin = 0;
        private static int hakuMax = 9;
        
        /**
         * Lukee taiat tiedostosta.
         */
        public Taiat() {
            lueTiedosto();
        }
        
        @Override
        public String suorita(String parametrit) {
            ArrayList<Taika> haku = new ArrayList<Taika>();
            StringBuilder b = new StringBuilder(parametrit);
            
            
            String oletus = " ";
            String termi = Mjonot.erota(b, ' ', oletus);

            int min = 0;
            int max = 9;
            
            try {
                 min = Mjonot.erota(b, ' ', hakuMin);
                 max = Mjonot.erota(b, ' ', hakuMax);
            } catch (Exception ex) {
                //
            }
            
            String termi2 = Mjonot.erota(b, ' ', oletus);
            
            for (int i = 0; i < taiat.size(); i++) {
                Taika taika2 = taiat.get(i);
                if (taika2.getNimi().toLowerCase().matches("(.*)" + termi2 + "(.*)") &&
                    (taika2.getLvl() >= min && taika2.getLvl() <= max) &&
                    (taika2.toString().toLowerCase().matches("(.*)" + termi + "(.*)"))) {
                    haku.add(taika2);
                }
            }
            if (haku.size() == 0) return "no results found";
            StringBuilder palautettava = new StringBuilder();
            for (int i = 0; i < haku.size(); i++) {
                palautettava.append(haku.get(i) + "\n");
            }
            return palautettava.toString();
        }
        
        private void lueTiedosto() {
            String tiedosto = "spell_list.txt";
            
            try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
               String rivi;
               while (tutka.hasNext()) {
                   try {
                       rivi = tutka.nextLine();
                       Taika taika = new Taika(); 
                       taika.parse(rivi);
                       this.lisaa(taika);      
                   } catch (NumberFormatException ex) {
                       System.err.println("Wrong format " + ex.getMessage());
                   } catch (IndexOutOfBoundsException ex) {
                       System.err.println("There is a format error in the data file: " + ex.getMessage());
                   }
               }
            } catch (FileNotFoundException ex) {
                System.err.println("Can not open the file " + ex.getMessage());
            } 
        }
        
        private void lisaa(Taika taika) {
            taiat.add(taika);
        }
        
        /**
         * @author Teemu Käpylä
         * @version 23.7.2019
         * Yksittäiset taiat
         */
        public static class Taika {
            private int bard;
            private int cleric;
            private int druid;
            private int paladin;
            private int ranger;
            private int sorcerer;
            private int warlock;
            private int wizard;
            private int level;
            private int verbal;
            private int somatic;
            private int material;
            private int concentration;
            private String action;
            private String range;
            private String name;
            private String school;
            private String duration;
            private String price;
            
            
            /**
             * Oletusmuodostaja.
             */
            public Taika() {
                //
            }
            
            /**
             * @return palauttaa taian nimen.
             */
            public String getNimi() {
                return this.name;
            }
            
            /**
             * @return palauttaa taian tason
             */
            public int getLvl() {
                return this.level;
            }
            
            @Override
            public String toString() {
                StringBuilder b = new StringBuilder();
                b.append(this.name + " | ");
                if (this.bard == 1) b.append(" Bar.|");
                if (this.cleric == 1) b.append(" Cler.|");
                if (this.druid == 1) b.append(" Dru.|");
                if (this.paladin == 1) b.append(" Pal.|");
                if (this.ranger == 1) b.append(" Rang.|");
                if (this.sorcerer == 1) b.append(" Sorc.|");
                if (this.warlock == 1) b.append(" Warl.|");
                if (this.wizard == 1) b.append(" Wiz.|");
                b.append(" Lvl: " + this.level + " | ");
                
                if (this.verbal == 1) b.append("V ");
                if (this.somatic == 1) b.append("S ");
                if (this.material == 1) b.append("M ");
                if (this.concentration == 1) b.append("(c)");
                b.append("  | ");
                b.append(this.action + "| ");
                b.append(this.range + "| ");
                b.append(this.school + " | ");
                b.append(this.duration + " |");
                b.append("| Gold: " + this.price);
                
                return b.toString().replaceAll("  ", "");
            }
            
            private void parse(String rivi) {
                StringBuilder b = new StringBuilder(rivi);
                this.bard = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.cleric = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.druid = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.paladin = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.ranger = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.sorcerer = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.warlock = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.wizard = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.level = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.verbal = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.somatic = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.material = Integer.parseInt(Mjonot.erota(b, '|', false));
                this.concentration = Integer.parseInt(Mjonot.erota(b, '|', false));
                
                this.action = Mjonot.erota(b, '|', false);
                this.range = Mjonot.erota(b, '|', false);
                this.name = Mjonot.erota(b, '|', false);
                this.school = Mjonot.erota(b, '|', false);
                this.duration = Mjonot.erota(b, '|', false);
                this.price = Mjonot.erota(b, '|', false);
            }     
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 23.7.2019
     * Nopanheitto.
     */
    public static class Noppa implements KomentoRajapinta {
        private static int viimeksi = 1;
        private static int viimeksi2 = 6;
        
        @Override
        public String suorita(String parametrit) {
            Random randomi = new Random();
            StringBuilder b = new StringBuilder(parametrit);
            int montako = Mjonot.erota(b, ' ', viimeksi);
            int noppa = Mjonot.erota(b, ' ', viimeksi2);
            
            String tulos = "";
            int i = 0;
            int summa = 0;
            while (i < montako) {
                int numero = randomi.nextInt(noppa);
                numero += 1;
                summa += numero;
                tulos = tulos + numero + " ";
                i++;
            }
            
            viimeksi = montako;
            viimeksi2 = noppa;
            
            return "Results were (d" + noppa + ") " + tulos + "| Total: " + summa;
        }
    }
    
    /**
     * @author Teemu Käpylä
     * @version 19.7.2019
     * Palauttaa tiedon siitä, että onko merkkijono palindromi vai ei.
     */
    public static class Palindromiko implements KomentoRajapinta {
        @Override
        public String suorita(String parametrit) {
            for (int i = 0, j = parametrit.length() -1; i < j; i++, j--) {
                if (parametrit.charAt(i) != parametrit.charAt(j))
                    return parametrit + " is not a palindrome :(";
            }
            return "The word " + parametrit + " is a palindrome!";
        }
    }
    /**
     * @author Teemu Käpylä
     * @version 19.7.2019
     * Muuttaa merkkijonon isoksi.
     */
    public static class Isoksi implements KomentoRajapinta {
        @Override
        public String suorita(String parametrit) {
            return parametrit + " in upper case results to " + parametrit.toUpperCase();
        }
    }

    
    /**
     * @author Teemu Käpylä
     * @version 19.7.2019
     * Tuntematon komento
     */
    public static class EiLoydy implements KomentoRajapinta {
        @Override
        public String suorita(String parametrit) {
            return "Unknown command!";
        }
    }
    
    /**
     * Komennon nimi ja vastaava "funktio".
     */
    public static class Komento {
        
        private String nimi;
        private KomentoRajapinta komento;
        
        /**
         * @param s merkkijono
         * @param kasky mikä komento kyseessä
         */
        public Komento(String s, KomentoRajapinta kasky) {
            this.nimi = s;
            this.komento = kasky;
        }
        
        /**
         * @return palauttaa komennon nimen.
         */
        public String getNimi() {
            return this.nimi;
        }
        
        /**
         * @return palauttaa komennon.
         */
        public KomentoRajapinta getKomento() {
            return this.komento;
        }    
    }

    /**
     * Lista komennoista ja metodit etsimiseksi ja suorittamiseksi.
     */
    public static class Komennot {
        private static List<Komento> komennot = new ArrayList<Komento>();
        
        
        /**
         * @param k lisätty komento
         */
        public void add(Komento k) {
            Komento uusi = k;
            komennot.add(uusi);
        }
        
        /**
         * @param jono käsiteltävä komento ja määreet
         * @return palauttaa vastauksen käsitellystä operaatiosta.
         */
        public String tulkitse(String jono) {
            StringBuilder b = new StringBuilder(jono);
            String komento = Mjonot.erota(b, ' ', false);
            String parametrit = b.toString();
            Komento kasiteltava = new Komento("eiloydy", new EiLoydy());
            for (Komento kasky : komennot) {
               if (komento.equals(kasky.getNimi())) {
                   kasiteltava = kasky;
                   break;
               }
            }
            if (kasiteltava.getNimi().equals("eiloydy")) return "Unknown command " + komento;
            return kasiteltava.getKomento().suorita(parametrit);
        }
        
    }
   
    /**
     * Testipääohjelma
     * @param args ei käytössä
     * @example
     * <pre name="test">
     * Komennot komennot2 = new Komennot();
     * komennot2.add(new Komento("ynnää", new Ynnaa()));
     * komennot2.add(new Komento("isoksi", new Isoksi()));
     * String komento1 = "ynnää 5 10 15";
     * String s = komennot2.tulkitse(komento1);
     * s === "Tulos on 30";
     * </pre>
     */
    public static void main(String[] args) {
        Komennot komennot = new Komennot();
        Apua apua = new Apua("tiedosto.txt");
        komennot.add(new Komento("?", apua));
        komennot.add(new Komento("apua", apua));
        komennot.add(new Komento("+", new Ynnaa()));
        komennot.add(new Komento("ynnää", new Ynnaa()));
        komennot.add(new Komento("isoksi", new Isoksi()));
        komennot.add(new Komento("palindromiko", new Palindromiko()));
        komennot.add(new Komento("noppa", new Noppa()));
        komennot.add(new Komento("d", new Noppa()));
        komennot.add(new Komento("c", new Yksikot()));
        komennot.add(new Komento("convert", new Yksikot()));
        komennot.add(new Komento("spell", new Taiat()));
        komennot.add(new Komento("item", new Items()));
        komennot.add(new Komento("search", new Termit()));

        String s;

        while (true) {
            s = Syotto.kysy("Command:");
            if ("".equals(s))
                break;
            String tulos = komennot.tulkitse(s);
            System.out.println(tulos.replaceAll("&&", "\n"));
        }
    }   
}