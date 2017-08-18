package com.xzymon.jee7.eden.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.xzymon.jee7.eden.fx.client.EventBeanClient;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;

public class StartFX extends Application {
	private static final String APP_TITLE = "xzymon EdenFX App";
	public static final int SCENE_WIDTH = 300;
	public static final int SCENE_HEIGHT = 50;

	public static final void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void init() throws Exception {
		//super.init();
		printMethodThread("init");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//primaryStage.setTitle(APP_TITLE);
		//fillWithText(primaryStage);
		printMethodThread("start");
		//fillWithTextArea(primaryStage);
		fillWithEJBResponse(primaryStage);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		//super.stop();
		printMethodThread("stop");
	}

	private void fillWithText(Stage primaryStage) {
		Text text = new Text("Some inner text\nsecond line");
		Button exit = prepareExitButton();
		VBox hostBox = new VBox();
		hostBox.getChildren().add(text);
		hostBox.getChildren().add(exit);
		Scene scene = new Scene(hostBox, SCENE_WIDTH, SCENE_HEIGHT);
		primaryStage.setScene(scene);
	}

	private Button prepareExitButton() {
		Button exitButton = new Button("Exit");
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		return exitButton;
	}

	private void fillWithTextArea(Stage primaryStage) {
		TextArea textArea = new TextArea(getApplicationParameters());
		Group root = new Group(textArea);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Application parameters");
	}

	private void fillWithEJBResponse(Stage primaryStage) {
		String serverMessage;
		try {
			serverMessage = getEventMessage();
		} catch (NamingException e) {
			serverMessage = e.getMessage();
		}
		TextArea textArea = new TextArea(serverMessage);
		Group root = new Group(textArea);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Application parameters");
	}

	private String getApplicationParameters() {
		Parameters parameters = this.getParameters();
		Map<String, String> namedParametersMap = parameters.getNamed();
		List<String> unnamedParametersList = parameters.getUnnamed();
		List<String> rawParametersList = parameters.getRaw();

		return String.format("Parameters:\n\tnamed: %1$s\n\tunnamed: %2$s\n\traw: %3$s", namedParametersMap, unnamedParametersList, rawParametersList);
	}

	private void printMethodThread(String methodName) {
		System.out.format("%1$s() method: %2$s%n", methodName, Thread.currentThread().getName());
	}

	private String getEventMessage() throws NamingException {
		return EventBeanClient.invokeEventHelloMessage();
	}
}
