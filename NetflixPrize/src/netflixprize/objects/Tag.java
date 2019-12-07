package netflixprize.objects;

@SuppressWarnings("unused")
public class Tag {
	private String name;
	private long timestamp;
	private int userId;
	
	public Tag(String name, long timestamp, int userId) {
		this.name = name;
		this.timestamp = timestamp;
		this.userId = userId;
	}
}