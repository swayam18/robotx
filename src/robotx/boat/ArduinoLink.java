package robotx.boat;
import java.net.Socket;
import java.io.PrintWriter;

// Connects to the python proxy
public class ArduinoLink {
  private PrintWriter out;

  public ArduinoLink() {
    String server = "localhost";
    int port = 6666;

    try {
      Socket arduinoSocket = new Socket(server, port);
      out = new PrintWriter(arduinoSocket.getOutputStream(), true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void sendData(String data) {
    System.out.println("sending:"+data);
    out.println(data); out.flush();
    System.out.println("sent!"+data);
  }
}
