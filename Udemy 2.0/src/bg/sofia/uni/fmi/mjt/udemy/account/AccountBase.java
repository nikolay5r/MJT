package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;

public abstract class AccountBase implements Account {
    protected final String username;
    protected double balance;
    protected Course[] purchasedCourses;
    protected Course[] completedCourses;
    protected int sizeOfPurchased;
    protected int sizeOfCompleted;
    protected double totalSumOfGrades;
    protected double avgGrade;

    protected static final int MAX_COURSE_CAPACITY = 100;

    protected AccountBase(String username, double balance) {
        this.username = username;
        this.balance = Math.round(balance * 100.0) / 100.0;
        this.purchasedCourses = new Course[MAX_COURSE_CAPACITY];
        this.completedCourses = new Course[MAX_COURSE_CAPACITY];
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public void addToBalance(double amount) throws IllegalArgumentException {
        if (amount < 0.0)
            throw new IllegalArgumentException();

        balance += Math.round(amount * 100.0) / 100.0;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public Course getLeastCompletedCourse() {
        if (sizeOfPurchased == 0)
            return null;

        Course leastCompleted = purchasedCourses[0];
        for (var element: purchasedCourses) {
            if (element == null)
                continue;

            if (element.getCompletionPercentage() < leastCompleted.getCompletionPercentage())
                leastCompleted = element;
        }

        return leastCompleted;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if (course == null || resourcesToComplete == null)
            throw new IllegalArgumentException();

        if (!course.isPurchased())
            throw new CourseNotPurchasedException();

        Course current = null;
        for (var element: purchasedCourses) {
            if (element.equals(course)) {
                current = element;
                break;
            }
        }

        if (current == null)
            throw new CourseNotPurchasedException();

        int size = 0;
        for (var element: current.getContent()) {
            for (var resToComplete: resourcesToComplete) {
                if (resToComplete.equals(element)) {
                    size++;
                    break;
                }
            }
        }

        if (size != resourcesToComplete.length)
            throw new ResourceNotFoundException();

        for (var element: current.getContent()) {
            for (var resToComplete: resourcesToComplete) {
                if (resToComplete.equals(element)) {
                    element.complete();
                    break;
                }
            }
        }
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null || grade < 2.0 || grade > 6.0)
            throw new IllegalArgumentException();

        Course current = null;
        for (var element: purchasedCourses) {
            if (element.equals(course)) {
                current = element;
                break;
            }
        }

        if (current == null || !current.isPurchased())
            throw new CourseNotPurchasedException();

        if (!current.isCompleted())
            throw new CourseNotCompletedException();

        for (int i = 0; i < sizeOfCompleted; i++) {
            if (completedCourses[i].equals(current))
            {
                return;
            }
        }

        completedCourses[sizeOfCompleted++] = current;
        totalSumOfGrades += grade;
        avgGrade = totalSumOfGrades / sizeOfCompleted;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AccountBase))
            throw new IllegalArgumentException();

        return this.username.equals(((AccountBase)other).username);
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(username);
    }

    @Override
    public String toString() {
        return "AccountBase: [username : " + username + ", balance: " + balance + ", purchasedCourses: [" + sizeOfPurchased + ", " + Arrays.toString(purchasedCourses) + ", avgGrade: " + avgGrade + "]";
    }

}
