package netflixprize.fileio;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");
	
	public static ArrayList<String> readFile(String filename) throws IOException {
		Scanner input = null;
		try {
			ArrayList<String> output = new ArrayList<String>();
			input = new Scanner(new FileReader(filename));
			while(input.hasNextLine()) output.add(input.nextLine());
			return output;
		} finally {
			if(input != null) input.close();
		}
	}
	
	public static void writeFile(String filename, ArrayList<String> fileData) throws IOException {
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(filename);
			for(String s : fileData) writer.write(s + lineSeparator);
		} finally {
			if(writer != null) writer.close();
		}
	}
}