package markobenacic;

import java.util.Random;

public class Food {
	public int x;
	public int y;
	
	public Food() {
		Random rand = new Random();
		this.x = rand.nextInt(513);
		this.y = rand.nextInt(513);
	}
}
