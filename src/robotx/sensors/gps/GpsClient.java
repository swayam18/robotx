package robotx.sensors.gps;

import java.io.*;
import java.net.*;
import com.google.gson.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsClient extends Thread {
  private BufferedReader in;
  private PrintWriter out;
  private GpsResponse lastResponse;

  public void open() throws Exception {
    String server = "localhost";
    int port = 3333;
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
    out.println("?WATCH={\"enable\":true,\"json\":true, \"device\": \"/dev/ttyACM0\"}");
    System.out.println("Watch Request for GPS Sent");

    // initialize File Object for logging
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
    Date date = new Date();

    String logFileName = "log/" + dateFormat.format(date) + ".gpx";

    try {
      FileWriter log = new FileWriter(logFileName);
      String jsonResponse;
      while ((jsonResponse = in.readLine()) != null) {
        GpsResponse response = gson.fromJson(jsonResponse, GpsResponse.class);
        if(response.isTPV()) {

          //System.out.println(response.TPVString());
          lastResponse = response;

          //write to logger here
          String logStatement = lastResponse.utcTime() + ": " + ",," + lastResponse.latlong() +",";
          log.write(logStatement);
        }
      log.close();
      }
    }
    catch (IOException e) {
      System.out.println("IOEXCEPTION!");
      e.printStackTrace();
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
