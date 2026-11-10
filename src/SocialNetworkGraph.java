import java.util.*;

public class SocialNetworkGraph {
    private final Map<String, SocialProfile> profiles = new HashMap<>();

    public boolean addProfile(String name) {
        if (name == null || name.trim().isEmpty() || containsProfile(name)) {
            return false;
        }
        profiles.put(name.trim().toLowerCase(), new SocialProfile(name.trim()));
        return true;
    }

    public boolean deleteProfile(String name) {
        if (!containsProfile(name)) return false;
        String key = name.trim().toLowerCase();
        SocialProfile toRemove = profiles.get(key);

        for (String friendName : new ArrayList<>(toRemove.getFriends())) {
            SocialProfile friendProfile = profiles.get(friendName.toLowerCase());
            if (friendProfile != null) {
                friendProfile.removeFriend(toRemove.getName());
            }
        }
        profiles.remove(key);
        return true;
    }

    public SocialProfile getProfile(String name) {
        if (name == null) return null;
        return profiles.get(name.trim().toLowerCase());
    }

    public boolean containsProfile(String name) {
        if (name == null) return false;
        return profiles.containsKey(name.trim().toLowerCase());
    }

    public boolean addFriendship(String name1, String name2) {
        SocialProfile p1 = getProfile(name1);
        SocialProfile p2 = getProfile(name2);
        if (p1 == null || p2 == null || name1.equalsIgnoreCase(name2)) return false;
        return p1.addFriend(p2.getName()) && p2.addFriend(p1.getName());
    }

    public boolean removeFriendship(String name1, String name2) {
        SocialProfile p1 = getProfile(name1);
        SocialProfile p2 = getProfile(name2);
        if (p1 == null || p2 == null) return false;
        return p1.removeFriend(p2.getName()) || p2.removeFriend(p1.getName());
    }

    public Collection<SocialProfile> getAllProfiles() {
        return profiles.values();
    }
}