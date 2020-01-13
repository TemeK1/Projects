using Jypeli;

/// <summary>
/// Luokka normaalille, ei-jahtaavalle vihulle, joka käyttää aina LabyrinthWandererBrain-aivoja
/// sattumanvaraiseen liikkumiseen kentällä.
/// </summary>
public class Vihollinen : PhysicsObject
{
    int Ruutu;
    public Vihollinen(double leveys, double korkeus, int ruudunKoko, Color vari) :
        base(leveys, korkeus)
    {
        Ruutu = ruudunKoko;
        Color = vari;
        Brain = Aivot();
    }
    public LabyrinthWandererBrain Aivot()
    {
        LabyrinthWandererBrain lumikonAivot = new LabyrinthWandererBrain(Ruutu)
        {
            Speed = RandomGen.NextDouble(80.0, 200.0),
            LabyrinthWallTag = "seina",
            TurnWhileMoving = true
        };
        return lumikonAivot;
    }
}

