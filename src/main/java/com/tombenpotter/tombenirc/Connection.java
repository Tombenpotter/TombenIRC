package com.tombenpotter.tombenirc;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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

    public Connection(String serverIP, int port, String nickname, String login, String username, String realName, String... channelsToJoin) {
        System.out.println("Establishing connection...");

        this.serverIP = serverIP;
        this.port = port;
        this.nickname = nickname;
        this.login = login;
        this.username = username;
        this.realName = realName;
        this.channels = new ArrayList<>();

        for (String channel : channelsToJoin) {
            this.channels.add(channel);
        }

        try {
            socket = new Socket(serverIP, port);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connection established.");
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


    public void setNickname(String nickname) {
        this.nickname = nickname;
        writeToBuffer("NICK :" + nickname);
    }

    public void sendUserInfo(String username, String realName) {
        writeToBuffer("USER " + username + " 0 * :" + realName);
    }

    public void joinChannels(List<String> channels, boolean addChannelsToList) {
        for (String channel : channels) {
            writeToBuffer("JOIN :" + channel);
            if (addChannelsToList) {
                this.channels.add(channel);
            }
        }
    }

    public void partChannels(String reason, List<String> channels) {
        for (String channel : channels) {
            writeToBuffer("PART" + channel + " :" + reason);
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
            writeToBuffer("PRIVMSG " + target + " :" + message);
        }
    }

    public void sendNotice(String message, List<String> targets) {
        for (String target : targets) {
            writeToBuffer("NOTICE " + target + " :" + message);
        }
    }

    public void sendAction(String message, List<String> targets) {
        for (String target : targets) {
            writeToBuffer("ACTION " + target + " :" + message);
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

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
