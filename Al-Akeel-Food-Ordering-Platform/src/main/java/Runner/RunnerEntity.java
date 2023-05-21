package Runner;

import Order.OrderEntity;
import User.UserEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class RunnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String password;

    private Status status;

    private double fees;

    public enum Status {
        available,
        busy
    }

    @OneToMany(mappedBy = "runner", cascade = CascadeType.ALL)
    private List<OrderEntity> orderEntity;

    public RunnerEntity() {
    }

    public RunnerEntity(String name, String password, Status status, double fees) {
            this.name = name;
            this.password = password;
            this.status = status;
            this.fees = fees;
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
