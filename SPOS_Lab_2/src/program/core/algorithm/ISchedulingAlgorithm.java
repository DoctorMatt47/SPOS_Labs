package program.core.algorithm;

import program.domain.model.Process;
import program.domain.model.Results;

import java.util.Vector;

public interface ISchedulingAlgorithm {
    Results run(int runtime, Vector<Process> processVector);
}
