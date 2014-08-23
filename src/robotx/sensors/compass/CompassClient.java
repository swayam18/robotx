package robotx.sensors.compass

public class CompassClient extends Thread {
  private BufferedReader in;
  private PrintWriter out;
  private CompassResponse lastResponse;

  public void open() throws Exception {
    String server = "localhost";
    int port = 2947;
    System.out.println("Opening Connection to GPSD Server for Compass");
    Socket gpsd = new Socket(server, port);
    in = new BufferedReader(new InputStreamReader(gpsd.getInputStream()));
    out = new PrintWriter(gpsd.getOutputStream(), true);
    System.out.println(in.readLine());
  }

  public void run() {
    stream();
  }

  private void stream() {
    Gson gson = new Gson();
    out.println("?WATCH={\"enable\":true,\"nmea\":true, \"device\": \"/dev/ttyUSB0\"}");
    System.out.println("Watch Request for COMPASS Sent");
    try {
      String nmeaResponse;
      while ((nmeaResponse = in.readLine()) != null) {
        System.out.println(nmeaResponse);
      }
    }
    catch (IOException e) {
    }
  }

  public static void main(String args[])throws Exception {
    CompassClient compass = new CompassClient();
    compass.open();
    compass.stream();
  }
}
