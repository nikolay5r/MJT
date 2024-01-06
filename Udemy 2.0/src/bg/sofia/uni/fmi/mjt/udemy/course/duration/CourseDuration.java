package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {
    public CourseDuration {
        if (hours < 0 || hours > 24 || minutes < 0 || minutes > 60)
            throw new IllegalArgumentException();
    }
    public static CourseDuration of(Resource[] resources)
    {
        int sumOfMins = 0;
        for (var resource: resources) {
            sumOfMins += resource.getDuration().minutes();
        }

        return new CourseDuration(sumOfMins / 60, sumOfMins % 60);
    }

    public int getTotalMinutes()
    {
        return hours * 60 + minutes;
    }
}
