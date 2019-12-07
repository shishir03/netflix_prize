import java.awt.event.KeyEvent;
// import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import netflixprize.fileio.FileIO;
import netflixprize.objects.Movie;
import processing.core.PApplet;

/*
 * Coded by: Shishir Iyer
 * Date: April 11, 2019
 * 
 * Additional Features:
 *  - Pressing S brings up a (not very good) search engine. Typing in the title or part of the title of a movie will
 *  bring up all related movies. Pressing S again will return to the original page.
 *  - Ratings are displayed below the movie titles.
 */

public class DrawingSurface extends PApplet {
	public static final String ratingsFile = "data" + FileIO.fileSeparator + 
			"ml-latest-small" + FileIO.fileSeparator + "ratings.csv";
	public static final String moviesFile = "data" + FileIO.fileSeparator +  
			"ml-latest-small" + FileIO.fileSeparator + "movies.csv";
	public static final String linksFile = "data" + FileIO.fileSeparator +  
			"ml-latest-small" + FileIO.fileSeparator + "links.csv";
	public static final String tagsFile = "data" + FileIO.fileSeparator + 
			"ml-latest-small" + FileIO.fileSeparator + "tags.csv";
	private final float DRAWING_WIDTH = 1280, DRAWING_HEIGHT = 1480;
	private final float movieWidth = DRAWING_WIDTH / 5, movieHeight = DRAWING_HEIGHT / 4;
	private float scaleW, scaleH;
	private int currentMovie;
	private float movieDrawingOffset;
	private float shiftSpeed;
	private NetflixPredictor predictor;
	private boolean predictorLoaded;
	private boolean searching;
	private ArrayList<DrawingMovie> drawableMovies;
	private ArrayList<Movie> hits;
	private ArrayList<DrawingMovie> hitDrawings;
	private DrawingMovie recommendedMovie;
	private int currentUserID;

	public DrawingSurface() {
		currentUserID = -1;
		predictorLoaded = false;
		recommendedMovie = new DrawingMovie(null);
		currentMovie = 0;
		shiftSpeed = 0;
		movieDrawingOffset = 0;
		drawableMovies = new ArrayList<DrawingMovie>();
		hits = new ArrayList<>();
		hitDrawings = new ArrayList<>();
		searching = false;
	}

	public void initializePredictor() {
		new Thread() {
			public void run() {
				predictor = new NetflixPredictor(moviesFile, ratingsFile, tagsFile, linksFile);

				int recommendedID = predictor.recommendMovie(currentUserID);
				ArrayList<Movie> movies = predictor.getMovies();
				if(recommendedID != 0) {
					Movie m = movies.get(Collections.binarySearch(movies, new Movie(recommendedID)));
					recommendedMovie = new DrawingMovie(m);
					recommendedMovie.downloadArt(DrawingSurface.this);
				}

				predictorLoaded = true;
			}
		}.start();
	}

	// The statements in the setup() function
	// execute once when the program begins
	public void setup() {
	}

	// The statements in draw() are executed until the
	// program is stopped. Each statement is executed in
	// sequence and after the last line is read, the first
	// line is executed again.
	public void draw() {
		background(255); // Clear the screen with a white background
		scaleW = width / DRAWING_WIDTH;
		scaleH = height / DRAWING_HEIGHT;
		pushMatrix();
		scale(scaleW, scaleH);

		if (predictorLoaded) {
			fill(0);
			textAlign(LEFT);
			textSize(30);
			text("All Movies", 10, movieHeight / 4 - 10);
			ArrayList<Movie> movieData = predictor.getMovies();

			if(!searching) {
				for (int i = 0; i < 6 && i + currentMovie < movieData.size(); i++) {
					if (i + currentMovie >= drawableMovies.size()) {
						DrawingMovie dm = new DrawingMovie(movieData.get(i + currentMovie));
						dm.downloadArt(this);
						drawableMovies.add(dm);
					}
					drawableMovies.get(i + currentMovie).draw(this,
							movieDrawingOffset + i * movieWidth + i * movieWidth / 5, movieHeight / 4, movieWidth,
							movieHeight);
				}
			} else {
				for (int i = 0; i < 6 && i + currentMovie < hits.size(); i++) {
					if (i + currentMovie >= hitDrawings.size()) {
						DrawingMovie dm = new DrawingMovie(hits.get(i + currentMovie));
						dm.downloadArt(this);
						hitDrawings.add(dm);
					}
					hitDrawings.get(i + currentMovie).draw(this,
							movieDrawingOffset + i * movieWidth + i * movieWidth / 5, movieHeight / 4, movieWidth,
							movieHeight);
				}
			}
	
			fill(0);
			textAlign(LEFT);
			textSize(30);
			text("Recommended", DRAWING_WIDTH / 2 - DRAWING_WIDTH / 4, DRAWING_HEIGHT / 2.5f);
			recommendedMovie.draw(this, DRAWING_WIDTH / 2 - DRAWING_WIDTH / 4, DRAWING_HEIGHT / 2.5f + 10, 
					DRAWING_WIDTH / 2, DRAWING_HEIGHT / 2);
		} else {
			fill(0);
			textAlign(CENTER);
			textSize(50);
			text("Press the 'R' Key to Load (or re-load) the Predictor", DRAWING_WIDTH / 2, DRAWING_HEIGHT / 2);
		}

		popMatrix();
	}

	/* public void mousePressed() {
		if (mouseButton == LEFT) {
			Point2D.Float click = new Point2D.Float(mouseX / scaleW, mouseY / scaleH);
		}
	}

	public void mouseDragged() {
		if (mouseButton == LEFT) {
			Point2D.Float click = new Point2D.Float(mouseX / scaleW, mouseY / scaleH);
		}
	} */

	public void keyReleased() {
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
			shiftSpeed = 0;
		}
	}
	
	public void searchMovies(String title) {
		for(Movie m : predictor.getMovies()) {
			if(m.getTitle().toLowerCase().contains(title.toLowerCase())) hits.add(m);
		}	
	}

	public void keyPressed() {
		if (keyCode == KeyEvent.VK_SPACE) {

		} else if (keyCode == KeyEvent.VK_RIGHT) {
			shiftSpeed = (float) Math.min(shiftSpeed + 1, 20);
			movieDrawingOffset -= shiftSpeed;
			if (movieDrawingOffset < -(movieWidth + movieWidth / 5)) {
				if (currentMovie == predictor.getMovies().size() - 1) {
					movieDrawingOffset = -(movieWidth + movieWidth / 5);
				} else {
					movieDrawingOffset = 0;
					currentMovie++;
				}
			}

		} else if (keyCode == KeyEvent.VK_LEFT) {
			shiftSpeed = (float) Math.min(shiftSpeed + 1, 20);
			movieDrawingOffset += shiftSpeed;
			if (movieDrawingOffset > 0) {
				if (currentMovie == 0) {
					movieDrawingOffset = 0;
				} else {
					movieDrawingOffset = -(movieWidth + movieWidth / 5);
					currentMovie--;
				}
			}

		} else if (keyCode == KeyEvent.VK_R) {
			String id = JOptionPane.showInputDialog("Please enter your user ID:");

			try {
				currentUserID = Integer.parseInt(id);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}

			initializePredictor();
		} else if(keyCode == KeyEvent.VK_S) {
			if(!searching) {
				String searchTitle = JOptionPane.showInputDialog("Search up a movie:");
				searchMovies(searchTitle);
				
				if(!hits.isEmpty()) {
					searching = true;
				} else {
					JOptionPane.showMessageDialog(null, "No movies found.");
				}
			} else {
				searching = false;
				hits.clear();
				hitDrawings.clear();
			}
		}
	}
}