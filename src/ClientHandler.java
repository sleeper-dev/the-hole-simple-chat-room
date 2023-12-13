import java.util.Scanner;

class ClientHandler implements Runnable {

    private Server server;
    private User user;

    public ClientHandler(Server server, User user) {
        this.server = server;
        this.user = user;
        this.server.broadcastAllUsers();
        this.server.broadcastMessage(this.user.toString() + " <span style='color:#53b548;'>has joined the chat!</span>");
    }

    public void run() {
        String message;


        Scanner sc = new Scanner(this.user.getInputStream());
        while (sc.hasNextLine()) {
            message = sc.nextLine();


            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private msg : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendMessageToUser(
                            message.substring(
                                    firstSpace+1, message.length()
                            ), user, userPrivate
                    );
                }


            } else if (message.charAt(0) == '#'){
                user.changeColor(message);

                this.server.broadcastAllUsers();
            }else{

                server.sendMessage(message, user);
            }
        }

        server.removeUser(user);
        this.server.broadcastAllUsers();
        this.server.broadcastMessage(user.toString() + " <span style='color:#b5903f;'>has left the chat!</span>");
        sc.close();
    }
}
