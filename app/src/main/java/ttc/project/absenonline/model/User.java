package ttc.project.absenonline.model;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class User {
    String uid, email, nama;

    public User(String uid, String email, String nama) {
        this.uid = uid;
        this.email = email;
        this.nama = nama;
    }

    public User(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
