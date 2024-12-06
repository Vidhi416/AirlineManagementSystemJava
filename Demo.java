import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.binding.BooleanBinding;
import javafx.stage.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

// *********VERY IMPORTANT***********
/* Calendar and GregorianCalendar will be used in the program to log the booking time of the passenger. It will also be used to capture the arrival and departure time of airplanes.

 * ArrayList is a data structure just like Arrays which will be used to store Objects in general. It is used over Arrays for the reason that memory can be dynamically allocated here. You don't have
 * to allocate size statically before adding elements. 
 * 
 * We will be using the JavaFX application thread for the graphical user interface (GUI)
 * 
 * javafx.beans.property, javafx.beans.binding.BooleanBinding for features like the ChangeListener that will be used to detect changes in real-time. Any change takes place in the 
 * number of seats? We will automatically detect it using listeners implemented. javafx.beans.property will have classes like DoubleProperty and BooleanProperty which are observable versions of 
 * double and boolean datatypes. They can be modified anytime when required and text properties of Labels, selection state of buttons, etc can be binded to them. We can make features like
 * text labels being binded to StringProperty, meaning whenever StringProperty changes, text label automatically changes!
 * 
 * We also have the usual routine imports like java.fx.stage.*, javafx.scene.*, javafx.scene.layout.*, javafx.scene.text.Font, javafx.scene.control* used to start the graphical user interface.
 * 
 * javafx.event.* will import generic interfaces like EventHandler and classes like ActionEvent that will be used for event handling. Ex - everytime a button is pressed, we can trigger a response to this event.
 * 
 * javafx.geometry.* will be used for features like Pos class which can be used to position the GUI elements and make the application more user friendly
 * 
 * javafx.collections.* will import generic classes like ObservableList and FXCollections. Both of these are used for helping make displayable list elements in the ListView control element
 * in javafx.scene.control. This will be used for making schedules, reports, etc.
 * 
 * javafx.collections.transformation.FilteredList will import generic class FilteredList which will be used to filter the required elements from an ObservableList. This will be used for the
 * search feature in the application. We can search through airplanes in the schedule.
 * 
 * javafx.text.SimpleDateFormat will be used to store the input of TextField inputs as HH:MM or DD-MM-YYYY format. This is used in conjunction with ParseException class in java.text which
 * helps exception handling when trying to convert a string text to HH:MM or DD-MM-YYYY format.
 * 
 * java.util.Date also has features which we can use for dates. We can store dates in HH:MM, DD-MM-YYYY format. We can compare between dates and see if a date/time lies between two timestamps.
 * This is similar to Calendar and GregorianCalendar but easier to use when trying to store raw text from TextField as dates.
 * 
 * java.util.Scanner provides the scanner object which can be used to take user inputs.
 */

 

 // This is a user defined exception that is thrown when all seats are booked in the airplane. The constructor takes a custom message. The toString() function is overriden so that whenever we
 // try printing the exception object, we get the string representation of it.

class AllSeatsBooked extends Exception {
    String message;
    public AllSeatsBooked(String message) {
        this.message = message;
    }
    public String toString() {
        return(message);
    }
}

/* This ia Seat class. It represents a Seat inside an airplane.
 * (int) position attribute represents its seat number. This will be used during booking
 * (String) airlines_name attribute represents the name of the airplane it is present in.
 * (String) passenger_name attribute represents the naem of the passenger that has booked the seat. By default, a seat is initialized to have a NULL passenger_name as it isn't booked.
 * 
 * (BooleanProperty) booked attribute represents a boolean value that tells us if a seat is booked or not, but with a twist! JavaFX elements like Label, Button can be BINDED to this BooleanProperty
 * Therefore whenever a seat is booked, a Label or Button can immediately detect it! By default, a seat is initialized to have a 'false' booked attribute as it isn't booked.
 * 
 * (double) seatPrice attribute represents the price of the seat. 
 * (Calendar) booking_time attribute represents the time at which the seat was booked. By default, a seat is initialized to have a NULL booking_time as it isn't booked.
 * 
 * (ToggleButton) ref attribute is the ToggleButton associated to it. During seat booking, we use ToggleButtons to book a seat. Therefore, we link the ToggleButton and its respective seat like this!
 */
class Seat {
    int position;
    String airlines_name;
    String passenger_name;
    BooleanProperty booked;
    double seatPrice;
    Calendar booking_time;
    ToggleButton ref;

    /*
     * In this program, each time a booking is made, a new thread is made 
     * that will call the book function. This function is made synchronized so that only one thread can access the seat object at a time! We don't want multiple threads (or users) to book a single seat
     * 
     * When a thread gets into the book function, it will check the booked variable of the Seat object. If the booked is false (if the seat isn't already booked), we set the passenger_name
     * of the seat to the name of the traveller who has accessed the seat. The totalCost of the traveller (an attribute to track the total expense of a traveller) with the price of seat in the airplane.
     * We then set the value of the booked variable to true and increment the number of booked seats in the airplane by 1. (bookedSeats is an attribute in the airplane class to track number of booked seats.)
     * booking_time is also assigned the current date and time to reflect the time at which the booking was made. 
     * timeOfBooking is a String that will store the booking_time Calendar object in the format HH:MM DD/MM/YYYY
     * We will also return a (1) after all this to show that booking was successful for a thread!
     * Now once this value is returned, the thread exits the synchronized block and other threads start entering the block one-by-one. 
     * 
     * This time however, the booked variable of the Seat object is checked but the booked variable is now 'true'... meaning the seat has already been booked.
     * As a result it returns (0) to show that booking was unsuccessful for a thread.
     * 
     * This mechanism ensures that only one thread can book a seat at a time. 
     */
    synchronized int book(Traveller traveller) {
        if (booked.get() == false) {
            passenger_name = traveller.getPassengerName();
            traveller.totalCost = traveller.totalCost + traveller.airplane.seatPrice.get();
            Platform.runLater(new Runnable() {
                public void run() {
                    booked.set(true);
                    traveller.airplane.bookedSeats.set(traveller.airplane.bookedSeats.get() + 1);
                }
            });
            booking_time = new GregorianCalendar();
            String timeOfBooking = booking_time.get(Calendar.HOUR_OF_DAY) + ":" + booking_time.get(Calendar.MINUTE)
                    + " " + booking_time.get(Calendar.DAY_OF_MONTH) + "/" + (booking_time.get(Calendar.MONTH) + 1) + "/"
                    + booking_time.get(Calendar.YEAR);
            traveller.seats_booked.add("SEAT " + position + " | " + "BOOKING TIME: " + timeOfBooking + " | " + "AIRLINE: " + airlines_name
                            + " | " + "BOOKED UNDER: " + passenger_name + " | SEAT PRICE: " + seatPrice);
            return(1);           
        } else {
            return(0);
        }
    }

    /* These are setter functions that help settings the seat number and airlines_name of the seat. This will be helpful when we're initializing the seats of an Airplane */
    void updateSeatNo(int position) {
        this.position = position;
    }

    void updateAirlinesName(String airlines_name) {
        this.airlines_name = airlines_name;
    }

    /* This is a Seat constructor that will help construct a Seat object. It takes a seatPrice parameter. The seatPrice of the seat is then set. The booked state of the seat is
     * made 'false' as the seat is not booked. The passenger_name of the seat is made empty as the seat is not booked. The booking_time is also made 'null' as the seat is not booked.
     */

    Seat(double seatPrice) {
        this.seatPrice = seatPrice;
        this.booked = new SimpleBooleanProperty(false);
        this.passenger_name = "";
        this.booking_time = null;
    }
}

/* Airplane class represents an Airplane.
 * (String) It has 'name', 'type', 'origin', 'destination' attributes that are self explanatory. It contains the name of the airplane, type (Domestic/International), from location and the to location respectively
 * (Calendar) arrival and departure are objects that will be used to record the arrival time and departure time of airplanes respectively. 
 * 
 * (DoubleProperty) seatPrice records the price of each seat in the airplane. Why is it made to a DoubleProperty and not a normal double? Making it into a DoubleProperty ensures that 
 * JavaFX elements like Label can be binded to it! Meaning if the seatPrice changes, the text Label will also change. This will be used to show the change of seat price during the time of seat 
 * booking. (Important for dynamic pricing, we will come back to this..)
 * 
 * (IntegerProperty) bookedSeats records the number of booked seats in the airplane. Why is it made to an IntegerProperty and not a normal integer? We wanna bind JavaFX elements to it. You
 * cannot bind JavaFX elements to normal integers or doubles. You can also add listeners to IntegerProperty, DoubleProperty etc that will DETECT CHANGE! So if IntegerProperty changes, we can
 * make a certain code run. In this program, we have a feature that detects change in bookedSeats. If this change is such that the number of bookedSeats is equal to the total number of seats,
 * we alert the user that the airplane is FULLY booked!
 * 
 * (Seat[]) seats is an array of seats (Seat object) in the airplane. 
 * 
 * (boolean) departed represents if an airplane is departed or not. If departed, it is set true and if not departed, it will be set to false. Why is it not made to a BooleanProperty?
 * We didn't make it into a BooleanProperty because we don't need to. We don't have any JavaFX element binded to it as opposed to the other cases.
 */
class Airplane {
    String name;
    String type;
    String origin, destination;
    Calendar arrival;
    Calendar departure;
    DoubleProperty seatPrice;
    IntegerProperty bookedSeats;
    Seat[] seats;
    boolean departed;

    /*
     * This is an Airplane constructor that helps construct Airplane objects using parameters name, type, seat_capacity (size of seats array), seatPrice, origin, destination
     * arrival_hour, arrival_minute, arrival_day, arrival_month, arrival_year which will be used to set the arrival Calendar object
     * departure_hour, departure_minute, departure_day, departure_month, departure_year which will be used to set the departure Calendar object
     * 
     * Once we set all of these to their respective attribute, we will then make the seat array and assign it to seats. We will update each seat in the array with the seat number.
     * This is done using updateSeatNo, we will give its seat number as its index in the array.
     * We will update the airplane name of the seat with the name of the airplane object it is present in.
     * 
     * We will also be initiating bookedSeats and seatPrice in the below given way. bookedSeats is 0 as initially, no seats are booked. 
     */
    Airplane(String name, String type, int seat_capacity, double seatPrice, String origin, String destination,
            int arrival_minute, int arrival_hour, int arrival_day, int arrival_month, int arrival_year,
            int departure_minute, int departure_hour, int departure_day, int departure_month, int departure_year) {
        this.departed = false;
        this.type = type;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        arrival = new GregorianCalendar(arrival_year, arrival_month - 1, arrival_day);
        arrival.set(Calendar.HOUR_OF_DAY, arrival_hour);
        arrival.set(Calendar.MINUTE, arrival_minute);
        departure = new GregorianCalendar(departure_year, departure_month - 1, departure_day);
        departure.set(Calendar.HOUR_OF_DAY, departure_hour);
        departure.set(Calendar.MINUTE, departure_minute);
        this.seats = new Seat[seat_capacity];
        for (int i = 0; i < seats.length; i++) {
            seats[i] = new Seat(seatPrice);
            seats[i].updateSeatNo(i);
            seats[i].updateAirlinesName(name);
        }
        this.bookedSeats = new SimpleIntegerProperty(0);
        this.seatPrice = new SimpleDoubleProperty(seatPrice);
    }

    /* This is a function that will count the number of booked seats in the airplane at any moment. It will loop through the array and count the number of the seats whose booked variable
    is set to true. This function may be unnecessary as we already have a bookedSeats attribute to track the number of seats booked but we've already used it in so many parts of the program.
    */

    int countBooked() {
        int count = 0;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i].booked.getValue()) {
                count++;
            }
        }
        return (count);
    }

    /* VERY IMPORTANT
     * Every object has a string representation. Everytime .toString() is called on an object, we get the representation in the
     * form Class_Name@HashCode. It is unreadable and doesn't give us much information. Thus we will be 
     * overriding the toString() method of the object. This is a method of the Object class which is the ancestor
     * of every class.
     * 
     * Here, we make the toString() method return a string that gives us all the information of an airplane. The name, 
     * arrival time, departure time, from, to, number of booked seats are given in the string produced in the toString()
     * method. We will use this string representation of the object whenever we want all the readable details of the object.
     */

    public String toString() {
        int bookedSeats = countBooked();
        // arrival.get(Calendar.MONTH) always returns a month from the range 0-11. You must add 1 to it if you want
        // to make the month number understandable to the user. Eg - 0 in GregorianCalendar means January.
        // This however does not apply for the others like Calendar.DAY_OF_MONTH, Calendar.YEAR, etc.
        String arrival_time = arrival.get(Calendar.HOUR_OF_DAY) + ":" + arrival.get(Calendar.MINUTE) + " "
                + arrival.get(Calendar.DAY_OF_MONTH) + "/" + (arrival.get(Calendar.MONTH) + 1) + "/"
                + arrival.get(Calendar.YEAR);
        // departure.get(Calendar.MONTH) always returns a month from the range 0-11. You must add 1 to it if you want
        // to make the month number understandable to the user. Eg - 0 in GregorianCalendar means January.
        // This however does not apply for the others like Calendar.DAY_OF_MONTH, Calendar.YEAR, etc.
        String departure_time = departure.get(Calendar.HOUR_OF_DAY) + ":" + departure.get(Calendar.MINUTE) + " "
                + departure.get(Calendar.DAY_OF_MONTH) + "/" + (departure.get(Calendar.MONTH) + 1) + "/"
                + departure.get(Calendar.YEAR);
        return (name + " | " + "ARRIVAL TIME: " + arrival_time + " | " + "DEPARTURE TIME: " + departure_time + " | "
                + "FROM: " + origin + " | " + "TO: " + destination + " | " + "BOOKED SEATS: " + bookedSeats + "/"
                + seats.length);
    }
}

/* Here, a generic class Stack is made of any type T. This is like a normal Stack where we can push, pop and display elements
 * The class Stack has an ArrayList in it which is also a generic class of the type T. Why did we use an ArrayList instead of Array?
 * ArrayList is dynamic, you can continuously add elements to the end of the ArrayList when required. You do not have to
 * statically allocate memory before adding new elements, unlike an Array.
 */
class Stack<T> {
    ArrayList<T> arr; 
    int top;
    Stack() {
        arr = new ArrayList<>();
        top = -1;
    }
    /*
     * .add(data) of an ArrayList adds data to the end of the ArrayList. Increment the top variable by 1 when this is done.
     */
    void push(T data) {
        arr.add(data);
        top = top + 1;
    }
    /* .get(index) returns the element at an index in ArrayList. We will use the top variable to retrieve the last element of the Array.
     * Once we get it, we decrement the top by 1.
     */
    T pop() {
        if (!arr.isEmpty()) {
            T popped = arr.get(top);
            top = top - 1;
            return (popped);
        } else {
            throw new RuntimeException("Stack is Empty");
        }
    }
    /* We iterate through thw ArrayList from index 0 to index top. We will print every element of the Stack.
     */
    void display() {
        for (int i = 0; i <= top; i++) {
            System.out.println(arr.get(i));
        }
    }
}

/* Report is a class that will consist of elements necessary to view the Departed flights, fully booked departed flights, the most frequented destination,
 * booking time and frequented departure period. It is only for the manager to see to obtain statistics of the departed flights.
 * Every member of the Report class is made static because all manager(s) have access to the SAME report. Change is made to this report and reflected
 * to every manager. 
 * 
 * It has attributes 'logs' stack (initiated as empty) which will consist of Airplanes that are departed. Everytime a flight has departed, it gets pushed to 'logs'.
 * 
 * We have an attribute departedFlights which is an observablelist of Airplane objects. This is also an empty list that will consist of Airplanes that are departed.
 * 
 * Everytime an airplane is departed, it gets added to logs and departedFlights. ObservableList is important so that we can link this to ListView which can make this list
 * visible to the users in a user-friendly way.
 */
class Report {
    static Stack<Airplane> logs = new Stack<>();
    static ObservableList<Airplane> departedFlights = FXCollections.observableArrayList();
    /* We also a static generic function isPresent which checks if a T element is present in a Stack<T> stack. It has a boolean variable set to false
     * initially. It loops through the stack and anytime it finds an element, the boolean variable is set to true and breaks from the loop. The value of
     * the boolean variable is then returned at last. If it is present, value will be true. Else, it will be false.
     */
    static <T> boolean isPresent(Stack<T> stack, T element) {
        boolean present = false;
        for (int i = 0; i <= stack.top; i++) {
            if (stack.arr.get(i).equals(element)) {
                present = true;
                break;
            }
        }
        return(present);
    }
    /* We have a static generic function countElement which counts the number of times T element appears in a Stack<T> stack. It loops through the stack.
    Everytime it gets a match element, the count element is incremented by one. The count is then returned at last. If it isn't present, it returns 0.
    Else it will return a nonzero postiive number.
     */
    static <T> int countElement(Stack<T> stack, T element) {
        int count = 0;
        for (int i = 0; i <= stack.top; i++) {
            if (stack.arr.get(i).equals(element)) {
                count++;
            }
        }
        return(count);
    }
    /* We have a static string function getFrequentDeparturePeriod() which returns the most frequent month Flights departed in. What it does is 
     * check if the stack of Airplane (logs) is empty. If it is not empty, it makes new Stack<Integer> months_booked and unique_months. Stack<Integer>
     * as we want a Stack of the month numbers 1/2/3/4../12. 
     * Now we loop through logs stack and push every departure month + 1 (Reminder: Calendar.MONTH returns a month number from 0 to 11) into months_booked.
     * If the departure month in question is not present in unique_months, we push it into unique_months. 
     * 
     * We now make an integer variable max_count and initiate it to 0 and a frequent_month = 0 (represents no month yet).
     * We will now loop through the unique_months and count the number of times a month in unique_months appears in months_booked. If this count is 
     * greater than the max_count, we equate max_count to this count and equate the frequent_month to this month. 
     * 
     * At last, we get the max_count to be the maximum number of times a month appears.
     * We get frequent_month to be the month that appears the month.
     * 
     * We will then initiate a "month" variable to be set to blank string. 
     * We will use a switch statement on the frequent_month. If the frequent_month is 1, we set "month" to January.. frequent_month 2, we set "month" to February.. etc
     * We will then return the month variable at last. This is the month airplanes frequently departed in.
     * 
     * If the logs stack is empty, it returns an empty string.
    */
    static String getFrequentDeparturePeriod() {
        if (logs.top != -1) {
            Stack<Integer> months_booked = new Stack<>();
            Stack<Integer> unique_months = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                months_booked.push(logs.arr.get(i).departure.get(Calendar.MONTH) + 1);
                if (!isPresent(unique_months, logs.arr.get(i).departure.get(Calendar.MONTH) + 1)) {
                    unique_months.push(logs.arr.get(i).departure.get(Calendar.MONTH) + 1);
                }
            }
            int max_count = 0;
            int frequent_month = 0;
            for (int i = 0; i <= unique_months.top; i++) {
                int count = countElement(months_booked, unique_months.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_month = unique_months.arr.get(i);
                }
            }
            String month = "";
            switch (frequent_month) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            return(month);
        } else {
            return("");
        }
    }
    /* We have a static string function getFrequentBookingMonth() which returns the most frequent month Flights were booked in. What it does is 
     * check if the stack of Airplane (logs) is empty. If it is not empty, it makes new Stack<Integer> booking_months and unique_months. Stack<Integer>
     * as we want a Stack of the month numbers 1/2/3/4../12. 
     * Now we loop through seats of every airplane in the logs and push the booking_time month + 1 (Reminder: Calendar.MONTH returns a month number from 0 to 11) into booking_months.
     * If the booking month in question is not present in unique_months, we push it into unique_months. 
     * 
     * We now make an integer variable max_count and initiate it to 0 and a frequent_month = 0 (represents no month yet).
     * We will now loop through the unique_months and count the number of times a month in unique_months appears in months_booked. If this count is 
     * greater than the max_count, we equate max_count to this count and equate the frequent_month to this month. 
     * 
     * At last, we get the max_count to be the maximum number of times a month appears.
     * We get frequent_month to be the month that appears the month.
     * 
     * We will then initiate a "month" variable to be set to blank string. 
     * We will use a switch statement on the frequent_month. If the frequent_month is 1, we set "month" to January.. frequent_month 2, we set "month" to February.. etc
     * We will then return the month variable at last. This is the month airplanes were frequently booked in.
     * 
     * If the logs stack is empty, it returns an empty string.
    */
    static String getFrequentBookingMonth() {
        if (logs.top != -1) {
            Stack<Integer> booking_months = new Stack<>();
            Stack<Integer> unique_months = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_months.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1);
                        if (!isPresent(unique_months, logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1)) {
                            unique_months.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.MONTH) + 1);
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_month = 0;
            for (int i = 0; i <= unique_months.top; i++) {
                int count = countElement(booking_months, unique_months.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_month = unique_months.arr.get(i);
                }
            }
            String month = "";
            switch (frequent_month) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            return(month);
        } else {
            return("");
        }
    }

    /* getFrequentBookingYear works the same as getFrequentBookingMonth but here we get Calendar.YEAR without adding any 1. This is because we get the
     * year as it is, e.g if it is the year 2024, we get the year 2024. There's no changes needed.
     */
    static String getFrequentBookingYear() {
        if (logs.top != -1) {
            Stack<Integer> booking_years = new Stack<>();
            Stack<Integer> unique_years = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_years.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR));
                        if (!isPresent(unique_years, logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR))) {
                            unique_years.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.YEAR));
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_year = 0;
            for (int i = 0; i <= unique_years.top; i++) {
                int count = countElement(booking_years, unique_years.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_year = unique_years.arr.get(i);
                }
            }
            return(frequent_year + "");
        } else {
            return("");
        }
    }

    /* getFrequentBookingYear works the same as getFrequentBookingMonth but here we don't subtract Calendar.DAY_OF_WEEK by 1. If it is Monday, we get Day 2
    Day 3 - Tuesday, etc. We leave it unchanged
     */
    static String getFrequentBookingDay() {
        if (logs.top != -1) {
            Stack<Integer> booking_days = new Stack<>();
            Stack<Integer> unique_days = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                for (int j = 0; j < logs.arr.get(i).seats.length; j++) {
                    if (logs.arr.get(i).seats[j].booked.getValue() == true) {
                        booking_days.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK));
                        if (!isPresent(unique_days, logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK))) {
                            unique_days.push(logs.arr.get(i).seats[j].booking_time.get(Calendar.DAY_OF_WEEK));
                        }
                    }
                }
            }
            int max_count = 0;
            int frequent_days = 0;
            for (int i = 0; i <= unique_days.top; i++) {
                int count = countElement(booking_days, unique_days.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_days = unique_days.arr.get(i);
                }
            }
            String day = "";
            switch (frequent_days) {
                case 1:
                    day = "Sunday";
                    break;
                case 2:
                    day = "Monday";
                    break;
                case 3:
                    day = "Tuesday";
                    break;
                case 4:
                    day = "Wednesday";
                    break;
                case 5:
                    day = "Thursday";
                    break;
                case 6:
                    day = "Friday";
                    break;
                case 7:
                    day = "Saturday";
                    break;
            }
            return(day);
        } else {
            return("");
        }
    }
    /* We have a static string function getFrequentDestination() which returns the most frequent destinations Flights were headed to. What it does is 
     * check if the stack of Airplane (logs) is empty. If it is not empty, it makes new Stack<String> destinations_booked and unique_destinations. Stack<String>
     * as we want a Stack of the destinations like Bangalore, Goa, Mumbai, etc. 
     * Now we loop through the logs and push the destination of the airplane to destinations_booked
     * If the destination in question is not present in unique_destinations, we push it into unique_destinations. 
     * 
     * We now make an integer variable max_count and initiate it to 0 and a frequent_destination = "" (represents no destination yet).
     * We will now loop through the unique_destinations and count the number of times a month in unique_destinations appears in destinations_booked. If this count is 
     * greater than the max_count, we equate max_count to this count and equate the frequent_destination to this month. 
     * 
     * At last, we get the max_count to be the maximum number of times a destination appears.
     * We get frequent_destination to be the destination that appears the month.
     * 
     * We will then return the frequent_destination.
     * If the logs stack is empty, it returns an empty string.
    */
    static String getFrequentDestination() {
        if (logs.top != -1) {
            Stack<String> destinations_booked = new Stack<>();
            Stack<String> unique_destinations = new Stack<>();
            for (int i = 0; i <= logs.top; i++) {
                destinations_booked.push(logs.arr.get(i).destination);
                if (!isPresent(unique_destinations, logs.arr.get(i).destination)) {
                    unique_destinations.push(logs.arr.get(i).destination);
                }
            }
            int max_count = 0;
            String frequent_destination = "";
            for (int i = 0; i <= unique_destinations.top; i++) {
                int count = countElement(destinations_booked, unique_destinations.arr.get(i));
                if (count > max_count) {
                    max_count = count;
                    frequent_destination = unique_destinations.arr.get(i);
                }
            }
            return(frequent_destination);
        } else {
            return("");
        }
    }
}

/* We will now make a Thread that calls the book(traveller) function. Everytime a booking request is made, a thread is made that calls .book(traveller)
 * Since this class Request is implementing a Runnable, we will link a Traveller object to it, a Seat object to it, a Thread linked to it and an
 * integer variable 'success' to it which represents if the booking was successful or not.
 * 
 * When we call the Request constructor passing it a traveller object and a seat object, we equate the traveller object of the class to this and 
 * the seat object of the class to the one passed. We will also initially set the success to 0 as no booking is made as of yet. We will also make a new
 * thread that uses the Request reference and the name of the thread being the name of the passenger.
 * 
 * What does this thread do? We write this implementation in the run function. Everytime a booking request is made, we call this Request constructor
 * which makes a thread. This thread calls the book(traveller) function of the seat class and we store the result of it in the success variable.
 * If the booking is successful, success is 1. If the booking is unsuccessful (Reminder: book() is a synchronized function. Only one thread can be successful when booking a seat)
 * the success variable is 0..
 */
class Request implements Runnable {
    Traveller traveller;
    Seat seatObj;
    Thread thread;
    int success;

    Request(Traveller traveller, Seat seatObj) {
        this.traveller = traveller;
        this.success = 0;
        this.seatObj = seatObj;
        this.thread = new Thread(this, traveller.getPassengerName());
        this.thread.start();
    }

    public void run() {
        this.success = seatObj.book(traveller);
    }
}

/* Schedule class again consists of elements that keeps a record of all the airplanes that are yet to depart. Every traveller, manager has access
 * to the SAME Schedule and thus we make every element in the Schedule class STATIC. Any change made to this schedule is reflected to every traveller and manager.
 */
/* It consists of the ObservableList scheduleList which consists of string representations of every Airplane yet to depart (Reminder: we override toString() of each Airplane)
 * Initially this is made empty as we haven't added any airplane as of yet (but we could hard code airplanes into it.)
 * It consists of the ObservableList schedule which consists of Airplane objects that are yet to depart. Initially it is made empty as we haven't added any airplane as of yet.
 * We then have a top static variable which represents the topmost index of scheduleList and schedule. (Remember: scheduleList consists of string representation of airplanes in schedule, they will be of same length)
 */
class Schedule {
    static ObservableList<String> scheduleList = FXCollections.observableArrayList();
    static ObservableList<Airplane> schedule = FXCollections.observableArrayList();
    static int top = -1;
    /* Here we hard-code some Airplane to the schedule from the start, as required. This is the BoeingC757, MH200 and A350 flights. We
     * make three traveller objects and link them to the 3 airplanes. We then book tickets under each Passenger for each flight to hard-code some
     * flights that were booked.
     */
    static {
        Traveller boeingPassenger = new Traveller("Boeing Passenger");
        Manager.addEntry("BoeingC757", "International", 6, 1000.0, "NYC", "Hong Kong", 30, 10, 10, 12, 2024, 30, 11, 10, 12, 2024);
        Airplane BoeingC757 = schedule.get(Manager.searchEntry("BoeingC757"));
        boeingPassenger.airplane = BoeingC757;
        boeingPassenger.bookSeats(BoeingC757.seats[0]);
        boeingPassenger.bookSeats(BoeingC757.seats[1]);
        boeingPassenger.bookSeats(BoeingC757.seats[2]);
        boeingPassenger.bookSeats(BoeingC757.seats[3]);
        boeingPassenger.bookSeats(BoeingC757.seats[4]);
        boeingPassenger.bookSeats(BoeingC757.seats[5]);
        Traveller MH200Passenger = new Traveller("MH200 Passenger");
        Manager.addEntry("MH200", "International", 20, 1500.0, "Sydney", "Delhi", 45, 11, 10, 11, 2024, 50, 11, 10, 11, 2024);
        Airplane MH200 = schedule.get(Manager.searchEntry("MH200"));
        MH200Passenger.airplane = MH200;
        MH200Passenger.bookSeats(MH200.seats[0]);
        MH200Passenger.bookSeats(MH200.seats[1]);
        Traveller A350Passenger = new Traveller("A350 Passenger");
        Manager.addEntry("A350", "Domestic", 15, 1000.0, "Delhi", "Bangalore", 05, 9, 7, 12, 2024, 30, 9, 7, 12, 2024);
        Airplane A350 = schedule.get(Manager.searchEntry("A350"));
        A350Passenger.airplane = A350;
        A350Passenger.bookSeats(A350.seats[0]);
        A350Passenger.bookSeats(A350.seats[1]);
        A350Passenger.bookSeats(A350.seats[2]);
    }
}

/* We have a class Traveller. In this program, every traveller is represented by an object. This traveller extends Schedule as we want the traveller
 * to have access to the schedule of Airplanes that are yet to depart so that the traveller can book tickets.
 * It consists of ObservableList of strings, seats_booked. It will contain information of every seat the traveller has booked and the cost of each seat.
 * We have made it an ObservableList so that we can display it as a ListView in the invoice section.
 * It consists of ObservableList of strings, addons_booked. It will contain information of every addon the traveller has purchased and the cost of each addon.
 * We have made it an ObservableList so that we can display it as a ListView in the invoice section.
 * 
 * It also contains a private attribute passenger_name representing the name of the passenger. We have made it private so that no other class has access to it.
 * No other class can modify this passenger_name.
 * 
 * We have an Airplane object associated to the traveller. Everytime a traveller tries booking an airplane, the airplane object of this traveller is linked to the airplane the traveller 
 * wants to book.
 * By default, the Airplane reference of the traveller is set to null as the traveller hasn't booked any ticket.
 * 
 * We have a totalCost double variable that will reflect the total amount spent by the traveller on seats + addons.
 */

class Traveller extends Schedule {
    ObservableList<String> seats_booked;
    ObservableList<String> addons_booked;
    private String passenger_name;
    Airplane airplane;
    double totalCost;

    /* We make a function getPassengerName that returns the name of the passenger. This is so other classes can get the name of the passenger.
     * Other classes can get the passenger_name but cannot modify it!
     */
    String getPassengerName() {
        return (passenger_name);
    }

    /* We make a constructor Traveller and pass the name of the passenger to it. We then link the passenger_name to this name. We initially
     * set the airplane to null as the traveller has not booked any tickets. seats_booked and addons_booked is set to empty lists as the traveller
     * has not booked any seats or addons respectively. The totalCost is set to 0 as the traveller has not booked anything.
     */

    Traveller(String passenger_name) {
        this.passenger_name = passenger_name;
        this.airplane = null;
        this.seats_booked = FXCollections.observableArrayList();
        this.addons_booked = FXCollections.observableArrayList();
        this.totalCost = 0;
    }

    /* We also have a bookSeats function which takes any seat object as parameter. Everytime a seat booking request is made by a traveller, 
     * a new thread is made by using the Request constructor (Reminder: Request implements Runnable). Each thread linked to the Request class calls
     * the synchronized book function. We access this thread using req.thread; 
     */

     /* Once the booking request is made, we WAIT for the thread to finish executing! We don't want to keep continuing with our program while the
      * booking request is still processing.. Hence we call requestThread.join(). This allows us to wait for the requestThread to stop executing 
      * so that we can proceed ahead with the program. Every .join() function calls an InterruptedException that must be handled as it is an unchecked
      * unchecked exception. Now we store the success of booking in the wasBookingSuccessful variable. If the wasBookingSuccessful is 1, it means
      * seat was successfully booked. Now what we do is increase the price of the seatPrice in Airplane by 10% and equate the price of each unbooked
      * seat in the airplane to the seatPrice of the airplane.
      */
    int bookSeats(Seat seatObj) {
        Request req = new Request(this, seatObj);
        Thread requestThread = req.thread;
        int wasBookingSuccessful = 0;
        try {
            requestThread.join();
            wasBookingSuccessful = req.success;
            if (wasBookingSuccessful == 1) {
                /* Reminder: We cannot .set() DoubleProperty in threads that are NOT Application thread of JavaFX. It is unsafe. 
                 * .set() for DoubleProperty or IntegerProperty etc, is a method reserved for JavaFX. Since we're trying to do this outside the 
                 * Application thread of JavaFX, we will do Platform.runLater() and create a Runnable reference in it which runs the code for us.
                 * This will execute the body of code in the run() statement in the Application thread of JavaFX, thus making it safe.
                 */

/* Traveller.this inside an Anonymous Inner class refers to the traveller object where the Anonymous inner class is made. */
                Platform.runLater(new Runnable() {
                    public void run() {
                       Traveller.this.airplane.seatPrice.set(Traveller.this.airplane.seatPrice.get() * 1.1);
                       for (int i = 0; i < Traveller.this.airplane.seats.length; i++) {
                            if (Traveller.this.airplane.seats[i].booked.get() == true) {
                                Traveller.this.airplane.seats[i].seatPrice = Traveller.this.airplane.seatPrice.get();
                            }
                       }
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return(wasBookingSuccessful);
    }
}


/* We have a class Manager that represents a manager. Every entry in this Manager is made static because we want only one manager to exist in the system.
The manager has the ability to search through a schedule for an airplane name, delete an airplane from the schedule, set any airplane from the schedule as departed
add an airplane into the schedule. It extends Schedule as it has access to the Schedule elements like the 
*/
class Manager extends Schedule {
    /* searchEntry is an integer function. It takes an airplane name as parameter. It then loops through the ObservableList schedule which is a
     * list of Airplane objects that are yet to depart. It checks if the lowercase of each schedule object is equal to the lowercase of the airplane 
     * name to be found. This is to remove dependency on the case sensitivity of the airplane name when searching for it.
     * If it is found, the index which was initially set to -1 before the loop is set to the index of the airplane found and the loop is broken.
     * The index is then returned at last. If it returns -1, the airplane is not found in the schedule. If it does not, the airplane is found in the
     * schedule. 
     */
    static int searchEntry(String airplane_name) {
        int index = -1;
        for (int i = 0; i <= top; i++) {
            if (schedule.get(i).name.toLowerCase().equals(airplane_name.toLowerCase())) {
                index = i;
                break;
            }
        }
        return (index);
    }

    /* deleteEntry is a boolean function that takes any airplane_name. First we initiate a success variable to false. We check if the schedule is empty
     * or not. If the schedule is not empty, we search for the airplane name in the schedule. If the airplane is found (index is not equal to -1), 
     * we remove the element of that index from the schedule observableList using the .remove(index) command of the ObservableList.
     * We also correspondingly remove the element of that index from Schedule.scheduleList (Reminder: it consists of string representation of the airplanes in schedule).
     * We then decrement the top-- to show that the top index is now one less. We then set the success variable to true.
     * We then return the success variable at last. If success is true, element has been deleted. If success is false, element has not been deleted either 
     * because the schedule is empty or the element was not found.
     */
    static boolean deleteEntry(String airplane_name) {
        boolean success = false;
        if (top != -1) {
            int index = searchEntry(airplane_name);
            if (index != -1) {
                schedule.remove(index);
                Schedule.scheduleList.remove(index);
                top--;
                success = true;
            }
        }
        return (success);
    }
    /* setDeparted is a boolean function that takes any airplane. First we initiate a success variable to false. We check if the schedule is empty or not.
     * If the schedule is not empty, we search for the airplane name in the schedule. If the airplane is found (index is not equal to -1), we make an
     * Airplane reference and set it to the found Airplane element in the schedule using its index and the .get(index) function of the ObservableList.
     * The departed of that Airplane reference is set to true to depict that the airplane has departed.
     * The airplane is then deleted from the schedule as it has departed. We will then push this airplane object to the Report.logs (Reminder: it consists of all airplanes
     * that have departed). We will also add this airplane object to the ObservableList Report.departedFlights using the .add(data) function of the ObservableList
     * (Reminder: we also have it as an ObservableList so that we can display it as a ListView in the JavaFX application thread)
     * We will check if the Airplane reference is fully booked, if it is fully booked, we add it to the Report.bookedDeparted ObservableList 
     * We then set the success variable to true
     * We then return the success variable at last.
     * If the success value returned by setDeparted is true, the depart process is successful and the airplane was departed.
     * If the success value returned by setDeparted is false, the depart process was unsuccessful either because the airplane was not found in the
     * schedule or if the schedule was empty.
     */
    static boolean setDeparted(String airplane_name) {
        boolean success = false;
        if (top != -1) {
            int index = searchEntry(airplane_name);
            if (index != -1) {
                Airplane reference = schedule.get(index);
                reference.departed = true;
                deleteEntry(airplane_name);
                Report.logs.push(reference);
                Report.departedFlights.add(reference);
                success = true;
            }
        }
        return (success);
    }
    /* addEntry is a function that is used by the manager to add entries to the schedule. It takes the following parameters */
    static void addEntry(String name, String type, int seat_capacity, double seatPrice, String origin,
            String destination, int arrival_minute, int arrival_hour, int arrival_day, int arrival_month,
            int arrival_year, int departure_minute, int departure_hour, int departure_day, int departure_month,
            int departure_year) {
        /* First we construct an Airplane object using the parameters provided. We then increment top by one and then add this airplane object
         * to the schedule ObservableList (Reminder: it is a an ObservableList of Airplane objects in the schedule)
         * We also correspondingly add the string representation of that airplane to the scheduleList ObservableList (Reminder: It is an ObservableList
         * of String in the schedule)
         */
        Airplane reference = new Airplane(name, type, seat_capacity, seatPrice, origin, destination, arrival_minute,
                arrival_hour, arrival_day, arrival_month, arrival_year, departure_minute, departure_hour, departure_day,
                departure_month, departure_year);
        top = top + 1;
        schedule.add(reference);
        Schedule.scheduleList.add(reference.toString());
    }
}
/* To demonstrate Concurrent requests, we make a new thread that represents another traveller. We implement Runnable.
 * We give the attribute traveller object which represents the traveller linked to the thread. We give the attribute Airplane object which represents
 * the airplane where traveller is booking seats at. We also have a Thread reference which represents the thread of the other traveller.
 */
class concurrentTraveller implements Runnable {
    private Traveller traveller;
    Airplane toBookAirplane;
    Thread travellerConsole;

    /* Everytime a thread is made and started, this function is executed. In the console, we can book tickets or seats in the perspective of the other
     * traveller. The traveller is booking tickets on the same airplane as the main traveller. If the airplane is already full booked, the console says
     * that the airplane is fully booked. Otherwise, you can enter the seat number to book from the console (in the perspective of other traveller).
     * You can keep entering the seat number until you enter -1 or until the airplane is fully booked.  
     */
    public void run() {
        System.out.println("Simulating multithreading in concurrent requests");
        @SuppressWarnings("resource")
        Scanner scannerObj = new Scanner(System.in);
        traveller = new Traveller("Traveller 2");
        traveller.airplane = toBookAirplane;
        System.out.println("Demo Traveller is now booking seats in airplane: " + traveller.airplane.name);
        if (traveller.airplane.seats.length == traveller.airplane.countBooked()) {
            System.out.print("Airplane is fully booked.");
        } else {
            do {
                System.out.print("Enter seat number to book (-1 to EXIT): ");
                int seatPos = scannerObj.nextInt();
                if (seatPos == -1) {
                    break;
                }
                scannerObj.nextLine();
                int success = traveller.bookSeats(traveller.airplane.seats[seatPos]);
                if (success == 1) {
                    System.out.println("Seat " + seatPos + " has been booked by Traveller 2");
                } else {
                    System.out.println("Seat " + seatPos + " has already been booked.");
                }
            } while (traveller.airplane.seats.length != traveller.airplane.countBooked());
        }
    }
    /* You can call the concurrentTraveller constructor and pass any airplane parameter. The airplane reference of the object is set to this parameter
     * A thread is made and started to represent the other traveller. 
    */
    concurrentTraveller(Airplane toBookAirplane) {
        this.toBookAirplane = toBookAirplane;
        travellerConsole = new Thread(this, "Traveller 2 Console");
        travellerConsole.start();
    }
}

/* This is the application thread where the stage, scenes will be made for the JavaFX application. */
public class Demo extends Application {
    private Traveller traveller;
    /* The Demo class also has the traveller object linked to it which represents the main traveller (YOU). */
    public static void main(String[] args) {
        /* The main function calls the static launch function in the Application class, this will now call the start() function of the Application class */
        launch(args);
    }

    public void start(Stage ps) {
        /* ps is the primary stage. It has the GridPane layout and a scene of the given dimensions 400 width 300 height.
         * The scene is the first scene to appear. It gives the ability to user to login as a Traveller or a Manager
         * 
         */
        ps.setTitle("Airline Reservation System");
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        Scene sc = new Scene(gp, 400, 300);
        Label lblWelcome = new Label("Welcome to Airline Reservation System");
        Label lblLogin = new Label("Login As:");
        Button btnUser = new Button("Traveller");
        Button btnAdmin = new Button("Manager");

        // When the Traveller button is pressed, the userWindow function is called. This creates a new stage for the Traveller which consists of a scene
        // with options to book tickets, check schedule, check schedule, logout etc.
        btnUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                userWindow();
            }
        });
        /* When the Manager button is pressed, the adminWindow function is called. This creates a new stage for the Manager which consists of a scene
         * with options to add entries to the schedule, check schedule, delete entries from the schedule, logout, depart flights, etc.
         */
        btnAdmin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                adminWindow();
            }
        });
        gp.add(lblWelcome, 0, 0);
        gp.add(lblLogin, 0, 1);
        GridPane.setHalignment(lblLogin, HPos.CENTER);
        gp.add(btnUser, 0, 2);
        GridPane.setHalignment(btnUser, HPos.CENTER);
        gp.add(btnAdmin, 0, 3);
        GridPane.setHalignment(btnAdmin, HPos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
        ps.setScene(sc);
        ps.show();
    }

    /* This is the Scene that will be set when the Delete Entry button is pressed in the Manager window. oldScene is the scene from where the button is 
     * pressed. This is needed so that we can make a button "Back" that will allow us to go back to the oldScene. primaryStage is the stage where the scene
     * will be set at.
     */
    public void deleteScene(Stage primaryStage, Scene oldScene) {
        /* We will use AnchorPane as positioning elements with AnchorPane seems much easier than GridPane or FlowPane (by preference).
         */
        AnchorPane apDelete = new AnchorPane();
        apDelete.setPrefSize(354, 273);

        Label titleLabel = new Label("Delete Flight");
        titleLabel.setLayoutX(14);
        titleLabel.setLayoutY(6);
        titleLabel.setFont(Font.font("System Bold", 16));
        
        /* We have a ListView JavaFX element which is assigned to the ObservableList<Airplane> Schedule.schedule
         * This makes it so that we can view the Airplane objects in the scrollable list in the Delete scene.
         * The ListView automatically displays the string representation of the Airplane object.
        */
        ListView<Airplane> listView = new ListView<>(Schedule.schedule);
        listView.setLayoutX(17);
        listView.setLayoutY(48);
        listView.setPrefSize(322, 112);

        Label scheduleLabel = new Label("Schedule");
        scheduleLabel.setLayoutX(17);
        scheduleLabel.setLayoutY(31);

        Label flightNameLabel = new Label("Flight Name");
        flightNameLabel.setLayoutX(17);
        flightNameLabel.setLayoutY(179);

        /*
         * flightNameField is a text field that allows us to enter the flight name to delete from the schedule.
         */
        TextField flightNameField = new TextField();
        flightNameField.setLayoutX(88);
        flightNameField.setLayoutY(175);
        flightNameField.setPrefSize(149, 25);
        
        /* placeholderLabel is a label that displays the status after the deletion process. It is made blank initially but as we press the 
         * Delete button, it updates to either "Successfully deleted" or "Not found".
         */

        Label placeholderLabel = new Label();
        placeholderLabel.setLayoutX(20);
        placeholderLabel.setLayoutY(237);
        placeholderLabel.setPrefSize(182, 17);
        placeholderLabel.setFont(Font.font(13));

        Button backButton = new Button("Back");
        backButton.setLayoutX(299);
        backButton.setLayoutY(234);

        Button deleteButton = new Button("Delete");
        deleteButton.setLayoutX(237);
        deleteButton.setLayoutY(234);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(175);
        updateButton.setLayoutY(234);

        /* apDelete.getChildren().addAll(elements) allows us to add elements to the AnchorPane layout. We can then add this AnchorPane layout
         * to the scene and make all these elements visible.
         */
        apDelete.getChildren().addAll(titleLabel, listView, scheduleLabel, flightNameLabel, flightNameField, placeholderLabel, backButton, deleteButton, updateButton);

        Scene scene = new Scene(apDelete);
        primaryStage.setScene(scene);
        /* We will then set the primaryStage passed in the parameter to this scene. */

        /* When we press the update Button, the listView is first set to an empty ObservableList. The listView is then set back to the Schedule.schedule 
         * ObservableList<Airplane> which will help update the listView and show us updated number of booked seats, updated elements, etc.
         * The flightNameField TextField is cleared so you are free to input a new airplane name. 
         */
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                listView.setItems(FXCollections.observableArrayList());
                listView.setItems(Schedule.schedule);
                flightNameField.clear();
            }
        });

        /* When the Back button is pressed, the scene of the primaryStage is set to the oldScene passed in the function */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });

        /* When the Delete button is pressed, the Manager.deleteEntry(flight_name) is called where flight_name is the flight name you input in the
         * text field. The result of this function is stored in a success variable. If the success is true, the airplane is successfully deleted and the
         * placeholderLabel is set to Successfully deleted. If the success is false, the airplane is not deleted and the placeholder label is set to "Not found."
         */
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                boolean success = Manager.deleteEntry(flightNameField.getText());
                if (success) {
                    placeholderLabel.setText("Successfully deleted.");
                } else {
                    placeholderLabel.setText("Not found.");
                }
            }
        });

    }

    /* This is the Scene that will be set when the Next button is pressed after the booking of seats is done. Dashboard is the scene of the main dashboard
     * provided which consists of all the buttons in the traveller stage.. This is needed so that we can press Purchase in the addon window, we go back to the
     * dashboard. primaryStage is the stage where the scene
     * will be set at.
     */
    public void addonScene(Stage primaryStage, Scene Dashboard) {
        AnchorPane apAddon = new AnchorPane();
        apAddon.setPrefSize(206, 243);

        Label titleLabel = new Label("Add-ons available");
        titleLabel.setLayoutX(14);
        titleLabel.setLayoutY(6);
        titleLabel.setFont(new Font("System Bold", 16));

        /* These are checkboxes which allows us to select what addons to buy. */
        CheckBox wifiCheckBox = new CheckBox("Wi-Fi access (₹3000)");
        wifiCheckBox.setLayoutX(14);
        wifiCheckBox.setLayoutY(37);

        CheckBox loungeCheckBox = new CheckBox("Lounge access (₹5000)");
        loungeCheckBox.setLayoutX(14);
        loungeCheckBox.setLayoutY(65);

        CheckBox cateringCheckBox = new CheckBox("On-demand catering (₹5000)");
        cateringCheckBox.setLayoutX(14);
        cateringCheckBox.setLayoutY(95);

        CheckBox luggageCheckBox = new CheckBox("Extra luggage space (₹5000)");
        luggageCheckBox.setLayoutX(14);
        luggageCheckBox.setLayoutY(123);

        Button purchaseButton = new Button("Purchase");
        purchaseButton.setLayoutX(122);
        purchaseButton.setLayoutY(204);
        purchaseButton.setPrefSize(65, 25);

        Label costLabel = new Label("Total Cost:");
        costLabel.setLayoutX(14);
        costLabel.setLayoutY(159);
        costLabel.setFont(new Font("System Bold", 12));

        /* totalCostLabel will be initially set to the totalCost of the traveller (totalCost accounting for all the amount spent by traveller on seats)
         * If we set it to traveller.totalCost alone, the number shown in the totalCostLabel would have 5-6 decimal places which would be inconvenient.
         * If we do String.format("%.2f", traveller.totalCost), we are formatting the totalCost of traveller to show only 2 decimal places.
         */
        Label totalCostLabel = new Label(String.format("%.2f", traveller.totalCost));
        totalCostLabel.setLayoutX(85);
        totalCostLabel.setLayoutY(159);

        apAddon.getChildren().addAll(titleLabel, wifiCheckBox, loungeCheckBox, cateringCheckBox, luggageCheckBox,
                                  purchaseButton, costLabel, totalCostLabel);

        Scene scene = new Scene(apAddon);
        primaryStage.setScene(scene);

        /* If the wifiCheckBox is clicked, an event is generated. This event is generated if the checkBox is selected or deselected.
         * If the checkbox is selected, we get the current totalCost by parsing the totalCostLabel's text (.getText()) into a Double by using the below syntax
         * We will then add the obtained value by 3000 and then set the totalCostLabel to this value and format it to 2 decimal places.
         * Remember, the totalCost of the traveller is not updated. We will update it when the Purchase button is pressed.'
         * 
         * If the checkbox is not selected, we get the current totalCost by parsing the totalCostLabel's text (.getText()) into a Double by using theb elow syntax
         * We will then subtract the obtained value by 3000 and then set the totalCostLabel to this value and format it to 2 decimal places. 
         * Remember, the totalCost of the traveller is not updated. We will update it when the Purchase button is pressed.  
         */

        wifiCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (wifiCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 3000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 3000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });

        /*
         * The same is done for loungeCheckBox but we add / subtract 5000.0
         */
        loungeCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (loungeCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });

        /*
         * The same is done for luggageCheckBox but we add / subtract 5000.0
         */
        luggageCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (luggageCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });

        /*
         * The same is done for cateringCheckBox but we add / subtract 5000.0
         */

        cateringCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (cateringCheckBox.isSelected()) {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue + 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                } else {
                    Double obtainValue = Double.parseDouble(totalCostLabel.getText());
                    obtainValue = obtainValue - 5000.0;
                    totalCostLabel.setText(String.format("%.2f", obtainValue));
                }
            }
        });
        /* When we press the purchaseButton, we check if each checkBox is selected individually. If wifiCheckBox is selected, we 
         * add "ADD_ON PURCHASED: WI-FI ACCESS | COST: 3000 | BOOKED UNDER: " + traveller's name to the addons_booked ObservableList in the traveller
         * class. 
         * We then add 3000.0 to the traveller's totalCost
         * 
         * The same is done for loungeCheckBox, cateringCheckBox and luggageCheckBox but using the amount 5000.0 instead of 3000.0
         * We will then set the scene of the primaryStage back to the Dashboard
         */
        purchaseButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (wifiCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: WI-FI ACCESS | COST: 3000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 3000.0;
                }

                if (loungeCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: LOUNGE ACCESS | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }

                if (cateringCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: ON-DEMAND ACCESS | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }

                if (luggageCheckBox.isSelected()) {
                    traveller.addons_booked.add("ADD-ON PURCHASED: EXTRA LUGGAGE SPACE | COST: 5000 | BOOKED UNDER: " + traveller.getPassengerName());
                    traveller.totalCost = traveller.totalCost + 5000.0;
                }
                primaryStage.setScene(Dashboard);
            }
        });
    }

    /* This is the Scene that will be set when the Report button is pressed in the Manager dashboard. oldScene is the scene from where Report
     * button is pressed (the dashboard).. This is needed so that we can press Back in the report window, we go back to the
     * old scene. primaryStage is the stage where the scene will be set at.
     */
    public void reportScene(Stage primaryStage, Scene oldScene) {
        Label reportLabel = new Label("Report");
        reportLabel.setLayoutX(14.0);
        reportLabel.setLayoutY(6.0);
        reportLabel.setFont(new Font("System Bold", 17.0));

        Label departedFlightsLabel = new Label("Departed Flights");
        departedFlightsLabel.setLayoutX(14.0);
        departedFlightsLabel.setLayoutY(32.0);

        Label frequentDestLabel = new Label("Frequent Destination:");
        frequentDestLabel.setLayoutX(14.0);
        frequentDestLabel.setLayoutY(182.0);

        Label frequentDepPeriodLabel = new Label("Frequent Departure Month:");
        frequentDepPeriodLabel.setLayoutX(14.0);
        frequentDepPeriodLabel.setLayoutY(199.0);

        Label frequentBookPeriodLabel = new Label("Frequent Booking Period:");
        frequentBookPeriodLabel.setLayoutX(14.0);
        frequentBookPeriodLabel.setLayoutY(216.0);

        Label fullyBookedFlightsLabel = new Label("Fully Booked Flights");
        fullyBookedFlightsLabel.setLayoutX(14.0);
        fullyBookedFlightsLabel.setLayoutY(254.0);

        /* We have a ListView JavaFX element which is assigned to the ObservableList<Airplane> Report.departedFlights
         * This makes it so that we can view the Airplane objects in the scrollable list in the Report scene.
         * Each cell in the ListView automatically displays the string representation of the Airplane object
        */
        ListView<Airplane> departedFlightsList = new ListView<>(Report.departedFlights);
        departedFlightsList.setLayoutX(14.0);
        departedFlightsList.setLayoutY(51.0);
        departedFlightsList.setPrefHeight(124.0);
        departedFlightsList.setPrefWidth(470.0);

        /* We have a ListView JavaFX element that is assigned to an empty list. We will later assign it to a FilteredList that will filter only
         * the fully booked flights from the Report.departedFlights
         */
        ListView<Airplane> fullyBookedFlightsList = new ListView<>();
        fullyBookedFlightsList.setLayoutX(14.0);
        fullyBookedFlightsList.setLayoutY(271.0);
        fullyBookedFlightsList.setPrefHeight(101.0);
        fullyBookedFlightsList.setPrefWidth(470.0);

        /* We are making a FilteredList<Airplane> from the original ObservableList<Airplane> Report.departedFlights. It consists of only those 
         * Airplane objects that return true when (i.countBooked() == i.seats.length) where i is any general Airplane object in Report.departedFlights
         * Thus every fully booked flight satisfies this condition and is displayed in the FilteredList.
         */
        FilteredList<Airplane> filteredListRef = new FilteredList<>(Report.departedFlights, i -> {
            return(i.countBooked() == i.seats.length);
        });

        fullyBookedFlightsList.setItems(filteredListRef);
        /* We then set this fullyBookedFlightsList to the filteredListRef */

        /* We will then make blank labels next to Frequent Destination, Frequent Departure Period, Frequent Booking Period
         * These labels will update when we press the Update button. They will display the frequent destination, frequent departure month,
         * frequent booking month from the list of departed flights.
         */
        Label frequentDestData = new Label();
        frequentDestData.setLayoutX(135.0);
        frequentDestData.setLayoutY(182.0);

        Label frequentDepPeriodData = new Label();
        frequentDepPeriodData.setLayoutX(163.0);
        frequentDepPeriodData.setLayoutY(199.0);

        Label frequentBookPeriodData = new Label();
        frequentBookPeriodData.setLayoutX(158.0);
        frequentBookPeriodData.setLayoutY(216.0);

        Button backButton = new Button("Back");
        backButton.setLayoutX(370.0);
        backButton.setLayoutY(187.0);
        backButton.setPrefHeight(25.0);
        backButton.setPrefWidth(48.0);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(426.0);
        updateButton.setLayoutY(187.0);

        /* When the back button is clicked, we go back to the oldScene */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });

        /* When the updateButton is pressed. We set the departedFlightsList to an empty list and then set it back to the Report.departedFlights
         * Any changes in Report.departedFlights will then be reflected (like number of booked seats, etc.)
         * The frequentDepPeriodData label also will be updated with the frequent departure period, same with frequentBookPeriodData and frequentDestData
         */
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                departedFlightsList.setItems(FXCollections.observableArrayList());
                departedFlightsList.setItems(Report.departedFlights);
                frequentDepPeriodData.setText(Report.getFrequentDeparturePeriod());
                frequentBookPeriodData.setText(Report.getFrequentBookingDay() + " | " + Report.getFrequentBookingMonth() + " | " + Report.getFrequentBookingYear());
                frequentDestData.setText(Report.getFrequentDestination());
            }
        });

        AnchorPane apReport = new AnchorPane();
        apReport.getChildren().addAll(
                reportLabel, departedFlightsLabel, frequentDestLabel, frequentDepPeriodLabel,
                frequentBookPeriodLabel, fullyBookedFlightsLabel, departedFlightsList, fullyBookedFlightsList,
                frequentDestData, frequentDepPeriodData, frequentBookPeriodData, backButton, updateButton
        );

        Scene scene = new Scene(apReport, 495, 387);
        primaryStage.setScene(scene);
    }

    /* This is the Scene that will be set when the Invoice button is pressed in the Traveller dashboard. oldScene is the scene from where Report
     * button is pressed (the dashboard).. This is needed so that we can press Back in the report window, we go back to the
     * old scene. primaryStage is the stage where the scene will be set at.
     */
    public void invoiceScene(Stage travellerStage, Scene oldScene) {
        AnchorPane apInvoice = new AnchorPane();
        apInvoice.setPrefSize(408, 232);

        Label invoiceLabel = new Label("Invoice");
        invoiceLabel.setLayoutX(14);
        invoiceLabel.setLayoutY(7);
        invoiceLabel.setFont(Font.font("System Bold", 19));

        Label seatsBookedLabel = new Label("Seats Booked");
        seatsBookedLabel.setLayoutX(14);
        seatsBookedLabel.setLayoutY(35);

        Label addonsPurchasedLabel = new Label("Addons purchased");
        addonsPurchasedLabel.setLayoutX(14);
        addonsPurchasedLabel.setLayoutY(112);

        Label totalCostLabel = new Label("TOTAL COST:");
        totalCostLabel.setLayoutX(14);
        totalCostLabel.setLayoutY(202);
        totalCostLabel.setPrefSize(79, 17);
        totalCostLabel.setFont(Font.font("System Bold", 12));

        /* This is a blank label next to totalCostLabel which will be updated when we press the Update button. It will display the totalCost attribute
         * of the traveller.
         */
        Label totalAmountLabel = new Label();
        totalAmountLabel.setLayoutX(99);
        totalAmountLabel.setLayoutY(202);

        /* We will make a ListView<String> seatsListView. This will be set to the seats_booked ObservableList<String> of the traveller. 
        */
        ListView<String> seatsListView = new ListView<>(traveller.seats_booked);
        seatsListView.setLayoutX(14);
        seatsListView.setLayoutY(52);
        seatsListView.setPrefSize(373, 53);
        /* We will make a ListView<String> addonsListView. This will be set to the addons_booked ObservableList<String> of the traveller.
        */
        ListView<String> addonsListView = new ListView<>(traveller.addons_booked);
        addonsListView.setLayoutX(14);
        addonsListView.setLayoutY(135);
        addonsListView.setPrefSize(373, 53);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(335);
        updateButton.setLayoutY(198);

        Button backButton = new Button("Back");
        backButton.setLayoutX(288);
        backButton.setLayoutY(198);

        apInvoice.getChildren().addAll(
            invoiceLabel, seatsBookedLabel, seatsListView,
            addonsPurchasedLabel, addonsListView,
            totalCostLabel, totalAmountLabel,
            updateButton, backButton
        );

        Scene scene = new Scene(apInvoice);
        travellerStage.setScene(scene);

        /* When we press the Back button, the travellerStage is set to the old scene. We are essentially going back to the dashboard. */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                travellerStage.setScene(oldScene);
            }
        });

        /* When the update button is pressed, the totalAmountLabel is updated with the totalCost of the traveller. We are formatting it to 
         * 2 decimal places only.
         */
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                totalAmountLabel.setText("₹" + String.format("%.2f", traveller.totalCost) + "");
            }
        });
    }

    /* This is the Scene that will be set when the Schedule button is pressed in the Manager or Traveller dashboard. oldScene is the scene from where Schedule button
     * button is pressed (the dashboard).. This is needed so that we can press Back in the Schedule window, we go back to the
     * old scene. primaryStage is the stage where the scene will be set at.
     */
    public void scheduleScene(Stage arbitraryStage, Scene oldScene) {
        AnchorPane apSchedule = new AnchorPane();
        apSchedule.setPrefHeight(358.0);
        apSchedule.setPrefWidth(679.0);

        /* We will make a ListView<Airplane> mainListView. This will be set to the Schedule.schedule ObservableList<Airplane>. Each cell in this ListView
        will automatically display the string form of the Airplane object.
        */
        ListView<Airplane> mainListView = new ListView<>(Schedule.schedule);
        mainListView.setLayoutX(23.0);
        mainListView.setLayoutY(54.0);
        mainListView.setPrefHeight(268.0);
        mainListView.setPrefWidth(317.0);
        
        /* We will make a ListView<Airplane> secondaryListView. This will be set to an empty list. We will make a FilteredList<Airplane> later that
        will contain a list of Airplane objects that have been filtered from the Schedule.schedule list using the searches the user has input. 
        We will then set this secondaryListView to the FilteredList. 
        */
        ListView<Airplane> secondaryListView = new ListView<>();
        secondaryListView.setLayoutX(362.0);
        secondaryListView.setLayoutY(222.0);
        secondaryListView.setPrefHeight(95.0);
        secondaryListView.setPrefWidth(282.0);

        Label titleLabel = new Label("Schedule");
        titleLabel.setLayoutX(306.0);
        titleLabel.setLayoutY(6.0);
        titleLabel.setPrefHeight(32.0);
        titleLabel.setPrefWidth(92.0);
        titleLabel.setFont(Font.font("System Bold", 21.0));

        Label searchByLabel = new Label("Search by:");
        searchByLabel.setLayoutX(495.0);
        searchByLabel.setLayoutY(46.0);

        Label originLabel = new Label("Origin");
        originLabel.setLayoutX(413.0);
        originLabel.setLayoutY(72.0);
        originLabel.setPrefHeight(17.0);
        originLabel.setPrefWidth(40.0);

        Label destinationLabel = new Label("Destination");
        destinationLabel.setLayoutX(390.0);
        destinationLabel.setLayoutY(101.0);
        destinationLabel.setPrefHeight(17.0);
        destinationLabel.setPrefWidth(64.0);

        Label fromTimeLabel = new Label("From Time");
        fromTimeLabel.setLayoutX(389.0);
        fromTimeLabel.setLayoutY(128.0);
        fromTimeLabel.setPrefSize(67.0, 17.0);

        Label tillTimeLabel = new Label("Till Time");
        tillTimeLabel.setLayoutX(399.0);
        tillTimeLabel.setLayoutY(157.0);
        tillTimeLabel.setPrefSize(48.0, 17.0);

        // TextField to enter the From location to match Airplanes that have the same From location.
        TextField originTextField = new TextField();
        originTextField.setLayoutX(462.0);
        originTextField.setLayoutY(68.0);
        originTextField.setPrefHeight(22.0);
        originTextField.setPrefWidth(137.0);

        // TextField to enter the To location to match Airplanes that have the same To location.
        TextField destinationTextField = new TextField();
        destinationTextField.setLayoutX(462.0);
        destinationTextField.setLayoutY(97.0);
        destinationTextField.setPrefHeight(22.0);
        destinationTextField.setPrefWidth(137.0);

        /* TextFields to enter From Time and Till Time. This will be used to search for Airplanes that arrive between From Time and Till Time */

        TextField fromTimeTextField = new TextField();
        fromTimeTextField.setLayoutX(462.0);
        fromTimeTextField.setLayoutY(124.0);
        fromTimeTextField.setPrefSize(137.0, 22.0);

        TextField tillTimeTextField = new TextField();
        tillTimeTextField.setLayoutX(462.0);
        tillTimeTextField.setLayoutY(153.0);
        tillTimeTextField.setPrefSize(137.0, 22.0);

        Button searchButton = new Button("Search");
        searchButton.setLayoutX(462.0);
        searchButton.setLayoutY(188.0);
        searchButton.setMnemonicParsing(false);

        Button backButton = new Button("Back");
        backButton.setLayoutX(515.0); 
        backButton.setLayoutY(188.0);
        backButton.setMnemonicParsing(false);

        Button clearButton = new Button("Clear");
        clearButton.setLayoutX(556.0);
        clearButton.setLayoutY(188.0);
        clearButton.setMnemonicParsing(false);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(590.0); 
        updateButton.setLayoutY(325.0);
        updateButton.setMnemonicParsing(false);

        apSchedule.getChildren().addAll(
            mainListView, titleLabel, searchByLabel, originLabel, destinationLabel,
            fromTimeLabel, tillTimeLabel, originTextField, destinationTextField,
            fromTimeTextField, tillTimeTextField, searchButton, clearButton, secondaryListView, backButton, updateButton
        );

        Scene scSchedule = new Scene(apSchedule);

        /* Update Button when pressed clears all the TextField and also sets the mainListView to an empty list and then back to the Schedule.schedule list
         * to account for any changes.
         * Now you can put fresh inputs to search through the Schedule of updated airplanes.
         */
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                mainListView.setItems(FXCollections.observableArrayList());
                mainListView.setItems(Schedule.schedule);
                fromTimeTextField.clear();
                tillTimeTextField.clear();
                originTextField.clear();
                destinationTextField.clear();
                secondaryListView.setItems(FXCollections.observableArrayList());
            }
        });

        /* Clear Button when pressed clears all the TextFields and also clears the secondaryListView so all searches are removed from display. This
         * gives a fresh start to the user.
         */
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                originTextField.clear();
                destinationTextField.clear();
                fromTimeTextField.clear();
                tillTimeTextField.clear();
                secondaryListView.setItems(FXCollections.observableArrayList());
            }
        });

        /* Back Button when pressed allows you to go back to the Manager/Admin dashboard. */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                arbitraryStage.setScene(oldScene);
            }
        });

        /* Search Button when pressed first clears the secondaryListView of any previous searches. It does this by first setting it to an empty list.
         * Then, a FilteredList is made that filters through the Schedule.schedule ObservableList which contains Airplane objects of airplanes yet to depart
         * It will then start checking if the String representation of the Airplane objects contain the From location input, To location input.
         * It will also pull the substring of the String representation containing the Arrival time of the Airplane object. It will then convert this 
         * string Arrival time to HH:mm format using SimpleDateFormat parsing. It will then check if the Airplane object's arrival time LIES BETWEEN
         * the From Time and Till Time. 
         * 
         * All of these checks are stored in separate variables. boolean originMatch contains the boolean if the string representation of the airplane 
         * contains the From location. boolean destinationMatch is similar but for the To location. boolean timeMatch contains the boolean if the 
         * airplane object's arrival time lies between the From Time and Till Time input by the user.
         * 
         * If there's no parsing error (The user inputs the time in correct format..), the FilteredList contains only those Airplane objects whose 
         * originMatch == true, destinationMatch == true and timeMatch == true
         * 
         * If there's a parsing error (The user inputs the time incorrect format..), the FilteredList contains only those Airplane objects 
         * whose originMatch == true and destinationMatch == true. It ignores the timeMatch.
         * 
         * After the FilteredList is made, the secondaryListView is set to this FilteredList
         */
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                secondaryListView.setItems(FXCollections.observableArrayList());
                FilteredList<Airplane> filteredListRef = new FilteredList<>(Schedule.schedule, i -> {
                    boolean originMatch = i.toString().contains("FROM: " + originTextField.getText());
                    boolean destinationMatch = i.toString().contains("TO: " + destinationTextField.getText());
                    SimpleDateFormat hhmm = new SimpleDateFormat("HH:mm");
                    try {
                        Date fromTime = hhmm.parse(fromTimeTextField.getText());
                        Date tillTime = hhmm.parse(tillTimeTextField.getText());
                        int indexOfArrivalTime = i.toString().indexOf("ARRIVAL TIME: ") + "ARRIVAL TIME: ".length();
                        Date generalArrivalTime = hhmm.parse(i.toString().substring(indexOfArrivalTime, indexOfArrivalTime + 6));
                        boolean timeMatch = generalArrivalTime.after(fromTime) && generalArrivalTime.before(tillTime);
                        return(originMatch && destinationMatch && timeMatch);
                    } catch (ParseException e) {
                        return (originMatch && destinationMatch);
                    }

                }
                );
                secondaryListView.setItems(filteredListRef);
            }
        });

        arbitraryStage.setScene(scSchedule);
    }

    /* The bookPrompt is the scene that opens up after you press the Book button in the Traveller dashboard. It shows the schedule of airplanes
     * that have yet to depart. There's also a TextField that will allow you to enter the name of the flight to book seats in.
     */
    public void bookPrompt(Stage primaryStage, Scene oldScene) {
        AnchorPane apBook = new AnchorPane();
        apBook.setPrefSize(496, 259);

        // This is a ListView that will show the schedule of Airplane objects. It is linked to Schedule.schedule ObservableList<Airplane>
        // The ListView cells automatically display the string representation of the Airplane object. There's no need to set each cell.
        ListView<Airplane> listView = new ListView<>(Schedule.schedule);
        listView.setLayoutX(20);
        listView.setLayoutY(31);
        listView.setPrefSize(458, 140);

        Label scheduleLabel = new Label("Schedule");
        scheduleLabel.setLayoutX(20);
        scheduleLabel.setLayoutY(14);

        Label flightNameLabel = new Label("Flight Name:");
        flightNameLabel.setLayoutX(20);
        flightNameLabel.setLayoutY(179);

        /* TextField to enter the name of the flight. */
        TextField flightNameField = new TextField();
        flightNameField.setLayoutX(20);
        flightNameField.setLayoutY(196);

        /* This is an empty label that will display the status of the booking. If you entered a wrong flight name, it says Flight was not found.
         * If you enter a Flight that is fully booked, it will display an appropriate message. If you enter a flight that hasn't been fully booked, you
         * will be led to the bookScene where the main booking of seats take place.
         */
        Label statusLabel = new Label();
        statusLabel.setLayoutX(184);
        statusLabel.setLayoutY(200);
        statusLabel.setPrefSize(167, 17);

        Button updateButton = new Button("Update");
        updateButton.setLayoutX(327);
        updateButton.setLayoutY(221);
        
        Button backButton = new Button("Back");
        backButton.setLayoutX(437);
        backButton.setLayoutY(221);

        /* When the back button is pressed, you are going back to the old scene. In this case, it is the Traveller dashboard. */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            }
        });

        /* When the update button is pressed, the listView is set to an empty list first and then back to the ObservableList<Airplane> Schedule.schedule
         * so any change made to the Schedule.schedule previously is reflected on the ListView (like the number of booked seats, etc).
         */
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                listView.setItems(FXCollections.observableArrayList());
                listView.setItems(Schedule.schedule);
            }
        });
    
        Button bookButton = new Button("Book");
        bookButton.setLayoutX(387);
        bookButton.setLayoutY(221);

        apBook.getChildren().addAll(listView, scheduleLabel, flightNameLabel, flightNameField, statusLabel, backButton, bookButton, updateButton);
        Scene scene = new Scene(apBook);
        primaryStage.setScene(scene);

        /* When the book button is pressed, it gets the Flight name from the text field and searches for the Flight in the Schedule.
         * If the flight was not found (index == -1), the appropriate message "Flight was not found." is set in the Status Label.
         * If the flight was found, we make an Airplane reference foundAirplane and then set it to the Airplane object in the Schedule using its index (.get(index) function
         * of ObservableList).
         * If the Airplane is fully booked, the appropriate message is displayed "Flight is fully booked." in the status label.
         * If the Airplane is not fully booked, the traveller's airplane attribute is set to the foundAirplane. The bookScene() function is then called
         * opening up the bookScene where all the booking takes place. Here we pass three parameters. primaryStage, scene and oldScene. The reason will be mentioned further
         */
        bookButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                int index = Manager.searchEntry(flightNameField.getText());
                if (index == -1) {
                    statusLabel.setText("Flight was not found.");
                } else {
                    Airplane foundAirplane = Schedule.schedule.get(index);
                    if (foundAirplane.countBooked() == foundAirplane.seats.length) {
                        statusLabel.setText("Flight is fully booked.");
                    } else {
                        traveller.airplane = foundAirplane;
                        bookScene(primaryStage, scene, oldScene);
                    }
                }
            }  
        });
    }

    /* bookScene is a function with three parameters - primaryStage, oldScene and Dashboard. 
     * primaryStage is the stage where we want the scene to be set in. oldScene consisting of the scene from where Book button is pressed (the scene in bookPrompt in this case)
     * Dashboard is the scene from where Book button is pressed IN THE TRAVELLER DASHBOARD. 
     */
    public void bookScene(Stage primaryStage, Scene oldScene, Scene Dashboard) {
        AnchorPane apBook = new AnchorPane();
        apBook.setPrefSize(229, 268);
    
        Label titleLabel = new Label("Seats");
        titleLabel.setFont(Font.font("System Bold", 20));
        titleLabel.setLayoutX(80);
        titleLabel.setLayoutY(14);
    
        Label flightNameLabel = new Label("Flight Name:");
        flightNameLabel.setLayoutX(15);
        flightNameLabel.setLayoutY(52);

        /* flightLabel is set to the name of the Airplane which the traveller has selected. It is displayed at the top of the window. */
        Label flightLabel = new Label(traveller.airplane.name);
        flightLabel.setLayoutX(88);
        flightLabel.setLayoutY(52);
        flightLabel.setPrefSize(123, 17);

        /* This is an array of ToggleButton which consist of all the buttons that represent the seats in the Airplane. The number of ToggleButtons
        in this case is equal to the number of airplane seats.
        */
        ToggleButton[] seatButtons = new ToggleButton[traveller.airplane.seats.length];

        /* This is for positioning JavaFX elements below the ToggleButtons, don't worry about it. */
        int baseY = 76 + (((seatButtons.length - 1) / 5) * 35);

        Label priceLabel = new Label("Price per Seat:");
        priceLabel.setLayoutX(16);
        priceLabel.setLayoutY(baseY+30);

        Label totalCostLabel = new Label("Total Cost:");
        totalCostLabel.setLayoutX(16);
        totalCostLabel.setLayoutY(baseY+47);

        /* This is a Blank label that will reflect the totalCost spent by the traveller on the seats. Initially it will be blank because the traveller
         * hasn't booked any ticket but we will update it when the traveller purchases seats.
         */
        Label totalCostValueLabel = new Label();
        totalCostValueLabel.setLayoutX(78);
        totalCostValueLabel.setLayoutY(baseY+47);
        totalCostValueLabel.setPrefSize(134, 17);

        Label statusLabel = new Label("Status:");
        statusLabel.setLayoutX(17);
        statusLabel.setLayoutY(baseY+64);

        /* This is a Blank label that will reflect the status of booking. If the seats are all booked, it automatically displays that the airplane is fully
         * booked. If the seat was successfully booked, it displays the message that the seat was booked.
         */
        Label statusValueLabel = new Label();
        statusValueLabel.setLayoutX(58);
        statusValueLabel.setLayoutY(baseY+64);
        statusValueLabel.setPrefSize(154, 17);

        /* This is a Label that will reflect the price of each seat in the airplane. It was started as a blank label but we will give it a value later.
         * This Label updates its text everytime a seat is booked. Everytime a seat is booked, the price of seat in the airplane increases by 10%
         * and thus the priceValueLabel is updated.
         */
        Label priceValueLabel = new Label();
        priceValueLabel.setLayoutX(101);
        priceValueLabel.setLayoutY(baseY+30);
        priceValueLabel.setPrefSize(113, 17);

        Button backButton = new Button("Back");
        backButton.setLayoutX(15);
        backButton.setLayoutY(baseY + 85);

        Button nextButton = new Button("Next");
        nextButton.setLayoutX(171);
        nextButton.setLayoutY(baseY + 85);

        /* During booking, we will start a concurrentTraveller() thread to demonstrate multithreading. */
        new concurrentTraveller(traveller.airplane); // Connect another traveller to the airplane
        
        /* Reminder: We made the seatPrice attribute in the Airplane object to a DoubleProperty. This is so that we can bind the text Label to the 
         * DoubleProperty. Everytime the DoubleProperty changes, the price Label changes. We can't do this with normal Double.
         */

         /* Just like DoubleProperty, IntegerProperty were made so that we can bind to these classes, the Text Label also has a textProperty 
          * that will allow it to bind to other observable values so that the label changes whenever the DoubleProperty changes.
          
          * However, textProperty cannot directly bind to DoubleProperty. It can only bind to the string version of it. So we bind it to 
          * traveller.airplane.seatPrice.asString("%.2f"). This converts the DoubleProperty to its appropriate string with 2 decimal places.

          Now whenever the traveller.airplane.seatPrice.asString("%.2f") changes, the priceValueLabel's text changes accordingly.

          */
        priceValueLabel.textProperty().bind(traveller.airplane.seatPrice.asString("%.2f"));
        for (int i = 0; i < seatButtons.length; i++) {
            /* We will now loop through the seatButtons (the ToggleButton array). Each ToggleButton will be constructed. */
            /* The corresponding seat in the airplane has a ToggleButton attribute. This attribute is linked to the ToggleButton the loop is currently at */
            seatButtons[i] = new ToggleButton();
            traveller.airplane.seats[i].ref = seatButtons[i];
            /* The ToggleButton's text is set to the seat number. */
            seatButtons[i].setText(traveller.airplane.seats[i].position + "");
            /* The below 5 lines describe the positioning of the ToggleButtons */
            seatButtons[i].setPrefSize(30, 25);
            int row = i / 5;
            int col = i % 5;
            seatButtons[i].setLayoutX(14 + col * 37);
            seatButtons[i].setLayoutY(76 + row * 35);

            /* The seatButtons the loop is currently at, has a disableProperty. This disableProperty describes if the Button is disabled (unusable) or not.
             * This disableProperty also has a bind property. We bind this disableProperty to the booked BooleanProperty attribute of the seat the loop is at.
             * So if the booked is set to true, the disableProperty of the seatButton is set to true and thus the ToggleButton is disabled.
             * If the booked is set to false, the disableProperty of the seatButton is set to false and thus the ToggleButton is enabled.
             */
            seatButtons[i].disableProperty().bind(traveller.airplane.seats[i].booked);
            /* BooleanBinding is like BooleanProperty but it has a difference. It can bind to MULTIPLE booleans at a time. Here, we are essentially making a subclass
             * that overrides the computeValue of the BooleanBinding class. computeValue() returns true if all the seats are booked, returns false if any one seat is unbooked.
             * Before that however, to make sure it is tracking if all the seats are booked or not, we bind it to the 'booked' BooleanProperty of all the seats in the airplane.
             * We do this by looping through the seats and binding the superclass (BooleanBinding in this case) to the booked BooleanProperty of each seat.
             * We then equate the reference of BooleanBinding areAllSeatsBookedCheck to the subclass object with overriden computeValue method.
             * 
             * Now everytime all seats are booked, areAllSeatsBookedCheck automatically turns to true. If any one seat is not booked, it automatically is false.
            */
            BooleanBinding areAllSeatsBookedCheck = new BooleanBinding() {
                {
                    for (int i = 0; i < traveller.airplane.seats.length; i++) {
                        super.bind(traveller.airplane.seats[i].booked);
                    }
                }
                protected boolean computeValue() {
                    boolean check = true;
                    for (int i = 0; i < traveller.airplane.seats.length; i++) {
                        if (traveller.airplane.seats[i].booked.get() == false) {
                            check = false;
                            break;
                        }
                    }
                    return(check);
                }
            };

            // Now BooleanBinding also has an addListener() method in it like BooleanProperty, DoubleProperty, IntegerProperty, etc. 
            // We will detect changes in the value of areAllSeatsBookedCheck
            /* It has a lambda function with the parameters (observable, oldValue and newValue). Everytime the areAllSeatsBookedCheck changes
             * in value, this is triggered.
             * Now if the newValue is true (areAllSeatsBookedCheck changed from false to true), the AllSeatsBooked user-defined exception we made earlier
             * is thrown. We pass the message "All seats have been booked.".
             * 
             * We surround this with a try and catch block. It will catch the exception and then sets the text of the statusValueLabel to this exception's toString.
             * (REMINDER: you can pass a message to this user defined exception. The toString method of the exception returns this message.)
             */
            areAllSeatsBookedCheck.addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        if (newValue == true) {
                            throw new AllSeatsBooked("All seats have been booked.");
                        }
                    } catch (AllSeatsBooked exc) {
                        statusValueLabel.setText(exc.toString());
                    }
                }
            );

            /* Now we do the event handling for each seatButton. Everytime a ToggleButton seatButtons is pressed, it gets the current ToggleButton
            from the ActionEvent ae.getSource() and typecasting it to (ToggleButton). 
            If the ToggleButton is selected (but not disabled yet), we will loop through all the seats in the airplane and find the seat in the airplane
            with the same ToggleButton it is linked to. We will then equate it to currentSeat.

            Now we will call traveller.bookSeats(currentSeat) to create a booking request for the traveller. Everytime a seat button is selected, a booking request is called for the traveller
            The result of this booking request in the int success variable. 
            
            If the success variable is 1, booking is successful and thus the BACK Button is disabled. Remember, the successful booking request also sets
            the booked BooleanProperty of the seat to true. This also disables the button automatically as the disableProperty of the button is binded to the 
            booked BooleanProperty of the seat.
            This is made so that the Traveller cannot go back to the Dashboard and the only way he can proceed is pressing Next and purchasing the addons.
            It then checks the value of areAllSeatsBookedCheck. If it is true, the status label displays "All seats are booked" (like the listener we added in Line 1787 )
            If it is false, the status label displays "(seat position) successfully booked"
            
            If the success variable is 0, it checks if all the seats are booekd. If yes, it displays "All seats are booked" (automatically due to the listener)
            If no, it displays "Seat already booked.". This is in the case two travellers try to book a seat at the same exact time.
            */

            seatButtons[i].setOnAction(new EventHandler<ActionEvent>() {
               public void handle(ActionEvent ae) {
                ToggleButton currentButton = (ToggleButton) ae.getSource();
                if ((currentButton.isSelected()) && (!currentButton.isDisabled())) {
                    for (int j = 0; j < traveller.airplane.seats.length; j++) {
                        if (traveller.airplane.seats[j].ref == currentButton) {
                            Seat currentSeat = traveller.airplane.seats[j];
                            int success = traveller.bookSeats(currentSeat);
                            if (success == 1) {
                                backButton.setDisable(true);
                                if (areAllSeatsBookedCheck.get() == false)
                                statusValueLabel.setText(currentSeat.position + " successfully booked.");
                                totalCostValueLabel.setText(String.format("%.2f", traveller.totalCost));
                            } else {
                                if (traveller.airplane.countBooked() < traveller.airplane.seats.length) {
                                    statusValueLabel.setText(currentSeat.position + " already booked.");
                                }
                            }
                            break;
                        }
                    }
                }
               } 
            });
            apBook.getChildren().add(seatButtons[i]);
            // Once all the positioning of seatButtons, event handling is done. We are adding the seatButton to the anchor pane.
        }
        apBook.getChildren().addAll(titleLabel, flightNameLabel, flightLabel, backButton, nextButton, priceValueLabel, priceLabel, statusLabel, statusValueLabel, totalCostLabel, totalCostValueLabel);
        Scene scene = new Scene(apBook);
        primaryStage.setScene(scene);

        /* When the nextButton is pressed, it checks if the seats_booked ObservableList<String> in the traveller is empty. If it is empty (it means no seats are booked by the traveller), it goes back to the oldScene.
        If the seats_booked is not empty, you go to the addonScene so you can book addons. This makes it so that the traveller can book addons only if he has booked a seat.
        Otherwise, you go back.
         */
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                if (traveller.seats_booked.isEmpty() == false) {
                    /* We pass the Dashboard as the oldScene in addonScene. This is so that if the Purchase button is pressed in the Addon scene, you go to the dashboard.
                     */
                    addonScene(primaryStage, Dashboard);
                } else {
                    primaryStage.setScene(oldScene);
                }
            }
        });

        /* If backButton is pressed, you go back to the oldScene [the scene in bookPrompt]. */
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                primaryStage.setScene(oldScene);
            } 
        });
    }

    public void userWindow() {
        // This is the userWindow. When we call userWindow, the userStage is created, title is set to Traveller Window. The positioning of elements takes place
        /* The name of the traveller is input, you can submit it or exit */
        Stage userStage = new Stage();
        userStage.setTitle("Traveller Window");
        GridPane gpName = new GridPane();
        gpName.setAlignment(Pos.CENTER);
        gpName.setHgap(10);
        gpName.setVgap(10);
        Scene scName = new Scene(gpName, 400, 300);
        Label lblName = new Label("Enter Name:");
        TextField tfName = new TextField();
        Button btnSubmit = new Button("Submit");
        Button btnExit = new Button("Exit");
        gpName.add(lblName, 0, 0);
        gpName.add(tfName, 1, 0);
        gpName.add(btnSubmit, 0, 1);
        gpName.add(btnExit, 1, 1);

        /* We will make a Dashboard scene which we will set to the userStage when needed */
        GridPane gpDashboard = new GridPane();
        gpDashboard.setAlignment(Pos.CENTER);
        gpDashboard.setHgap(10);
        gpDashboard.setVgap(10);
        Scene scDashBoard = new Scene(gpDashboard, 400, 300);
        Label lblWelcome = new Label();
        Button btnSchedule = new Button("Schedule");
        Button btnBook = new Button("Book");
        Button btnInvoice = new Button("Invoice");
        Button btnBack = new Button("Back");
        gpDashboard.add(lblWelcome, 0, 0);
        gpDashboard.add(btnSchedule, 0, 1);
        GridPane.setHalignment(btnSchedule, HPos.CENTER);
        gpDashboard.add(btnBook, 0, 2);
        GridPane.setHalignment(btnBook, HPos.CENTER);
        gpDashboard.add(btnInvoice, 0, 3);
        GridPane.setHalignment(btnInvoice, HPos.CENTER);
        gpDashboard.add(btnBack, 0, 4);
        GridPane.setHalignment(btnBack, HPos.CENTER);

        /* We will make a Dashboard scene which we will set to the userStage when needed */
        GridPane gpBook = new GridPane();
        gpBook.setAlignment(Pos.CENTER);
        gpBook.setHgap(10);
        gpBook.setVgap(10);
        Scene scBook = new Scene(gpBook,400,300);
        Label lblBookFlightName = new Label("Enter Flight Name");
        Label lblBookFlightResponse = new Label();
        TextField tfBookFlightName = new TextField();
        Button btnSubmitFlightName = new Button("Submit");
        Button btnBookBack = new Button("Back");
        gpBook.add(lblBookFlightName,0,0);
        gpBook.add(tfBookFlightName,1,0);
        gpBook.add(lblBookFlightResponse,1,1);
        gpBook.add(btnSubmitFlightName,0,2);
        gpBook.add(btnBookBack,1,2);

        // We will make an scAlreadyBooked scene which we will set to the userStage when needed
        GridPane gpAlreadyBooked = new GridPane();
        gpAlreadyBooked.setAlignment(Pos.CENTER);
        gpAlreadyBooked.setHgap(10);
        gpAlreadyBooked.setVgap(10);
        Scene scAlreadyBooked = new Scene(gpAlreadyBooked,200,200);
        Label lblAlreadyBooked = new Label("You have already booked tickets");
        Button btnAlreadyBookedBack = new Button("Back");
        gpAlreadyBooked.add(lblAlreadyBooked,0,0);
        gpAlreadyBooked.add(btnAlreadyBookedBack,0,1);
        GridPane.setHalignment(lblAlreadyBooked, HPos.CENTER);


        
        // Username Submit button
        btnSubmit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                traveller = new Traveller(tfName.getText());
                lblWelcome.setText("Welcome " + traveller.getPassengerName());
                userStage.setScene(scDashBoard);
            }
        });

        // Name scene exit button
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent o) {
                userStage.close();
            }
        });

        // Dashboard back button
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scName);
                tfName.setText("");
            }
        });

        //Back to dashboard from Book
        btnBookBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scDashBoard);
            }
        });

        //Back to dashboard from Already Booked
        btnAlreadyBookedBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                userStage.setScene(scDashBoard);
            }
        });

        // Schedule button
        btnSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                scheduleScene(userStage, scDashBoard);
            }
        });

        // Book button is pressed. It checks if the user's seat_booked is empty. If the user has no seat booked, he goes to scene in bookPrompt
        // Otherwise, if the user has a seat booked, the userStage is set to scAlreadyBooked. The user is not able to book anymore seats after he's done 
        // booking seats and addons with a flight
        btnBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (traveller.seats_booked.isEmpty() == false) { 
                    userStage.setScene(scAlreadyBooked);
                } else {
                    bookPrompt(userStage, scDashBoard);
                }   
            }
        });

        /* When the invoice button is pressed, the invoiceScene is called so that the invoice scene is opened. */
        btnInvoice.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                invoiceScene(userStage, scDashBoard);
            }
        });

        btnSubmitFlightName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                int index_airplane = Manager.searchEntry(tfBookFlightName.getText());
                if(index_airplane == -1)
                    lblBookFlightResponse.setText("No such airplane found");
                else {
                    Airplane found_airplane = Schedule.schedule.get(index_airplane);
                    if (found_airplane.countBooked() == found_airplane.seats.length) {
                        lblBookFlightResponse.setText("All the seats have been booked");
                    }
                }
            }
        });

        userStage.setScene(scName);
        userStage.show();
    }

    public void adminWindow() {
        Stage adminStage = new Stage();
        adminStage.setTitle("Manager Window");

        GridPane gpLogin = new GridPane();
        gpLogin.setAlignment(Pos.CENTER);
        gpLogin.setHgap(10);
        gpLogin.setVgap(10);
        Scene scLogin = new Scene(gpLogin, 400, 300);
        Label lblPassword = new Label("Enter Password");
        Label lblPasswordResponse = new Label();
        PasswordField tfPassword = new PasswordField();
        Button btnPasswordSubmit = new Button("Submit");
        Button btnExit = new Button("Exit");
        gpLogin.add(lblPassword, 0, 0);
        gpLogin.add(tfPassword, 1, 0);
        gpLogin.add(lblPasswordResponse, 1, 1);
        gpLogin.add(btnPasswordSubmit, 0, 2);
        gpLogin.add(btnExit, 1, 2);

        // Dashboard Scene
        GridPane gpDashboard = new GridPane();
        gpDashboard.setAlignment(Pos.CENTER);
        gpDashboard.setHgap(10);
        gpDashboard.setVgap(10);
        Scene scDashboard = new Scene(gpDashboard, 400, 400);
        Label lblWelcomeAdmin = new Label("Welcome Manager");
        Button btnAdd = new Button("Add Flights");
        Button btnDepart = new Button("Depart Flights");
        Button btnDelete = new Button("Delete Flights");
        Button btnLogout = new Button("Logout");
        Button btnSchedule = new Button("Schedule");
        Button btnReport = new Button("Report");
        gpDashboard.add(lblWelcomeAdmin, 0, 0);
        gpDashboard.add(btnAdd, 0, 1);
        GridPane.setHalignment(btnAdd, HPos.CENTER);
        gpDashboard.add(btnDepart, 0, 2);
        GridPane.setHalignment(btnDepart, HPos.CENTER);
        gpDashboard.add(btnDelete,0,3);
        GridPane.setHalignment(btnLogout, HPos.CENTER);
        gpDashboard.add(btnSchedule,0,4);
        GridPane.setHalignment(btnSchedule, HPos.CENTER);
        gpDashboard.add(btnReport,0,5);
        GridPane.setHalignment(btnReport, HPos.CENTER);
        gpDashboard.add(btnLogout,0,6);
        GridPane.setHalignment(btnDelete, HPos.CENTER);


        // Add Flight Scene
        GridPane gpAddFlight = new GridPane();
        gpAddFlight.setAlignment(Pos.CENTER);
        gpAddFlight.setHgap(10);
        gpAddFlight.setVgap(10);
        Scene scAddFlight = new Scene(gpAddFlight, 450, 650);
        Label lblEnterDetails = new Label("Enter Flight Details:");
        gpAddFlight.add(lblEnterDetails, 0, 0);
        Label lblFlightName = new Label("Name:");
        gpAddFlight.add(lblFlightName, 0, 1);
        Label lblFlightIntDome = new Label("International/Domestic");
        gpAddFlight.add(lblFlightIntDome, 0, 2);
        Label lblFlightOrigin = new Label("Origin:");
        gpAddFlight.add(lblFlightOrigin, 0, 3);
        Label lblFlightDestination = new Label("Destination:");
        gpAddFlight.add(lblFlightDestination, 0, 4);
        Label lblFlightDateArrival = new Label("Date of Arrival");
        gpAddFlight.add(lblFlightDateArrival, 0, 5);
        Label lblFlightTimeArrival = new Label("Time of Arrival");
        gpAddFlight.add(lblFlightTimeArrival, 0, 6);
        Label lblFlightDateDeparture = new Label("Date oF Departure");
        gpAddFlight.add(lblFlightDateDeparture, 0, 7);
        Label lblFlightTimeDeparture = new Label("Time of Departure");
        gpAddFlight.add(lblFlightTimeDeparture, 0, 8);
        Label lblFlightCapacity = new Label("Seat Capacity");
        gpAddFlight.add(lblFlightCapacity, 0, 9);
        Label lblFlightPrice = new Label("Seat Price");
        gpAddFlight.add(lblFlightPrice, 0, 10);
        Label lblFlightDetailsResponse = new Label();
        gpAddFlight.add(lblFlightDetailsResponse, 1, 11);
        TextField tfFlightName = new TextField();
        gpAddFlight.add(tfFlightName, 1, 1);
        TextField tfFlightType = new TextField();
        gpAddFlight.add(tfFlightType, 1, 2);
        TextField tfFlightOrigin = new TextField();
        gpAddFlight.add(tfFlightOrigin, 1, 3);
        TextField tfFlightDestination = new TextField();
        gpAddFlight.add(tfFlightDestination, 1, 4);
        TextField tfFlightDateArrival = new TextField();
        tfFlightDateArrival.setPromptText("dd-mm-yyyy");
        gpAddFlight.add(tfFlightDateArrival, 1, 5);
        TextField tfFlightTimeArrival = new TextField();
        tfFlightTimeArrival.setPromptText("hh:mm");
        gpAddFlight.add(tfFlightTimeArrival, 1, 6);
        TextField tfFlightDateDeparture = new TextField();
        tfFlightDateDeparture.setPromptText("dd-mm-yyyy");
        gpAddFlight.add(tfFlightDateDeparture, 1, 7);
        TextField tfFlightTimeDeparture = new TextField();
        tfFlightTimeDeparture.setPromptText("hh:mm");
        gpAddFlight.add(tfFlightTimeDeparture, 1, 8);
        TextField tfFlightCapacity = new TextField();
        gpAddFlight.add(tfFlightCapacity, 1, 9);
        TextField tfFlightPrice = new TextField();
        gpAddFlight.add(tfFlightPrice, 1, 10);
        Button btnSubmitFlightDetails = new Button("Submit");
        gpAddFlight.add(btnSubmitFlightDetails, 0, 12);
        Button btnClearFlightDetails = new Button("Clear");
        gpAddFlight.add(btnClearFlightDetails, 1, 12);
        Button btnBackFlightDetails = new Button("Back");
        gpAddFlight.add(btnBackFlightDetails, 0, 13);

        // Depart Scene
        GridPane gpDepart = new GridPane();
        gpDepart.setAlignment(Pos.CENTER);
        gpDepart.setHgap(10);
        gpDepart.setVgap(10);
        Scene scDepart = new Scene(gpDepart, 400, 300);
        Label lblDepartFlight = new Label("Enter Flight name:");
        Label lblDepartResponse = new Label();
        TextField tfDepartFlight = new TextField();
        Button btnSubmitDepart = new Button("Submit");
        Button btnBackDepart = new Button("Back");
        gpDepart.add(lblDepartFlight, 0, 0);
        gpDepart.add(tfDepartFlight, 1, 0);
        gpDepart.add(lblDepartResponse, 1, 1);
        gpDepart.add(btnSubmitDepart, 0, 2);
        gpDepart.add(btnBackDepart, 1, 2);

        //Delete Scene
        GridPane gpDelete = new GridPane();
        gpDelete.setAlignment(Pos.CENTER);
        gpDelete.setHgap(10);
        gpDelete.setVgap(10);
        Scene scDelete = new Scene(gpDelete, 400, 300);
        Label lblDeleteFlight = new Label("Enter Flight name:");
        Label lblDeleteResponse = new Label();
        TextField tfDeleteFlight = new TextField();
        Button btnSubmitDelete = new Button("Submit");
        Button btnBackDelete = new Button("Back");
        gpDelete.add(lblDeleteFlight, 0, 0);
        gpDelete.add(tfDeleteFlight, 1, 0);
        gpDelete.add(lblDeleteResponse, 1, 1);
        gpDelete.add(btnSubmitDelete, 0, 2);
        gpDelete.add(btnBackDelete, 1, 2);

        // Button Actions

        // Password Submit
        btnPasswordSubmit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (tfPassword.getText().equals("pass123"))
                    adminStage.setScene(scDashboard);
                else
                    lblPasswordResponse.setText("Incorrect Password");
            }
        });

        // Admin exit
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.close();
            }
        });

        // Add Flights
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scAddFlight);
            }
        });

        // Logout
        btnLogout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scLogin);
                tfPassword.setText("");
            }
        });

        /* When we're trying to add flights to the schedule and press Submit Flight Details, this event is triggered. When this is pressed, 
        it checks if any textfield is empty. If a textfield is found to be empty, the lblFlightDetailsResponse label is set to "Please fill all the details" */

        /* Otherwise if all the textfields are submitted, we will do Manager.addEntry and pass the textfield inputs as parameters. */

        btnSubmitFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (tfFlightName.getText().isEmpty() || tfFlightType.getText().isEmpty()
                        || tfFlightOrigin.getText().isEmpty() ||
                        tfFlightDestination.getText().isEmpty() || tfFlightDestination.getText().isEmpty()
                        || tfFlightDateArrival.getText().isEmpty()
                        || tfFlightTimeArrival.getText().isEmpty() || tfFlightDateDeparture.getText().isEmpty()
                        || tfFlightDateDeparture.getText().isEmpty() ||
                        tfFlightPrice.getText().isEmpty() || tfFlightCapacity.getText().isEmpty())
                    lblFlightDetailsResponse.setText("Please fill all the details");
                else {
                    Manager.addEntry(tfFlightName.getText(), tfFlightType.getText(),
                            Integer.parseInt(tfFlightCapacity.getText()), Double.parseDouble(tfFlightPrice.getText()),
                            tfFlightOrigin.getText(), tfFlightDestination.getText(),
                            Integer.parseInt(tfFlightTimeArrival.getText().substring(3)),
                            Integer.parseInt(tfFlightTimeArrival.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(3, 5)),
                            Integer.parseInt(tfFlightDateArrival.getText().substring(6)),
                            Integer.parseInt(tfFlightTimeDeparture.getText().substring(3)),
                            Integer.parseInt(tfFlightTimeDeparture.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(0, 2)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(3, 5)),
                            Integer.parseInt(tfFlightDateDeparture.getText().substring(6)));

                    lblFlightDetailsResponse.setText("Flight Details Added");
                    tfFlightName.setText("");
                }
            }
        });

        // Clear Flight Details
        btnClearFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                tfFlightCapacity.setText("");
                tfFlightDateArrival.setText("");
                tfFlightDateDeparture.setText("");
                tfFlightDestination.setText("");
                tfFlightName.setText("");
                tfFlightOrigin.setText("");
                tfFlightPrice.setText("");
                tfFlightTimeArrival.setText("");
                tfFlightTimeDeparture.setText("");
                tfFlightType.setText("");
            }
        });

        // Back Add Flight
        btnBackFlightDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });

        // Schedule Dashboard
        btnSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                scheduleScene(adminStage, scDashboard);
            }
        });
        // Depart Dashboard
        btnDepart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDepart);
            }
        });

        btnReport.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                reportScene(adminStage, scDashboard);
            }
        });

        // Submit Depart
        btnSubmitDepart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                boolean flag = Manager.setDeparted(tfDepartFlight.getText());
                if (flag == false)
                    lblDepartResponse.setText("Flight could not be departed");
                else
                    lblDepartResponse.setText("Flight Departed");
            }
        });

        // Back Depart
        btnBackDepart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });

        //Delete Button
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                deleteScene(adminStage, scDashboard);
            }
        });

        // Submit Delete
        btnSubmitDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                boolean flag = Manager.deleteEntry(tfDepartFlight.getText());
                if (flag == false)
                    lblDepartResponse.setText("Flight could not be deleted");
                else
                    lblDepartResponse.setText("Flight Deleted");
            }
        });

        // Back Delete
        btnBackDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                adminStage.setScene(scDashboard);
            }
        });

        adminStage.setScene(scLogin);
        adminStage.show();
    }
}