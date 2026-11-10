import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        Parent root = FXMLLoader.load(getClass().getResource("mainview.fxml"));
        primaryStage.setTitle("JaimeNet");

        Scene scene = new Scene(root, 0.8 * screen.getWidth(), 0.8 * screen.getHeight());

        // Attach stylesheet here:
        scene.getStylesheets().add(getClass().getResource("mystyle.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}