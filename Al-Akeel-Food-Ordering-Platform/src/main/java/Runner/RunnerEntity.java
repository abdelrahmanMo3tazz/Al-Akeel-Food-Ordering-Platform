package Runner;

import Order.OrderEntity;
import User.UserEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RunnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String password;

    private Status status;

    private double deliveryFees;

    public enum Status {
        available,
        busy
    }

    @OneToOne
    private OrderEntity orderEntity;

    @Transient
    private List<OrderEntity> completedOrders = new ArrayList<>();

    public RunnerEntity() {
    }

    public RunnerEntity(UserEntity userEntity) {
        this.name = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.deliveryFees = userEntity.getFees();
        this.status = Status.available;
    }

    public RunnerEntity(String name, String password, Status status, double deliveryFees) {
            this.name = name;
            this.password = password;
            this.status = status;
            this.deliveryFees = deliveryFees;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(double deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }

    public void setStatusBusy() {
        this.status = Status.busy;;
    }

    public void setStatusAvailable() {
        this.status = Status.available;
    }

    public List<OrderEntity> getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(List<OrderEntity> completedOrders) {
        this.completedOrders = completedOrders;
    }
}
