import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        String server = "irc.esper.net";
        String nick = "Tombenpotter";
        String login = "ThisIsATestForIRC";

        String channel = "#tehnuttest";

        Socket socket = new Socket(server, 6667);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.write("NICK " + nick + "\n");
        writer.write("USER " + login + " Test Test Test\n");
        writer.flush();

        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.contains("004")) {
                //We are logged in
                break;
            } else if (line.contains("433")) {
                System.out.println("Nickname is already in use.");
                return;
            } else if (line.toLowerCase().startsWith("ping ")) {
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.flush();
            }
        }

        writer.write("JOIN " + channel + "\n");
        writer.write("PRIVMSG " + channel + " :You shall not mock meee.\n");
        writer.flush();

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.toLowerCase().startsWith("ping ")) {
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.write("PRIVMSG " + channel + " :I got pinged by the server!\n");
                writer.flush();
            } else if (line.toLowerCase().contains("privmsg " + nick.toLowerCase())) {
                writer.write("PRIVMSG " + channel + " :Who's the stupidass who pinged meh\n");
                writer.flush();
            }
        }
    }
}
