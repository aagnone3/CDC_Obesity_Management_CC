package edu.gatech.johndoe.carecoordinator;

/**
 * Created by rakyu012 on 2/23/2016.
 */
public class Referral {


    private String name;
    private String detail;
    private boolean clicked = false;
    //push?

    public Referral () {
    }

    public Referral (String name) {
        this.name = name;
        this.detail = "...";
    }

    public Referral (String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public Referral (String name, String detail, boolean clicked) {
        this.name = name;
        this.detail = detail;
        this.clicked = clicked;
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
