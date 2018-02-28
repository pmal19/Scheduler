import java.util.*;
import java.io.*;

public class Process{

    private int processID;
	private int arrivalTime;
    private int cpuBurstLimit;
    private int cpuTimeNeeded;
    private int remainingCPUTimeNeeded;
    private int ioBurstLimit;
    private String state = "unstarted";
    private int remainingIOBurstTime = 0;
    private int remainingCPUBurstTime = 0;
    private int quantum = 2;
    private int remainingQuantumTime = 2;
    private int totalIOTime = 0;
    private int totalWaitTime = 0;
    private int finishTime = -1;
    private int turnAroundTime = -1;

    public Process(int processID, int arrivalTime, int cpuBurstLimit, int cpuTimeNeeded, int ioBurstLimit){
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.cpuBurstLimit = cpuBurstLimit;
        this.cpuTimeNeeded = cpuTimeNeeded;
        this.ioBurstLimit = ioBurstLimit;
        this.remainingCPUTimeNeeded = cpuTimeNeeded;
    }

    public Process(Process ref){
        this.processID = ref.processID;
        this.arrivalTime = ref.arrivalTime;
        this.cpuBurstLimit = ref.cpuBurstLimit;
        this.cpuTimeNeeded = ref.cpuTimeNeeded;
        this.ioBurstLimit = ref.ioBurstLimit;
        this.remainingCPUBurstTime = ref.remainingCPUBurstTime;
        this.remainingCPUTimeNeeded = ref.remainingCPUTimeNeeded;
        this.quantum = ref.quantum;
        this.remainingQuantumTime = ref.remainingQuantumTime;
        this.state = ref.state;
    }

    public void printProcessSimple(){
        System.out.print(" "+processID+"("+arrivalTime+" "+cpuBurstLimit+" "+cpuTimeNeeded+" "+ioBurstLimit+")");
    }

    public void decrementRemainingQuantumTime(){
        remainingQuantumTime--;
    }

    public void resetRemainingQuantumTime(){
        remainingQuantumTime = quantum;
    }

    public int getRemainingQuantumTime(){
        return remainingQuantumTime;
    }

    public void incrementWaitTime(){
        totalWaitTime++;
    }

    public void incrementIOTime(){
        totalIOTime++;
    }

    public void decrementRemainingCPUBurstTime(){
        remainingCPUBurstTime--;
    }

    public void decrementRemainingCPUTimeNeeded(){
        remainingCPUTimeNeeded--;
    }

    public void decrementRemainingIOBurstTime(){
        remainingIOBurstTime--;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }

    public int getRemainingIOBurstTime(){
        return remainingIOBurstTime;
    }

    public int getRemainingCPUBurstTime(){
        return remainingCPUBurstTime;
    }

    public int getRemainingCPUTimeNeeded(){
        return remainingCPUTimeNeeded;
    }

    public int getCPUTimeNeeded(){
        return cpuTimeNeeded;
    }

    public int getFinishTime(){
        return finishTime;
    }

    public int getTurnAroundTime(){
        return turnAroundTime;
    }

    public int getTotalWaitTime(){
        return totalWaitTime;
    }

    public int getTotalIOTime(){
        return totalIOTime;
    }

    public int getBurstTime(String algo){
        if(state == "running"){
            if(algo == "FCFS"){
                return remainingCPUBurstTime+1;
            }
            else if(algo == "RR"){
                return Math.min(remainingQuantumTime,remainingCPUBurstTime)+1;
            }
            else if(algo == "SRTN"){
                return remainingCPUBurstTime+1;
            }
            else{
                return -1;
            }
        }
        else if(state == "blocked"){
            return remainingIOBurstTime+1;
        }
        else if(state == "ready"){
            if(algo == "SRTN"){
                return remainingCPUBurstTime;    
            }
            else{
                return 0;
            }
        }
        else{
            return 0;
        }
    }

    public void setProcessID(int processID){
        this.processID = processID;
    }

    public int getProcessID(){
        return processID;
    }

    public void finish(int time){
        finishTime = time;
        turnAroundTime = finishTime-arrivalTime;
    }

    public String getState(){
        return state;
    }

    public void setState(String state){
        this.state = state;
    }

    private int randomOS(Udri udri,int limit){
        int returnRandomOS = (udri.getRandomNumber()%limit)+1;
        // System.out.println(returnRandomOS+" returnRandomOS");
        // System.out.println(" ");
        return returnRandomOS;
    }

    public int randomCPUBurst(Udri udri){
        // System.out.printf("\nrandomCPUBurst "+processID+" "+remainingCPUBurstTime);
        remainingCPUBurstTime = randomOS(udri,cpuBurstLimit);
        return remainingCPUBurstTime;
    }

    public int randomIOBurst(Udri udri){
        // System.out.printf("\nrandomIOBurst "+processID);
        remainingIOBurstTime = randomOS(udri,ioBurstLimit);
        return remainingIOBurstTime;
    }

	public void run(){
        try{
            decrementRemainingCPUTimeNeeded();
            decrementRemainingCPUBurstTime();
            decrementRemainingQuantumTime();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printProcess(){
        System.out.println("ProcessID "+processID+":");
        System.out.print("              (A,B,C,IO) = ");
        
        System.out.print("(");
        System.out.print(arrivalTime + " ");
        System.out.print(cpuBurstLimit + " ");
        System.out.print(cpuTimeNeeded + " ");
        System.out.print(ioBurstLimit + ")");

        System.out.println("");
        System.out.print("              Finishing time: ");
        System.out.println(finishTime);
        System.out.print("              Turnaround time:  ");
        System.out.println(turnAroundTime);
        System.out.print("              I/O time: ");
        System.out.println(totalIOTime);
        System.out.print("              Waiting time: ");
        System.out.println(totalWaitTime);
    }
	

    public static final Comparator<Process> IDComparator = new Comparator<Process>(){
        public int compare(Process p1, Process p2){
            return p1.processID - p2.processID;
        }
    };

    public static final Comparator<Process> ArrivalComparator = new Comparator<Process>(){
        public int compare(Process p1, Process p2){
            int comp = p1.arrivalTime - p2.arrivalTime;
            if(comp == 0){
                return p1.processID - p2.processID;
            }
            else{
                return comp;
            }
        }
    };
  
    public static final Comparator<Process> RemainingCPUTimeNeededComparator = new Comparator<Process>(){
        public int compare(Process p1, Process p2){
            int comp = p1.remainingCPUTimeNeeded - p2.remainingCPUTimeNeeded;
            if(comp == 0){
                return p1.processID - p2.processID;
            }
            else{
                return comp;
            }
        }
    };
    

}