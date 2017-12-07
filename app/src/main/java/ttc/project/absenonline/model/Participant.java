package ttc.project.absenonline.model;

/**
 * Created by Fikry-PC on 12/6/2017.
 */

public class Participant {
    String name, email;
    Long date_in;

    public Participant(String name, String email, Long date_in) {
        this.name = name;
        this.email = email;
        this.date_in = date_in;
    }

    public Participant(){

    }

    public Long getDate_in() {
        return date_in;
    }

    public void setDate_in(Long date_in) {
        this.date_in = date_in;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
