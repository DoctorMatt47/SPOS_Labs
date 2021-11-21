package program.domain.model;

public class Results {
  public String schedulingType;
  public String schedulingName;
  public int compTime;

  public Results (String schedulingType, String schedulingName, int compTime) {
    this.schedulingType = schedulingType;
    this.schedulingName = schedulingName;
    this.compTime = compTime;
  } 	
}
