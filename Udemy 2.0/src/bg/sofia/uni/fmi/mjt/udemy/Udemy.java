package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.util.Arrays;

public class Udemy implements LearningPlatform{

    private final Account[] accounts;
    private final Course[] courses;
    private Course max;

    public Udemy(Account[] accounts, Course[] courses) {
        this.accounts = accounts;
        this.courses = courses;
        this.max = null;
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException, IllegalArgumentException {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException();

        for (var element: courses) {
            if (element.getName().equals(name))
            {
                return element;
            }
        }

        throw new CourseNotFoundException();
    }

    @Override
    public Course[] findByKeyword(String keyword) throws IllegalArgumentException {
        if (keyword == null || keyword.isBlank() || keyword.matches("\\w+"))
            throw new IllegalArgumentException();

        if (courses == null)
            return null;

        Course[] keywordCourses = new Course[courses.length];
        int size = 0;

        for (var course: courses) {
            if (course == null)
                continue;

            if (course.getName().contains(keyword) || course.getDescription().contains(keyword))
                keywordCourses[size++] = course;
        }

        return Arrays.copyOfRange(keywordCourses, 0, size);
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        if (category == null)
            throw new IllegalArgumentException();

        if (courses == null)
            return null;

        Course[] categoryCourses = new Course[courses.length];
        int size = 0;

        for (var course: courses) {
            if (course == null)
                continue;

            if (course.getCategory().equals(category))
                categoryCourses[size++] = course;
        }

        return Arrays.copyOfRange(categoryCourses, 0, size);
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException, IllegalArgumentException {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException();

        for (var element: accounts) {
            if (element == null)
                continue;
            if (element.getUsername().equals(name))
            {
                return element;
            }
        }

        throw new AccountNotFoundException();
    }

    @Override
    public Course getLongestCourse() {
        if (courses == null)
            return null;

        if (max == null)
        {
            Course currMax = courses[0];
            for (var course: courses) {
                if (course.getTotalTime().getTotalMinutes() > currMax.getTotalTime().getTotalMinutes())
                {
                    currMax = course;
                }
            }

            max = currMax;
        }

        return max;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        Course[] categoryCourses = getAllCoursesByCategory(category);

        if (categoryCourses == null || categoryCourses.length == 0)
            return null;

        Course min = categoryCourses[0];

        for (var course: categoryCourses) {
            if (course == null)
                continue;
            if (course.getPrice() < min.getPrice())
                min = course;
        }

        return min;
    }
}
