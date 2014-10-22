package robotx.boat;

import robotx.sensors.gps.*;
import robotx.sensors.compass.*;
import java.util.LinkedList;
/**
 * Given destination latitude and longitude,
 * This will send control signals to the propellers.
**/

public class Controller implements Runnable {

  GpsClient gps;
  CompassClient compass;
  ArduinoLink link;

  double destination_latitude;
  double destination_longitude;
  private double[] s_errors = new double[100];
  private double[] theta_errors = new double[100];
  private int s_idx;
  private int theta_idx;

  double current_latitude;
  double current_longitude;
  double current_bearing;

  private double previous_s_error;
  private double previous_theta_error;

  private LinkedList<Location> waypoints = new LinkedList<Location>();

  // K values used for the controller
  private double k_theta = 0.004;
  //private double k_theta_d = 0.005;
  private double k_theta_d = 0.0;
  private double k_theta_i = 0.0;
  //private double k_theta_i = 0.0001;

  private double k_s = 0.08;
  //private double k_s_d = 0.005;
  private double k_s_d = 0.0;
  private double k_s_i = 0.01;
  //private double k_s_i = 0.0001;


  private double dt;

  public Controller(GpsClient gps, CompassClient compass, ArduinoLink link) {
    this.gps = gps;
    this.compass = compass;
    this.link = link;
  }

  public void setDt(double dt) {
    this.dt = dt;
  }

  public void addWaypoint(double latitude, double longitude) {
    Location waypoint = new Location(latitude, longitude);
    waypoints.add(waypoint);
  }

  public void setDestinationFromWaypoints() {
    System.out.println("Setting new destination!");
    Location newDestination = waypoints.poll();   
    if (newDestination!= null) {
      setDestination(newDestination.latitude, newDestination.longitude);
      System.out.println("New Destination:" + destination_latitude+","+ destination_longitude);
    }
  }

  public void setDestination(double latitude, double longitude) {
    destination_latitude = latitude;
    destination_longitude = longitude;
  }

  @Override
  public void run(){
    control();
  }

  public double getBearingError() {
    //reference https://gist.github.com/jeromer/200558

    double [] pointA = {0,0};
    double [] pointB = {0,0};
		pointA[0] = Math.toRadians(current_latitude);
		pointA[1] = Math.toRadians(current_longitude);
		pointB[0] = Math.toRadians(destination_latitude);
		pointB[1] = Math.toRadians(destination_longitude);
		double lat1 = pointA[0];
		double lat2 = pointB[0];
		double diffLong = pointB[1]-pointA[1];
	
		double x = Math.sin(diffLong)*Math.cos(lat2);
		double y = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(diffLong));
	
		double initialBearing = Math.toDegrees(Math.atan2(x, y));
		double error = (initialBearing + 360) % 360;
    //System.out.println("normalized bearing:"+ error);
    //System.out.println("current heading"+ current_bearing);

    double actual_error = error - current_bearing;
    if(actual_error > 180) { 
      actual_error = actual_error - 360;
    }

    else if(actual_error < -180) { 
      actual_error = actual_error + 360;
    }

		return actual_error;
  }

  public double getDistanceError() {
		double lat1 = current_latitude;
		double lat2 = destination_latitude;
		double lon1 = current_longitude;
		double lon2 = current_longitude;
		double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
		double L = Math.toRadians(lon2 - lon1);
		double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
		double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
		double cosSqAlpha;
		double sinSigma;
		double cos2SigmaM;
		double cosSigma;
		double sigma;

		double lambda = L, lambdaP, iterLimit = 100;
		do 
		{
			double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt(	(cosU2 * sinLambda)
									* (cosU2 * sinLambda)
									+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
									* (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
								);
			if (sinSigma == 0) 
			{
				return 0;
			}

			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = 	L + (1 - C) * f * sinAlpha	
						* 	(sigma + C * sinSigma	
								* 	(cos2SigmaM + C * cosSigma
										* 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
									)
							);
		
		} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0) 
		{
			return 0;
		}

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384
				* (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = 
					B * sinSigma
						* (cos2SigmaM + B / 4
							* (cosSigma 
								* (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
									* (-3 + 4 * sinSigma * sinSigma)
										* (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		
		double s = b * A * (sigma - deltaSigma);
		
		return s;

  }

  private double getRunningError(double[] errors) {
    double s =0;
    for(int i = 0 ; i < 100; i++) s+= errors[i];
    return s;
  }

  private void addPositionError(double e) {
    s_errors[s_idx] = e;
    s_idx = (s_idx + 1)%100;
  }

  private void addThetaError(double e) {
    theta_errors[theta_idx] = e;
    theta_idx = (theta_idx + 1) % 100;
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

    CompassResponse heading = compass.getLastBearing();
    if(heading == null) { System.out.println("Heading null!");return; } // do nothing...
    this.current_bearing = heading.getHeading();
    System.out.println("Bearing: "+ this.current_bearing);

    GpsResponse location = gps.getLastLocation();
    if(location == null) { System.out.println("location null!"); return; } // do nothing...

    this.current_latitude = location.getLatitude();
    this.current_longitude = location.getLongitude();
    //this.current_longitude = 103.85683;
    //this.current_latitude = 1.28733;

    System.out.println("Location: "+ this.current_longitude +","+ this.current_latitude);
    // get current error
    double current_s_error = getDistanceError();
    double current_theta_error = getBearingError();

    // calculate instantaneous change in error
    double d_s_error =  (current_s_error - previous_s_error)/dt;
    double d_theta_error = (current_theta_error - previous_theta_error)/dt;

    double i_s_error = getRunningError(this.s_errors) + current_s_error;
    double i_theta_error = getRunningError(this.theta_errors) + current_theta_error;

    //System.out.println("Change in error:" + d_theta_error);
    //System.out.println("i_theta_error:" + i_theta_error);

    //

    // calculate first the forward speed.
    u1 = k_s*current_s_error + k_s_d*d_s_error + k_s_i * i_s_error; // maybe divide in dt (which seems to be 0.1)
    // threshold the forward value
    u1 = u1 > 0.3 ? 0.3 : u1;
    u2 = u1; // equivalent.
    System.out.println("Forward Motor:" + u1);

    // now, calculate the speed differential (turning):

    double differential = k_theta*current_theta_error + k_theta_d*d_theta_error + k_theta_i * i_theta_error ;
    // threshold
    differential = differential > 0.3? 0.3 : differential;
    differential = differential < -0.3? -0.3 : differential;
    // TODO: change the 0.5 to u1, maybe?

    u1 = u1 - differential;
    u2 = u2 + differential;

    // send this over serial link

    int _u1 = (int) (normalize(u1));
    int _u2 = (int) (normalize(u2));
    System.out.println("angle error:"+ current_theta_error);
    System.out.println("distance error:"+ current_s_error);

    if(current_s_error < 3.0) {
    	System.out.println("Approaching Destination!");
      _u1 = 90;
      _u2 = 90;
      setDestinationFromWaypoints();
    }
    _u2 = _u2+10;
    link.sendData("$"+_u2+","+_u1);
    //link.sendData("120,120");

    // Finally, set current error as last.
    previous_s_error = current_s_error;
    previous_theta_error = current_theta_error;
    //and add in the errors
    addThetaError(current_theta_error);
    addPositionError(current_s_error);
  }

  public double normalize(double motor) {
    double normal = 0;
    if(motor < 0) {
      normal = motor*80 + 80;
    }
    else if (motor > 0 ) {
      normal = motor*80 + 100;
    }
    else {
      normal = 90;
    }
    System.out.println(motor);
    System.out.println(normal);

    return normal;
  }

  public static void main(String args[]) {
  }
}

class Location {
  double latitude;
  double longitude ;

  Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
