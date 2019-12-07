
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import netflixprize.fileio.FileIO;
import netflixprize.fileio.MovieCSVReader;
import netflixprize.objects.Movie;
import netflixprize.objects.Rating;
import netflixprize.objects.Tag;
import netflixprize.objects.User;

public class NetflixPredictor {
	// Add fields to represent your database.
	private ArrayList<Movie> movies;
	private ArrayList<User> users;

	/**
	 * 
	 * Use the file names to read all data into some local structures. 
	 * 
	 * @param movieFilePath The full path to the movies database.
	 * @param ratingFilePath The full path to the ratings database.
	 * @param tagFilePath The full path to the tags database.
	 * @param linkFilePath The full path to the links database.
	 */
	public NetflixPredictor(String filenameMovie, String filenameRatings, 
			String filenameTags, String filenameLinks) {
		MovieCSVReader reader = new MovieCSVReader();
		movies = new ArrayList<>();
		users = new ArrayList<>();
		
		try {
			ArrayList<String> movieLines = FileIO.readFile(filenameMovie);
			ArrayList<String> links = FileIO.readFile(filenameLinks);
			ArrayList<String> ratingLines = FileIO.readFile(filenameRatings);
			ArrayList<String> tagLines = FileIO.readFile(filenameTags);
			
			movieLines.remove(0);
			links.remove(0);
			ratingLines.remove(0);
			tagLines.remove(0);
			for(String s : movieLines) movies.add(reader.translateMovie(s));
			for(int i = 0; i < movies.size(); i++) {
				String line = links.get(i);
				String[] ids = reader.translateLinks(line);
				String imdbId = ids[1];
				String tmdbId = "";
				if(ids.length > 2) tmdbId = ids[2];
				movies.get(i).setIds(imdbId, tmdbId);
			}
			
			Collections.sort(movies);
			
			int prevId = 0;
			for(String s : ratingLines) {
				String[] stuff = reader.translateRatings(s);
				int userId = Integer.parseInt(stuff[0]);
				int movieId = Integer.parseInt(stuff[1]);
				Rating r = new Rating(Double.parseDouble(stuff[2]), 
						Long.parseLong(stuff[3]), userId, movieId);
				
				Movie m = movies.get(Collections.binarySearch(movies, new Movie(movieId)));
				m.addRating(r);
					
				if(prevId != userId) {
					User u = new User(userId);
					u.addRating(r);
					users.add(u);
					prevId = userId;
				} else {
					users.get(users.size() - 1).addRating(r);
				}
			}
			
			for(String s : tagLines) {
				String[] stuff = reader.translateRatings(s);
				int userId = Integer.parseInt(stuff[0]);
				int movieId = Integer.parseInt(stuff[1]);
				Tag t = new Tag(stuff[2], Long.parseLong(stuff[3]), userId);
				
				movies.get(Collections.binarySearch(movies, new Movie(movieId))).addTag(t);
					
				if(Collections.binarySearch(users, new User(userId)) == -1) {
					User u = new User(userId);
					u.addTag(t);
				} else users.get(users.size() - 1).addTag(t);
				
				Collections.sort(users);
			}
			
			for(User u : users) {
				u.computeMean();
				u.computeStdDev();
				Collections.sort(u.getRatings());
			}
			
			for(Movie m : movies) {
				m.computeMean();
				m.computeStdDev();
				Collections.sort(m.getRatings());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/**
	 * If userNumber has rated movieNumber, return the rating. Otherwise, return -1.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or -1 if the user does not 
	 * exist in the database, the movie does not exist, or the movie has not been rated by 
	 * this user.
	 */
	public double getRating(int userID, int movieID) {
		int movieIndex = Collections.binarySearch(movies, new Movie(movieID));
		if(movieIndex >= 0) {
			Movie m = movies.get(movieIndex);
			ArrayList<Rating> ratings = m.getRatings();
			Collections.sort(ratings);
			int index = Collections.binarySearch(ratings, new Rating(userID));
			if(index >= 0) return ratings.get(index).getValue();
		}
		
		return -1;
	}
	
	/**
	 * If userNumber has rated movieNumber, return the rating. 
	 * Otherwise, use other available data to guess what this user would rate the movie.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or the best guess if the movie 
	 * has not been rated by this user.
	 * @pre A user with id userID and a movie with id movieID exist in the database.
	 */
	public double guessRating(int userID, int movieID) {
		double x = getRating(userID, movieID);
		if(x != -1) return x;
		else {
			Movie m = movies.get(Collections.binarySearch(movies, new Movie(movieID)));
			int userIndex = Collections.binarySearch(users, new User(userID));
			User u = null;
			if(userIndex != -1) u = users.get(userIndex);
			double meanRating = m.getMeanRating();
			double meanUserRating = 0;
			if(u != null) meanUserRating = u.getMeanRating();
			String[] genres = m.getGenres();
			int numRatings = m.getRatings().size();
			ArrayList<Rating> genreRatings = new ArrayList<>();
			
			for(Movie m1 : movies) {
				if(commonElements(m1.getGenres(), genres) && 
						Math.abs(m1.getYear() - m.getYear()) <= 6) {
					for(Rating r : m1.getRatings()) {
						if(r.getUserId() == userID) {
							genreRatings.add(r);
						}
					}
				}
			}
			
			double meanGenreRating = mean(genreRatings);
			int size = genreRatings.size();
			
			if(numRatings == 0 && size == 0) return 3;
			else if(numRatings == 0 || size == 0) return meanUserRating;
			else if(u == null) return meanRating;
			else return (3 * meanRating + 5 * meanGenreRating) / 8;
		}
	}
	
	private boolean commonElements(String[] s1, String[] s2) {
		for(String s : s1) {
			for(String t : s2) {
				if(s.equalsIgnoreCase(t)) return true;
			}
		}
		
		return false;
	}
	
	private double mean(ArrayList<Rating> ratings) {
		double sum = 0;
		for(Rating r : ratings) sum += r.getValue();
		return sum / ratings.size();
	}
	
	/**
	 * Recommend a movie that you think this user would enjoy (but they have not currently
	 * rated it). 
	 * 
	 * @param userNumber The ID of the user.
	 * @return The ID of a movie that data suggests this user would rate highly 
	 * (but they haven't rated it currently).
	 * @pre A user with id userID exists in the database.
	 */
	public int recommendMovie(int userID) {
		ArrayList<String> likedGenres = new ArrayList<>();
		User u = users.get(Collections.binarySearch(users, new User(userID)));
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		for(Rating r : u.getRatings()) {
			if(r.getValue() >= u.getMeanRating() + u.getStdDevRating()) {
				Movie m = movies.get(Collections.binarySearch(movies, 
						new Movie(r.getMovieId())));
				for(String s : m.getGenres()) {
					if(!likedGenres.contains(s)) likedGenres.add(s);
				}
			}
		}
		
		ArrayList<Movie> recommendedMovies = new ArrayList<>();
		
		for(Movie m : movies) {
			if(m.getMeanRating() > 3.5 && 
					(m.getYear() - currentYear < 5 || m.getRatings().size() > 50)) {
				for(String s : m.getGenres()) {
					if(likedGenres.contains(s)) recommendedMovies.add(m);
				}
			}
		}
		
		Movie recommendedMovie = null;
		if(recommendedMovies.size() > 0) recommendedMovie = recommendedMovies.get(
				(int)(Math.random() * recommendedMovies.size()));
		
		if(recommendedMovie != null) return recommendedMovie.getId();
		return 0;
	}

	public ArrayList<Movie> getMovies() {
		// TODO Auto-generated method stub
		return movies;
	}
}