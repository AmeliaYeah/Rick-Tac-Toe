import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.util.*;

public class GameExit {
	
	public static void Initiate()
	{
		//Set the variables needed
		int frameSize = 500;
		
		//Construct the JFrame
		JFrame exitFrame = new JFrame();
		exitFrame.setUndecorated(true);
		exitFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		exitFrame.setAlwaysOnTop(true);
		exitFrame.setSize(new Dimension(frameSize, frameSize));
		
		//Create the JFrame background
		JLabel background = new JLabel();
		
		//Generate the background's icon
		URL url = new BoardGameSpace().getClass().getResource("final_man.jpg");
		ImageIcon icon = new ImageIcon(url);
		
		//Set the background data
		background.setIcon(icon);
		background.setLocation(new Point(0,0));
		background.setSize(new Dimension(frameSize, frameSize));
		
		//Add the background to the frame, then pack the frame so it is able to fit the size of the picture
		exitFrame.add(background);
		exitFrame.pack();
		
		//Get the halved width and the halved length of the frame, so we can use them for the location math
		int frameSizeHalfWidth = exitFrame.getWidth() / 2;
		int frameSizeHalfHeight = exitFrame.getHeight() / 2;
				
		//Center the JFrame, then set it to be visible.
		//The centering is done at the end of the process since the pack() method changes the size of the JFrame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		exitFrame.setLocation((screenSize.width / 2) - frameSizeHalfWidth, (screenSize.height / 2) - frameSizeHalfHeight);
		exitFrame.setVisible(true);
		
		//Get the audio file and play it
		File audio = Utils.FileDependancyLoader.getDependancy("FinalSound");
		Utils.AudioManager.loadSoundFromFile(audio);
		
		//Wait some seconds so the audio has enough time to play
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run()
			{
				//Dispose of the frame and quit the application
				exitFrame.dispose();
				System.exit(0);
			}
			
		}, 4000);
	}
	
}
