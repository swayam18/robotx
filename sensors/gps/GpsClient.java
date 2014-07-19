package robotx.sensors.gps;

import java.io.*;
import java.net.*;
import com.google.gson.*;

public class GpsClient extends Thread {
  private BufferedReader in;
  private PrintWriter out;
  private GpsResponse lastResponse;

  public void open() throws Exception {
    String server = "localhost";
    int port = 2947;
    System.out.println("Opening Connection to GPSD Server");
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
    out.println("?WATCH={\"enable\":true,\"json\":true}");
    System.out.println("Watch Request Sent");
    try {
      String jsonResponse;
      while ((jsonResponse = in.readLine()) != null) {
        GpsResponse response = gson.fromJson(jsonResponse, GpsResponse.class);
        if(response.isTPV()) {
          System.out.println(response.TPVString());
          lastResponse = response;
        }
      }
    }
    catch (IOException e) {
    }
  }

  public GpsResponse getLastLocation() {
    return lastResponse;
  }

  public static void main(String args[])throws Exception {
    GpsClient gps = new GpsClient();
    gps.open();
    gps.stream();
  }
}
