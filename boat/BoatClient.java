import java.io.*;
import java.net.*;

/**
* Sends NNMEA messaged to Judge
*/
public class BoatClient {
  PrintWriter outputStream;

  public void sendHeartBeat(HeartBeat message) {
    outputStream.println(message.toNMEA());
    System.out.println(message.toNMEA());
    outputStream.flush();
  }
  public void initialize() throws Exception {
    String server= "localhost";
    int port = 12345;
    System.out.println("Opening Socket");
    Socket judgeSocket= new Socket(server, port);
    System.out.println("Socket Opened ");

    outputStream = new PrintWriter(judgeSocket.getOutputStream(), true);
  }

  public static void main(String args[])throws Exception {
    BoatClient client = new BoatClient();
    System.out.println("Starting CLient");
    client.initialize();
    System.out.println("Sending Message");
    client.sendHeartBeat(new HeartBeat());
  }
}

class HeartBeat {
  public String toNMEA() {
    return "$RXSEA,161229,AUVSI,37.267458,N,12.376548,W,1.3*15";
  }
}   
