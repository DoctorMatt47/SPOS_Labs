package program.core.algorithm;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import program.core.printer.IProcessPrinter;
import program.domain.model.Process;
import program.domain.model.Results;

import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

public class ShortestJobFirstNonPreemptive implements ISchedulingAlgorithm {

    private final IProcessPrinter printer;

    public ShortestJobFirstNonPreemptive(IProcessPrinter printer) {
        this.printer = printer;
    }

    @Override
    public Results run(int runtime, Vector<Process> processVector) {
        var comptime = 0;
        var completed = 0;

        var result = new Results(null, null, 0);

        result.schedulingType = "Non-preemptive";
        result.schedulingName = "Shortest Job First";

        processVector.sort(Comparator.comparingInt(o -> o.withoutBlocking));

        Process currentProcess = null;
        while (comptime < runtime) {
            if (currentProcess != null) {
                if (currentProcess.cpuDone == currentProcess.cpuTime) {
                    completed++;
                    printer.print(currentProcess, "completed");
                    if (completed == processVector.size()) {
                        result.compTime = comptime;
                        return result;
                    }
                    var sortedProcesses = processVector.stream()
                            .filter(p -> p.isArrived)
                            .sorted(Comparator.comparingInt(o -> o.withoutBlocking - o.withoutBlockingDone))
                            .collect(Collectors.toCollection(Vector::new));
                    var currentId = getNextProcessId(sortedProcesses, -1);
                    if (currentId == -1) {
                        currentProcess = null;
                        continue;
                    }
                    currentProcess = processVector.stream().filter(p -> p.id == currentId).findFirst().get();
                }
                if (currentProcess.withoutBlocking == currentProcess.withoutBlockingDone) {
                    printer.print(currentProcess, "blocked");
                    currentProcess.blockedCount++;
                    currentProcess.withoutBlockingDone = 0;
                    var sortedProcesses = processVector.stream()
                            .filter(p -> p.isArrived)
                            .sorted(Comparator.comparingInt(o -> o.withoutBlocking - o.withoutBlockingDone))
                            .collect(Collectors.toCollection(Vector::new));
                    var currentId = getNextProcessId(sortedProcesses, currentProcess.id);
                    currentProcess = processVector.stream().filter(p -> p.id == currentId).findFirst().get();
                }
                currentProcess.cpuDone++;
                if (currentProcess.withoutBlocking > 0) {
                    currentProcess.withoutBlockingDone++;
                }
            }
            comptime++;
            for (var process : processVector) {
                if (process.arrivalTime == comptime) {
                    process.isArrived = true;
                    //printer.print(process, "arrived");
                    if (currentProcess == null) {
                        currentProcess = process;
                    }
                }
            }
        }
        result.compTime = comptime;
        return result;
    }

    private int getNextProcessId(Vector<Process> processVector, int previousId) {
        for (var i = 0; i < processVector.size(); i++) {
            var process = processVector.elementAt(i);
            if (process.cpuDone < process.cpuTime && previousId != process.id) {
                return process.id;
            }
        }
        return previousId;
    }
}
