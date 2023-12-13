import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class User {
    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String nickname;
    private Socket client;
    private String color;


    public User(Socket client, String name) throws IOException {
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.nickname = name;
        this.userId = nbUser;
        this.color = RandomColor.getColor(this.userId);
        nbUser += 1;
    }

    public void changeColor(String hexColor){

        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
        Matcher m = colorPattern.matcher(hexColor);
        if (m.matches()){
            Color c = Color.decode(hexColor);

            double luma = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
            if (luma > 160) {
                this.getOutStream().println("<b>Color Too Bright</b>");
                return;
            }
            this.color = hexColor;
            this.getOutStream().println("<b>Color changed successfully</b> " + this.toString());
            return;
        }
        this.getOutStream().println("<b>Failed to change color</b>");
    }


    public PrintStream getOutStream(){
        return this.streamOut;
    }

    public InputStream getInputStream(){
        return this.streamIn;
    }

    public String getNickname(){
        return this.nickname;
    }


    public String toString(){

        return "<u><span style='color:" + this.color
                + "'>" + this.getNickname() + "</span></u>";

    }
}
