package com.tombenpotter.tombenirc;

import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.HashMap;

public class ChatScene extends Scene {

    public static final String defaultChannel = "Default";
    protected Connection connection;
    protected String currentChannel;

    protected BorderPane mainWindow;
    protected TextField textField;
    protected StackPane channelsPane;
    protected HashMap<String, TextArea> textAreaForChannel;
    protected VBox buttonsVBox;
    protected HashMap<String, Button> buttonForChannel;

    public ChatScene(@NamedArg("root") Parent root, @NamedArg("width") double width, @NamedArg("height") double height, Connection serverConnection) {
        super(root, width, height);
        this.connection = serverConnection;

        this.textField = new TextField();
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connection.sendMessage(textField.getText(), Collections.singletonList(currentChannel));
                textField.clear();
            }
        });

        TextArea defaultArea = new TextArea("Welcome to TombenIRC!\n");
        defaultArea.setWrapText(true);
        defaultArea.setEditable(false);
        this.channelsPane.getChildren().add(defaultArea);
        this.textAreaForChannel.put(defaultChannel, defaultArea);

        this.currentChannel = defaultChannel;

        for (String channel : connection.getChannels()) {
            TextArea textArea = new TextArea();
            textArea.setWrapText(true);
            textArea.setEditable(false);
            textArea.setVisible(false);
            textArea.setManaged(false);
            this.channelsPane.getChildren().add(textArea);
            this.textAreaForChannel.put(channel, textArea);

            Button button = new Button(channel);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentChannel = button.getText();
                }
            });
            this.buttonsVBox.getChildren().add(button);
            this.buttonForChannel.put(channel, button);
        }

        this.mainWindow = new BorderPane(channelsPane);
        this.mainWindow.setBottom(textField);
        this.mainWindow.setLeft(buttonsVBox);
    }
}
