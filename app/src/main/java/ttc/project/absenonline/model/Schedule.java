package ttc.project.absenonline.model;

/**
 * Created by Fikry-PC on 12/6/2017.
 */

public class Schedule {
    String activityId;

    public Schedule(String activityId) {
        this.activityId = activityId;
    }

    public Schedule(){

    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
