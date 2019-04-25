package markobenacic;


import java.util.ArrayList;
import org.la4j.matrix.*;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.linear.*;
import java.util.Random;

public class Organism {
	
	//neuronska mreza
	public static int inputNodes = 4;
	public static int hiddentNodes = 5;
	public static int outputNodes = 2;
	
	public DenseMatrix wih = DenseMatrix.zero(5, 4);	//weights input -> hidden
	public DenseMatrix who = DenseMatrix.zero(2, 5);	//hidden -> output
	
	
	
	//moguce pozicije
	public static int x_min = 0;
	public static int x_max = 512;
	public static int y_min = 0;
	public static int y_max = 512;
	public static int r_max = 360;
	
	
	public float x; 	// x koordinata
	public float y;	// y koordinata
	
	public float smjer_x;		
	public float smjer_y;
	public int brzina = 1;
	
	public float smjerDoNajblizeHrane;
	public double najblizaHrana_x; //x kordinata normalizirana [0,1];
	public double najblizaHrana_y; //y kordinata normalizirana na [0,1];
	
	
	public int x_food; 	// x od najblize hrane
	public int y_food;		//y od najblize hrane
	public int fitness;  //food count
	
	
	public String name;
	
	
	
	//int nn_dv;	// 1 = accelerate, 0 = deaccelerate
	//int nn_dr;  // left = 1, right = -1
	
	
	public Organism(String name) {
		Random rand = new Random();
		this.x = rand.nextInt(x_max);
		this.y = rand.nextInt(y_max);
		
		this.smjer_x =  rand.nextInt(3) - 1;
		this.smjer_y = rand.nextInt(3) - 1;
		
		//this.brzina = rand.nextInt(4);
		
		this.fitness = 0;
		this.name = name;
		
		//postavi random težine
		for(int i = 0; i < wih.rows(); i++) {
			for(int j = 0; j < wih.columns(); j++) {
				int broj = rand.nextInt(2);
				if(broj == 0) {
					wih.set(i, j, Math.random());
				} else {
					wih.set(i, j, Math.random() * (double) -1);
				}
			}
		}
		for(int i = 0; i < who.rows(); i++) {
			for(int j = 0; j < who.columns(); j++) {
				int broj = rand.nextInt(2);
				if(broj == 0) {
					who.set(i, j, Math.random());
				} else {
					who.set(i, j, Math.random() * (double) -1);
				}
			}
		}
	}
	
	public void think(ArrayList<Food> food) {					
														
		Food najHrana = najblizaHrana(food);
		
		DenseMatrix input = DenseMatrix.zero(4, 1);	// (4,1)
		
		input.set(0, 0, (double) getAngle(najHrana));
		input.set(1, 0, (double) 1);
		input.set(2, 0, (double) 1);
		input.set(3, 0, (double) 1);
		
		
		DenseMatrix hiddenLayer = wih.multiply(input).toDenseMatrix();
		for(int i = 0; i < hiddenLayer.rows(); i++) {
			for(int j = 0; j < hiddenLayer.columns(); j++) {
				hiddenLayer.update(new MatricaFunkcija());
			}
		}
		DenseMatrix output = who.multiply(hiddenLayer).toDenseMatrix();
		for(int i = 0; i < hiddenLayer.rows(); i++) {
			for(int j = 0; j < hiddenLayer.columns(); j++) {
				output.update(new MatricaFunkcija());
			}
		}
		
		smjer_x=(float)output.get(0, 0);
		smjer_y=(float)output.get(1, 0);
		
//		this.najblizaHrana_x = (double)(najHrana.x - 256) / (double)256;
		//	this.najblizaHrana_y = (double)(najHrana.y - 256) / (double)256;
		
//		smjerDoNajblizeHrane = getAngle(najblizaHrana(food)); 
		
//		if(output.get(0, 0) > 0) {
//		this.smjer_x = 1;
//	} else {
//		this.smjer_x = -1;
//	}
//	if(output.get(1, 0) > 0) {
//		this.smjer_y = 1;
//	} else {
//		this.smjer_y = -1;
//	}
//	
		//hidden layer
	/*	DenseMatrix hiddenLayer = wih.multiply(smjerDoNajblizeHrane).toDenseMatrix() ;
		
		//applying activation function on hidden layer
		for(int i = 0; i < hiddenLayer.rows(); i++) {
			for(int j = 0; j < hiddenLayer.columns(); j++) {
				hiddenLayer.update(new MatricaFunkcija());
			}
		}
		
		// calculating output layer
		DenseMatrix output = who.multiply(hiddenLayer).toDenseMatrix();
		for(int i = 0; i < hiddenLayer.rows(); i++) {
			for(int j = 0; j < hiddenLayer.columns(); j++) {
				output.update(new MatricaFunkcija());
			}
		}
		if(output.get(0, 0) > 0) {
			this.smjer_x = 1;
		} else {
			this.smjer_x = -1;
		}
		if(output.get(1, 0) > 0) {
			this.smjer_y = 1;
		} else {
			this.smjer_y = -1;
		}
		*/
		
		
	//	Random rand = new Random();
	//	this.smjer_x =  rand.nextInt(3) - 1;
	//	this.smjer_y = rand.nextInt(3) - 1;
	}
	public void move() {
		this.x = (this.x + (smjer_x*brzina)) % 512 ;
		if (x < 0) x = 512;
		this.y = (this.y + (smjer_y*brzina)) % 512 ;
		if(y < 0) y = 512;
	}
	public int staoNaHranu(ArrayList<Food> hrana) {
		
		//vraca redni broj u listi s hranom, ako nema hrane vraca -1
		
		int i = 0;
		for(Food h : hrana) {
			if((Math.abs(h.x - this.x) < 3) && (Math.abs(h.y - this.y) < 3)) {	
				return i;
			}
			i++;
		}
		
	/*	for(Food h : hrana) {
			if((this.x == h.x) && (this.y == h.y)) {	
				return i;
			}
			i++;
		} */
		
		return -1;
	}
	public Food najblizaHrana(ArrayList<Food> hrana){
		Food najbliza = hrana.get(0);
		double najmanjaUdaljenost = Math.sqrt(Math.pow(this.x - najbliza.x, 2) + Math.pow(this.y - najbliza.y, 2));
		for(int i = 1; i < hrana.size(); i++) {
			Food trenutna = hrana.get(i);
			double trenutnaUdaljenost = Math.sqrt(Math.pow(this.x - trenutna.x, 2) + Math.pow(this.y - trenutna.y, 2));
			if(trenutnaUdaljenost < najmanjaUdaljenost) {
				najbliza = trenutna;
				najmanjaUdaljenost = trenutnaUdaljenost;
			}
		}
		return najbliza;
	}
	public float getAngle(Food hrana) {
	    float angle =  (float)Math.toDegrees(Math.atan2(hrana.y - y, hrana.x - x));

	    if(Math.abs(angle) > 180) {
	    	angle += 360;
	    }

	    return angle / 180 ;
	}
	public double getUdaljenost(Food food) {
		return Math.sqrt(Math.pow(this.x - food.x, 2) + Math.pow(this.y - food.y, 2) - 256) / 256;
	}
}
