package robotx.boat;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import robotx.sensors.gps.*;
import robotx.sensors.compass.*;

/**
* Sends NNMEA messaged to Judge
*/
public class BoatClient {
  PrintWriter outputStream;
  GpsClient gps;
  CompassClient compass;
  ArduinoLink link;
  HeartBeat heartbeat;

  public void initializeCompass() {
    compass = new CompassClient();
    try {
      compass.open();
      compass.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
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

  public void initializeSerial() {
    link = new ArduinoLink(heartbeat);
    try {
      link.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  public void initializeController() {
    Controller control = new Controller(gps,compass,link);
    long time = 1000; //ms

    // TODO: Change this guy!
    //control.addWaypoint(1.288458, 103.860045);
    //control.addWaypoint(1.288135, 103.8599583);
    control.addWaypoint(1.28779, 103.85775);
    control.setDestinationFromWaypoints();

    control.setDt(time/1000.0);

    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleWithFixedDelay(control, time, time, TimeUnit.MILLISECONDS);

  }
  public void initializeHeart() {
    System.out.println("Starting Heart...");
    heartbeat = new HeartBeat(gps, outputStream);
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleWithFixedDelay(heartbeat, 1, 1, TimeUnit.SECONDS);
  }

  public void initialize() throws Exception {
    String server= "10.0.2.1";
    //String server= "localhost";
    int port = 12345;
    System.out.println("Opening Socket at server:"+server);
    Socket judgeSocket= new Socket(server, port);
    System.out.println("Socket Opened ");

    outputStream = new PrintWriter(judgeSocket.getOutputStream(), true);
    initializeGps();
    initializeCompass();
    initializeSerial();
    initializeController();
    initializeHeart();
  }

  public static void main(String args[])throws Exception {
    BoatClient client = new BoatClient();
    System.out.println("Starting Client");
    client.initialize();
  }
}
