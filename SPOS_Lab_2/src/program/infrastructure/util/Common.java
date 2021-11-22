package program.infrastructure.util;
import program.domain.model.Process;

import java.util.Vector;
import java.util.stream.Collectors;

public class Common {

    static public Vector<Process> processesCopy(Vector<Process> processes) {
        return processes.stream()
                .map(p -> {
                    var process = new Process(p.cpuTime, p.withoutBlocking, p.arrivalTime);
                    process.id = p.id;
                    return process;
                })
                .collect(Collectors.toCollection(Vector::new));
    }

    static public int s2i (String s) {
        int i = 0;

        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        return i;
    }

    static public double R1 () {
        java.util.Random generator = new java.util.Random(System.nanoTime());
        double U = generator.nextDouble();
        double V = generator.nextDouble();
        double X =  Math.sqrt((8/Math.E)) * (V - 0.5)/U;
        if (!(R2(X,U))) { return -1; }
        if (!(R3(X,U))) { return -1; }
        if (!(R4(X,U))) { return -1; }
        return X;
    }

    static public boolean R2 (double X, double U) {
        return (X * X) <= (5 - 4 * Math.exp(.25) * U);
    }

    static public boolean R3 (double X, double U) {
        return !((X * X) >= (4 * Math.exp(-1.35) / U + 1.4));
    }

    static public boolean R4 (double X, double U) {
        return (X * X) < (-4 * Math.log(U));
    }

}

