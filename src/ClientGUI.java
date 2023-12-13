import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientGUI extends Thread {
    final JTextPane chatTP = new JTextPane();
    final JTextPane userListTP = new JTextPane();
    final JTextField messageTF = new JTextField();

    private Socket socket;

    private Server server;
    private BufferedReader input;
    private Thread read;
    private PrintWriter output;
    private String name;
    private String serverName;
    private int PORT;
    private String oldMsg = "";

    public ClientGUI() {

        this.serverName = "localhost";
        this.PORT = 1234;

        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        final JFrame jfr = new JFrame("the hole");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatTP.setBounds(25, 25, 490, 320);
        chatTP.setFont(font);
        chatTP.setMargin(new Insets(6, 6, 6, 6));
        chatTP.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(chatTP);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        chatTP.setContentType("text/html");
        chatTP.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);


        userListTP.setBounds(520, 25, 156, 320);
        userListTP.setEditable(true);
        userListTP.setFont(font);
        userListTP.setMargin(new Insets(6, 6, 6, 6));
        userListTP.setEditable(false);
        JScrollPane jsplistuser = new JScrollPane(userListTP);
        jsplistuser.setBounds(520, 25, 156, 320);

        userListTP.setContentType("text/html");
        userListTP.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);


        messageTF.setBounds(0, 350, 400, 50);
        messageTF.setFont(font);
        messageTF.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(messageTF);
        jtextInputChatSP.setBounds(25, 350, 650, 50);


        final JButton sendBTN = new JButton("Send");
        sendBTN.setFont(font);
        sendBTN.setBounds(575, 410, 100, 35);


        final JButton disconnectBTN = new JButton("Disconnect");
        disconnectBTN.setFont(font);
        disconnectBTN.setBounds(25, 410, 130, 35);

        final JTextField usernameTF = new JTextField(this.name);
        final JButton connectBTN = new JButton("Connect");

        messageTF.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }


                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = messageTF.getText().trim();
                    messageTF.setText(oldMsg);
                    oldMsg = currentMessage;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = messageTF.getText().trim();
                    messageTF.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });


        sendBTN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        usernameTF.getDocument().addDocumentListener(new TextListener(usernameTF, connectBTN));

        connectBTN.setFont(font);
        usernameTF.setBounds(315, 380, 200, 35);
        connectBTN.setBounds(520, 380, 130, 35);

        chatTP.setBackground(Color.LIGHT_GRAY);
        userListTP.setBackground(Color.LIGHT_GRAY);

        jfr.add(connectBTN);
        jfr.add(jtextFilDiscuSP);
        jfr.add(jsplistuser);
        jfr.add(usernameTF);

        jfr.setVisible(true);

        appendToPane(chatTP, "<h1>Welcome to <span style='color:red;'>THE HOLE</span>.</h1>");

        connectBTN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = usernameTF.getText();

                    appendToPane(chatTP, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
                    socket = new Socket(serverName, PORT);

                    appendToPane(chatTP, "<span>Connected to " +
                            socket.getRemoteSocketAddress()+"</span>");

                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);


                    output.println(name);


                    read = new Read();
                    read.start();
                    jfr.remove(usernameTF);
                    jfr.remove(connectBTN);
                    jfr.add(sendBTN);
                    jfr.add(jtextInputChatSP);
                    jfr.add(disconnectBTN);
                    jfr.revalidate();
                    jfr.repaint();
                    chatTP.setBackground(Color.WHITE);
                    userListTP.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(chatTP, "<span>Could not connect to Server</span>");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }

        });

        disconnectBTN.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                jfr.add(usernameTF);
                jfr.add(connectBTN);
                jfr.remove(sendBTN);
                jfr.remove(jtextInputChatSP);
                jfr.remove(disconnectBTN);
                jfr.revalidate();
                jfr.repaint();
                read.interrupt();
                userListTP.setText(null);
                chatTP.setBackground(Color.LIGHT_GRAY);
                userListTP.setBackground(Color.LIGHT_GRAY);
                appendToPane(chatTP, "<span>Connection closed.</span>");
                output.close();
            }
        });
    }

    public static void main(String[] args) {
        ClientGUI client = new ClientGUI();
    }

    public void sendMessage() {
        try {
            String message = messageTF.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            output.println(message);
            messageTF.requestFocus();
            messageTF.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", "))
                            );
                            userListTP.setText(null);
                            for (String user : ListUser) {
                                appendToPane(userListTP, user);
                            }
                        }else{
                            appendToPane(chatTP, message);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Failed to parse incoming message");
                }
            }
        }
    }

    private void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
