import java.io.*;
import java.net.*;
import com.google.gson.*;

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
    Gson gson = new Gson();
    out.println("?WATCH={\"enable\":true,\"json\":true}");
    System.out.println("Watch Request Sent");
    try {
      String jsonResponse;
      while ((jsonResponse = in.readLine()) != null) {
        GpsResponse response = gson.fromJson(jsonResponse, GpsResponse.class);
        System.out.println(response.TPVString());
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
