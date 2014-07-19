package robotx.boat;

import java.io.*;
import robotx.sensors.gps.*;

/**
 * Sends out a HeartBeat to the Judge
 * This happens every second.
**/
public class HeartBeat implements Runnable {
  private GpsClient gps;
  private PrintWriter out;

  public HeartBeat(GpsClient gps, PrintWriter out) {
    this.gps = gps;
    this.out = out;
    //TODO: implement a message service with thread safe buffer.
    //TODO: instead of relying directly on the printwriter object.
  }

  @Override
  public void run() {
    GpsResponse location = gps.getLastLocation();
    System.out.println("Beating...");

    out.println(location.toNMEA());
    out.flush();
  }
}
