package robotx.boat;

import robotx.sensors.gps.*;
/**
 * Given destination latitude and longitude,
 * This will send control signals to the propellers.
**/

public class Controller {
  double destination_latitude;
  double destination_longitude;

  double current_latitude;
  double current_longitude;

  private double previous_s_error;
  private double previous_theta_error;

  // K values used for the controller
  private double k_theta = 0.1;
  private double k_theta_d = 0.1;
  private double k_s = 0.1;
  private double k_s_d = 0.1;

  public Controller(GpsClient gps, CompassClient compass) {
  }

  public void setDestination(double longitude, double latitude) {
    destination_latitude = latitude;
    destination_longitude = longitude;
  }

  public void start(){
    control();
  }

  public double getBearingError() {
    //reference https://gist.github.com/jeromer/200558

    pointA = [current_latitude, current_longitude];
    pointB = [destination_latitude, destination_longitude];
		pointA[0] = Math.toRadians(pointA[0])
		pointA[1] = Math.toRadians(pointA[1])
		pointB[0] = Math.toRadians(pointB[0])
		pointB[1] = Math.toRadians(pointB[1])
		double lat1 = pointA[0]
		double lat2 = pointB[0]
		double diffLong = pointB[1]-pointA[1]
	
		double x = Math.sin(diffLong)*Math.cos(lat2)
		double y = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(diffLong))
	
		double initialBearing = Math.toDegrees(Math.atan2(x, y))
		double error = (initialBearing + 360) % 360;
		return error - current_bearing;
  }

  public double getDistanceError() {
  }

  public void control() {
    // define u1 as first engine and u2 as second engine
    // this will output a value between -1 and 1 for each engine. 

    // -1 = full reverse
    // 0 = stop
    // 1 = full forward

    double u1 = 0;
    double u2 = 0;

    //first, get current location and bearing

    GpsResponse location = gps.getLastLocation();
    this.current_latitude = location.getLatitude();
    this.current_longitude = location.getLongitude();
    this.current_bearing = compass.getLastBearing().bearing;
    
    // get current error
    double current_s_error = getDistanceError();
    double current_theta_error = getBearingError();

    // calculate instantaneous change in error
    double d_s_error =  current_s_error - previous_s_error;
    double d_theta_error = current_theta_error - previous_theta_error;

    // calculate first the forward speed.
    u1 = k_s*current_s_error + k_s_d*d_s_error;
    // threshold the forward value
    u1 = u1 > 0.5 ? 0.5 : u1;
    u2 = u1; // equivalent.

    // now, calculate the speed differential (turning):

    differential = k_theta*current_theta_error + k_theta_d*d_theta_error;
    // threshold
    differential = differential > 0.5? 0.5 : differential
    differential = differential < -0.5? -0.5 : differential
    // TODO: change the 0.5 to u1, maybe?

    u1 = u1 - differential;
    u2 = u2 + differential;

    // Finally, set current error as last.
    previous_s_error = current_s_error;
    previous_theta_error = current_theta_error;
  }
}
