package Meal;

import Order.OrderEntity;
import Restaurant.RestaurantEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class MealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private double price;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private RestaurantEntity restaurantEntity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private OrderEntity orderEntity;

//    @ManyToOne
//    @JoinColumn(name = "menu")
//    @JsonIgnore
//    private RestaurantEntity menu;

    public MealEntity() {
    }

    public MealEntity(String name, double price) {
        this.name = name;
        this.price = price;
        this.orderEntity = new OrderEntity();
        this.restaurantEntity = new RestaurantEntity();
    }

    public MealEntity(String name, double price, RestaurantEntity restaurantEntity) {
        this.name = name;
        this.price = price;
        this.orderEntity = new OrderEntity();
        this.restaurantEntity = restaurantEntity;
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

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RestaurantEntity getRestaurantEntity() {
        return restaurantEntity;
    }

    public void setRestaurantEntity(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }
}
