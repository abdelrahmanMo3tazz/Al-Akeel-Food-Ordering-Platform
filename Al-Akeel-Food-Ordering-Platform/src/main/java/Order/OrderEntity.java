package Order;

import Meal.MealEntity;
import Restaurant.RestaurantEntity;
import Runner.RunnerEntity;
import User.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL)
    private List<MealEntity> meals;

    private double totalPrice;

    private double deliveryFee;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private UserEntity customer;

    @OneToOne(mappedBy = "orderEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private RunnerEntity runner;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private RestaurantEntity restaurant;

    private orderStatus status;

    public enum orderStatus {
        preparing,
        delivered,
        cancelled
    }

    public OrderEntity() {
    }

    public OrderEntity(List<MealEntity> meals, double totalPrice, UserEntity customer, RestaurantEntity restaurant) {
        this.meals = meals;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.restaurant = restaurant;
        this.date = new Date();
        this.status = orderStatus.preparing;
    }

    public OrderEntity(List<MealEntity> meals, double totalPrice, UserEntity customer, RunnerEntity runner, RestaurantEntity restaurant) {
        this.meals = meals;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.runner = runner;
        this.restaurant = restaurant;
        this.date = new Date();
        this.status = orderStatus.preparing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MealEntity> getMeals() {
        return meals;
    }

    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addTotalPrice(double totalPrice) {
        this.totalPrice += totalPrice;
    }

    public void subtractTotalPrice(double totalPrice) {
        this.totalPrice -= totalPrice;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public void setCustomer(UserEntity customer) {
        this.customer = customer;
    }

    public RunnerEntity getRunner() {
        return runner;
    }

    public void setRunner(RunnerEntity runner) {
        this.runner = runner;
    }

    public String getRestaurantName() {
        return restaurant.getName();
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public orderStatus getStatus() {
        return status;
    }

    public void setStatus(orderStatus status) {
        this.status = status;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    //set status preparing
    public void setStatusPreparing() {
        this.status = orderStatus.preparing;
    }

    //set status delivered
    public void setStatusDelivered() {
        this.status = orderStatus.delivered;
    }

    //set status cancelled
    public void setStatusCancelled() {
        this.status = orderStatus.cancelled;
    }


}
