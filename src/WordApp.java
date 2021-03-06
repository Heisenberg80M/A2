//package skeletonCodeAssgnmt2;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

  static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static 	Score score = new Score();

	static WordPanel w;



	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	JFrame frame = new JFrame("WordGame");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);

    	JPanel g = new JPanel();
      g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
    	g.setSize(frameX,frameY);


			w = new WordPanel(words,yLimit,score);
			w.setSize(frameX,yLimit+100);
	    g.add(w);


	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
	    JLabel caught =new JLabel("Caught: " + score.getCaught() + "    ");
	    JLabel missed =new JLabel("Missed:" + score.getMissed()+ "    ");
	    JLabel scr =new JLabel("Score:" + score.getScore()+ "    ");
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
			w.completed();

	    final JTextField textEntry = new JTextField("",20);
	    textEntry.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent evt) {
	          String text = textEntry.getText();
	          for (int j =0; j < words.length; j++){
							if(w.checkInput(text)){
								score.caughtWord(text.length());
								break;
							}
						}
	          textEntry.setText("");
	          textEntry.requestFocus();
	      }
	    });

	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);

     JPanel b = new JPanel();
     b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

		 JButton startB = new JButton("Start");;
		 // add the listener to the jbutton to handle the "pressed" event
		 startB.addActionListener(new ActionListener()
	     {
	      public void actionPerformed(ActionEvent e)
	      {
	    	  if(!w.checkCompletion()){
						System.out.println("Game already started!");
					}
					else{
						Thread t = new Thread(w);
						t.start();
					}
	    	  textEntry.requestFocus();  //return focus to the text entry field
	      }
	   });

		 JButton endB = new JButton("End");;
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	    	  //[snip]
					w.completed();
					score.resetScore();
	      }
	    });

			JButton closeGameB = new JButton("Quit");;
			// add the listener to the jbutton to handle the "pressed" event
			closeGameB.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						System.exit(0);
					}
				});

		b.add(startB);
		b.add(endB);
		b.add(closeGameB);
		g.add(b);

    frame.setLocationRelativeTo(null);  // Center window on screen.
  	frame.add(g); //add contents to window
    frame.setContentPane(g);
   	//frame.pack();  // don't do this - packs it into small space
    frame.setVisible(true);

		 Runnable keepScore = new Runnable() //runnable for updating the score
		 {
	 			public void run()
	 			{
	 					while(true)
	 					{

							caught.setText("Caught: " + score.getCaught() + "    ");
							missed.setText("Missed:" + score.getMissed()+ "    ");
							scr.setText("Score:" + score.getScore()+ "    ");
							if (!(totalWords >score.getTotal())){ //check if words captured or missed < no of words
									w.completed();  //end game

									// create a dialog Box
									JDialog d = new JDialog(frame, "Game Over!");

									// create a label
									JLabel l = new JLabel("You caught "+score.getCaught()+" out of "+score.getTotal()+" words. Your Score was "+ score.getScore());

									d.add(l);

									// setsize of dialog
									d.setSize(400, 200);

			            // set location of dialog
			            d.setLocation(200, 200);

									// set visibility of dialog
									d.setVisible(true);

									score.resetScore(); //reset scores
							}
						}
				}
		 };
		 Thread keepS = new Thread(keepScore);
		 keepS.start();
	}


public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;

	}

	public static void main(String[] args) {

		//deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall
		noWords=Integer.parseInt(args[1]); // total words falling at any point
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words
		if (tmpDict!=null){
			dict= new WordDictionary(tmpDict);
		}
		else{
			dict = new WordDictionary();
		}

		WordRecord.dict=dict; //set the class dictionary for the words.

		words = new WordRecord[noWords];  //shared array of current words

		//[snip]

		setupGUI(frameX, frameY, yLimit);
    	//Start WordPanel thread - for redrawing animation

		int x_inc=(int)frameX/noWords;
	  	//initialize shared array of current words

		for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}


	}

}
