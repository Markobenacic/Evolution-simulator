package markobenacic;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

import org.la4j.matrix.DenseMatrix;

public class Simulation {
	
	//namještanje parametara (0 je najbrže, ali pregori CPU)
	public static int BRZINA_SIMULACIJE = 2;
	
	public static int BROJ_ORG = 15;
	public static int BROJ_ELIT = 3;
	public static int BROJ_HRANE = 30;
	public static int OTKUCAJI_HRANA_SPAWN = 100;
	public static int NOVA_GENERACIJA_SVAKIH = 1500;
	
	public static double mutation_rate = 0.1;
	
	public static ArrayList<Organism> organizmi = new ArrayList<Organism>();
	public static ArrayList<Food> hrana = new ArrayList<Food>(); 
	
	public static void main(String[] args) {
		StdDraw.setScale(0,512);
		StdDraw.enableDoubleBuffering();
		 StdDraw.setPenRadius(0.03);
         StdDraw.setPenColor(StdDraw.BLUE);
         
         long brojacVremena = 0;
         long generacija = 0;
         int najboljiFitness = 0;
		
         //stvori organizme
		
		for(int i = 0; i < BROJ_ORG; i++) {
			organizmi.add(new Organism ("Organism " + i));
		}
		int brojOrganizama = organizmi.size();
		
		//stvori hranu
		
		for(int i = 0; i < BROJ_HRANE; i++) {
			hrana.add(new Food());
		}
		
		
		
		Font font = new Font("Arial", Font.BOLD, 20);
		while(true) {
			StdDraw.clear();
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setFont(font);
			StdDraw.text((double)65, (double)490, "generacija" + Long.toString(generacija));
			StdDraw.text((double) 81, (double) 470, "bestFitness : " + najboljiFitness);
			
			//crtaj organizme ==>> nacrtaj najbolji crveno
			StdDraw.setPenRadius(0.03);
	        StdDraw.setPenColor(StdDraw.RED);
	        StdDraw.point(organizmi.get(0).x, organizmi.get(0).y);
			
			StdDraw.setPenRadius(0.03);
	        StdDraw.setPenColor(StdDraw.BLUE);
			for(int j = 1; j < organizmi.size(); j++) {
				StdDraw.point(organizmi.get(j).x, organizmi.get(j).y);
			}
			
			//mouse listener za dodavanje hrane
			
			if(StdDraw.isMousePressed()) {
				Food foodArtificial = new Food();
				foodArtificial.x = (int) StdDraw.mouseX();
				foodArtificial.y = (int) StdDraw.mouseY();
				hrana.add(foodArtificial);
			}
			
			//crtaj hranu
			StdDraw.setPenRadius(0.04);
	        StdDraw.setPenColor(StdDraw.GREEN);
			for(int j = 0; j < hrana.size(); j++) {
				StdDraw.point(hrana.get(j).x, hrana.get(j).y);
			}
			
			// nacrtaj scenu, pauziraj x milisekundi
			StdDraw.show();
			StdDraw.pause(BRZINA_SIMULACIJE);
			
			//kretanje
			for(int k = 0; k < brojOrganizama; k++) {
				organizmi.get(k).move();
				int stao = organizmi.get(k).staoNaHranu(hrana);
				if(stao != -1) {
					organizmi.get(k).fitness++;
					hrana.remove(stao);
				}
			}
			
			//razmisljanje
			for(int k = 0; k < brojOrganizama; k++) {
				organizmi.get(k).think(hrana);
			}
			//spawn hrane
			if(brojacVremena % OTKUCAJI_HRANA_SPAWN == 0) {
				hrana.add(new Food());
			}
			
			brojacVremena++;
			System.out.println(brojacVremena);
			
			
			
			
			
			//evolucija i selekcija
			if(brojacVremena % NOVA_GENERACIJA_SVAKIH == 0) {
				generacija++;
				
				//elitism
				ArrayList<Organism> topGeneracija = new ArrayList<Organism>();
				for(int i = 0; i < BROJ_ELIT; i++) {
					Organism najbolji = organizmi.get(0);
					int topFitness = 0;
					int k = 0;
					for(int j = 0; j < organizmi.size(); j++) {
						if(organizmi.get(j).fitness > topFitness) {
							najbolji = organizmi.get(j);
							topFitness = najbolji.fitness;
							k = j;
						}
					}
					najbolji.x = (float) (Math.random() * 512);		// -->  restartiram poziciju najboljih
					najbolji.y = (float) (Math.random() * 512);
					topGeneracija.add(najbolji);
					organizmi.remove(k);
				}
				//za ispisati najbolji fitness u generaciji
				najboljiFitness = topGeneracija.get(0).fitness;
				//resetiraj fitness na nulu
				for(Organism o4 : topGeneracija) {
					o4.fitness = 0;
				}
				
				
				//stvori (ukupno - elitnih) novi organizama s crossover i mutacijom
				ArrayList<Organism> noviOrganizmi = new ArrayList<Organism>();
				Random rand1 = new Random();
				
				for(int i = 0; i < BROJ_ORG - BROJ_ELIT; i++) {
					Organism noviOrganizam = new Organism("organizam " + i+3);
					
					//crossover
					int p1 = rand1.nextInt(BROJ_ELIT);
					int p2 = rand1.nextInt(BROJ_ELIT);
					if(p1==p2) p2 = (p1 + 1) % BROJ_ELIT;
					
					Organism parent1 = topGeneracija.get(p1);
					Organism parent2 = topGeneracija.get(p2);
					
					double crossover_weight = Math.random();	
					DenseMatrix wih_new = parent1.wih.multiply(crossover_weight)
												 .add(parent2.wih.multiply(1 - crossover_weight)).toDenseMatrix();
					DenseMatrix who_new = parent1.who.multiply(crossover_weight)
													 .add(parent2.who.multiply(1 - crossover_weight)).toDenseMatrix();
					noviOrganizam.wih = wih_new;
					noviOrganizam.who = who_new;
					
					//mutacija
					double mutate = Math.random();
					if(mutate <= mutation_rate) {	//mutation rate
						
						int mat_pick = rand1.nextInt(2);
						
						if(mat_pick == 0) {
							int index_row = rand1.nextInt(5);
							int index_column = rand1.nextInt(4);
							noviOrganizam.wih.set(index_row, index_column, 
									noviOrganizam.wih.get(index_row, index_column) * ((double) 1.1 - ((double) rand1.nextInt(3) / (double)10)));
							if(noviOrganizam.wih.get(index_row, index_column) > 1) 
								noviOrganizam.wih.set(index_row, index_column, 1);
							if(noviOrganizam.wih.get(index_row, index_column) < -1) 
								noviOrganizam.wih.set(index_row, index_column, -1);
						}
						if(mat_pick == 1) {
							int index_row = rand1.nextInt(2);
							int index_column = rand1.nextInt(5);
							noviOrganizam.who.set(index_row, index_column, 
									noviOrganizam.who.get(index_row, index_column) * ((double) 1.1 - ((double) rand1.nextInt(3) / (double)10)));
							if(noviOrganizam.who.get(index_row, index_column) > 1) 
								noviOrganizam.who.set(index_row, index_column, 1);
							if(noviOrganizam.who.get(index_row, index_column) < -1) 
								noviOrganizam.who.set(index_row, index_column, -1);
						}
					}
					noviOrganizmi.add(noviOrganizam);
				}
				//dodaj elite i noveorganizme u trenutne organizme
				organizmi.clear();
				
				for(Organism o : topGeneracija) {
					organizmi.add(o);
				}
				for(Organism o1 : noviOrganizmi) {
					organizmi.add(o1);
				}
				
				
			}
		}
	}

}
