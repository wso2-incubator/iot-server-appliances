package org.wso2.carbon.device.mgt.iot.displayagent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSObject;

class Browser extends Region {

	private HBox toolBar;
	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();
	final WebView smallView = new WebView();

	public Browser() {
		//apply the styles
		getStyleClass().add("browser");


		// create the toolbar
		toolBar = new HBox();
		toolBar.setAlignment(Pos.CENTER);
		toolBar.getStyleClass().add("browser-toolbar");
		toolBar.getChildren().add(createSpacer());

		smallView.setPrefSize(120, 80);

		//handle popup windows
		webEngine.setCreatePopupHandler(
				new Callback<PopupFeatures, WebEngine>() {
					public WebEngine call(PopupFeatures config) {
						smallView.setFontScale(0.8);
						if (!toolBar.getChildren().contains(smallView)) {
							toolBar.getChildren().add(smallView);
						}
						return smallView.getEngine();
					}
				}
		);

		// process page loading
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<Worker.State>() {
					public void changed(ObservableValue<? extends Worker.State> ov,
										Worker.State oldState, Worker.State newState) {
						if (newState == Worker.State.SUCCEEDED) {
							JSObject win =
									(JSObject) webEngine.executeScript("window");
							win.setMember("app", new JavaApp());
						}
					}
				}
		);

		// load the home page
		webEngine.load("http://www.wso2.com");

		//add components
		getChildren().add(toolBar);
		getChildren().add(browser);
	}

	// JavaScript interface object
	public class JavaApp {

		public void exit() {
			Platform.exit();
		}
	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		double tbHeight = toolBar.prefHeight(w);
		layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
		layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
	}


}