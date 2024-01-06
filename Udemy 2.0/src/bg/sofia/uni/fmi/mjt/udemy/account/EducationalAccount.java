package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class EducationalAccount extends AccountBase {
    private final double[] lastFiveGrades;
    private int count;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
        this.lastFiveGrades = new double[5];
        for (var grade : lastFiveGrades) {
            grade = 0;
        }
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null) {
            throw new IllegalArgumentException();
        }

        double totalSumOfGradesFromLastFive = 0;
        for (var grade : lastFiveGrades) {
            totalSumOfGradesFromLastFive += grade;
        }

        if ((count == 5 && totalSumOfGradesFromLastFive / count >= 4.5 && balance < course.getPrice() * (1 - AccountType.EDUCATION.getDiscount())) || balance < course.getPrice() * (1 - AccountType.STANDARD.getDiscount())) {
            throw new InsufficientBalanceException();
        }

        if (sizeOfPurchased == MAX_COURSE_CAPACITY)
            throw new MaxCourseCapacityReachedException();

        if (course.isPurchased())
            throw new CourseAlreadyPurchasedException();

        purchasedCourses[sizeOfPurchased] = course;
        purchasedCourses[sizeOfPurchased].purchase();

        if (count == 5 && totalSumOfGradesFromLastFive / count >= 4.5) {
            balance -= course.getPrice() * (1 - AccountType.EDUCATION.getDiscount());
            for (var grade : lastFiveGrades)
                grade = 0;
            count = 0;
        } else {
            balance -= course.getPrice() * (1 - AccountType.STANDARD.getDiscount());
        }
        sizeOfPurchased++;
    }

    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        if (course == null || grade < 2.0 || grade > 6.0)
            throw new IllegalArgumentException();

        Course current = null;
        for (var element : purchasedCourses) {
            if (element.equals(course)) {
                current = element;
                break;
            }
        }

        if (current == null || !current.isPurchased()) {
            throw new CourseNotPurchasedException();
        }

        if (!current.isCompleted()) {
            throw new CourseNotCompletedException();
        }

        for (int i = 0; i < sizeOfCompleted; i++) {
            if (completedCourses[i].equals(current)) {
                return;
            }
        }

        if (count == 5) {
            for (int i = 0; i < count - 1; i++) {
                lastFiveGrades[i] = lastFiveGrades[i + 1];
            }

            lastFiveGrades[count - 1] = grade;
        } else {
            lastFiveGrades[count++] = grade;
        }

        completedCourses[sizeOfCompleted++] = current;
        totalSumOfGrades += grade;
        avgGrade = totalSumOfGrades / sizeOfCompleted;
    }
}
