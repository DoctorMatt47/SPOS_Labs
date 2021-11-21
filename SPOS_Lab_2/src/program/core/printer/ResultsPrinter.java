package program.core.printer;

import java.io.*;
import java.util.Vector;

import program.domain.model.Process;
import program.domain.model.Results;

public class ResultsPrinter implements IResultsPrinter, Closeable {
    private final String filePath;
    private final PrintStream out;

    public ResultsPrinter(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        this.out = new PrintStream(new FileOutputStream(filePath));
    }

    public void print(Results result, int meanDev, int standardDev, Vector<Process> processVector) {
        out.println("Scheduling Type: " + result.schedulingType);
        out.println("Scheduling Name: " + result.schedulingName);
        out.println("Simulation Run Time: " + result.compTime);
        out.println("Mean: " + meanDev);
        out.println("Standard Deviation: " + standardDev);
        out.println("scheduling.Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
        for (var i = 0; i < processVector.size(); i++) {
            Process process = processVector.elementAt(i);
            out.print(i);
            if (i < 100) { out.print("\t\t"); } else { out.print("\t"); }
            out.print(process.cpuTime);
            if (process.cpuTime < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
            out.print(process.withoutBlocking);
            if (process.withoutBlocking < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
            out.print(process.cpuDone);
            if (process.cpuDone < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
            out.println(process.blockedCount + " times");
        }
        out.close();
    }

    @Override
    public void close() {

    }
}
