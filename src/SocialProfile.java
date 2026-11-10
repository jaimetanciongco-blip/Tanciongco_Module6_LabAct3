import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SocialProfile {
    private final String name;
    private String status;
    private String quote;
    private String imagePath;
    private final Set<String> friends;

    public SocialProfile(String name) {
        this.name = name;
        this.status = "is active";
        this.quote = "\"Stay hungry, stay foolish.\"";
        this.imagePath = "unknown.png";
        this.friends = new LinkedHashSet<>();
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean addFriend(String friendName) {
        if (!friendName.equalsIgnoreCase(this.name)) {
            return friends.add(friendName);
        }
        return false;
    }

    public boolean removeFriend(String friendName) {
        return friends.remove(friendName);
    }

    public Set<String> getFriends() {
        return Collections.unmodifiableSet(friends);
    }
}