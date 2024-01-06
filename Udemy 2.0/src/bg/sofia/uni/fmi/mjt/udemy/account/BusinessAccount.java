package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase{
    private final Category[] allowedCategories;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = allowedCategories;
    }

    private boolean isInAllowedCategories(Category category)
    {
        for (var allowedCategory: allowedCategories) {
            if (allowedCategory.equals(category))
                return true;
        }

        return false;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (course == null || !isInAllowedCategories(course.getCategory()))
            throw new IllegalArgumentException();

        if (balance < course.getPrice() * (1 - AccountType.BUSINESS.getDiscount()))
            throw new InsufficientBalanceException();

        if (sizeOfPurchased == MAX_COURSE_CAPACITY)
            throw new MaxCourseCapacityReachedException();

        if (course.isPurchased())
            throw new CourseAlreadyPurchasedException();

        purchasedCourses[sizeOfPurchased] = course;
        purchasedCourses[sizeOfPurchased++].purchase();
        balance -= course.getPrice() * (1 - AccountType.BUSINESS.getDiscount());
    }
}
