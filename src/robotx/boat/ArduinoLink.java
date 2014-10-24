package robotx.boat;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Connects to the python proxy
public class ArduinoLink extends Thread{
  private PrintWriter out;
  private BufferedReader in;
  private HeartBeat heart;

  public ArduinoLink(HeartBeat heart) {
    this.heart = heart;
    String server = "localhost";
    int port = 6666;

    try {
      Socket arduinoSocket = new Socket(server, port);
      out = new PrintWriter(arduinoSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader (arduinoSocket.getInputStream()));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    String data = "";
    try {
    while( (data= in.readLine()) != null) {
      if (heart == null) { continue; }
      if (data.equals("AUTO")) { heart.setMode(2); }
      else if (data.equals("RC")) { heart.setMode(1); }
    }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  public synchronized void sendData(String data) {
    System.out.println("sending:"+data);
    out.println(data); out.flush();
    System.out.println("sent!"+data);
  }

  public String getMode(){
		int heartMode = heart.getMode();
		if (heartMode==1)
			return "------------RC MODE--------------";
		else
			return "------------AUTO MODE------------";
  }
}
