package netflixprize.objects;

@SuppressWarnings("unused")
public class Rating implements Comparable<Rating> {
	private double rating;
	private long timestamp;
	private int userId;
	private int movieId;
	
	public Rating(double rating, long timestamp, int userId, int movieId) {
		this.rating = rating;
		this.timestamp = timestamp;
		this.userId = userId;
		this.movieId = movieId;
	}
	
	public Rating(int userId) {
		this.userId = userId;
	}
	
	public double getValue() {
		return rating;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getMovieId() {
		return movieId;
	}

	@Override
	public int compareTo(Rating other) {
		// TODO Auto-generated method stub
		/* if(userId != other.userId) */ return userId - other.userId;
		// else return movieId - other.movieId;
	}
}