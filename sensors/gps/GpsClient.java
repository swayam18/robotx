import java.io.*;

class GpsClient {
  public static void main(String args[]) throws Exception {
    File gps = new File("/dev/ttyACM0");
    BufferedReader reader = new BufferedReader( new FileReader(gps));
    String text = "";
    while ((text = reader.readLine()) != null) {
      System.out.println(text);
    }
  }
}
