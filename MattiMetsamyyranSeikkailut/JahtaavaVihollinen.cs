using Jypeli;

/// <summary>
/// Luokka jahtaavalle viholliselle. Normaalioloissa se seuraa pelaajaa etäisyyden x päästä vauhdilla y.
/// Jos jahtaava vihu on pelaajasta liian kaukana, se käyttää LabyrinthWandererBrain-aivoja
/// satunnaiseen liikkumiseen.
/// </summary>
public class JahtaavaVihollinen : PhysicsObject
{
    int Ruutu;
    public JahtaavaVihollinen(double leveys, double korkeus, int ruudunKoko, Color vari) :
      base(leveys, korkeus)
    {
        Ruutu = ruudunKoko;
        Color = vari;
        Brain = Aivot2();
    }
  
    public FollowerBrain Aivot2()
    {
        LabyrinthWandererBrain satunnaisAivot = new LabyrinthWandererBrain(40)
        {
            Speed = RandomGen.NextDouble(80.0, 200.0),
            LabyrinthWallTag = "seina",
            TurnWhileMoving = true
        };

        FollowerBrain lumikonAivotSeuraa = new FollowerBrain("myyra")
        {
            Speed = RandomGen.NextInt(250, 350),                 // Millä nopeudella kohdetta seurataan
            DistanceFar = RandomGen.NextInt(350, 550),           // Etäisyys jolla aletaan seurata kohdetta
            DistanceClose = 150,         // Etäisyys jolloin ollaan lähellä kohdetta
            StopWhenTargetClose = false,  // Pysähdytään kun ollaan lähellä kohdetta
            TurnWhileMoving = true,
            FarBrain = satunnaisAivot   // Käytetään satunnaisaivoja kun ollaan kaukana
        };
        return lumikonAivotSeuraa;
    }
}
