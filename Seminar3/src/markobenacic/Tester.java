package markobenacic;

public class Tester {
	//ZANEMARITI OVU KLASU TU SAM ISPROBAVAO FUNKCIJE
	public static void main(String[] args) {
		
		System.out.println(getAngle(10,50,460,450));
		double broj = Math.random() * (double) -1;
		System.out.println(broj);
	}
	public static float getAngle(int y1, int x1, int y2, int x2) {
	    float angle =  (float)Math.toDegrees(Math.atan2(y1 - y2, x1 - x2));

	    if(Math.abs(angle) > 180) {
	    	angle += 360;
	    }

	    return angle / 180 ;
	}
}
