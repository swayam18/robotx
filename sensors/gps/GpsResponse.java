class GpsResponse {
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

  public String TPVString() {
    return "Time:"+time+" Latitude: " + lat + "Longitude: "+lon;
  }
}
