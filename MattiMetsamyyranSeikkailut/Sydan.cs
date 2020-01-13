using Jypeli;

/// <summary>
/// Oma luokka Sydan-oliolle. Siltä varalta, että päätän tehdä sillä jotain erikoisempaa.
/// </summary>
public class Sydan : PhysicsObject
{
    public Sydan(double leveys, double korkeus, Color vari) :
        base(leveys, korkeus)
    {
        Color = vari;
    }

}
