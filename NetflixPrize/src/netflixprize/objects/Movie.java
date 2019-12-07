package netflixprize.objects;

import java.util.ArrayList;

import netflixprize.fileio.FileIO;

@SuppressWarnings("unused")
public class Movie implements Comparable<Movie> {
	private String title;
	private int id;
	private int year;
	private String imdbID;
	private String tmdbID;
	private String[] genres;
	private ArrayList<Tag> tags;
	private ArrayList<Rating> ratings;
	private double mean;
	private double stDev;
	
	public Movie(int id) {
		this.id = id;
	}
	
	public Movie(int id, String title, int year, String[] genres) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.genres = genres;
		ratings = new ArrayList<>();
		tags = new ArrayList<>();
	}
	
	public String toString() {
		return id + FileIO.lineSeparator + title + FileIO.lineSeparator + year + 
				FileIO.lineSeparator + genres + FileIO.lineSeparator;
	}
	
	public int getId() {
		return id;
	}
	
	public String[] getGenres() {
		return genres;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setIds(String i, String t) {
		imdbID = i;
		tmdbID = t;
	}
	
	public String getTmdbId() {
		return tmdbID;
	}
	
	public int getYear() {
		return year;
	}
	
	public void addRating(Rating r) {
		ratings.add(r);
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}
	
	public ArrayList<Rating> getRatings() {
		return ratings;
	}
	
	public void computeMean() {
		double sum = 0;
		for(Rating r : ratings) sum += r.getValue();
		mean = sum / ratings.size();
	}
	
	public void computeStdDev() {
		double sum = 0;
		for(Rating r : ratings) sum += Math.pow(r.getValue() - mean, 2);
		stDev = Math.sqrt(sum / ratings.size());
	}
	
	public double getMeanRating() {
		return mean;
	}
	
	public double getStdDevRating() {
		return stDev;
	}

	@Override
	public int compareTo(Movie other) {
		// TODO Auto-generated method stub
		return id - other.id;
	}
}