using Jypeli;

/// <summary>
/// Oma luokka Ruoka-oliolle. Siltä varalta, että päätän tehdä sillä jotain erityisen hienoa.
/// </summary>
public class Ruoka : PhysicsObject
{
    public Ruoka(double leveys, double korkeus, Color vari) : 
        base(leveys, korkeus)
	{
        Color = vari;  
	}

}
