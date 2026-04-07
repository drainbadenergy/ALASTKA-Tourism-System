package model;

public class User extends Person {
    // Private access specifiers for Encapsulation (Activity 1 / CO1)
    private int userId;
    private String email;
    private String password;

    // Constructor to initialize the User and the inherited 'name' variable
    public User(int userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name; // Inherited from protected variable in Person
        this.email = email;
        this.password = password;
    }

    // Demonstrating POLYMORPHISM (Activity 2 / CO2)
    // Overriding the method from the parent Person class
    @Override
    public void showDetails() {
        System.out.println("User Profile -> ID: " + userId + ", Name: " + name + ", Email: " + email);
    }

    // Getters for database operations
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
}