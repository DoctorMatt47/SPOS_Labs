package program.core.printer;

import program.domain.model.Process;

import java.io.*;

public class ProcessPrinter implements Closeable, IProcessPrinter {
    private final PrintStream out;

    public ProcessPrinter(String filePath) throws FileNotFoundException {
        out = new PrintStream(new FileOutputStream(filePath));
    }

    @Override
    public void print(Process process, String action) {
        out.printf("scheduling.Process: %d %s... %s\n", process.id, action, process);
    }

    @Override
    public void close(){
        out.close();
    }
}
