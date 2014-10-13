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
  private String teamName="AUVSI";
  private int mode = 2;
  private int task = 3;


  public HeartBeat(GpsClient gps, PrintWriter out) {
    this.gps = gps;
    this.out = out;
    //TODO: implement a message service with thread safe buffer.
    //TODO: instead of relying directly on the printwriter object.
  }

  public String checksum(String s) {
    int sum = 0;
    for(int i = 1; i < s.length(); i++) { sum^= (int) s.charAt(i); }
    return Integer.toHexString(sum);
  }

  @Override
  public void run() {
    GpsResponse location = gps.getLastLocation();
    if(location == null) return;
//    System.out.println("Beating...");
    String NMEA = location.toNMEA() +",";
    NMEA += teamName +",";
    NMEA += mode +",";
    NMEA += task;
    NMEA += "*"+checksum(NMEA);

    out.println(NMEA);
    out.flush();
  }
}
