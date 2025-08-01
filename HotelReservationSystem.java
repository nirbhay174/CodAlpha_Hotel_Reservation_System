import java.io.*;
import java.util.*;

public class HotelReservationSystem {

    // Room class
    static class Room {
        int roomNumber;
        String type; // Standard, Deluxe, Suite
        boolean isBooked;

        Room(int roomNumber, String type) {
            this.roomNumber = roomNumber;
            this.type = type;
            this.isBooked = false;
        }

        @Override
        public String toString() {
            return "Room " + roomNumber + " (" + type + ") - " + (isBooked ? "Booked" : "Available");
        }
    }

    // Booking class
    static class Booking {
        String customerName;
        int roomNumber;
        String roomType;
        String paymentStatus;

        Booking(String customerName, int roomNumber, String roomType, String paymentStatus) {
            this.customerName = customerName;
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.paymentStatus = paymentStatus;
        }

        @Override
        public String toString() {
            return customerName + " | Room: " + roomNumber + " (" + roomType + ") | Payment: " + paymentStatus;
        }
    }

    static class Hotel {
        List<Room> rooms = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        final String fileName = "bookings.txt";

        Hotel() {
            loadRooms();
            loadBookings();
        }

        void loadRooms() {
            for (int i = 1; i <= 5; i++) rooms.add(new Room(i, "Standard"));
            for (int i = 6; i <= 8; i++) rooms.add(new Room(i, "Deluxe"));
            for (int i = 9; i <= 10; i++) rooms.add(new Room(i, "Suite"));
        }

        void displayAvailableRooms() {
            System.out.println("\nAvailable Rooms:");
            for (Room r : rooms) {
                if (!r.isBooked) System.out.println(r);
            }
        }

        Room findAvailableRoom(String type) {
            for (Room r : rooms) {
                if (r.type.equalsIgnoreCase(type) && !r.isBooked)
                    return r;
            }
            return null;
        }

        void makeBooking(String customerName, String type) {
            Room room = findAvailableRoom(type);
            if (room == null) {
                System.out.println("❌ No available rooms in " + type + " category.");
                return;
            }

            // Simulate payment
            System.out.println("💳 Simulating payment...");
            String paymentStatus = "Paid";

            // Book the room
            room.isBooked = true;
            Booking booking = new Booking(customerName, room.roomNumber, room.type, paymentStatus);
            bookings.add(booking);
            saveBookingToFile(booking);
            System.out.println("✅ Booking successful: " + booking);
        }

        void cancelBooking(String customerName) {
            boolean found = false;
            Iterator<Booking> iterator = bookings.iterator();

            while (iterator.hasNext()) {
                Booking b = iterator.next();
                if (b.customerName.equalsIgnoreCase(customerName)) {
                    iterator.remove();
                    Room r = getRoomByNumber(b.roomNumber);
                    if (r != null) r.isBooked = false;
                    found = true;
                    break;
                }
            }

            if (found) {
                saveAllBookingsToFile();
                System.out.println("✅ Booking cancelled for " + customerName);
            } else {
                System.out.println("❌ No booking found for " + customerName);
            }
        }

        void viewBookings() {
            System.out.println("\n📄 Current Bookings:");
            if (bookings.isEmpty()) {
                System.out.println("No bookings found.");
            } else {
                for (Booking b : bookings) {
                    System.out.println(b);
                }
            }
        }

        Room getRoomByNumber(int roomNumber) {
            for (Room r : rooms) {
                if (r.roomNumber == roomNumber) return r;
            }
            return null;
        }

        void loadBookings() {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String name = parts[0];
                        int roomNum = Integer.parseInt(parts[1]);
                        String roomType = parts[2];
                        String payment = parts[3];
                        bookings.add(new Booking(name, roomNum, roomType, payment));
                        Room room = getRoomByNumber(roomNum);
                        if (room != null) room.isBooked = true;
                    }
                }
            } catch (IOException e) {
                // File might not exist initially
            }
        }

        void saveBookingToFile(Booking b) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
                bw.write(b.customerName + "," + b.roomNumber + "," + b.roomType + "," + b.paymentStatus);
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error writing booking to file.");
            }
        }

        void saveAllBookingsToFile() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                for (Booking b : bookings) {
                    bw.write(b.customerName + "," + b.roomNumber + "," + b.roomType + "," + b.paymentStatus);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating bookings file.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();
        int choice;

        do {
            System.out.println("\n==== Hotel Reservation System ====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make Booking");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    hotel.displayAvailableRooms();
                    break;
                case 2:
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter room type (Standard/Deluxe/Suite): ");
                    String type = sc.nextLine();
                    hotel.makeBooking(name, type);
                    break;
                case 3:
                    System.out.print("Enter your name to cancel booking: ");
                    String cancelName = sc.nextLine();
                    hotel.cancelBooking(cancelName);
                    break;
                case 4:
                    hotel.viewBookings();
                    break;
                case 0:
                    System.out.println("👋 Thank you for using the Hotel Reservation System!");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } while (choice != 0);

        sc.close();
    }
}
