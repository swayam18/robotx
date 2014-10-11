package robotx.sensors.compass;

public class CompassResponse {
  double heading;
  public CompassResponse(String nmeaResponse) {
    int lastIndex = nmeaResponse.indexOf('P');
    heading = Double.parseDouble(nmeaResponse.substring(2,lastIndex));
  }

  public double getHeading() {
    return heading;
  }

  public static boolean isValid(String nmeaResponse) {
    return nmeaResponse.charAt(0) == '$';
  }
}

