package com.tombenpotter.tombenirc;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TombenIRC extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TombenIRC");

        Connection connection = new Connection("irc.esper.net", 6667, "TombenIRCTest", "TombenIRCTest", "TombenDaTest", "TombenDaDerp", "#tehnuttest");

        ChatScene chatScene = new ChatScene(new BorderPane(), 500, 500, connection);

        primaryStage.setScene(chatScene);

        primaryStage.show();

        chatScene.connectToServer();
    }
}
