

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.JOptionPane;

import netflixprize.fileio.FileIO;
import netflixprize.objects.Movie;
import processing.core.PApplet;
import processing.core.PImage;

public class DrawingMovie {
	private Movie movie;
	private PImage coverArt;
	
	public DrawingMovie(Movie m) {
		this.movie = m;
		coverArt = null;
	}
	
	public void draw(PApplet p, float x, float y, float width, float height) {
		if (movie != null) {
			if (coverArt != null) {
				p.image(coverArt, x, y,width,height);
			}
			
			String title = movie.getTitle();
			if(title.length() > 15) title = title.substring(0, 13) + "...";
			p.text(title + FileIO.lineSeparator + "Mean rating: " + Math.round(movie.getMeanRating() * 100) / 100.0, 
					x, y + height + p.height / 25);
		}
		
		p.stroke(0);
		p.noFill();
		p.rect(x, y, width, height);
	}

	public void downloadArt(PApplet p) {
		Thread downloader = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner input = null;
				String tmdbId = movie.getTmdbId();
				
				String url = "https://api.themoviedb.org/3/movie/" + 
						tmdbId + "?api_key=5f6d9e3d5dbcbabfdcb78aa2846ee47f";
				
				try {
					String output = "";
					URLConnection u = (new URL(url)).openConnection();
					input = new Scanner(u.getInputStream());
					while(input.hasNextLine()) output += input.nextLine() + FileIO.lineSeparator;
					String imgUrl = "https://image.tmdb.org/t/p/w300_and_h450_bestv2";
					int posterPosition = output.lastIndexOf("poster_path");
					posterPosition = output.indexOf("\"/", posterPosition);
					int endQuote = output.indexOf("\"", posterPosition + 1);
					imgUrl += output.substring(posterPosition + 1, endQuote);
					coverArt = p.loadImage(imgUrl);
				} catch(IOException e) {
					JOptionPane.showMessageDialog(null, 
							"Some cover art failed to load correctly. Check your internet connection.");
					// e.printStackTrace();
				} finally {
					if(input != null) input.close();
				}
			}
		});

		downloader.start();
	}
}