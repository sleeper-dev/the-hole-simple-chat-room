import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private int port;
    private List<User> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(1234).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void run() throws IOException {
        server = new ServerSocket(port) {
            @Override
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("Port " + this.port + " is now open.");

        while (true) {

            Socket client = server.accept();


            String nickname = (new Scanner( client.getInputStream() )).nextLine();
            nickname = nickname.replace(",", "");
            nickname = nickname.replace(" ", "_");
            System.out.println("New Client: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());


            User newUser = new User(client, nickname);


            this.clients.add(newUser);


            newUser.getOutStream().println("<b>Welcome</b> " + newUser.toString());


            new Thread(new ClientHandler(this, newUser)).start();
        }
    }


    public void removeUser(User user){
        this.clients.remove(user);
    }


    public void sendMessage(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(
                    userSender.toString() + "<span>: " + msg + "</span>");
        }
    }

    public void broadcastMessage(String msg) {
        for (User client : this.clients) {
            client.getOutStream().println(msg);
        }
    }


    public void broadcastAllUsers(){
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }


    public void sendMessageToUser(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println(
                        userSender.toString() + " <span style='color:#0c8ec2;'>whispers: " + msg + "</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println("<span style='color:red;'>User doesn't exist!</span>");
        }
    }
}
