import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

public class Utils
{
	public static class FileDependancyLoader
	{
		private static HashMap<String, File> dependancies = new HashMap<String, File>();
		
		public static void loadDependancies()
		{
			//Generate the hashmap containing all the file names we want to load, along with the file path
			HashMap<String, String> filesToLoad = new HashMap<String, String>();
			filesToLoad.put("RickSound", "rick_fast.wav");
			filesToLoad.put("RickVictory", "rick.wav");
			filesToLoad.put("RickGIF", "rickers.gif");
			filesToLoad.put("FinalMan", "final_man.jpg");
			filesToLoad.put("FinalSound", "final_sound.wav");
			
			//Convert our hashmap into the dependancies hashmap by loading the specified file paths
			//If there is an error during loading, we return
			
			//Generate the iterator
			Iterator it = filesToLoad.entrySet().iterator();
			
			//Begin looping through the dependancies set
			while(it.hasNext())
			{
				//Generate the entry and get the file from the file path
				Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
				File loaded = Utils.loadFile(entry.getValue());
				
				//If the file is null, return from the function
				if(loaded == null) return;
				
				//Add the file along with the entry name to the dependancies hashmap
				dependancies.put(entry.getKey(), loaded);
			}
		}
		
		public static File getDependancy(String fileName)
		{
			//Check if the dependancies hashmap contains the requested file name
			//If not, return null
			if(!dependancies.containsKey(fileName)) return null;
			
			//Return the file for the requested filename
			return dependancies.get(fileName);
		}
	}
	
	public static class AudioManager 
	{
		static ArrayList<Clip> currentClips = new ArrayList<Clip>();
		public static void loadSoundFromFile(File file)
		{
			//Create an audio input stream variable and the audio clip variable.
			AudioInputStream str = null;
			Clip audioClip = null;
			
			//Try to load the audio input stream.
			//If it doesn't load, we write to the console, as the game can still operate without sound.
			try
			{
				str = AudioSystem.getAudioInputStream(file);
			}catch(Exception e)
			{
				System.out.println("Could not load the audio from '" + file.getName() + "'\n" + e.getMessage());
				return;
			}
			
			//Another audio input stream event handler.
			//This time, we're going to load in the audioclip, set it to our stream data, and play it.
			try
			{
				audioClip = AudioSystem.getClip();
				audioClip.open(str);
				audioClip.start();
			}catch(Exception e)
			{
				System.out.println("Could not load the audio clip\n" + e.getMessage());
				return;
			}
			
			//Add the audioclip into the currentClips array
			currentClips.add(audioClip);
		}
		
		public static void stopAllAudio()
		{
			//Clone the arraylist, since we are going to be modifying the main clips array
			ArrayList<Clip> copy = (ArrayList<Clip>)currentClips.clone();
			
			//Loop through the copied arraylist
			for(Clip clip : copy)
			{
				//Stop the clip, then remove the clip from the main currentClips array
				clip.stop();
				currentClips.remove(clip);
			}
		}
	}
	
	static Utils instance = new Utils();
	public static File loadFile(String nameRaw)
	{
		//Set the loaded file variable
		File loaded = null;
		
		//Set the file name through getResource() (to get a relative path)
		URL nameURL = instance.getClass().getResource(nameRaw);
		
		//Decode the url and set it to the "name" string via a try/catch
		String name = "";
		try
		{
			name = URLDecoder.decode(nameURL.getPath(), "ASCII");
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Could not decode the URL for file " + nameRaw + "\n" + e.getMessage() + "\nThe program will now abort");
			System.exit(0);
			return null;
		}
		
		//Try to load the file from the pathname. If it throws an error, abort the program.
		try
		{
			loaded = new File(name);
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "The file '" + name + "' could not be loaded.\nThe program will now abort.");
			System.exit(0);
			return null;
		}
		
		//Return the file
		return loaded;
	}
	
	public static int getIntegerInput(String message, int min, int max)
	{
		//Set the integer input variable
		int input = -1;
		
		//Keep looping until the input is usable
		while(input == -1)
		{
			//Set the integer value
			input = (int)getInput(message + "\n(Please make sure the number is atleast " + min + " and at most " + max + ")", true);
			
			//Check if the integer is in between the minimum and maximum values.
			if(input > max || input < min)
			{
				//Start constructing a response message
				String responseMessage = "";
				
				//Construct the message depending on what condition they violated.
				if(input > max) responseMessage = "The value you entered is above " + max + "!";
				else if(input < min) responseMessage = "The value you entered is below " + min + "!";
				
				//Send the response message, then set the input back to -1, to ensure the loop runs again
				JOptionPane.showMessageDialog(null, responseMessage);
				input = -1;
			}
		}
		
		//Return the integer
		return input;
	}
	
	public static Object getInput(String message, boolean numeric)
	{
		//Set an input string variable
		String input = "";
		
		//Continue looping until the input is valid
		while(input == "")
		{
			//Create a JOptionPane that asks the user for input
			input = JOptionPane.showInputDialog(message);
			
			//Check to see if the numeric variable is true. If it is, we attempt to parse the string to an integer.
			if(numeric)
			{
				//Attempt to parse the string.
				//If it is not an integer and throws an error, we set the input variable back to null.
				//This means that the user will be asked for input again.
				try 
				{
					//Get the integer and return it
					// (This is because we want to return the parsed int. Not the string.)
					int integer = Integer.parseInt(input);
					return integer;
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Your input must be a valid number.");
					input = "";
				}
			}
		}
		
		//Woosh! Return the string.
		return input;
	}
}
