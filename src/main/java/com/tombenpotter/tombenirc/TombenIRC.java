package com.tombenpotter.tombenirc;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TombenIRC extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("TombenIRC");

        TextArea textArea = new TextArea("Test\n");
        textArea.setEditable(false);
        TextArea textArea1 = new TextArea("Test n2");
        TextField textField = new TextField();

        textArea1.setVisible(false);
        textArea1.setManaged(false);

        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.appendText(textField.getText() + "\n");
                textField.clear();

                textArea.setVisible(!textArea.isVisible());
                textArea.setManaged(!textArea.isManaged());
                textArea1.setVisible(!textArea.isVisible());
                textArea1.setManaged(!textArea.isManaged());
            }
        });

        textArea.setWrapText(true);

        StackPane stackPane = new StackPane(textArea, textArea1);
        stackPane.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane(stackPane);
        borderPane.setBottom(textField);

        Rectangle rectangle = new Rectangle(100, 100, Color.BLUE);
        rectangle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rectangle.setFill(Color.RED);
            }
        });
        rectangle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rectangle.setFill(Color.BLUE);
            }
        });

        borderPane.setLeft(rectangle);

        Button button = new Button("Test");
        button.setTextFill(Color.DARKRED);
        borderPane.setRight(button);


        primaryStage.setScene(new Scene(borderPane, 500, 500));
        primaryStage.show();
    }
}
