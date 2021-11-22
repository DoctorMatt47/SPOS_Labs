// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  scheduling.SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-scheduling.Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

package program;

import program.core.algorithm.ISchedulingAlgorithm;
import program.core.algorithm.ShortestJobFirstNonPreemptive;
import program.core.algorithm.ShortestJobFirstPreemptive;
import program.core.printer.IProcessPrinter;
import program.core.printer.ProcessPrinter;
import program.core.printer.ResultsPrinter;
import program.domain.model.Process;
import program.infrastructure.util.Common;

import java.io.*;
import java.util.*;

public class Main {

    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static final Vector<Process> processVector = new Vector<>();
    private static final String resourcesDir = "src/resources/";

    private static final int ALGORITHMS_COUNT = 2;
    private static final ArrayList<ISchedulingAlgorithm> algorithms = new ArrayList<>();
    private static final ArrayList<IProcessPrinter> printers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        errorHandler(args);
        addProcesses(args);

        for (int i = 0; i < processVector.size(); i++) {
            processVector.get(i).id = i;
        }

        printers.add(new ProcessPrinter(resourcesDir + "Summary-Processes-0.txt"));
        printers.add(new ProcessPrinter(resourcesDir + "Summary-Processes-1.txt"));

        algorithms.add(new ShortestJobFirstPreemptive(printers.get(0)));
        algorithms.add(new ShortestJobFirstNonPreemptive(printers.get(1)));

        System.out.println("Working...");

        for (int i = 0; i < ALGORITHMS_COUNT; i++) {
            var processes = Common.processesCopy(processVector);
            var result = algorithms.get(i).run(runtime, processes);
            if (printers.get(i) instanceof Closeable) ((Closeable) printers.get(i)).close();

            var resultsPrinter = new ResultsPrinter(resourcesDir + "Summary-Results-" + i + ".txt");
            resultsPrinter.print(result, meanDev, standardDev, processes);
            resultsPrinter.close();
        }

        System.out.println("Completed.");
    }

    private static void errorHandler(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File f = new File(args[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
    }

    private static void addProcesses(String[] args) {
        Init(args[0]);
        if (processVector.size() < processnum) {
            var i = 0;
            while (processVector.size() < processnum) {
                double X = Common.R1();
                while (X == -1.0) {
                    X = Common.R1();
                }
                X = X * standardDev;
                int cputime = (int) X + meanDev;
                var arrivalTime = new Random().nextInt(cputime / 4);
                processVector.addElement(new Process(cputime, i*100, arrivalTime));
                i++;
            }
        }
    }

    private static void Init(String file) {
        File f = new File(file);
        String line;
        String tmp;
        int cputime = 0;
        int ioblocking = 0;

        try {
            //BufferedReader in = new BufferedReader(new FileReader(f));
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("meandev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    meanDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("standdev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    standardDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    ioblocking = Common.s2i(st.nextToken());
                    var X = Common.R1();
                    while (X == -1.0) {
                        X = Common.R1();
                    }
                    X = X * standardDev;
                    cputime = (int) X + meanDev;
                    var arrivalTime = new Random().nextInt(cputime / 4);
                    processVector.addElement(new Process(cputime, ioblocking, arrivalTime));
                }
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Common.s2i(st.nextToken());
                }
            }
            in.close();
        } catch (IOException e) { /* Handle exceptions */ }
    }

    private static void debug() {
        int i = 0;

        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + meanDev);
        System.out.println("standdev " + standardDev);
        int size = processVector.size();
        for (i = 0; i < size; i++) {
            Process process = processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cpuTime + " " + process.withoutBlocking + " " + process.cpuDone + " " + process.blockedCount);
        }
        System.out.println("runtime " + runtime);
    }
}

