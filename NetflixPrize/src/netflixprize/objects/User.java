package netflixprize.objects;

import java.util.ArrayList;

public class User implements Comparable<User> {
	private int id;
	private ArrayList<Tag> tags;
	private ArrayList<Rating> ratings;
	private double mean;
	private double stdDev;
	
	public User(int id) {
		this.id = id;
		tags = new ArrayList<>();
		ratings = new ArrayList<>();
	}
	
	public void addRating(Rating r) {
		ratings.add(r);
	}
	
	public ArrayList<Rating> getRatings() {
		return ratings;
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}

	public int getId() {
		return id;
	}
	
	public void computeMean() {
		double sum = 0;
		for(Rating r : ratings) sum += r.getValue();
		mean = sum / ratings.size();
	}
	
	public void computeStdDev() {
		double sum = 0;
		for(Rating r : ratings) sum += Math.pow(r.getValue() - mean, 2);
		stdDev = Math.sqrt(sum / ratings.size());
	}
	
	public double getMeanRating() {
		return mean;
	}
	
	public double getStdDevRating() {
		return stdDev;
	}

	@Override
	public int compareTo(User other) {
		// TODO Auto-generated method stub
		return id - other.id;
	}
}