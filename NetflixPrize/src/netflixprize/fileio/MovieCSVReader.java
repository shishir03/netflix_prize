package netflixprize.fileio;

import netflixprize.objects.Movie;

public class MovieCSVReader {
	public Movie translateMovie(String line) {
		int firstComma = line.indexOf(',');
		int id = Integer.parseInt(line.substring(0, firstComma));
		int secondComma = 0;
		if(line.charAt(firstComma + 1) == '"') secondComma = line.indexOf('"', firstComma + 2) + 1;
		else secondComma = line.indexOf(',', firstComma + 1);
		int parentheses = line.lastIndexOf('(');
		
		int year = 0;
		if(parentheses >= 0 && Character.isDigit(line.charAt(parentheses + 1))) {
			String y = line.substring(parentheses + 1, parentheses + 5);
			year = Integer.parseInt(y);
		} else {
			parentheses = secondComma + 1;
			year = 0;
		}
		
		String title = line.substring(firstComma + 1, parentheses - 1);
		int comma = title.indexOf(", The");
		if(comma != -1) title = "The " + title.substring(1, comma);
		String[] genre = line.substring(secondComma + 1).split("\\|");
		return new Movie(id, title, year, genre);
	}
	
	public String[] translateLinks(String line) {
		return line.split(",");
	}
	
	public String[] translateRatings(String line) {
		return line.split(",");
	}
	
	public String[] translateTags(String line) {
		return line.split(",");
	}
}