import java.io.*;
import java.net.*;

class GpsClient {
  private BufferedReader in;
  private PrintWriter out;

  public void open() throws Exception {
    String server = "localhost";
    int port = 2947;
    System.out.println("Opening Connection to GPSD Server");
    Socket gpsd = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(gpsd.getInputStream()));
    out = new PrintWriter(gpsd.getOutputStream(), true);
    System.out.println(in.readLine());
  }

  private void stream() {
    out.println("?WATCH={\"enable\":true,\"json\":true}");
    System.out.println("Watch Request Sent");
    try {
      String response;
      while ((response = in.readLine()) != null) {
        System.out.println(response);
      }
    }
    catch (IOException e) {
    }
  }

  public static void main(String args[])throws Exception {
    GpsClient gps = new GpsClient();
    gps.open();
    gps.stream();
  }
}

    //File gps = new File("/dev/ttyACM0");
    //BufferedReader reader = new BufferedReader( new FileReader(gps));
    //String text = "";
    //while ((text = reader.readLine()) != null) {
      //System.out.println(text);
    //}
