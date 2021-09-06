//package skeletonCodeAssgnmt2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable {
		public static volatile boolean done;
		private WordRecord[] words;
		private int noWords;
		private int maxY;
		private Score score;

		Thread[] threads;


		public void paintComponent(Graphics g) {
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);

		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		   //draw the words
		   //animation must be added
		    for (int i=0;i<noWords;i++){
		    	//g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());
		    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY()+20);  //y-offset for skeleton so that you can see the words
		    }

		  }

		WordPanel(WordRecord[] words, int maxY, Score score) {
			this.words=words; //will this work?
			noWords = words.length;
			done=false;
			this.maxY=maxY;
			this.score = score;
		}

		public boolean checkCompletion(){
				return done;
		}

		public void completed(){
				done = true;
		}

		public void incomplete(){
				done = false;
		}

		@Override
		public void run() {

			threads = new Thread[noWords];
			incomplete(); //keep the game running

			for (int j = 0; j < noWords; j++){
				final WordRecord cWord = words[j]; // Each word assigned to its own thread
				Runnable tWord = new Runnable() //initialize runnable for particular word thread
				{
					public void run(){ // run method for each word thread

						while (done == false){
							try{
								TimeUnit.MILLISECONDS.sleep(300);
							}
							catch (InterruptedException ex){
								ex.printStackTrace();
							}
							cWord.drop(cWord.getSpeed()/200); //the word drops based on its speed

							if (cWord.dropped()){  //if word reaches the bottom
								score.missedWord(); //missed word Counter updated
								cWord.resetWord(); //a new word is fetched
							}
						}
					}
			  };

				threads[j] = new Thread(tWord); //initialize thread for word
				threads[j].start(); //Start it up
			}

			while (done == false){
				repaint();
			}

			for (int j = 0; j < noWords; j++){
							words[j].resetWord();
			}
			repaint();
		}

		public boolean checkInput(String a){
			boolean found = false;
			for (int j = 0; j < noWords; j++){
				if(words[j].matchWord(a)){
					found = true;
					return found;
				}
			}
			return found;
		}

	}
