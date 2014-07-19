package robotx.boat;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import robotx.sensors.gps.*;

/**
* Sends NNMEA messaged to Judge
*/
public class BoatClient {
  PrintWriter outputStream;
  GpsClient gps;

  public void initializeGps() {
    gps = new GpsClient();
    try {
      gps.open();
      gps.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void initializeHeart() {
    System.out.println("Starting Heart...");
    HeartBeat heartbeat = new HeartBeat(gps, outputStream);
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleWithFixedDelay(heartbeat, 1, 1, TimeUnit.SECONDS);
  }

  public void initialize() throws Exception {
    String server= "localhost";
    int port = 12345;
    System.out.println("Opening Socket");
    Socket judgeSocket= new Socket(server, port);
    System.out.println("Socket Opened ");

    outputStream = new PrintWriter(judgeSocket.getOutputStream(), true);
    initializeGps();
    initializeHeart();
  }

  public static void main(String args[])throws Exception {
    BoatClient client = new BoatClient();
    System.out.println("Starting Client");
    client.initialize();
  }
}
