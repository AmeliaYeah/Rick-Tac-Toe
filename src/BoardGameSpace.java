import java.awt.Color;

public class BoardGameSpace
{
	//Look im gonna be honest I was expecting more than 1 variable when I wrote this class but now the programming's done so I guess we're just gonna have this lad chill here living his best life and existing with only 1 class variable
	
	Color takenBy = null;
	
	public void claim(Color player)
	{
		takenBy = player;
	}
	
	public Color getOwner()
	{
		return takenBy;
	}
}
