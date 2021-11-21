package program.core.algorithm;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import program.domain.model.Process;
import program.core.printer.ProcessPrinter;
import program.domain.model.Results;

import java.util.Comparator;
import java.util.Vector;

public class SjfSchedulingAlgorithm implements ISchedulingAlgorithm {

    private final ProcessPrinter printer;

    public SjfSchedulingAlgorithm(ProcessPrinter printer) {
        this.printer = printer;
    }

    @Override
    public Results run(int runtime, Vector<Process> processVector) {
        var comptime = 0;
        var currentId = 0;
        var previousId = 0;
        var completed = 0;

        var result = new Results(null, null, 0);

        result.schedulingType = "Batch (Non-preemptive)";
        result.schedulingName = "Shortest Job First";

        processVector.sort(Comparator.comparingInt(o -> o.ioBlocking));

        var currentProcess = (Process) processVector.elementAt(currentId);
        printer.print(currentProcess, currentId, "registered");
        while (comptime < runtime) {
            if (currentProcess.cpuDone == currentProcess.cpuTime) {
                completed++;
                printer.print(currentProcess, currentId, "completed");
                if (completed == processVector.size()) {
                    result.compTime = comptime;
                    printer.close();
                    return result;
                }
                for (var i = processVector.size() - 1; i >= 0; i--) {
                    currentProcess = processVector.elementAt(i);
                    if (currentProcess.cpuDone < currentProcess.cpuTime) {
                        currentId = i;
                    }
                }
                currentProcess = processVector.elementAt(currentId);
                printer.print(currentProcess, currentId, "registered");
            }
            if (currentProcess.ioBlocking == currentProcess.ioNext) {
                printer.print(currentProcess, currentId, "I/O blocked");
                currentProcess.blockedCount++;
                currentProcess.ioNext = 0;
                previousId = currentId;
                for (var i = 0; i < processVector.size(); i++) {
                    var process = processVector.elementAt(i);
                    if (process.cpuDone < process.cpuTime && previousId != i) {
                        currentId = i;
                        break;
                    }
                }
                currentProcess = processVector.elementAt(currentId);
                printer.print(currentProcess, currentId, "registered");
            }
            currentProcess.cpuDone++;
            if (currentProcess.ioBlocking > 0) {
                currentProcess.ioNext++;
            }
            comptime++;
        }
        result.compTime = comptime;
        return result;
    }
}
