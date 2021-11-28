import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class Initialization {

	public static void main(String[] args)
	{
		//Load the dependancies required for the game
		JOptionPane.showMessageDialog(null, "Checking if the required file dependancies exist...");
		Utils.FileDependancyLoader.loadDependancies();
		
		//Send a confirmation dialog to the user, letting them know we love them
		JOptionPane.showMessageDialog(null, "Welcome to Rick-Tack-Toe.");
		JOptionPane.showMessageDialog(null, "Please complete this momentary setup process before starting.\nAfter this is complete, your game will start.");
		JOptionPane.showMessageDialog(null, "Thank you for choosing Comcast™, and I hope you enjoy the game.");
		
		//Ask the user for what size they want their board to be.
		// (If the value is greater than the maximum, we ask them again.)
		int boardSize = Utils.getIntegerInput("Enter how much spaces you want (1 for 1x1, 2 for 2x2, etc...)", 2, 7);
		
		//Construct a color dictionary
		//The string will contain the color name, the color will contain the actual color
		HashMap<String, Color> colors = new HashMap<String, Color>();
		colors.put("black", Color.black);
		colors.put("blue", Color.blue);
		colors.put("cyan", Color.cyan);
		colors.put("green", Color.green);
		colors.put("red", Color.red);
		colors.put("orange", Color.orange);
		colors.put("yellow", Color.yellow);
		colors.put("magenta", Color.magenta);
		colors.put("pink", Color.pink);
		
		//Prompts the player on how much players they want to play with
		int playerCount = Utils.getIntegerInput("How much players are with you?", 2, 4);
		
		//Loop through each of the players and beg them for a color.
		//We use a switch statement then to check if their input matches with a color in the color array.
		//If it does, use the color. If it doesn't...well, they get begged again.
		ArrayList<Color> players = new ArrayList<Color>();
		for(int i = 0; i < playerCount; i++) 
		{
			//Generate an enumerator from the hashmap
			Iterator it = colors.entrySet().iterator();
			
			//Loop through the colors dictionary to get a string of all available colors
			String colorsStr = "";
			while(it.hasNext())
			{
				//Get the current color, then append the key to the colors string
				Map.Entry<String, Color> current = (Entry<String, Color>)it.next();
				colorsStr += current.getKey();
				
				//Add a new line marker, for easier formatting in the JOptionPane dialog
				colorsStr += "\n";
			}
			
			//Initialize the input variable
			String input = "";
			
			//Continue looping until they enter a valid color
			while(!colors.containsKey(input))
			{
				//Ask the user for input
				input = (String)Utils.getInput("Player " + (i + 1) + ", please enter your color.\nThe current list of colors are:\n" + colorsStr, false);
				
				//Convert the requested color to lowercase, so it can be compared properly to the hashmap.
				input = input.toLowerCase();
				
				//If the color isn't found in the hashmap, notify the user
				if(!colors.containsKey(input)) JOptionPane.showMessageDialog(null, "That color does not exist! Sorry :c");
			}
			
			//Now that the input points toward a valid color, we get the color from the key.
			//After we get it from the colors dictionary, we remove it, to ensure nobody else can claim the color.
			Color chosen = colors.get(input);
			colors.remove(input);
			
			//Append the chosen key to the players color array, and we're done!
			players.add(chosen);
		}
		
		//Now we start the board game, with all our variables defined.
		BoardGame.start(boardSize, players, true);
	}

}
