package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
private static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			Main.primaryStage = primaryStage;

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainGui.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Scene scene = new Scene(root, 476, 740);
			scene.getStylesheets().add(getClass().getResource("mainTheme.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
    public static Stage getStage() {
        return Main.primaryStage;
    }
}
