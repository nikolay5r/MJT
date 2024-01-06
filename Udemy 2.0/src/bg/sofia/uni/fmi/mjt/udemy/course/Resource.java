package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

public class Resource implements Completable {
    private final String name;
    private final ResourceDuration duration;
    private int minutesWatched;

    public Resource(String name, ResourceDuration duration)
    {
        this.duration = duration;
        this.name = name;
        this.minutesWatched = 0;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Resource))
            throw new IllegalArgumentException();
        return this.name.equals(((Resource)other).name);
    }

    public int hashCode()
    {
        return Integer.parseInt(name);
    }

    public String toString()
    {
        return "Resource: [name: " + name + ", duration: " + duration.toString() + ", minutesWatched: " +  minutesWatched + "]";
    }

    /**
     * Returns the resource name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total duration of the resource.
     */
    public ResourceDuration getDuration() {
        return duration;
    }

    /**
     * Marks the resource as completed.
     */
    public void complete() {
        minutesWatched = duration.minutes();
    }

    /**
     * Increases the minutes that the resource was being watched
      */
    public void increaseMinutesWatched() {
        if (!isCompleted())
            ++minutesWatched;
    }

    public int getMinutesWatched() {
        return minutesWatched;
    }

    @Override
    public boolean isCompleted() {
        return minutesWatched == duration.minutes();
    }

    @Override
    public int getCompletionPercentage() {
        return minutesWatched / duration.minutes() * 100;
    }
}
