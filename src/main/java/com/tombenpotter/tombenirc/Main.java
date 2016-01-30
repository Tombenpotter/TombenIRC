package com.tombenpotter.tombenirc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Main {

    public static class TextDemo extends JPanel implements ActionListener {
        public static JTextField textField;
        public static JTextArea textArea;
        private final static String newline = "\n";

        public TextDemo() {
            super(new GridBagLayout());

            textField = new JTextField(20);
            textField.addActionListener(this);

            textArea = new JTextArea(5, 20);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            //Add Components to this panel.
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;

            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            add(scrollPane, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            add(textField, c);
        }

        public void actionPerformed(ActionEvent evt) {
            String text = textField.getText();
            textArea.append(text + newline);
            textField.selectAll();

            //Make sure the new text is visible, even if there
            //was a selection in the text area.
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new TextDemo());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        createAndShowGUI();
        badNetCode();
    }

    public static void badNetCode() throws Exception {
        String server = "irc.esper.net";
        String nick = "ThisIsGUITest";
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
            TextDemo.textArea.append(line + TextDemo.newline);
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
            TextDemo.textArea.append(line + TextDemo.newline);
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
