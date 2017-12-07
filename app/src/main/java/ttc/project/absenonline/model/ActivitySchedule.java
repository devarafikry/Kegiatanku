package ttc.project.absenonline.model;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class ActivitySchedule {
    String name, place_id, key;
    int participant_count;
    Long date;
    Long time_start;
    Long time_end;

    public ActivitySchedule(){

    }

    public ActivitySchedule(String name, String place_id, String key, int participant_count, Long date, Long time_start, Long time_end) {
        this.name = name;
        this.place_id = place_id;
        this.participant_count = participant_count;
        this.date = date;
        this.time_start = time_start;
        this.time_end = time_end;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public int getParticipant_count() {
        return participant_count;
    }

    public void setParticipant_count(int participant_count) {
        this.participant_count = participant_count;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTime_start() {
        return time_start;
    }

    public void setTime_start(Long time_start) {
        this.time_start = time_start;
    }

    public Long getTime_end() {
        return time_end;
    }

    public void setTime_end(Long time_end) {
        this.time_end = time_end;
    }
}
