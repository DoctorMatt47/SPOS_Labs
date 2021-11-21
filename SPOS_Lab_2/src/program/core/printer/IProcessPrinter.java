package program.core.printer;

import program.domain.model.Process;

public interface IProcessPrinter {
    void print(Process process, int processId, String action);
}
