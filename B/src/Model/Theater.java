/**
 * Represents a theater entity with its basic information.
 * Contains details like name and seating capacity.
 */
package Model;

public class Theater {
    private int id;           // Unique identifier for the theater
    private String name;      // Name of the theater
    private  int capacity;    // Seating capacity of the theater

    /**
     * Default constructor for the Theater class.
     */
    public Theater(){}

    /**
     * Parameterized constructor to create a Theater with all properties
     * @param id Unique identifier for the theater
     * @param name Name of the theater
     * @param capacity Seating capacity of the theater
     */
    public Theater(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}
}
