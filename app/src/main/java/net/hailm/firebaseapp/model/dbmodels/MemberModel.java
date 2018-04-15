package net.hailm.firebaseapp.model.dbmodels;

/**
 * Created by hai.lm on 15/04/2018.
 */

public class MemberModel {
    private String avatar;
    private String name;

    public MemberModel() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
