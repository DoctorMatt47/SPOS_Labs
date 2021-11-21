package program.domain.model;

public class Process {
  public int cpuTime;
  public int withoutBlocking;
  public int cpuDone;
  public int ioNext;
  public int blockedCount;

  public Process(int cpuTime, int ioBlocking, int cpuDone, int ioNext, int blockedCount) {
    this.cpuTime = cpuTime;
    this.withoutBlocking = ioBlocking;
    this.cpuDone = cpuDone;
    this.ioNext = ioNext;
    this.blockedCount = blockedCount;
  }

  @Override
  public String toString() {
    return "{" +
            "cputime=" + cpuTime +
            ", ioblocking=" + withoutBlocking +
            ", cpudone=" + cpuDone +
            ", ionext=" + ioNext +
            ", numblocked=" + blockedCount +
            '}';
  }
}
