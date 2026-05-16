package com.example.model;

public class VirtualUser {

    private final long userId;
    private final String country;
    private final String device;
    private final double purchaseProbability;
    private final double activityScore;

    public VirtualUser(
            long userId,
            String country,
            String device,
            double purchaseProbability,
            double activityScore
    ) {
        this.userId = userId;
        this.country = country;
        this.device = device;
        this.purchaseProbability = purchaseProbability;
        this.activityScore = activityScore;
    }

    public long getUserId() {
        return userId;
    }

    public String getCountry() {
        return country;
    }

    public String getDevice() {
        return device;
    }

    public double getPurchaseProbability() {
        return purchaseProbability;
    }

    public double getActivityScore() {
        return activityScore;
    }
}
