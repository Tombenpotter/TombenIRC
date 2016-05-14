package com.tombenpotter.tombenirc;

import javafx.beans.NamedArg;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class ChatScene extends Scene {

    public static final String defaultChannel = "Default";
    public static final String newLine = "\n";

    protected CurrentLineReceiver currentLineReceiver;
    protected Connection connection;
    protected String currentChannel;

    protected BorderPane mainWindow;
    protected TextField textField;
    protected StackPane channelsPane;
    protected TreeMap<String, TextArea> textAreaForChannel;
    protected VBox buttonsVBox;
    protected TreeMap<String, Button> buttonForChannel;

    public ChatScene(@NamedArg("root") BorderPane root, @NamedArg("width") double width, @NamedArg("height") double height, Connection serverConnection) {
        super(root, width, height);

        System.out.println("Initializing ChatScene...");

        this.connection = serverConnection;
        this.currentChannel = defaultChannel;

        this.currentLineReceiver = new CurrentLineReceiver();
        this.channelsPane = new StackPane();
        this.buttonsVBox = new VBox();
        this.textAreaForChannel = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.buttonForChannel = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        this.textField = new TextField();
        this.textField.setOnAction(event -> {
            connection.sendMessage(textField.getText(), Collections.singletonList(currentChannel));
            textAreaForChannel.get(currentChannel).appendText(textField.getText());
            textField.clear();
        });

        List<String> channels = new ArrayList<>(connection.getChannels());
        channels.add(0, defaultChannel);
        for (String channel : channels) {
            TextArea textArea = new TextArea();
            textArea.setWrapText(true);
            textArea.setEditable(false);
            textArea.setVisible(false);
            textArea.setManaged(false);
            this.channelsPane.getChildren().add(textArea);
            this.textAreaForChannel.put(channel, textArea);

            Button button = new Button(channel);
            button.setOnAction(event -> {
                switchToChannel(button.getText());
            });
            this.buttonsVBox.getChildren().add(button);
            this.buttonForChannel.put(channel, button);
        }

        this.textAreaForChannel.get(defaultChannel).setVisible(true);
        this.textAreaForChannel.get(defaultChannel).setManaged(true);

        this.mainWindow = root;
        this.mainWindow.setCenter(channelsPane);
        this.mainWindow.setBottom(textField);
        this.mainWindow.setLeft(buttonsVBox);

        this.currentLineReceiver.start();

        System.out.println("ChatScene initialized.");
    }

    public void connectToServer() {
        System.out.println("Initializing IRC Protocol checks...");

        connection.setNickname(connection.getNickname());
        connection.sendUserInfo(connection.getUsername(), connection.getRealName());
        loginChecks();
        connection.joinChannels(connection.getChannels(), false);

        System.out.println("Connection to IRC complete.");
    }

    public void loginChecks() {
        int i = 0;
        boolean isLoginDone = false;
        boolean pongSent = false;

        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!isLoginDone) {
            if (i == 0) {
                connection.setServerName(currentLineReceiver.getCurrentLine().split("NOTICE")[0]);
            }

            if (currentLineReceiver.getCurrentLine().startsWith(connection.getServerName())) {
                switch (Utils.getNumbersInString(currentLineReceiver.getCurrentLine())) {
                    case "004":
                        isLoginDone = true;
                        break;
                    case "433":
                        return;
                    default:
                        ;
                }
            }
            if (!pongSent && currentLineReceiver.getCurrentLine().startsWith("PING ")) {
                connection.writeToBuffer("PONG " + currentLineReceiver.getCurrentLine().substring(5));
                pongSent = true;
            }
            i++;
        }
    }

    public void switchToChannel(String channelString) {
        currentChannel = channelString;
        TextArea channel = textAreaForChannel.get(channelString);
        channel.setVisible(true);
        channel.setManaged(true);

        for (TextArea textArea : textAreaForChannel.values()) {
            if (!textArea.equals(channel)) {
                textArea.setVisible(false);
                textArea.setManaged(false);
            }
        }
    }

    public synchronized void displayMessageOnChannel(String message) {
        //:Tombenpotter!~Tombenpot@candicejoy.com PRIVMSG #TehNutTest :Example

        if (message.contains("PRIVMSG #")) {
            String username = message.substring(1, message.indexOf("!"));
            String channel = message.substring(message.indexOf("#"));
            channel = channel.substring(0, channel.indexOf(":") - 1);
            String realMessage = message.split(":")[2];

            TextArea textArea = textAreaForChannel.get(channel);
            if (textArea != null) {
                textArea.appendText(username + "|" + realMessage + newLine);
            }
        } else {
            TextArea textArea = textAreaForChannel.get(currentChannel);
            textArea.appendText(message);
        }
    }

    public class CurrentLineReceiver implements Runnable {
        private volatile String currentLine;

        protected Thread thread;
        protected String threadName = "LineReceiverThread";

        @Override
        public void run() {
            while ((currentLine = connection.readFromBuffer()) != null) {
                displayMessageOnChannel(getCurrentLine());
            }
        }

        public void start() {
            System.out.println("Starting line-receiver thread.");
            if (thread == null) {
                thread = new Thread(this, threadName);
                thread.setDaemon(true);
                thread.start();
                System.out.println("Line-receiver thread started.");
            }
        }

        public synchronized String getCurrentLine() {
            return currentLine;
        }
    }
}
