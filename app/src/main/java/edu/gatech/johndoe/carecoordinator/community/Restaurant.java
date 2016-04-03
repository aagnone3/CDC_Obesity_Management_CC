package edu.gatech.johndoe.carecoordinator.community;

/**
 * healthy restaurant community resource
 */
public class Restaurant extends Community {

    private String foodType;
    private int costLevel;
    private int carryOut;

    public Restaurant() {}

    public String getFoodType() {
        return foodType;
    }

    public int getCostLevel() {
        return costLevel;
    }

    public int getCarryOut() {
        return carryOut;
    }
}
