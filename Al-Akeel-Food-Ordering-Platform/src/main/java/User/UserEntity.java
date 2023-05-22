package User;

import Order.OrderEntity;
import Restaurant.RestaurantEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    private Role role;

    private double fees;

    public enum Role {
        customer,
        restaurantOwner,
        runner
    }

    @OneToOne
    private RestaurantEntity restaurantEntity;

    @OneToMany(mappedBy = "customer")
    private List<OrderEntity> orderEntity;

    public UserEntity() {
    }

    public UserEntity(String username, String password, Role role, double fees) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fees = fees;
        this.restaurantEntity = new RestaurantEntity();
        this.orderEntity = new ArrayList<>();
    }


    public UserEntity(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fees = 0;
        this.restaurantEntity = new RestaurantEntity();
        this.orderEntity = new ArrayList<>();
    }

    public RestaurantEntity getRestaurantEntity() {
        return restaurantEntity;
    }

    public void setRestaurantEntity(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public List<OrderEntity> getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(List<OrderEntity> orderEntity) {
        this.orderEntity = orderEntity;
    }
}
