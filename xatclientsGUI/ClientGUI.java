package xatclientsGUI;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.TitledBorder;

public class ClientGUI {
    private JFrame frame;
    private JTextPane messagePane;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private MySocket socket;
    private String nick;
    private Map<String, Color> userColors;

    public ClientGUI(String host, int port, String nick) throws IOException {
        this.nick = nick;
        this.socket = new MySocket(host, port);
        this.userColors = new HashMap<>();
        createUI();
        new Thread(new Visualitzar()).start();
        socket.println(nick);
    }

    private void createUI() {
        frame = new JFrame(nick);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setParagraphAttributes(SimpleAttributeSet.EMPTY, true);

        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("Entrada"));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Missatges"));
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(messagePane), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        panel.add(new JScrollPane(userList), BorderLayout.EAST);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (message != null && !message.isEmpty()) {
            socket.println(message);
            inputField.setText("");
        }
    }

    private void actualitzarUsuaris(String[] users) {
        userListModel.clear();
        userColors.clear();
        for (String user : users) {
            String[] userColor = user.split(":");
            userListModel.addElement(userColor[0]);
            userColors.put(userColor[0], Color.decode(userColor[1]));
        }
    }

    private void colorMissatge(String user, String message) {
        Color color = userColors.getOrDefault(user, Color.BLACK);
        appendToPane(messagePane, user + ": ", color);
        appendToPane(messagePane, message + "\n", Color.BLACK);
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyledDocument doc = tp.getStyledDocument();

        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, c);

        try {
            doc.insertString(doc.getLength(), msg, keyWord);
            doc.setParagraphAttributes(doc.getLength(), 1, keyWord, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private class Visualitzar implements Runnable {
        public void run() {
            String message;
            while ((message = socket.readLine()) != null) {
                if (message.startsWith("NICKLIST:")) {
                    String[] users = message.substring(9).split(",");
                    actualitzarUsuaris(users);
                } else {
                    int colonIndex = message.indexOf(':');
                    if (colonIndex > 0) {
                        String user = message.substring(0, colonIndex);
                        String userMessage = message.substring(colonIndex + 2); // Skip ": "
                        colorMissatge(user, userMessage);
                    } else {
                        appendToPane(messagePane, message + "\n", Color.BLACK);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ClientGUI <host> <port> <nick>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String nick = args[2];

        try {
            ClientGUI client = new ClientGUI(host, port, nick);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
