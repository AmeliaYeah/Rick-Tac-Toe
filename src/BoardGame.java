import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class BoardGame
{
	static int size;
	static ArrayList<Color> players;
	
	static BoardGameSpace[][] spaces;
	static JFrame frame;
	
	static int currentPlayer = 0;
	
	static int gamesPlayedTotal = 0;
	static ArrayList<Integer> playerWinCount = new ArrayList<Integer>();
	
	//Starts the board game
	public static void start(int sizeParam, ArrayList<Color> playersParam, boolean firstTime)
	{
		//Update the gamesPlayedTotal variable so we can keep track on how much games were played
		gamesPlayedTotal++;
		
		//Set the board game variables to the parameters
		size = sizeParam;
		players = playersParam;
		
		//Initialize the spaces array and set it's values from null
		spaces = new BoardGameSpace[size][size];
		for(int x = 0; x < size; x++)
		{
			for(int y = 0; y < size; y++)
			{
				spaces[x][y] = new BoardGameSpace();
			}
		}
		
		//Create the board game window
		frame = new JFrame("rick-tack-toe");
		
		//Compute the actual board game size
		//The JFrame size will be increased depending on how big the physical board is
		int width = 800;
		int height = 600;
		int addition = 10 * size;
		frame.setSize(width + addition, height + addition);
		
		//Set the rest of the board game window data
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		
		//Build the board's grid
		buildGrid();
		
		//Make the frame visible
		frame.setVisible(true);
		
		//Set the current player to a random player
		currentPlayer = new Random().nextInt(players.size() - 1);
		
		//Run the code that is supposed to only run for when the game first begins
		if(firstTime)
		{
			//Initialize the playerWinCount hashmap by looping through the players and adding them into it
			for(Color player : players)
			{
				playerWinCount.add(0);
			}
			
			//Notify all the players that the game will start
			JOptionPane.showMessageDialog(null, "Alright! Good luck everyone. Player " + (currentPlayer + 1) + " is going first!");
		}
	}
	
	static void buildGrid()
	{
		//Load the rick sound effect file
		File rickSoundEffect = Utils.FileDependancyLoader.getDependancy("RickSound");
		
		//Get the board's size
		Dimension size = frame.getSize();
		
		//Set the actual button size (this is how large the buttons will be)
		int buttonSize = 100;
		
		//Load the button icon beforehand to eliminate that "black square" phenomenon
		createButtonIcon();
		
		//Loop through the spaces array we just set a moment ago, and place all the buttons accordingly
		for(int x = 0; x < spaces.length; x++)
		{
			//Get the current spaces for this X coordinate
			BoardGameSpace[] currentSpaces = spaces[x];
			
			//Then, loop through those spaces
			for(int y = 0; y < currentSpaces.length; y++)
			{
				//Get the current X and Y values
				//This is so the button handler points toward our coordinates
				int currentX = x;
				int currentY = y;
				
				//Get the maximum X and Y values
				//This is for button placement, so it can be placed starting from the bottom right
				//The reason for this type of placement is because the line checking code will not function properly if the buttons were placed from top to bottom
				int maxX = buttonSize * (spaces.length - 1);
				int maxY = buttonSize * (currentSpaces.length - 1);
				
				//Get the current board game space and create the JButton instance for it
				BoardGameSpace currentSpace = currentSpaces[y];
				JButton button = new JButton();
				
				//Set the button's size and text
				button.setSize(buttonSize, buttonSize);
				button.setText("Empty");
				
				//Calculate the button's coordinate placement
				//The coordinates are [MAX] - (buttonsize * [x or y]) because it will ensure that the buttons are placed bottom -> top
				Point coordinatePlacement = new Point();
				coordinatePlacement.x = maxX - (buttonSize * x);
				coordinatePlacement.y = maxY - (buttonSize * y);
				
				//Set the button's position
				button.setLocation(coordinatePlacement);
				
				//Finally, set the onClick handler for when the player clicks it
				button.addActionListener(new ActionListener() {
					
					//Function for when the player clicks the button
					public void actionPerformed(ActionEvent e)
					{
						//If the current space is already occupied, we return.
						if(currentSpace.getOwner() != null) return;
						
						//Set the current space's owner to the current player
						currentSpace.claim(players.get(currentPlayer));
						
						//Play the sound effect, and then mark this button as claimed
						//We will also set the button's image color to that of the player
						Utils.AudioManager.loadSoundFromFile(rickSoundEffect);
						button.setIcon(createButtonIcon());
						
						//Continue to the next player
						nextPlayer();
					}
					
				});
				
				//Now, finally, we add the button to the board
				frame.add(button);
			}
		}
	}
	
	//Generates a tinted image with the current player's color
	static ImageIcon createButtonIcon()
	{
		//Get the player
		Color player = players.get(currentPlayer);
		
		//Generate the GIF URL
		URL url = new BoardGameSpace().getClass().getResource("rickers.gif");
		
		//Load the gif and create the buffered image
		ImageIcon icon = new ImageIcon(url);
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		
		//Create a graphics object and paint the image
		Graphics g = img.getGraphics();
		g.drawImage(icon.getImage(), 0, 0, null);
		
		//Allow for the graphics to tint with the player's color with the alpha channel set to a see-through value
		// (This is because with the original alpha value we'd just get a solid color)
		//After this, paint a rectangle over the image with the color to tint it
		Color newColor = new Color(player.getRed() / 255f, player.getGreen() / 255f, player.getBlue() / 255f, 0.8f);
		g.setColor(newColor);
		g.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		
		//Dispose of the graphics, then return the image
		g.dispose();
		return new ImageIcon(img);
	}
	
	//Function that checks if the player won
	static void scanBoardForWin()
	{
		//Get the lengths for both the X and the Y
		int xLength = spaces.length - 1;
		int yLength = spaces[0].length - 1;
		
		//Loop through all the X indexes to scan purely vertical lines
		for(int x = 0; x <= xLength; x++)
		{
			validateBoardLine(x, 0, 0, 1);
		}
		
		//Loop through all the Y indexes to scan purely horizontal lines
		for(int y = 0; y <= yLength; y++)
		{
			validateBoardLine(0, y, 1, 0);
		}
		
		//Scan the corners of the board to see if the player hit a diagonal line
		validateBoardLine(0, 0, 1, 1); //Bottom right
		validateBoardLine(xLength, yLength, -1, -1); //Top left
		validateBoardLine(0, yLength, 1, -1); //Top right
		validateBoardLine(xLength, 0, -1, 1); //Bottom left
		
		//Check to see if all the spaces have been occupied
		restartGameIfFull();
	}
	
	static void restartGameIfFull()
	{
		//Loop through the spaces
		for(BoardGameSpace[] spacesCurrent : spaces)
		{
			for(BoardGameSpace space : spacesCurrent)
			{
				//If the space isn't occupied, return and exit the function.
				//This is because the game is playable and is in no need of a restart.
				if(space.getOwner() == null) return;
			}
		}
		
		//Notify the user that the game will restart
		JOptionPane.showMessageDialog(null, "Uh oh! It looks like all the spaces have been taken.\nThe game will now restart.");
		
		//Restart the game
		newGame();
	}
	
	//Function that checks to see if the player created a consistent line
	//Not to be confused with scanBoardLine(), as this function handles the player actually winning
	static void validateBoardLine(int x, int y, int directionX, int directionY)
	{
		//Create a "win" boolean, then check if the player won.
		//If the didn't win, return.
		boolean win = scanBoardLine(x, y, directionX, directionY);
		if(!win) return;
		
		//Clear the board
		clearBoard();
		
		//Create the background picture
		//It's a jlabel because java is great I guess (There's no JImage class)
		JLabel backgroundLabel = new JLabel();
		backgroundLabel.setIcon(new ImageIcon("rickers.gif"));
		backgroundLabel.setSize(new Dimension(frame.getWidth(), frame.getHeight()));
		frame.add(backgroundLabel);
		
		//Create a new label displaying that the player won, then add the label to the board game frame
		JLabel label = new JLabel();
		label.setText("yayyy player " + (currentPlayer + 1) + " :DDDDDDD");
		label.setForeground(players.get(currentPlayer));
		label.setLocation(0, 0);
		label.setSize(new Dimension(frame.getWidth(), frame.getHeight()));
		label.setFont(new Font("Courier-New", Font.BOLD, 40));
		frame.add(label);
		
		//Flush the label addition
		flushComponentEdit();
		
		//Play the victory sound and update the current player's score
		Utils.AudioManager.loadSoundFromFile(Utils.FileDependancyLoader.getDependancy("RickVictory"));
		playerWinCount.set(currentPlayer, playerWinCount.get(currentPlayer) + 1);
		
		//Ask the user if they want to play again
		int play = Utils.getIntegerInput("Would you like to play again? Or would you like to quit?\n1 = 'Play'\n2 = 'Quit'", 1, 2);
		switch(play)
		{
			case 1:
				//Start the new game
				newGame();
				break;
				
			case 2:
				//Notify the program that the game has ended by ticking the flag.
				//This is because we don't want the code to continue execution, as this would result in a "Player X, it is your turn" prompt.
				gameEnded = true;
				
				//Construct the final score base
				String score = "FINAL SCORE\n------------\nTOTAL GAMES: " + gamesPlayedTotal + "\n";
				
				//For each of the players, append their score to the score string
				//We add the newline character before the actual string since the "score" string doesn't end with a newline
				//Or..well..it does, but it's only so we can pad between the TOTAL GAMES display and the actual player scores.
				for(int i = 0; i < players.size(); i++)
				{
					score += "\nPlayer " + (i + 1) + " win count: " + playerWinCount.get(i);
				}
				
				//Finally, display the score.
				JOptionPane.showMessageDialog(null, score);
				
				//Stop all the currently running audio
				Utils.AudioManager.stopAllAudio();
				
				//After the player reads the score, terminate the form
				//Then, begin the exit process
				frame.dispose();
				GameExit.Initiate();
				break;
		}
	}
	
	//Stop all currently playing audio, dispose of the frame, and then start a new game
	//Note: nextPlayer() won't be called after the start invocation.
	//This is because the nextPlayer() function that brought us here is still executing.
	static void newGame()
	{
		Utils.AudioManager.stopAllAudio();
		frame.dispose();
		start(size, players, false);
	}
	
	//Repaints the frame so any addition (components etc.) can be displayed
	static void flushComponentEdit()
	{
		frame.validate();
		frame.repaint();
	}
	
	//Self-explanatory. This function clears the entire board.
	static void clearBoard()
	{
		frame.getContentPane().removeAll();
		frame.repaint();
	}
	
	//Function that scans a line to see if the player filled it all with their color
	static boolean scanBoardLine(int x, int y, int directionX, int directionY)
	{
		//If we've managed to complete the entire scan without interruption, then the player has successfully filled the line with their own color
		if(x > spaces.length - 1) return true;
		
		//Get the current spaces row
		BoardGameSpace[] currentSpaces = spaces[x];
		
		//Again, if we've managed to complete the entire scan without interruption...you know the drill.
		if(y > currentSpaces.length - 1) return true;
		
		//If the current space is occupied by a color which doesn't belong to the player, return false.
		//This is because the player does not have a solid color line.
		if(currentSpaces[y].getOwner() != players.get(currentPlayer)) return false;
		
		//Continue the scan, incrementing by the direction
		return scanBoardLine(x + directionX, y + directionY, directionX, directionY);
	}
	
	static boolean gameEnded = false;
	static void nextPlayer()
	{
		//Check to see if the player won, but only if the game is actually running
		scanBoardForWin();
		
		//If the game has ended, we return from the function.
		//The reasoning for this should be self-explanatory.
		if(gameEnded) return;
		
		//Set the current player to the next value
		//If it is greater than the obtainable player index (length - 1), we set it back to 0
		currentPlayer++;
		if(currentPlayer > players.size() - 1) currentPlayer = 0;
		
		//Notify the user that we've moved to the next player
		JOptionPane.showMessageDialog(null, "Player " + (currentPlayer + 1) + ", it's your turn.");
	}
}
