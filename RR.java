import java.util.*;
import java.io.*;

public class RR{

	private Udri udri = new Udri();
	private ArrayList<Process> unstartedProcesses = new ArrayList<Process>();
	private ArrayList<Process> incomingProcesses = new ArrayList<Process>();
	private Queue<Process> readyQueue = new LinkedList<Process>();
	private ArrayList<Process> blockedProcesses = new ArrayList<Process>();
	private ArrayList<Process> finishedProcesses = new ArrayList<Process>();
	private int numberOfProcesses = 0;
	private Process currProcesses = null;
	private Boolean verbose = false;
	private int totalIOTime = 0;
	private int quantum = 0;


	public RR(int quantum,ArrayList<Process> processes,Boolean verbose){
		this.quantum = quantum;
		numberOfProcesses = processes.size();
		this.verbose = verbose;
		System.out.print("The original input was: "+numberOfProcesses+" ");
		Iterator<Process> itBeforeSorting = processes.iterator();
		while(itBeforeSorting.hasNext()){
			Process temp = itBeforeSorting.next();
			Process tempCopy = new Process(temp);
			unstartedProcesses.add(tempCopy);
			tempCopy.printProcessSimple();
		}
		System.out.println("");
		System.out.print("The (sorted) input is:  "+numberOfProcesses+" ");
		Collections.sort(unstartedProcesses,Process.ArrivalComparator);
		Iterator<Process> itAfterSorting = unstartedProcesses.iterator();
		int processNum = 0;
		while(itAfterSorting.hasNext()){
			Process tempCopy = itAfterSorting.next();
			tempCopy.setProcessID(processNum++);
			tempCopy.printProcessSimple();
		}
		System.out.println("");
	}

	private void fillArrivingProcesses(int time){
		ArrayList<Process> tempProcessesArriving = new ArrayList<Process>();
		while(unstartedProcesses.size() > 0){
			Process tempCopy = unstartedProcesses.remove(0);
			if(tempCopy.getArrivalTime() <= time){
				incomingProcesses.add(tempCopy);
			}
			else{
				tempProcessesArriving.add(tempCopy);
			}
		}
		while(tempProcessesArriving.size() > 0){
			unstartedProcesses.add(tempProcessesArriving.remove(0));
		}
		Collections.sort(incomingProcesses,Process.ArrivalComparator);
		while(incomingProcesses.size() > 0){
			Process temp = incomingProcesses.remove(0);
			temp.setState("ready");
			temp.resetRemainingQuantumTime();
			readyQueue.add(temp);
		}
	}

	private void fillCompletedBlockedProcesses(int time){
		ArrayList<Process> tempProcessesUnblocked = new ArrayList<Process>();
		while(blockedProcesses.size() > 0){
			Process tempCopy = blockedProcesses.remove(0);
			if(tempCopy.getRemainingIOBurstTime() == 0){
				incomingProcesses.add(tempCopy);
			}
			else{
				tempProcessesUnblocked.add(tempCopy);
			}
		}
		while(tempProcessesUnblocked.size() > 0){
			blockedProcesses.add(tempProcessesUnblocked.remove(0));
		}
		Collections.sort(incomingProcesses,Process.ArrivalComparator);
		while(incomingProcesses.size() > 0){
			Process temp = incomingProcesses.remove(0);
			temp.setState("ready");
			temp.resetRemainingQuantumTime();
			readyQueue.add(temp);
		}
	}

	private void fillCompletedBlockedProcessesAndArrivingProcessesandPrempted(int time,Process toPush){
		ArrayList<Process> tempProcessesArriving = new ArrayList<Process>();
		while(unstartedProcesses.size() > 0){
			Process tempCopy = unstartedProcesses.remove(0);
			if(tempCopy.getArrivalTime() <= time){
				incomingProcesses.add(tempCopy);
			}
			else{
				tempProcessesArriving.add(tempCopy);
			}
		}
		while(tempProcessesArriving.size() > 0){
			unstartedProcesses.add(tempProcessesArriving.remove(0));
		}
		ArrayList<Process> tempProcessesUnblocked = new ArrayList<Process>();
		while(blockedProcesses.size() > 0){
			Process tempCopy = blockedProcesses.remove(0);
			if(tempCopy.getRemainingIOBurstTime() == 0){
				incomingProcesses.add(tempCopy);
			}
			else{
				tempProcessesUnblocked.add(tempCopy);
			}
		}
		while(tempProcessesUnblocked.size() > 0){
			blockedProcesses.add(tempProcessesUnblocked.remove(0));
		}
		if(toPush != null){
			incomingProcesses.add(toPush);
		}
		Collections.sort(incomingProcesses,Process.ArrivalComparator);
		while(incomingProcesses.size() > 0){
			Process temp = incomingProcesses.remove(0);
			temp.setState("ready");
			temp.resetRemainingQuantumTime();
			readyQueue.add(temp);
		}
	}

	private void pushToReadyQueue(Process currentProcess){
		currentProcess.setState("ready");
		currentProcess.resetRemainingQuantumTime();
		readyQueue.add(currentProcess);
	}

	private void incrementWaitTimes(){
		Iterator<Process> it = readyQueue.iterator();
		while(it.hasNext()){
			Process temp = it.next();
			temp.incrementWaitTime();
		}
	}

	private void decrementIOTimes(){
		if(blockedProcesses.size() > 0){
			totalIOTime++;
		}
		Iterator<Process> it = blockedProcesses.iterator();
		while(it.hasNext()){
			Process temp = it.next();
			temp.decrementRemainingIOBurstTime();
			temp.incrementIOTime();
		}
	}

	private void printReadyQueue(){
		System.out.printf("\nprintReadyQueue\n");
		Iterator<Process> it = readyQueue.iterator();
		while(it.hasNext()){
			Process temp = it.next();
			System.out.println(temp.getProcessID());
		}
	}

	private void printAllProcessses(int totalTime){
		System.out.println("");
		System.out.println("\nThe scheduling algorithm used was Round Robbin");
		System.out.println("");
		int totalTurnAroundTime = 0;
		int totalWaitingTime = 0;
		int totalCPUTime = 0;

		Iterator<Process> it = finishedProcesses.iterator();
		while(it.hasNext()){
			Process temp = it.next();
			totalTurnAroundTime += temp.getTurnAroundTime();
			totalWaitingTime += temp.getTotalWaitTime();
			totalCPUTime += temp.getCPUTimeNeeded();
			temp.printProcess();
		}

		System.out.println("Summing Data:");
		System.out.println("              Finishing time: "+totalTime);
		System.out.println("              CPU Utilization: "+(double)totalCPUTime/totalTime);
		System.out.println("              I/O Utilization: "+(double)totalIOTime/totalTime);
		System.out.println("              Throughput: "+(double)numberOfProcesses*100/totalTime+" processes per hundred cycles");
		System.out.println("              Average turnaround time: "+(double)totalTurnAroundTime/numberOfProcesses);
		System.out.println("              Average waiting time: "+(double)totalWaitingTime/numberOfProcesses);
	}

	private void printVerbose(Boolean verbose,int time){
		if(verbose){
			String algo = "RR";
			String timeString = Integer.toString(time);
			String tempString = ("     "+timeString+":").substring(timeString.length());
			System.out.print("\nBefore cycle"+tempString);
			for(int processNum = 0; processNum < numberOfProcesses; processNum++){

				for(Process temp : unstartedProcesses){
			        if(temp.getProcessID() == processNum) {
			        	String state = temp.getState();
			        	String tempString1 = ("             "+state).substring(state.length());
			        	String burstTime = Integer.toString(temp.getBurstTime(algo));
			        	String tempString2 = ("   "+burstTime).substring(burstTime.length());
			            System.out.print(tempString1+tempString2);
			            break;
			        }
			    }

			    for(Process temp : readyQueue){
			        if(temp.getProcessID() == processNum) {
			        	String state = temp.getState();
			        	String tempString1 = ("             "+state).substring(state.length());
			        	String burstTime = Integer.toString(temp.getBurstTime(algo));
			        	String tempString2 = ("   "+burstTime).substring(burstTime.length());
			            System.out.print(tempString1+tempString2);
			            break;
			        }
			    }

			    for(Process temp : blockedProcesses){
			        if(temp.getProcessID() == processNum) {
			        	String state = temp.getState();
			        	String tempString1 = ("             "+state).substring(state.length());
			        	String burstTime = Integer.toString(temp.getBurstTime(algo));
			        	String tempString2 = ("   "+burstTime).substring(burstTime.length());
			            System.out.print(tempString1+tempString2);
			            break;
			        }
			    }

			    for(Process temp : finishedProcesses){
			        if(temp.getProcessID() == processNum) {
			        	String state = temp.getState();
			        	String tempString1 = ("             "+state).substring(state.length());
			        	String burstTime = Integer.toString(temp.getBurstTime(algo));
			        	String tempString2 = ("   "+burstTime).substring(burstTime.length());
			            System.out.print(tempString1+tempString2);
			            break;
			        }
			    }
			    if(currProcesses != null){
			    	if(currProcesses.getProcessID() == processNum){
				    	String state = currProcesses.getState();
			        	String tempString1 = ("             "+state).substring(state.length());
			        	String burstTime = Integer.toString(currProcesses.getBurstTime(algo));
			        	String tempString2 = ("   "+burstTime).substring(burstTime.length());
			            System.out.print(tempString1+tempString2);	
				    }
			    }
			}
		}
	}

	private void runRR(Boolean verbose){

		int time = -1;
		currProcesses = null;
		Process toPush = null;
		Boolean currProcessesSetToNullFlag = false;

		while(finishedProcesses.size() < numberOfProcesses){
			time++;
			printVerbose(verbose,time);

			if(currProcesses != null){
				if(currProcesses.getRemainingCPUTimeNeeded() == 0 || currProcesses.getRemainingCPUBurstTime() == 0 || currProcesses.getRemainingQuantumTime() == 0){
					if(currProcesses.getRemainingCPUTimeNeeded() == 0){
						currProcesses.finish(time);
						currProcesses.setState("terminated");
						finishedProcesses.add(currProcesses);
					}
					else if(currProcesses.getRemainingCPUBurstTime() == 0){
						currProcesses.randomIOBurst(udri);
						currProcesses.setState("blocked");
						blockedProcesses.add(currProcesses);
					}
					else{
						toPush = currProcesses;
						currProcessesSetToNullFlag = true;
					}
					currProcesses = null;
				}
			}

			if(currProcesses == null){
				currProcesses = readyQueue.poll();
				if(currProcesses != null){
					if(currProcesses.getRemainingCPUBurstTime() == 0){
						currProcesses.randomCPUBurst(udri);
					}
					currProcesses.setState("running");
					currProcesses.resetRemainingQuantumTime();
				}
				if(currProcesses == null){
					fillCompletedBlockedProcessesAndArrivingProcessesandPrempted(time,toPush);
					toPush = null;
					currProcesses = readyQueue.poll();
					if(currProcesses != null){
						if(currProcesses.getRemainingCPUBurstTime() == 0){
							currProcesses.randomCPUBurst(udri);
						}
						currProcesses.setState("running");
						currProcesses.resetRemainingQuantumTime();
					}
				}
			}
			fillCompletedBlockedProcessesAndArrivingProcessesandPrempted(time,toPush);
			toPush = null;
			if(currProcesses != null){
				currProcesses.run();
			}
			incrementWaitTimes();
			decrementIOTimes();
						
		}

		printAllProcessses(time);

	}

	public void run(){
		try{
			System.out.println("This detailed printout gives the state and remaining burst for each process");
			runRR(verbose);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


}