package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;

public class Course implements Completable, Purchasable {
    private final String name;
    private final String description;
    private final double price;
    private final Category category;
    private final Resource[] content;
    private final CourseDuration totalTime;
    private boolean isPurchased;

    public Course(String name, String description, double price, Resource[] content, Category category) {
        this.name = name;
        this.description = description;
        this.price = Math.round(price * 100.0) / 100.0;
        this.category = category;
        this.content = content;
        this.totalTime = CourseDuration.of(content);
    }

    private int calculateTheTotalWatchedTime() {
        int sum = 0;

        for (var element : content) {
            sum += element.getMinutesWatched();
        }

        return sum;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Course))
            throw new IllegalArgumentException();
        return this.name.equals(((Course)other).name);
    }

    @Override
    public int hashCode()
    {
        return Integer.parseInt(name);
    }

    @Override
    public String toString()
    {
        return "Course: [name: " + name + ", description: " + description + ", price: " +  price + ", category: " + category.toString() + ", content: " + Arrays.toString(content) + "  ]";
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return Math.round(price * 100.0) / 100.0;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {
        return totalTime;
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException  if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */

    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if (resourceToComplete == null)
            throw new IllegalArgumentException();

        for (var element : content) {
            if (element.equals(resourceToComplete)) {
                element.complete();
                return;
            }
        }

        throw new ResourceNotFoundException();
    }

    @Override
    public boolean isCompleted() {
        return getCompletionPercentage() == 100;
    }

    @Override
    public int getCompletionPercentage() {
        int count = 0;
        for (var resource: content) {
            if (resource.isCompleted())
                count++;
        }

        return (int) (Math.round(((double)count) / content.length * 100.0));
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
    }

    /**
     * @return clone of current course
     * @throws CloneNotSupportedException

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object obj = super.clone();

        Course course = (Course) obj;
        course.name = this.name;
        course.description = this.description;
        course.price = this.price;
        course.category = this.category;



        return course;
    }*/
}
