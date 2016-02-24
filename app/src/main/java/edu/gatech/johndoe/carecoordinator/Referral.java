package edu.gatech.johndoe.carecoordinator;

/**
 * Created by rakyu012 on 2/23/2016.
 */
public class Referral {

    private boolean clicked = false;
    private String name;
    private String detail;
    //push?

    public Referral (String name) {
        this.name = name;
        this.detail = "...";
    }

    public Referral (String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public String getName() {
        return this.name;
    }

    public void setClicked() {
        if (clicked) {
            clicked = false;
        } else {
            clicked = true;
        }
    }

    public boolean getClicked() {
        return this.clicked;
    }

    public String getDetail() {
        return this.detail;
    }
}
