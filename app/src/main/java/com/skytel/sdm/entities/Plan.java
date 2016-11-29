package com.skytel.sdm.entities;

/**
 * Created by Altanchimeg on 10/8/2016.
 */
public class Plan {
    private int id;
    private float plan;
    private float perform;
    private float percent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPlan() {
        return plan;
    }

    public void setPlan(float plan) {
        this.plan = plan;
    }

    public float getPerform() {
        return perform;
    }

    public void setPerform(float perform) {
        this.perform = perform;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
