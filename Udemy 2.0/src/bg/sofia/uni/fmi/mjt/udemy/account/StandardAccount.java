package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class StandardAccount extends AccountBase{
    public StandardAccount(String username, double balance) {
        super(username, balance);
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null)
            throw new IllegalArgumentException();

        if (balance < course.getPrice() * (1 - AccountType.STANDARD.getDiscount()))
            throw new InsufficientBalanceException();

        if (sizeOfPurchased == MAX_COURSE_CAPACITY)
            throw new MaxCourseCapacityReachedException();

        if (course.isPurchased())
            throw new CourseAlreadyPurchasedException();

        purchasedCourses[sizeOfPurchased] = course;
        purchasedCourses[sizeOfPurchased++].purchase();
        balance -= course.getPrice() * (1 - AccountType.STANDARD.getDiscount());
    }


}
