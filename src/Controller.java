import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField statusChangeLabel;
    @FXML
    private TextField pictureLabel;
    @FXML
    private TextField friendLabel;
    @FXML
    private TextField unfriendLabel;
    @FXML
    private TextField quoteLabel;
    @FXML
    private VBox profileViewPane;
    @FXML
    private Canvas graphCanvas;
    @FXML
    private Label displayNameLabel;
    @FXML
    private Label displayStatusLabel;
    @FXML
    private Label displayQuoteLabel;
    @FXML
    private ImageView pictureView;
    @FXML
    private ListView<String> listView;
    @FXML
    private Label statusLabel;

    private SocialNetworkGraph network;
    private SocialProfile currentProfile;
    private boolean showingNetwork = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new SocialNetworkGraph();
        seedData();

        nameField.setOnAction(e -> handleLookup());
        statusChangeLabel.setOnAction(e -> handleStatus());
        pictureLabel.setOnAction(e -> handlePicture());
        friendLabel.setOnAction(e -> handleAddFriend());
        unfriendLabel.setOnAction(e -> handleUnfriend());
        quoteLabel.setOnAction(e -> handleQuote());

        lookupProfile("Jaime");
    }

    private void seedData() {
        String[] users = {
                "Jaime",
                "Bill Gates", "Steve Jobs", "Dennis Ritchie",
                "Linus Torvalds", "Larry Page", "Satya Nadella"
        };
        for (String user : users) {
            network.addProfile(user);
        }

        SocialProfile jaime = network.getProfile("Jaime");
        jaime.setStatus("is online");
        jaime.setQuote("\"Stay hungry, stay foolish.\"");
        jaime.setImagePath("jaime.png");

        network.addFriendship("Jaime", "Bill Gates");
        network.addFriendship("Jaime", "Steve Jobs");
        network.addFriendship("Jaime", "Linus Torvalds");
        network.addFriendship("Jaime", "Dennis Ritchie");
        network.addFriendship("Jaime", "Larry Page");

        SocialProfile bill = network.getProfile("Bill Gates");
        bill.setStatus("is speaking...");
        bill.setQuote("\"Success is a lousy teacher. It seduces smart people into thinking they can't lose\"");
        bill.setImagePath("cabatuanm.PNG");
        network.addFriendship("Bill Gates", "Steve Jobs");
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            setStatusMessage("Please enter a name to add.");
            return;
        }
        if (network.addProfile(name.trim())) {
            lookupProfile(name.trim());
            setStatusMessage("New profile created for " + name.trim());
            nameField.clear();
        } else {
            setStatusMessage("Profile already exists: " + name.trim());
        }
    }

    @FXML
    private void handleDelete() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            if (currentProfile != null) {
                name = currentProfile.getName();
            } else {
                setStatusMessage("Please specify a profile to delete.");
                return;
            }
        }
        if (network.deleteProfile(name.trim())) {
            setStatusMessage("Profile deleted: " + name.trim());
            clearView();
            nameField.clear();
        } else {
            setStatusMessage("Profile not found: " + name.trim());
        }
    }

    @FXML
    private void handleLookup() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            setStatusMessage("Please enter a profile name to lookup.");
            return;
        }
        lookupProfile(name.trim());
    }

    private void lookupProfile(String name) {
        SocialProfile profile = network.getProfile(name);
        if (profile != null) {
            currentProfile = profile;
            updateView();
            setStatusMessage("Displaying " + profile.getName() + ".");
        } else {
            clearView();
            setStatusMessage("A profile with the name " + name + " does not exist.");
        }
    }

    @FXML
    private void handleStatus() {
        if (currentProfile == null) {
            setStatusMessage("No profile currently selected.");
            return;
        }
        String status = statusChangeLabel.getText();
        if (status != null && !status.trim().isEmpty()) {
            currentProfile.setStatus(status.trim());
            displayStatusLabel.setText(currentProfile.getName() + " " + currentProfile.getStatus());
            setStatusMessage("Status updated");
            statusChangeLabel.clear();
        }
    }

    @FXML
    private void handlePicture() {
        if (currentProfile == null) {
            setStatusMessage("No profile currently selected.");
            return;
        }
        String pic = pictureLabel.getText();
        if (pic != null && !pic.trim().isEmpty()) {
            currentProfile.setImagePath(pic.trim());
            loadProfileImage(currentProfile.getImagePath());
            setStatusMessage("Picture updated");
            pictureLabel.clear();
        }
    }

    @FXML
    private void handleQuote() {
        if (currentProfile == null) {
            setStatusMessage("No profile currently selected.");
            return;
        }
        String quote = quoteLabel.getText();
        if (quote != null && !quote.trim().isEmpty()) {
            currentProfile.setQuote("\"" + quote.trim() + "\"");
            displayQuoteLabel.setText(currentProfile.getQuote());
            setStatusMessage("Quote updated");
            quoteLabel.clear();
        }
    }

    @FXML
    private void handleAddFriend() {
        if (currentProfile == null) {
            setStatusMessage("No profile currently selected.");
            return;
        }
        String friend = friendLabel.getText();
        if (friend == null || friend.trim().isEmpty()) {
            setStatusMessage("Please enter a friend's name.");
            return;
        }
        if (!network.containsProfile(friend.trim())) {
            setStatusMessage(friend.trim() + " does not exist in the network.");
            return;
        }
        if (network.addFriendship(currentProfile.getName(), friend.trim())) {
            refreshFriends();
            setStatusMessage(friend.trim() + " added as a friend.");
            friendLabel.clear();
        } else {
            setStatusMessage("Could not add friend (already friends or same person).");
        }
    }

    @FXML
    private void handleUnfriend() {
        if (currentProfile == null) {
            setStatusMessage("No profile currently selected.");
            return;
        }
        String friend = unfriendLabel.getText();
        if (friend == null || friend.trim().isEmpty()) {
            friend = listView.getSelectionModel().getSelectedItem();
        }
        if (friend != null && network.removeFriendship(currentProfile.getName(), friend.trim())) {
            refreshFriends();
            setStatusMessage(friend.trim() + " removed from friends.");
            unfriendLabel.clear();
        } else {
            setStatusMessage("Failed to remove friend: " + (friend != null ? friend : "none selected"));
        }
    }

    @FXML
    private void handleToggleNetwork() {
        showingNetwork = !showingNetwork;
        if (showingNetwork) {
            profileViewPane.setVisible(false);
            graphCanvas.setVisible(true);
            renderNetworkGraph();
            setStatusMessage("Displaying network graph.");
        } else {
            graphCanvas.setVisible(false);
            profileViewPane.setVisible(true);
            setStatusMessage("Displaying profile view.");
        }
    }

    private void updateView() {
        if (currentProfile == null) return;
        displayNameLabel.setText(currentProfile.getName());
        displayStatusLabel.setText(currentProfile.getName() + " " + currentProfile.getStatus());
        displayQuoteLabel.setText(currentProfile.getQuote());
        loadProfileImage(currentProfile.getImagePath());
        refreshFriends();
    }

    private void refreshFriends() {
        listView.getItems().clear();
        if (currentProfile != null) {
            listView.getItems().addAll(currentProfile.getFriends());
        }
    }

    private void clearView() {
        currentProfile = null;
        displayNameLabel.setText("");
        displayStatusLabel.setText("");
        displayQuoteLabel.setText("");
        listView.getItems().clear();
        loadProfileImage("unknown.png");
    }

    private void loadProfileImage(String path) {
        try {
            InputStream in = getClass().getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (in != null) {
                pictureView.setImage(new Image(in));
                return;
            }
            File f = new File(path);
            if (f.exists()) {
                pictureView.setImage(new Image(f.toURI().toString()));
                return;
            }
        } catch (Exception ignored) {
        }

        InputStream fallback = getClass().getResourceAsStream("/unknown.png");
        if (fallback != null) {
            pictureView.setImage(new Image(fallback));
        }
    }

    private void setStatusMessage(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void renderNetworkGraph() {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        gc.setFill(Color.web("#161922"));
        gc.fillRoundRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight(), 16, 16);

        List<SocialProfile> profiles = new ArrayList<>(network.getAllProfiles());
        int count = profiles.size();
        if (count == 0) return;

        double midX = graphCanvas.getWidth() / 2.0;
        double midY = graphCanvas.getHeight() / 2.0;
        double circleRadius = Math.min(midX, midY) - 55.0;

        Map<String, double[]> positions = new HashMap<>();

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = midX + circleRadius * Math.cos(angle);
            double y = midY + circleRadius * Math.sin(angle);
            positions.put(profiles.get(i).getName(), new double[]{x, y});
        }

        gc.setStroke(Color.web("#374151"));
        gc.setLineWidth(1.5);
        for (SocialProfile p : profiles) {
            double[] from = positions.get(p.getName());
            for (String friend : p.getFriends()) {
                double[] to = positions.get(friend);
                if (to != null) {
                    gc.strokeLine(from[0], from[1], to[0], to[1]);
                }
            }
        }

        double r = 14.0;
        for (SocialProfile p : profiles) {
            double[] pos = positions.get(p.getName());
            boolean isCur = currentProfile != null && currentProfile.getName().equalsIgnoreCase(p.getName());

            gc.setFill(isCur ? Color.web("#6366f1") : Color.web("#10b981"));
            gc.fillOval(pos[0] - r, pos[1] - r, r * 2, r * 2);

            gc.setFill(Color.web("#f1f5f9"));
            gc.fillText(p.getName(), pos[0] - 25, pos[1] + r + 14);
        }
    }
}