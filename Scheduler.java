import java.util.*;
import java.io.*;

public class Scheduler{

	private String fileName;
	private Boolean verbose;
	private ArrayList<Process> processes = new ArrayList<Process>();




	private void readFile(){
		try{
			Scanner fileReader = new Scanner(new File(fileName));
			int numberOfProcesses = fileReader.nextInt();
	    	for(int processNum = 0; processNum < numberOfProcesses; processNum++){
	    		int a = fileReader.nextInt();
	    		int b = fileReader.nextInt();
	    		int c = fileReader.nextInt();
	    		int d = fileReader.nextInt();
	    		Process thisProcess = new Process(processNum,a,b,c,d);
	    		processes.add(thisProcess);
	    		// thisProcess.printProcess();
	    	}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}


	private void run(){
        readFile();
        FCFS fistComeFirstServe = new FCFS(processes,verbose);
        fistComeFirstServe.run();
        RR roundRobin = new RR(2,processes,verbose);
        roundRobin.run();
        Uniprogrammed uniprogrammed = new Uniprogrammed(processes,verbose);
        uniprogrammed.run();
        SRTN srtn = new SRTN(processes,verbose);
        srtn.run();
        System.out.println("---------------");
    }

	public Scheduler(String fileName,Boolean verbose){
        this.fileName = fileName;
        this.verbose = verbose;
        run();
    }

	public static void main(String[] args){
        if(args.length > 2 || args.length < 1)
            throw new IllegalArgumentException("Incorrect number of parameters.");
        Boolean verboseArg = (args.length == 2);
        String fileNameArg = (args.length == 2) ? args[1] : args[0];
        Scheduler scheduler = new Scheduler(fileNameArg,verboseArg);
    }

}