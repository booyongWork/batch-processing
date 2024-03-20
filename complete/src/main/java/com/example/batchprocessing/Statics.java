package com.example.batchprocessing;

public record Statics(
        String jobNm,
        int totalPeople,
        int maleCount,
        int femaleCount,
        int marriedCount,
        int unmarriedCount,
        double teenagePercentage,
        double twentiesPercentage,
        double thirtiesPercentage,
        double fortiesPercentage,
        double fiftiesPercentage
) {}
