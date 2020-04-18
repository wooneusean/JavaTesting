/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javatesting;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author Woon Eusean
 */
class ProcInfo implements Comparable<ProcInfo> {

    String procName;
    Integer burstTime;
    Integer remainingBurstTime;

    public ProcInfo(String n, int bt) {
        procName = n;
        burstTime = bt;
        remainingBurstTime = bt;
    }

    @Override
    public int compareTo(ProcInfo o) {
        return procName.compareTo(o.procName);
    }
}

class ProcRecord {

    ProcInfo Proc;
    Integer arrivalTime;
    Integer exitTime;

    public ProcRecord(ProcInfo p, Integer at, Integer et) {
        Proc = p;
        arrivalTime = at;
        exitTime = et;
    }
}

public class OSAnswerer {

    static boolean isRunning = true;
    static Scanner scn = new Scanner(System.in);

    static void roundRobin() {
        ArrayList<ProcInfo> processes = new ArrayList<ProcInfo>();
        ArrayList<ProcRecord> processRecords = new ArrayList<ProcRecord>();

        System.out.println("Enter the time slice:");
        int timeSlice = Integer.parseInt(scn.nextLine());

        System.out.println("Enter process burst times seperated by commas.");
        String in = scn.nextLine();

        int i = 1;
        for (String s : in.split(",")) {
            processes.add(new ProcInfo("P" + i, Integer.parseInt(s)));
            i++;
        }

        boolean isEnded = false;
        int counter = 0;
        while (isEnded == false) {
            for (ProcInfo p : processes) {
                if (p.remainingBurstTime <= 0) {
                    continue;
                }
                if (p.remainingBurstTime - timeSlice > 0) {
                    int exitTime = counter + timeSlice;
                    processRecords.add(new ProcRecord(p, counter, exitTime));
                    counter += timeSlice;

                    p.remainingBurstTime -= timeSlice;
                } else if (p.remainingBurstTime - timeSlice <= 0) {
                    int exitTime = counter + p.remainingBurstTime;
                    processRecords.add(new ProcRecord(p, counter, exitTime));
                    counter += p.remainingBurstTime;

                    p.remainingBurstTime = 0;
                }
            }

            int totalRemainaingBurstTime = 0;
            for (ProcInfo p : processes) {
                totalRemainaingBurstTime += p.remainingBurstTime;
            }
            if (totalRemainaingBurstTime <= 0) {
                isEnded = true;
            }
        }
        for (ProcRecord processRecord : processRecords) {
            if (processRecord == processRecords.get(processRecords.size() - 1)) {
                System.out.print(String.format("[%d] %s [%d]\n\n", processRecord.arrivalTime, processRecord.Proc.procName, processRecord.exitTime));
            } else {
                System.out.print(String.format("[%d] %s ", processRecord.arrivalTime, processRecord.Proc.procName));
            }
        }

        TreeMap<ProcInfo, Integer> waitingTimes = new TreeMap<>();
        for (ProcInfo p : processes) {
            int currWaitingTime = 0;
            int previousExitTime = 0;
            for (ProcRecord procRec : processRecords) {
                if (procRec.Proc == p) {
                    currWaitingTime += (procRec.arrivalTime - previousExitTime);
                    previousExitTime = procRec.exitTime;
                }
            }
            waitingTimes.put(p, currWaitingTime);
        }

        TreeMap<ProcInfo, Integer> turnAroundTimes = new TreeMap<>();
        float totalWaitingTime = 0;
        for (ProcInfo p : waitingTimes.keySet()) {
            System.out.println(String.format("Tw(%s) = %d", p.procName, waitingTimes.get(p)));
            turnAroundTimes.put(p, p.burstTime + waitingTimes.get(p));
            totalWaitingTime += waitingTimes.get(p);
        }
        System.out.println("Tw(Average) = " + totalWaitingTime / waitingTimes.size() + " ms\n");
        float totalTurnaroundTime = 0;
        for (ProcInfo p : turnAroundTimes.keySet()) {
            int wt = waitingTimes.get(p);
            int bt = p.burstTime;
            System.out.println(String.format("Tt(%s) = (%d+%d) = %d", p.procName, wt, bt, wt + bt));
            totalTurnaroundTime += wt + bt;
        }
        System.out.println("Tt(Average) = " + totalTurnaroundTime / turnAroundTimes.size() + " ms\n");
        
        System.out.println("Press enter to return to menu.");
        scn.nextLine();
    }

    static void FCFS() {
        ArrayList<ProcInfo> processes = new ArrayList<ProcInfo>();
        ArrayList<ProcRecord> processRecords = new ArrayList<ProcRecord>();

        System.out.println("Enter process burst times seperated by commas.");
        String in = scn.nextLine();

        int i = 1;
        for (String s : in.split(",")) {
            processes.add(new ProcInfo("P" + i, Integer.parseInt(s)));
            i++;
        }

        boolean isEnded = false;
        int counter = 0;
        while (isEnded == false) {
            for (ProcInfo p : processes) {
                if (p.remainingBurstTime <= 0) {
                    continue;
                }

                int exitTime = counter + p.remainingBurstTime;
                processRecords.add(new ProcRecord(p, counter, exitTime));
                counter += p.remainingBurstTime;
                p.remainingBurstTime = 0;
            }
            int totalRemainaingBurstTime = 0;
            for (ProcInfo p : processes) {
                totalRemainaingBurstTime += p.remainingBurstTime;
            }
            if (totalRemainaingBurstTime <= 0) {
                isEnded = true;
            }
        }

        TreeMap<ProcInfo, Integer> waitingTimes = new TreeMap<>();
        for (ProcInfo p : processes) {
            int prevExitTime = 0;
            for (ProcRecord procRec : processRecords) {
                if (procRec.Proc == p) {
                    waitingTimes.put(p, procRec.arrivalTime - prevExitTime);
                    prevExitTime = procRec.exitTime;
                    break;
                }
            }
        }

        TreeMap<ProcInfo, Integer> turnAroundTimes = new TreeMap<>();
        float totalWaitingTime = 0;
        for (ProcInfo p : waitingTimes.keySet()) {
            System.out.println(String.format("Tw(%s) = %d", p.procName, waitingTimes.get(p)));
            turnAroundTimes.put(p, p.burstTime + waitingTimes.get(p));
            totalWaitingTime += waitingTimes.get(p);
        }
        System.out.println("Tw(Average) = " + totalWaitingTime / waitingTimes.size() + " ms\n");
        float totalTurnaroundTime = 0;
        for (ProcInfo p : turnAroundTimes.keySet()) {
            int wt = waitingTimes.get(p);
            int bt = p.burstTime;
            System.out.println(String.format("Tt(%s) = (%d+%d) = %d", p.procName, wt, bt, wt + bt));
            totalTurnaroundTime += wt + bt;
        }
        System.out.println("Tt(Average) = " + totalTurnaroundTime / turnAroundTimes.size() + " ms\n");
        
        System.out.println("Press enter to return to menu.");
        scn.nextLine();
    }

    static void FIFO() {
        System.out.println("Please enter the number of page frames");
        int cap = Integer.parseInt(scn.nextLine());
        Queue<String> pageFrames = new ArrayBlockingQueue<String>(cap);

        System.out.println("Please enter reference strings seperated by commas.");
        String in = scn.nextLine();

        String refStrings[] = in.split(",");

        int counter = 0;
        for (String refStr : refStrings) {
            if (pageFrames.size() >= cap) {
                if (pageFrames.contains(refStr)) {
                    continue;
                } else {
                    pageFrames.poll();
                    pageFrames.add(refStr);
                    counter++;
                }
            } else {
                if (pageFrames.contains(refStr)) {
                    continue;
                } else {
                    pageFrames.add(refStr);
                    counter++;
                }
            }
            System.out.println(pageFrames);
        }
        System.out.println("Total Page Faults: " + counter + "\n");
        
        System.out.println("Press enter to return to menu.");
        scn.nextLine();
    }

    static void LRU() {
        System.out.println("Please enter the number of page frames.");
        int cap = Integer.parseInt(scn.nextLine());
        ArrayList<String> pageFrames = new ArrayList<String>();

        System.out.println("Please enter reference strings seperated by commas.");
        String in = scn.nextLine();

        String refStrings[] = in.split(",");

        int counter = 0;
        for (String refStr : refStrings) {
            if (pageFrames.size() >= cap) {
                if (pageFrames.contains(refStr)) {
                    pageFrames.add(pageFrames.remove(pageFrames.indexOf(refStr)));
                } else {
                    pageFrames.remove(0);
                    pageFrames.add(refStr);
                    counter++;
                    System.out.println(pageFrames);
                }
            } else {
                if (pageFrames.contains(refStr)) {
                    pageFrames.add(pageFrames.remove(pageFrames.indexOf(refStr)));
                } else {
                    pageFrames.add(refStr);
                    counter++;
                    System.out.println(pageFrames);
                }

            }

        }
        System.out.println("Total Page Faults: " + counter + "\n");
        
        System.out.println("Press enter to return to menu.");
        scn.nextLine();
    }

    public static void main(String[] args) {
        while (isRunning) {
            System.out.println(
                    "   ____  _____    ___    _   ________       ____________  __________ \n"
                    + "  / __ \\/ ___/   /   |  / | / / ___/ |     / / ____/ __ \\/ ____/ __ \\\n"
                    + " / / / /\\__ \\   / /| | /  |/ /\\__ \\| | /| / / __/ / /_/ / __/ / /_/ /\n"
                    + "/ /_/ /___/ /  / ___ |/ /|  /___/ /| |/ |/ / /___/ _, _/ /___/ _, _/ \n"
                    + "\\____//____/  /_/  |_/_/ |_//____/ |__/|__/_____/_/ |_/_____/_/ |_|  \n"
                    + "                                                                     \n"
                    + "Choose a mode :\n"
                    + "[1] PCM (Round Robin)\n"
                    + "[2] PCM (FCFS)\n"
                    + "[3] Page Replacement (FIFO)\n"
                    + "[4] Page Replacement (LRU)\n"
                    + "[5] Exit");
            int choice = Integer.parseInt(scn.nextLine());
            switch (choice) {
                case 1:
                    roundRobin();
                    break;
                case 2:
                    FCFS();
                    break;
                case 3:
                    FIFO();
                    break;
                case 4:
                    LRU();
                    break;
                case 5:
                    isRunning = false;
                    break;
                default:
                    return;
            }
        }
    }
}
