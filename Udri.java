import java.util.*;
import java.io.*;

public class Udri{

	private static ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
	private static int num = 0;

	public Udri(){
		try{
			String fileName = "random-numbers.txt";
			Scanner fileReader = new Scanner(new File(fileName));
			while(fileReader.hasNextInt()){
			    randomNumbers.add(fileReader.nextInt());
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	public int getRandomNumber(){
		int randomNumber = randomNumbers.get(num++);
		// System.out.println("\nUdri "+num+" "+randomNumber);
		return randomNumber;
	}

	public static void reset(){
		num = 0;
	}

}