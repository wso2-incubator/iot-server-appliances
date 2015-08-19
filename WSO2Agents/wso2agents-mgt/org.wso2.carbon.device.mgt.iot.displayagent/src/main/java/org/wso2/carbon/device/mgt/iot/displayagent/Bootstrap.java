package org.wso2.carbon.device.mgt.iot.displayagent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Bootstrap extends Application {

	private Scene scene;

	@Override
	public void start(Stage stage) {
		// create scene
		stage.setTitle("Web View");
		scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		stage.setFullScreen(true);
		// show stage
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}