package com.tombenpotter.tombenirc;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connection {

    protected String serverIP;
    protected int port;

    protected String nickname;
    protected String login;
    protected String username;
    protected String realName;

    protected String serverName;

    protected List<String> channels;

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String currentLine;

    public Connection(String serverIP, int port, String nickname, String login, String username, String realName, String... channels) {
        this.serverIP = serverIP;
        this.port = port;
        this.nickname = nickname;
        this.login = login;
        this.username = username;
        this.realName = realName;
        this.channels = new ArrayList<>();

        this.currentLine = null;

        Collections.addAll(this.channels, channels);

        try {
            socket = new Socket(serverIP, port);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.serverName = readFromBuffer().split("NOTICE")[0];
    }

    public void writeToBuffer(String message) {
        try {
            bufferedWriter.write(message + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromBuffer() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServerName() {
        return serverName;
    }

    public void connectToServer() {
        setNickname(nickname);
        sendUserInfo(username, realName);
        loginChecks();
        joinChannels(channels);
    }

    public void loginChecks() {
        boolean isLoginDone = false;
        while (!isLoginDone) {
            if ((currentLine = readFromBuffer()) != null) {
                if (currentLine.startsWith(getServerName())) {
                    switch (Utils.getNumbersInString(currentLine)) {
                        case "004":
                            isLoginDone = true;
                            break;
                        case "433":
                            return;
                        default:
                            ;
                    }

                    if (currentLine.startsWith("PING ")) {
                        writeToBuffer("PONG " + currentLine.substring(5));
                    }
                }
            }
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        writeToBuffer("NICK :" + nickname);
    }

    public void sendUserInfo(String username, String realName) {
        writeToBuffer("USER " + username + " 0 * :" + realName);
    }

    public void joinChannels(List<String> channels) {
        for (String channel : channels) {
            writeToBuffer("JOIN :" + channel);
            this.channels.add(channel);
        }
    }

    public void partChannels(String reason, List<String> channels) {
        for (String channel : channels) {
            writeToBuffer("PART " + channel + " :" + reason);
            this.channels.remove(channel);
        }
    }

    public void sendPing(List<String> targets) {
        for (String target : targets) {
            writeToBuffer("PING " + target);
        }
    }

    public void sendPong(List<String> targets) {
        for (String target : targets) {
            writeToBuffer("PONG " + target);
        }
    }

    public void sendMessage(String message, List<String> targets) {
        for (String target : targets) {
            writeToBuffer("PRIVMSG" + target + ":" + message);
        }
    }

    public void sendNotice(String message, List<String> targets) {
        for (String target : targets) {
            writeToBuffer("NOTICE" + target + ":" + message);
        }
    }

    public void sendAction(String message, List<String> targets) {
        for (String target : targets) {
            writeToBuffer("ACTION" + target + ":" + message);
        }
    }

    public void sendInvite(String user, List<String> targets) {
        for (String target : targets) {
            writeToBuffer(":" + username + " INVITE " + user + " " + target);
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public List<String> getChannels() {
        return channels;
    }
}
