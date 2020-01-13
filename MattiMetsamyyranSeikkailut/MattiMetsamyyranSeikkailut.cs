using System.Collections.Generic;
using Jypeli;
using Jypeli.Widgets;

/// @author Teemu Käpylä
/// @version 22.3.2019
/// <summary>   
/// Matti Metsämyyrän seikkailut. Pelissä on idea kerätä mahdollisimman paljon auringonkukansiemeniä Matin talvipesään,
/// jotta hän selviäisi pitkän talven tuomista haasteista,
/// samalla välttäen joutumasta lumikon saaliiksi. Pelissä on kolme eri vuodenaikaa: kesä, syksy ja talvi. Kesästä alotetaan.
/// Yksi vuodenaika kestää 30 sekuntia. Lisäaikaa saa kerätystä ruoasta. 
/// Pelin alussa generoidaan sattumanvaraisesti siemeniä, elämäitemejä (sydämet) ja kahdenlaisia lumikkoja: 
/// sattumanvaraisesti liikkuvia 
/// sekä Mattia etäisyydeltä x jahtaavia lumikkoja. 
/// Jännitystä lisää se, että pelaaja ei voi ulkoisesti tunnistaa kumpi vihu on kyseessä 
/// (tosin lumikko-tutkasta löytyy apuja tähänkin pulmaan). 
/// Top-listan kärkeen pääsee se, joka selviää hengissä ja onnistuu keräämään eniten ruokaa talvipesään ennen ajan loppumista. 
/// Ajankulkua voi seurata käyttöliittymän laskurista.
/// Matti menettää aina yhden elämäpisteen kosketuksesta vihuun (joten oikean yläreunan elämämittaria kannattaa tarkkailla visusti). 
/// Älä siis kuole, ellet halua menettää huipputulostasi.
/// Kuten oikeassa luonnossa, myyrällä ei ole keinoja tappaa lumikkoa. Ainoa keino on paeta.
/// 
/// Ääniefektit ovat omaa tuotantoa. Ne on itse nauhoitettu ja käsitelty Audacityllä autenttisen lopputuloksen aikaansaamiseksi.
/// Menun taustakuva on myös pelintekijän oma otos.
/// 
/// Kiitos seuraavaksi mainituista upeista grafiikoista kuuluu @Arjane:lle, joka piirsi ne ekslusiivisesti käytettäväksi vain
/// Matti Metsämyyrän Seikkailut pelissä: myyrä, lumikot, pelikentän tausta ja sydän.
/// </summary>

public class MattiMetsamyyra : PhysicsGame
{
    /// <summary>
    /// Vakiot ja attribuutit sekä pelin sisällön lataaminen.
    /// </summary>
    
    //Vakiomuuttujat sekä pari muuta
    private const bool painovoima = false;
    private const int leveys = 1280;
    private const int korkeus = 768;
    private bool koskettaakoSiementa = true; //pelaajan ja siemenen välisen liitoksen tarkastelua
    private int vuodenAika = 0; //kevät

    //Seuraavissa taulukoissa on pelin asetuksia: yleiset sekä fysiikka-asetukset, joita sitten tarvittavissa 
    //aliohjelmissa kutsutaan oikealta riviltä ja sarakkeelta

    //pelinAsetuksiaInt = ruudunKoko, vuodenAjanKesto, vihuryhmä, matin ryhmä
    private static readonly int[] pelinAsetuksiaInt = new int[4] { 80, 60, 2, 4 };
    //pelinAsetuksia = matin nopeus, lisäaika yhdestä siemenestä, tarinallisten viestien intervalli, kimmoisuus, 
    //lepokitka, liikekitka, nopeuskerroin ja kulmanopeuskeroin.
    private static readonly double[] pelinAsetuksia = new double[8] { 500.0, 3.5, 1.0, 0.0, 1.0, 1.0, 0.95, 0.95 };
    //kenttienOliot = kesa, syksy, talvi: siemenet, sydamet, lumikot ja jahtaavat lumikot. Generoitavien
    //olioiden määrät siis kutakin vuodenaikaa kohden.
    private static readonly int[,] kenttienOliot = new int[3, 4] { { 80, 10, 15, 8 }, { 50, 5, 7, 4 }, { 50, 5, 4, 3 } };
    private static readonly int[] ympyranSateet = new int[6] { 500, 1000, 1500, 2500, 3500, 700 };

    //pelissä tarvittavien olioiden alustusta
    private EasyHighScore topLista = new EasyHighScore();
    private DoubleMeter elamaLaskuri;
    private IntMeter Ruokalaskuri;
    private PhysicsObject matti1;
    private GameObject kotiPesa;
    private DoubleMeter alaspainLaskuri;
    private Timer aikaLaskuri;
    private readonly List<PhysicsObject> lumikot = new List<PhysicsObject>();

    //ladataan ääniefektit
    private SoundEffect loydaAarre = LoadSoundEffect("happy_vole");
    private SoundEffect saikahdys = LoadSoundEffect("squeal");
    private SoundEffect lumikkoAani = LoadSoundEffect("weasel_s");

    //ladataan kuvat
    private static readonly Image mattiTitle = LoadImage("matti_title");
    private static readonly Image siemenenKuva = LoadImage("seed");
    private static readonly Image reunanKuva = LoadImage("reuna");
    private static readonly Image nakokenttaKuva = LoadImage("pimennettyNakokentta");
    private static readonly Image kesaTausta = LoadImage("Grass");
    private static readonly Image syysTausta = LoadImage("Grass_autumn");
    private static readonly Image talviTausta = LoadImage("Grass_winter");
    private static readonly Image menuKuva = LoadImage("menu_tausta");
    private static readonly Image maanAlla = LoadImage("maanAlla");
    private static readonly Image elamaTyhja = LoadImage("hitpoint_empty");
    private static readonly Image elamaTaysi = LoadImage("hitpoint_full");

    //ladataan kuvat animaatioita varten
    private static readonly Image[] talviPesa = LoadImages("Nest", "Nest2");
    private static readonly Image[] huutoMerkki = LoadImages("huutomerkki", "huutomerkki2");
    private static readonly Image[] taloMerkki = LoadImages("talo4", "talo3", "talo2", "talo1");
    private static readonly Image[] myyranLiike = LoadImages("vole1", "vole2");
    private static readonly Image[] lumikonLiike = LoadImages("weasel_s_1", "weasel_s_2");
    private static readonly Image[] lumikonLiikeTalvi = LoadImages("weasel_w_1", "weasel_w_2");
    private static readonly Image[] sydanLiike = LoadImages("heart1", "heart2");

    /// <summary>
    /// Begin-aliohjelma. Kentän koon ja käyttöliittymän määrittely.
    /// </summary>
    public override void Begin()
    {
        SetWindowSize(leveys, korkeus, false);
        Menu();
    }


    private void Menu()
    {
        MultiSelectWindow alkuMenu = new MultiSelectWindow("", "Aloita peli", "Parhaat pisteet", "Lopeta")
        {
            X = Screen.LeftSafe + 600,
            Y = Screen.TopSafe - 500
        };
        Add(alkuMenu);
        alkuMenu.AddItemHandler(0, Aloitus);
        alkuMenu.AddItemHandler(1, TopLista);
        alkuMenu.AddItemHandler(2, Exit);
        alkuMenu.DefaultCancel = 2;

        Widget pelinNimi = new Widget(450, 250, Shape.Rectangle)
        {
            X = alkuMenu.X,
            Y = alkuMenu.Y + 300
        };
        Add(pelinNimi);
        pelinNimi.Image = mattiTitle;

        alkuMenu.Color = Color.Transparent;
        Level.Background.Image = menuKuva;
    }


    /// <summary>
    /// Lista parhaista pisteistä.
    /// </summary>
    private void TopLista()
    {
        topLista.Show();
        topLista.HighScoreWindow.Closed += AloitaPeli;
    }


    /// <summary>
    /// Aloitusaliohjelma. Nollaa kaiken, resetoi vuodenaika-muuttujan, kutsuu erinäisiä aliohjelmia kentänluontiin, 
    /// ohjainten asettamiseen ja elämälaskurin luomiseen.
    /// </summary>
    private void Aloitus()
    {
        ClearAll();
        IsPaused = false;
        Keyboard.Listen(Key.Escape, ButtonState.Pressed, ConfirmExit, "Lopeta peli");
        Level.BackgroundColor = Color.DarkJungleGreen;
        vuodenAika = 0;
        KentanLuonti();
        LuoElamaLaskuri();
    }


    /// <summary>
    /// Aliohjelma kentän luontiin. Luo kentän rajat ja maalialueen lukemalla tilejen sijainnit pikselikartalta.
    /// Luo kotipesän staattisena GameObjektina. Luo pelaajan ja ruokalaskurin.
    /// Aliohjelmakutsuja silmukoissa seuraavien olioiden luontiin: lumikot, jahtaavat lumikot ja sydämet.
    /// Sisältää lisäksi taustaväri sekä taustakuvamäärittelyn ja näkökenttäolion.
    /// Kertaluontoisissa SingleShot Timereissä kutsutaan myös Viestit-funktiota useita kertoja pelin alkutarinan kertomiseen. 
    /// Viimeisen viestin yhteydessä Matti "vapautetaan" asettamalla hänelle ohjaimet, aikalaskuri käynnistetään sekä Matin
    /// CollisionIgnoreGroup vaihdetaan, jotta vihulaiset pystyvät nyt vahingoittamaan häntä.
    /// </summary>
    private void KentanLuonti()
    {
        ColorTileMap ruudut = ColorTileMap.FromLevelAsset("kartta");

        ruudut.SetTileMethod(Color.Azure, LuoTaso);
        ruudut.SetTileMethod(Color.Brown, LuoMaali);
        ruudut.Execute(pelinAsetuksiaInt[0], pelinAsetuksiaInt[0]);

        kotiPesa = new GameObject(250, 250)
        {
            Position = new Vector(0, 0),
            Image = talviPesa[0]
        };
        Add(kotiPesa, 2);

        matti1 = Pelaaja(this, 0, -50);
        AddCollisionHandler<PhysicsObject, Ruoka>(matti1, PelaajaTormaaSiemeneen);
        AddCollisionHandler<PhysicsObject, Sydan>(matti1, PelaajaTormaaSydameen);
        AddCollisionHandler<PhysicsObject, Vihollinen>(matti1, PelaajaTormaaViholliseen);
        AddCollisionHandler<PhysicsObject, JahtaavaVihollinen>(matti1, PelaajaTormaaViholliseen);

        for (int i = 0; i < kenttienOliot[0, 1]; i++)
        {
            LuoSydan();
        }

        for (int i = 0; i < kenttienOliot[0, 2]; i++)
        {
            LuoLumikko(lumikonLiike);
        }

        for (int i = 0; i < kenttienOliot[0, 3]; i++)
        {
            LuoJahtaavaLumikko(lumikot, lumikonLiike);
        }

        Level.BackgroundColor = Color.DarkJungleGreen;
        Level.Background.Image = kesaTausta;
        Level.Background.TileToLevel();
        Camera.Follow(matti1);

        GameObject nakokentta = new GameObject(Screen.Width * 1.5, Screen.Height * 1.5)
        {
            Image = nakokenttaKuva,
            IsVisible = true
        };
        this.Add(nakokentta, 3);
        Layers[3].RelativeTransition = new Vector(0.0, 0.0);

        GameObject kompassi = new GameObject(40, 40)
        {
            Image = taloMerkki[3],
            Position = new Vector(-15, 100),
        };
        Add(kompassi, 3);

        GameObject huutomerkki = new GameObject(40, 40)
        {
            Image = huutoMerkki[1],
            Position = new Vector(15, 100),
        };
        Add(huutomerkki, 3);

        Keyboard.Listen(Key.W, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.A, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.S, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.D, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.Up, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.Left, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.Down, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);
        Keyboard.Listen(Key.Right, ButtonState.Down, KaannaKompassia, null, kompassi, matti1);

        Keyboard.Listen(Key.W, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.A, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.S, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.D, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.Up, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.Left, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.Down, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);
        Keyboard.Listen(Key.Right, ButtonState.Down, LumikkoTutka, null, lumikot, matti1, huutomerkki);


        Timer.SingleShot((pelinAsetuksia[2] * 0) + 1, () => { Viestit(8); });
        Timer.SingleShot(pelinAsetuksia[2] * 1, () => { Viestit(9); });
        Timer.SingleShot(pelinAsetuksia[2] * 2, () => { Viestit(10); });
        Timer.SingleShot(pelinAsetuksia[2] * 3, () => { Viestit(11); });
        Timer.SingleShot(pelinAsetuksia[2] * 4, () => { Viestit(12); });
        Timer.SingleShot(pelinAsetuksia[2] * 5, () => { Viestit(13); });
        Timer.SingleShot(pelinAsetuksia[2] * 6, () => { Viestit(14); });
        Timer.SingleShot(pelinAsetuksia[2] * 8, () => { Viestit(15); });
        Timer.SingleShot(pelinAsetuksia[2] * 8, () => { LuoAikaLaskuri(Screen.Right - 70.0, Screen.Top - 90); Ruokalaskuri = LuoPisteLaskuri(Screen.Right - 290.0, Screen.Top - 70.0); AsetaMatinOhjaimet(); matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[3]; matti1.Tag = "myyra"; });
    }


    /// <summary>
    /// Aliohjelma, joka luo kentän rajat sille syötettyjen parametrien perusteella.
    /// </summary>
    /// <param name="paikka">Vektori tilen sijainnille</param>
    /// <param name="leveys">Tilen leveys</param>
    /// <param name="korkeus">Tilen korkeus</param>
    private void LuoTaso(Vector paikka, double leveys, double korkeus)
    {
        PhysicsObject taso = PhysicsObject.CreateStaticObject(pelinAsetuksiaInt[0] * 1.5, pelinAsetuksiaInt[0] * 1.5);
        taso.Position = paikka;
        taso.Color = Color.Brown;
        taso.Image = reunanKuva;
        taso.CollisionIgnoreGroup = 5;
        taso.Restitution = 0.0;
        taso.Tag = "seina";
        Add(taso);
    }


    /// <summary>
    /// Luo ja määrittelee lumikko-oliot, jotka kulkevat kentällä sattumanvaraisesti.
    /// </summary>
    private void LuoLumikko(Image[] liike)
    {
        Vihollinen lumikko = new Vihollinen(200, 60, pelinAsetuksiaInt[0], Color.White)
        {
            X = RandomGen.NextDouble(Level.Left + 400, Level.Right - 400),
            Y = RandomGen.NextDouble(Level.Top - 400, Level.Bottom + 400),
            CanRotate = false,
            Restitution = pelinAsetuksia[3],
            LinearDamping = pelinAsetuksia[6],
            AngularDamping = pelinAsetuksia[7],
            CollisionIgnoreGroup = pelinAsetuksiaInt[2],
            Animation = new Animation(liike)
        };
        lumikko.Animation.Start();
        lumikko.Animation.FPS = 3;
        Add(lumikko);
    }


    /// <summary>
    /// Luo ja määrittelee pelaajaa jahtaavat lumikko-oliot.
    /// </summary>
    private void LuoJahtaavaLumikko(List<PhysicsObject> lumikot, Image[] liike)
    {
            JahtaavaVihollinen lumikko2 = new JahtaavaVihollinen(200, 60, pelinAsetuksiaInt[0], Color.Red)
        {
            X = RandomGen.NextDouble(Level.Left + 400, Level.Right - 400),
            Y = RandomGen.NextDouble(Level.Top - 400, Level.Bottom + 400),
            CanRotate = false,
            Restitution = pelinAsetuksia[3],
            LinearDamping = pelinAsetuksia[6],
            AngularDamping = pelinAsetuksia[7],
            CollisionIgnoreGroup = pelinAsetuksiaInt[2],
            Animation = new Animation(liike)
        };
        lumikko2.Animation.Start();
        lumikko2.Animation.FPS = 3;
        AddCollisionHandler(lumikko2, "seina", JahtaavaLumikkoTormaaPesaan);
        Add(lumikko2);
        lumikot.Add(lumikko2);
    }


    /// <summary>
    /// Määrittää sen mitä tapahtuu kun lumikko törmää pesä/maalialueeseen.
    /// </summary>
    /// <param name="hahmo">Törmäävä olio (lumikko)</param>
    /// <param name="kohde">Törmättävä olio (maalialue)</param>
    private void JahtaavaLumikkoTormaaPesaan(PhysicsObject hahmo, PhysicsObject kohde)
    {
        hahmo.X = RandomGen.NextDouble(Level.Left + 200, Level.Right - 200);
        hahmo.Y = RandomGen.NextDouble(Level.Top - 200, Level.Bottom + 200);
    }


    /// <summary>
    /// Luo maalialueen, jonne auringonkukansiemenet palautetaan.
    /// </summary>
    /// <param name="paikka">Vektori tilen sijainnille</param>
    /// <param name="leveys">Tilen leveys</param>
    /// <param name="korkeus">Tilen korkeus</param>
    private void LuoMaali(Vector paikka, double leveys, double korkeus)
    {
        PhysicsObject maali = PhysicsObject.CreateStaticObject(leveys, korkeus);
        maali.Position = paikka;
        maali.Color = Color.Transparent;
        maali.IsVisible = true;
        maali.Tag = "seina";
        maali.CollisionIgnoreGroup = 4;
        AddCollisionHandler<PhysicsObject, Ruoka>(maali, RuokaTuodaanMaaliin);
        Add(maali);
    }


    /// <summary>
    /// Aliohjelma luo ja palauttaa Matin sille aliohjelmakutsulla 
    /// parametreina syötetyn sijaintitiedon perusteella.
    /// </summary>
    /// <param name="peli">Tämä peli</param>
    /// <param name="x">Matin sijainti x</param>
    /// <param name="y">Matin sijainti y</param>
    /// <returns></returns>
    private PhysicsObject Pelaaja(PhysicsGame peli, double x, double y)
    {
        PhysicsObject pelaaja = new PhysicsObject(60, 120)
        {
            X = x,
            Y = y,
            Color = Color.Brown,
            CanRotate = false,
            IgnoresPhysicsLogics = true,
            IgnoresGravity = painovoima,
            Restitution = pelinAsetuksia[3],
            Tag = "valelumikko",
            CollisionIgnoreGroup = pelinAsetuksiaInt[2],
            Mass = 15000,
        };
        MatinAnimaatio(pelaaja);
        peli.Add(pelaaja, 1);
        return pelaaja;
    }

    /// <summary>
    /// Asettaa Matin (pelihahmon) näppäinkuuntelut ja toteutettavat toimenpiteet.
    /// </summary>
    private void AsetaMatinOhjaimet()
    {
        Keyboard.Listen(Key.A, ButtonState.Down, LiikutaMattia, "Liiku vasemmalle", matti1, new Vector(-pelinAsetuksia[0], 0));
        Keyboard.Listen(Key.A, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.D, ButtonState.Down, LiikutaMattia, "Liiku oikealle", matti1, new Vector(pelinAsetuksia[0], 0));
        Keyboard.Listen(Key.D, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.S, ButtonState.Down, LiikutaMattia, "Liiku alas", matti1, new Vector(0, -pelinAsetuksia[0]));
        Keyboard.Listen(Key.S, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.W, ButtonState.Down, LiikutaMattia, "Liiku ylös", matti1, new Vector(0, pelinAsetuksia[0]));
        Keyboard.Listen(Key.W, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);

        Keyboard.Listen(Key.Left, ButtonState.Down, LiikutaMattia, "Liiku vasemmalle", matti1, new Vector(-pelinAsetuksia[0], 0));
        Keyboard.Listen(Key.Left, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.Right, ButtonState.Down, LiikutaMattia, "Liiku oikealle", matti1, new Vector(pelinAsetuksia[0], 0));
        Keyboard.Listen(Key.Right, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.Down, ButtonState.Down, LiikutaMattia, "Liiku alas", matti1, new Vector(0, -pelinAsetuksia[0]));
        Keyboard.Listen(Key.Down, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.Up, ButtonState.Down, LiikutaMattia, "Liiku ylös", matti1, new Vector(0, pelinAsetuksia[0]));
        Keyboard.Listen(Key.Up, ButtonState.Released, LiikutaMattia, null, matti1, Vector.Zero);
        Keyboard.Listen(Key.F1, ButtonState.Pressed, ShowControlHelp, "Näytä ohjeet");
        Keyboard.Listen(Key.U, ButtonState.Released, Aloitus, "Aloita peli uudelleen"); 
    }

    /// <summary>
    /// LumikkoTutka-aliohjelma nimensä mukaisesti laskee etäisyyksiä kaikkiin vihuihin (jotka on 
    /// tallennettu lumikot-listaan dynaamisesti) selvittääkseen kulloisellakin liikkumisen hetkellä
    /// lähimmän vihulaisen suhteessa Mattiin. Jos vihulainen on tietyn etäisyyden päässä niin
    /// tutka-indikaattori eli huutomerkki muuttuu sinisestä punaiseksi. Ja sitten taas takaisin siniseksi,
    /// kun vaara on ohi.
    /// </summary>
    /// <param name="lumikot">Dynaaminen lista lumikoista.</param>
    /// <param name="pelaaja">Pelaaja eli Matti</param>
    /// <param name="huutomerkki">huutomerkki, joka indikoi lumikko-uhkaa</param>
    private void LumikkoTutka(List<PhysicsObject> lumikot, PhysicsObject pelaaja, GameObject huutomerkki)
    {
        PhysicsObject lahin = lumikot[0];
        double lahinEtaisyys = double.MaxValue;
        for (int i = 0; i < lumikot.Count; i++)
        {
            double etaisyys = Vector.Distance(lumikot[i].Position, pelaaja.Position);
            if (etaisyys < lahinEtaisyys)
            {
                lahin = lumikot[i];
                lahinEtaisyys = etaisyys;
            }
        }
        if (lahinEtaisyys <= ympyranSateet[5])
        {
            huutomerkki.Image = huutoMerkki[0];
        }
        if (lahinEtaisyys > ympyranSateet[5])
        {
            huutomerkki.Image = huutoMerkki[1];
        }
    }

    /// <summary>
    /// Kompassin virkaa toteuttava aliohjelma, joka laskee kullakin liikkumisen hetkellä
    /// etäisyyttä kotipesään (eli origoon (0, 0)).
    /// Tietyin intervallein kotipesää kuvaavaa talo-ikonin väriä muutetaan:
    /// mitä lähempänä pesää ollaan, sen iloisempi väri on. Eli tiivistetysti, kaukana
    /// ollessa se on punainen ja lähellä ollessa vihreä + muutama välimalli.
    /// Tarkoituksena auttaa pelaajaa löytämään koti sen kerran hukattuaan.
    /// </summary>
    /// <param name="talo">Talo-kuvake eli "kompassi"</param>
    /// <param name="pelaaja">Pelaaja eli Matti</param>
    private void KaannaKompassia(GameObject talo, PhysicsObject pelaaja)
    {
        double etaisyys = Vector.Distance(pelaaja.Position, new Vector(0, 0));
        if (etaisyys <= ympyranSateet[0])
        {
            talo.Image = taloMerkki[3];
        }
        if ((etaisyys <= ympyranSateet[1]) && (etaisyys > ympyranSateet[0]))
        {
            talo.Image = taloMerkki[1];
        }
        if ((etaisyys < ympyranSateet[2]) && (etaisyys > ympyranSateet[1]))
        {
            talo.Image = taloMerkki[2];
        }
        if (etaisyys >= ympyranSateet[3])
        {
            talo.Image = taloMerkki[0];
        }
    }


    /// <summary>
    /// Määrittelee kahden olion liitostapahtuman kun matti (pelaaja) törmää auringonkukansiemeneen.
    /// </summary>
    /// <param name="hahmo">Törmääjä (matti)</param>
    /// <param name="kohde">Törmättävä (auringonkukansiemen)</param>
    private void PelaajaTormaaSiemeneen(PhysicsObject hahmo, PhysicsObject kohde)
    {
        if (koskettaakoSiementa)
        {
            koskettaakoSiementa = false;
            saikahdys.Play();
            Viestit(1);
            AxleJoint liitos = new AxleJoint(hahmo, kohde);
            Add(liitos);
        }
    }


    /// <summary>
    /// Määrittelee tapahtumat kun matti (pelaaja) törmää elämä-olioon (sydämeen).
    /// ElämäLaskuriin lisätään (+)1 ja soitetaan menestystä kuvaavaa myyrän ääntelyä ja kutsutaan asiaan kuuluvaa MessageDisplay-viestiä.
    /// Sydän myös tuhotaan.
    /// </summary>
    /// <param name="hahmo">Törmääjä (matti)</param>
    /// <param name="kohde">Törmmättävä (sydän)</param>
    private void PelaajaTormaaSydameen(PhysicsObject hahmo, PhysicsObject kohde)
    {
        elamaLaskuri.Value += 1.0;
        loydaAarre.Play();
        Viestit(3);
        kohde.Destroy();
    }


    /// <summary>
    /// Määrittelee tapahtumat kun matti (pelaaja) törmää vihulaiseen eli lumikkoon.
    /// Elämälaskuriin (-)1 sekä Matin törmäysryhmä (ja tagi) vaihtuu vihulaiseksi.
    /// Lisäksi SingleShot timer joka vaihtaa Matin törmäysryhmän ja tagin alkuperäiseksi eli ns. myyrä-ryhmään.
    /// Tämä siksi, että Matti ei menettäisi kaikkia elämäpisteitä muutaman millisekunnin interaktion aikana lumikon kanssa. 
    /// ns. lyhytkestoinen koskemattomuussuoja.
    /// </summary>
    /// <param name="hahmo">Törmääjä (matti)</param>
    /// <param name="kohde">Törmättävä (vihu)</param>
    private void PelaajaTormaaViholliseen(PhysicsObject hahmo, PhysicsObject kohde)

    {
        elamaLaskuri.Value -= 1.0;
        hahmo.CollisionIgnoreGroup = pelinAsetuksiaInt[2];
        hahmo.Tag = "valelumikko";
        lumikkoAani.Play();
        Timer.SingleShot(2.0, delegate { hahmo.CollisionIgnoreGroup = pelinAsetuksiaInt[3]; hahmo.Tag = "myyra"; } );
        Viestit(2);
    }


    /// <summary>
    /// Tätä kutsuttaessa luodaan ja alustetaan elämälaskuri. Lisäksi luodaan ProgressBar olio,
    /// johon elämämittari upotetaan loppukäyttäjälle näytettäväksi.
    /// Määritellään myös mitä tapahtuu kun elämäpisteet loppuvat: silloin kutsutaan ElamaLoppui-funktiota.
    /// </summary>
    private void LuoElamaLaskuri()
    {
        elamaLaskuri = new DoubleMeter(4)
        {
            MaxValue = 6
        };
        elamaLaskuri.LowerLimit += ElamaLoppui;

        ProgressBar elamaPalkki = new ProgressBar(260, 60)
        {
            X = Screen.Right - 165,
            Y = Screen.Top - 40,
            Image = elamaTyhja,
            BarImage = elamaTaysi
        };
        elamaPalkki.BindTo(elamaLaskuri);
        Add(elamaPalkki);
    }


    /// <summary>
    /// Kun pelaajan (matin) elämäpisteet loppuvat niin montakoKoskettaa (varmistetaan, että käyttäjä voi jatkossakin
    /// kantaa vain yhtä siementä. Eikä mitään omituista tapahdu.) -attribuutti alustetaan. Kutsutaan myös Viestit-aliohjelmaa.
    /// Pelaaja (matti) tuhotaan.
    /// Käynnistetään SingleShot Timer, joka välittää delegaattina pyynnön alustaa pelin.
    /// </summary>
    private void ElamaLoppui()
    {
        koskettaakoSiementa = true;
        Viestit(7);
        matti1.Destroy();
        Timer.SingleShot(3.0, delegate { Aloitus(); });
    }
    

    /// <summary>
    /// Tätä aliohjelmaa kutsutaan pelin loputtua.
    /// </summary>
    /// <param name="sender"></param>
    private void AloitaPeli(Window sender)
    {
        Aloitus();
    }


    /// <summary>
    /// Määrittelee tapahtumat kun auringonkukansiemen koskettaa maalialuetta.
    /// Todennäköisyyksiä: arvotaan RandomGenillä numero. Jos saadaan tietty numero niin 
    /// kutsutaan LuoSydan-aliohjelmaa.
    /// kutsutaan myös LuoSiemen ja Viestit-aliohjelmia.
    /// Lisätään bonusaikaa alaspainLaskuriin (eli peliaikalaskuriin)
    /// Soitetaan voittoisa ääni.
    /// Vähennetään montakoKoskettaa-attribuutista -1 (eli arvoksi tulee 0), jotta seuraavakin siemen voidaan poimia kyytiin.
    /// Siemen-olio tuhotaan. 
    /// </summary>
    /// <param name="hahmo">Törmääjä (ruoka)</param>
    /// <param name="kohde">Törmättävä (maalialue)</param>
    private void RuokaTuodaanMaaliin(PhysicsObject hahmo, PhysicsObject kohde)
    {
        int numero;
        numero = RandomGen.NextInt(0, 4);
        if ((numero == 1) || (numero == 2))
        {
            LuoSydan();
        }
        LuoSiemen();
        Viestit(0);
        alaspainLaskuri.Value += pelinAsetuksia[1];
        Ruokalaskuri.Value++;
        loydaAarre.Play();
        koskettaakoSiementa = true;
        kohde.Destroy();
    }


    /// <summary>
    /// Aliohjelma luo Ruoka-olioita (auringonkukansiemeniä).
    /// Sijainti sattumanvarainen RandomGen.NextDouble - arvo pelikentän sisällä.
    /// </summary>
    private void LuoSiemen()
    {
        Ruoka siemen = new Ruoka(40, 60, Color.Black)
        {
            Image = siemenenKuva,
            X = RandomGen.NextDouble(Level.Left + 400, Level.Right - 400),
            Y = RandomGen.NextDouble(Level.Top - 400, Level.Bottom + 400),
            IgnoresGravity = painovoima,
            Restitution = pelinAsetuksia[3],
            StaticFriction = pelinAsetuksia[4],
            KineticFriction = pelinAsetuksia[5],
            LinearDamping = pelinAsetuksia[6],
            AngularDamping = pelinAsetuksia[7],
            CollisionIgnoreGroup = pelinAsetuksiaInt[2],
            Mass = 50
        };
        Add(siemen);
    }


    /// <summary>
    /// Aliohjelma luo Sydän-olioita.
    /// Sijainti sattumanvarainen RandomGen.NextDouble - arvo pelikentän sisällä.
    /// </summary>
    private void LuoSydan()
    {
        Sydan hitpoint = new Sydan(90, 90, Color.Red);
        hitpoint.MakeStatic();
        hitpoint.X = RandomGen.NextDouble(Level.Left + 400, Level.Right - 400);
        hitpoint.Y = RandomGen.NextDouble(Level.Top - 400, Level.Bottom + 400);
        hitpoint.IgnoresGravity = painovoima;
        hitpoint.CollisionIgnoreGroup = pelinAsetuksiaInt[2];
        hitpoint.Animation = new Animation(sydanLiike);
        hitpoint.Animation.Start();
        hitpoint.Animation.FPS = 3;
        Add(hitpoint);
    }


    /// <summary>
    /// Määrittelee pelaajan (Matin) liikkeet aliohjelmakutsuna saatujen parametrien arvojen perusteella.
    /// </summary>
    /// <param name="hahmo">pelaaja (matti)</param>
    /// <param name="suunta">pelaajan liikesuunta vektorina</param>
    private void LiikutaMattia(PhysicsObject hahmo, Vector suunta)
    {
        {
            hahmo.Velocity = suunta;
            if ((suunta.X < 0) && (suunta.Y == 0))
            {
                hahmo.Angle = Angle.FromDegrees(90);
            }
            else if ((suunta.X > 0) && (suunta.Y == 0))
            {
                hahmo.Angle = Angle.FromDegrees(270);
            }
            else if ((suunta.X == 0) && (suunta.Y < 0))
            {
                hahmo.Angle = Angle.FromDegrees(180);
            }
            else if ((suunta.X == 0) && (suunta.Y > 0))
            {
                hahmo.Angle = Angle.FromDegrees(0);
            }
        }
    }


    /// <summary>
    /// Määrittelee pelihahmon animaation kutsuttaessa.
    /// </summary>
    /// <param name="hahmo">pelaaja (matti)</param>
    private void MatinAnimaatio(PhysicsObject hahmo)
    {
        hahmo.Animation = new Animation(myyranLiike);
        hahmo.Animation.Start();
        hahmo.Animation.FPS = 3;
    }


    /// <summary>
    /// Luo IntMeter pistelaskurin, jota käytetään laskettaessa kerättyjen siementen määrää.
    /// Luo myös Label-olion, johon pistelaskuri sijoitetaan loppukäyttäjän pällisteltäväksi.
    /// </summary>
    /// <param name="x">Label-olion x-koordinaatti</param>
    /// <param name="y">Label-olion y-koordinaatti.</param>
    /// <returns></returns>
    private IntMeter LuoPisteLaskuri(double x, double y)
    {
        IntMeter laskuri = new IntMeter(0)
        {
            MaxValue = 1000
        };

        Label naytto = new Label();
        naytto.BindTo(laskuri);
        naytto.X = x;
        naytto.Y = y - 50;
        naytto.TextColor = Color.White;
        naytto.BorderColor = Color.Black;
        naytto.Color = Color.Black;
        Add(naytto);

        return laskuri;
    }


    /// <summary>
    /// Luo DoubleMeter aikalaskurin, jota käytetään laskettaessa jäljellä olevaa peliaikaa.
    /// Kutsutaan myös LaskeAlaspain-ohjelmaa jokaisen timeoutin jälkeen.
    /// Luo myös Label-olion, johon aikalaskuri sijoitetaan loppukäyttäjän pällisteltäväksi.
    /// Lisäksi kutsutaan silmukassa LuoSiemen-aliohjelmaa auringonkukansiementen generoimiseksi pelikentälle.
    /// </summary>
    /// <param name="x">Label-olion x-koordinaatti</param>
    /// <param name="y">Label-olion y-koordinaatti</param>
    private void LuoAikaLaskuri(double x, double y)
    {
        alaspainLaskuri = new DoubleMeter(pelinAsetuksiaInt[1]);

        aikaLaskuri = new Timer
        {
            Interval = 0.1
        };
        aikaLaskuri.Timeout += LaskeAlaspain;
        aikaLaskuri.Start();


        Label aikaNaytto = new Label
        {
            TextColor = Color.White,
            DecimalPlaces = 1,
            X = x + 10,
            Y = y
        };
        aikaNaytto.BindTo(alaspainLaskuri);
        Add(aikaNaytto);

        Timer kokonaisKesto = new Timer();
        kokonaisKesto.Start();

        Label pisteTeksti = new Label("Vuodenaikaa jäljellä: ")
        {
            TextColor = Color.White,
            X = x - 120,
            Y = y
        };
        Add(pisteTeksti);

        Label aikaNaytto2 = new Label
        {
            TextColor = Color.White,
            DecimalPlaces = 1,
            X = x,
            Y = y - 25
        };
        aikaNaytto2.BindTo(kokonaisKesto.SecondCounter);
        Add(aikaNaytto2);
   
        for (int i = 0; i < kenttienOliot[0, 0]; i++)
        {
            LuoSiemen();
        }
    }


    /// <summary>
    /// Käytetään laskettaessa aikaa takaperoisesti. 
    /// Tarkastellaan myös aikaa ja vuodenaikaa tietyillä ehdoilla. 
    /// Jos ehdot täyttyvät niin vuodenaika muuttuu ja alaspainLaskurin aika alustetaan vuodenAjanKesto-attribuutin mukaiseksi.
    /// </summary>
    private void LaskeAlaspain()
    {
        alaspainLaskuri.Value -= 0.1;

        if ((alaspainLaskuri.Value <= 0) && (vuodenAika == 0))
        {
            Viestit(4);
            Syksy();
            vuodenAika++;
            alaspainLaskuri.Value = pelinAsetuksiaInt[1];
        }
        if ((alaspainLaskuri.Value <= 0) && (vuodenAika == 1))
        {
            Viestit(5);
            Talvi();
            vuodenAika++;
            alaspainLaskuri.Value = pelinAsetuksiaInt[1];
        }
        if ((alaspainLaskuri.Value <= 0) && (vuodenAika == 2))
        {
            vuodenAika++;
        }

        if (vuodenAika == 3)
        {
            aikaLaskuri.Stop();
            Viestit(6);
            matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[2];
            matti1.Tag = "valelumikko";
            topLista.EnterAndShow(Ruokalaskuri.Value);
            Ruokalaskuri.Reset();
            IsPaused = true;
            topLista.HighScoreWindow.Closed += LopetusIkkuna;
        }
    }


    /// <summary>
    /// Luo syksyn.
    /// Taustaväri vaihdetaan.
    /// Kesätausta vaihdetaan syystaustaksi ja mukautetaan pelitason kokoiseksi.
    /// Pelaaja myös hetkeksi laitetaan lumikot-ryhmään, jotta ei menetetä elämäpisteitä kun uusia vihuja generoivia
    /// aliohjelmia kutsutaan. Sillä ne saattavat ilmestyä pelaajan niskaan.
    /// Silmukoissa kutsutaan myös aliohjelmia uusien siementen ja sydämien luomiseksi.
    /// </summary>
    private void Syksy()
    {
        Level.BackgroundColor = Color.BrownGreen;
        Level.Background.Image = syysTausta;
        Level.Background.TileToLevel();

        matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[2];
        matti1.Tag = "valelumikko";
        Timer.SingleShot(2.0, delegate { matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[3]; matti1.Tag = "myyra"; });

        for (int i = 0; i < kenttienOliot[1, 0]; i++)
        {
            LuoSiemen();
        }

        for (int i = 0; i < kenttienOliot[1, 1]; i++)
        {
            LuoSydan();
        }

        for (int i = 0; i < kenttienOliot[1, 2]; i++)
        {
            LuoLumikko(lumikonLiikeTalvi);
        }

        for (int i = 0; i < kenttienOliot[1, 3]; i++)
        {
            LuoJahtaavaLumikko(lumikot, lumikonLiikeTalvi);
        }
    }


    /// <summary>
    /// Luo talven.
    /// Taustaväri vaihdetaan.
    /// Syystausta vaihdetaan talvitaustaksi ja mukautetaan pelitason kokoiseksi.
    /// Pelaaja myös hetkeksi laitetaan lumikot-ryhmään, jotta ei menetetä elämäpisteitä kun uusia vihuja generoivia
    /// aliohjelmia kutsutaan. Sillä ne saattavat ilmestyä pelaajan niskaan.
    /// Silmukoissa kutsutaan myös aliohjelmia uusien siementen ja sydämien luomiseksi.
    /// </summary>
    private void Talvi()
    {
        Level.BackgroundColor = Color.Snow;
        Level.Background.Image = talviTausta;
        Level.Background.TileToLevel();

        kotiPesa.Image = talviPesa[1];

        matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[2];
        matti1.Tag = "valelumikko";
        Timer.SingleShot(2.0, delegate { matti1.CollisionIgnoreGroup = pelinAsetuksiaInt[3]; matti1.Tag = "myyra"; });


        for (int i = 0; i < kenttienOliot[2, 0]; i++)
        {
            LuoSiemen();
        }

        for (int i = 0; i < kenttienOliot[2, 1]; i++)
        {
            LuoSydan();
        }

        for (int i = 0; i < kenttienOliot[2, 2]; i++)
        {
            LuoLumikko(lumikonLiikeTalvi);
        }

        for (int i = 0; i < kenttienOliot[2, 3]; i++)
        {
            LuoJahtaavaLumikko(lumikot, lumikonLiikeTalvi);
        }
    }

    private void LopetusIkkuna(Window sender)
    {
        Widget lopetusIkkuna = new Widget(600, 250, Shape.Rectangle)
        {
            X = 0,
            Y = 0,
            Color = Color.White
        };
        matti1.Destroy();
        Add(lopetusIkkuna);
        Label pisteTeksti = new Label("Onneksi olkoon! Sait talvipesän valmiiksi ja voitit pelin!\nVoit lopettaa pelin painamalla ESC tai \nvaihtoehtoisesti aloittaa uuden pelin painamalla U")
        {
            TextColor = Color.Black
        };
        lopetusIkkuna.Add(pisteTeksti);
        koskettaakoSiementa = true;
        Timer.SingleShot(3, () => { Aloitus(); });

    }


    /// <summary>
    /// Aliohjelmassa luodaan jono-taulukko, johon talletetaan
    /// pelin kaikki viestit.
    /// MessageDisplay:tä käyttäen printataan näytölle käyttäjän pällisteltäväksi viesti parametrina
    /// vastaanotetun viestin tunnistenumeron perusteella.
    /// </summary>
    /// <param name="viesti">Int-arvo, jota käyttäen osataan kutsuta taulukosta oikeaa riviä.</param>
    private void Viestit(int viesti)
    {
        string[] viestit = {
            "Lisää! Liian täyttä varastoa ei olekaan!",
            "Skviik! Skviik!",
            "Skviik, kamala lumikko!",
            "Skviik, tunnen oloni vahvemmaksi!",
            "Talvi on tulossa.",
            "Vielä vähän. Kohta on valmista.",
            "Sait talvivaraston valmiiksi!",
            "Oi voi, nyt taisi lähteä henki. Täytyy olla varovaisempi seuraavassa elämässä.",
            "Täytyy löytää mahdollisimman monta auringonkukansiemeniä ennen kylmintä talvea... ",
            "...ja varastoida ne kannon juuressa sijaitsevaan pesäko    loon. Skviik!",
            "Asiat tärkeysjärjestykseen, sillä edes kaunein ja suurin auringonkukansiemen...",
            "...ei ole riittävän hyvä syy päätyä lumikon kitaan.",
            "Muista myös kerätä lisäelämiä niin kestät kolhun tai pari enemmän.",
            "Voit liikkua mieltymyksiesi mukaan WASDilla/nuolinäppäimillä.",
            "Onnea matkaan Matti! Saat lisäaikaa jokaisesta keräämästäsi siemenestä.",
            "NYT!"
        };
        MessageDisplay.Add(viestit[viesti]);
    }


}
