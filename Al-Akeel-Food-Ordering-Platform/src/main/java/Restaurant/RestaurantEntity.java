package Restaurant;

import Meal.MealEntity;
import Order.OrderEntity;
import User.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private double earns;

    @OneToOne(mappedBy = "restaurantEntity", cascade = CascadeType.ALL)
    private UserEntity owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private ArrayList<OrderEntity> orders;

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private ArrayList<MealEntity> meals;


    public RestaurantEntity() {
    }

    public RestaurantEntity(String name) {
        this.name = name;
        this.meals = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public RestaurantEntity(String name, UserEntity owner) {
        this.name = name;
        this.owner = owner;
        this.earns = 0;
        this.meals = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public RestaurantEntity(String name, UserEntity owner, ArrayList<MealEntity> meals) {
        this.name = name;
        this.owner = owner;
        this.meals = meals;
        this.earns = 0;
        this.orders = new ArrayList<>();
    }

    public RestaurantEntity(String name, UserEntity owner, ArrayList<MealEntity> meals, ArrayList<OrderEntity> orders) {
        this.name = name;
        this.owner = owner;
        this.earns = 0;
        this.meals = meals;
        this.orders = orders;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getEarns() {
        return earns;
    }

    public void setEarns(double earns) {
        this.earns = earns;
    }

    public void addEarns(double earns) {
        this.earns += earns;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderEntity> orders) {
        this.orders = orders;
    }

    public ArrayList<MealEntity> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<MealEntity> meals) {
        this.meals = meals;
    }

    public void addMeal(MealEntity meal) {
        this.meals.add(meal);
    }

    public void removeMeal(MealEntity meal) {
        this.meals.remove(meal);
    }
}
