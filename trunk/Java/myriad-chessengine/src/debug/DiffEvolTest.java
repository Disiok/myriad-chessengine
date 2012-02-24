package debug;
import java.util.*;

public class DiffEvolTest extends Random{
	public static long[] population;
	public static double[] result;
	public static int pop = 200;

	public static void init(){
		population = new long[pop];
		result = new double[pop];
		DiffEvolTest num = new DiffEvolTest();

		for(int i = 0; i < pop/2; i++){
			population[i] = num.nextVal(30);
		}
	}

	public int nextVal(int bits){
		return next(bits);
	}

	public static void mutate(){
		for(int i = pop/2; i < pop; i++){
			population[i] = change(population[i - (pop/2)]);
		}
	}

	public static long change(long string){
		Random strength = new Random();
		int str = strength.nextInt(30);
		long toReturn = string;

		for(int i = 0; i < str; i++){
			toReturn ^= 1 << str;
		}

		return toReturn;
	}

	public static void eval(){
		double x = 0.0;
		for(int i = 0; i < pop; i++){
			x = population[i]/10000000.0;
			result[i] = Math.pow(5, -x*x/(x+1)); //((2*x)-(x*x*x))*(x*x);//-(x*(x + 1)*((x*x)+(5*x)+18))/24;//-(x + 1)*(x - 8);//(-population[i]*(population[i] + 1));
		}
	}

	public static void elim(){
		for(int i = 0; i < pop/2; i++){
			int ind = 0;
			double temp = result[0];
			for(int j = 0; j < pop; j++){
				if(result[j] < temp){
					temp = result[j];
					ind = j;
				}
			}
			result[ind] = Integer.MAX_VALUE;
			population[ind] = -1;
		}
		Arrays.sort(population);

		int ind = pop - 1;
		for(int i = 0; i < pop/2; i++){
			population[i] = population[ind];
			ind --;
		}
	}

	public static void output(long[] array){
		for(int i = 0; i < array.length; i++){
			System.out.println(array[i]);
		}
		System.out.println("***********************************************************");
	}

	public static void getResult(){
		for(int i = 0; i < result.length; i++){
			System.out.println(result[i]);
		}
		System.out.println("***********************************************************");
	}

	public static void getFittest(){
		System.out.println(population[0]/10000000.0);
	}

	public static void main(String[] args){
		init();
		for(int i = 0; i < 5000000; i++){
			mutate();
			eval();
			elim();
		}
		getFittest();
		//output(population);
	}
}