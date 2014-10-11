package robotx.sensors.gps;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

public class GpsResponse {
  int mode;
  String time;
  String lat;
  String lon;
  double alt;
  double speed;

  double epx;
  double epy;
  double epv;
  double climb;
  double track; //course over ground, degrees from true north

  private DateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
  private DateFormat UTCFormat = new SimpleDateFormat("HHmmss");

  public boolean isTPV() {
    return time != null && lat!= null && lon != null;
  }
  public String TPVString() {
    return "Time:"+time+" Latitude: " + lat + "Longitude: "+lon;
  }

  // Get Time
  public String utcTime() {
    Date date = null;
    try {
      date = ISO8601.parse(time); // Rely on sys time instead of gps time if laggy
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return UTCFormat.format(date);
  }

  public double getLatitude() {
    return Double.parseDouble(lat);
  }

  public double getLongitude() {
    return Double.parseDouble(lon);
  }

  // Format [Lat],[N/S],[LON],[E/W]
  private String latlong() {
    String latlong = "";
    double latitude = Double.parseDouble(lat);
    double longitude = Double.parseDouble(lon);

    latlong += Math.abs(latitude) + ",";
    if(latitude < 0) latlong += "S,";
    else latlong +="N,";
    latlong += Math.abs(longitude) + ",";
    if(longitude < 0) latlong += "W";
    else latlong +="E";
    return latlong;
  }

  public String toNMEA() {
    if (!isTPV()) return null;
    String NMEA = "";
    NMEA+= "$RXHRT,";  //Protocol Header
    NMEA+= utcTime() +",";
    NMEA+= latlong();
    return NMEA;
  }
}
