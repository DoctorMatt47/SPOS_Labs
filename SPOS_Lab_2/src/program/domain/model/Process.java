package program.domain.model;

public class Process {
  public int id;
  public int cpuTime;
  public int cpuDone;
  public int withoutBlocking;
  public int withoutBlockingDone;
  public int arrivalTime;
  public int blockedCount;
  public boolean isArrived = false;

  public Process(int cpuTime, int withoutBlocking, int arrivalTime) {
    this.cpuTime = cpuTime;
    this.withoutBlocking = withoutBlocking;
    this.arrivalTime = arrivalTime;
  }

  @Override
  public String toString() {
    return "Process{" +
            "cpuTime=" + cpuTime +
            ", cpuDone=" + cpuDone +
            ", withoutBlocking=" + withoutBlocking +
            ", withoutBlockingDone=" + withoutBlockingDone +
            ", arrivalTime=" + arrivalTime +
            ", blockedCount=" + blockedCount +
            ", isArrived=" + isArrived +
            '}';
  }
}
