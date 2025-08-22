package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.GymCentre;
import com.flipfit.dao.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDao;
    private UserDAO userDao;
    private GymCentreDAO gymCentreDao;
    private BookingDAO bookingDao;

    public CustomerService(UserDAO userDao, CustomerDAO customerDao,GymOwnerDAO gymOwnerDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.bookingDao = bookingDao; // Inject BookingDAO
    }

    public List<Booking> viewBookedSlots(int customerId) {
        System.out.println("Fetching your booked slots...");
        // Call the new database-based method from BookingDAO
        return bookingDao.getBookingsByCustomerId(customerId);
    }

    public List<GymCentre> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        // Call the new database-based method from GymCentreDAO
        return customerDao.getApprovedGyms();
    }

    public void bookSlot(int bookingId, int customerId, int gymId, int slotId) {
        System.out.println("Booking your slot...");

        // Create a new Booking object with the provided details
        Booking newBooking = new Booking(
                bookingId,
                customerId,
                gymId,
                slotId,
                "BOOKED", // Default status
                LocalDate.now(),
                LocalTime.now()
        );

        // Call the database-based method in BookingDAO
        customerDao.bookSlot(newBooking);

        System.out.println("Slot booked successfully!");
    }

    public void makePayments(int customerId, int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with account: " + paymentInfo);
        // Fetch the customer to update their payment details
        customerDao.getCustomerById(customerId).ifPresent(customer -> {
            customer.setPaymentType(paymentType);
            customer.setPaymentInfo(paymentInfo);
            customerDao.updateCustomer(customer);
        });
    }

    public void editCustomerDetails(int userId, int choice, String newValue) {
        userDao.getUserById(userId).ifPresent(user -> {
            switch (choice) {
                case 1: // Name
                    user.setFullName(newValue);
                    break;
                case 2: // Email
                    user.setEmail(newValue);
                    break;
                case 3: // Password
                    user.setPassword(newValue);
                    break;
                case 4: // Phone Number
                    try {
                        long newPhone = Long.parseLong(newValue);
                        user.setUserPhone(newPhone);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid phone number format. Please enter a valid number.");
                        return; // Exit the method on error
                    }
                    break;
                case 5: // City
                    user.setCity(newValue);
                    break;
                case 6: // Pincode
                    try {
                        int newPincode = Integer.parseInt(newValue);
                        user.setPinCode(newPincode);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid pincode format. Please enter a valid number.");
                        return; // Exit the method on error
                    }
                    break;
                default:
                    System.err.println("Invalid choice for update.");
                    return; // Exit the method on invalid choice
            }
            userDao.updateUser(user);
            System.out.println("Customer details updated successfully.");
        });
    }
}