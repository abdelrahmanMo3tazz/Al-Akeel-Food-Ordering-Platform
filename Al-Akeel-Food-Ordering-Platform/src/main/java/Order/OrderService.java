package Order;

import Meal.MealEntity;
import Restaurant.RestaurantEntity;
import Runner.RunnerEntity;
import Runner.RunnerService;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/orders")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    //create order
    @POST
    @Path("createOrder/{customerId}/{restaurantId}")
    public String createOrder(List<Integer> mealsIds,@PathParam("customerId") int customerId,@PathParam("restaurantId") int restaurantId) {
        List<MealEntity> mealEntities = new ArrayList<>();
        UserEntity customer = null;
        RestaurantEntity restaurant = null;
        int totalPrice = 0;
        RunnerService runnerService = new RunnerService();
        RunnerEntity runnerEntity = runnerService.getRandomAvailableRunner();
        try{
            customer = em.find(UserEntity.class, customerId);
            restaurant = em.find(RestaurantEntity.class, restaurantId);
        } catch (Exception e){
            return "Customer or Restaurant not found";
        }
        for(int mealId : mealsIds){
            MealEntity mealEntity = em.find(MealEntity.class, mealId);
            totalPrice += mealEntity.getPrice();
            mealEntities.add(mealEntity);
        }
        totalPrice += runnerEntity.getDeliveryFees();
        runnerEntity.setStatusBusy();
        OrderEntity orderEntity = new OrderEntity(mealEntities, totalPrice, customer, runnerEntity, restaurant);
        for (MealEntity mealEntity : mealEntities){
            mealEntity.setOrderEntity(orderEntity);
        }
        runnerEntity.setOrderEntity(orderEntity);
        restaurant.getOrders().add(orderEntity);
        orderEntity.setStatusPreparing();
        em.getTransaction().begin();
        em.persist(orderEntity);
        em.getTransaction().commit();
        return "Order Created Successfully";
    }

    //get all customer orders
    @GET
    @Path("getAllCustomerOrders/{customerId}")
    public List<OrderEntity> getAllCustomerOrders(@PathParam("customerId") int customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\"";
        return em.createQuery(query).getResultList();
    }

    //get all customer preparing orders
    @GET
    @Path("getAllCustomerPreparingOrders/{customerId}")
    public List<OrderEntity> getAllCustomerPreparingOrders(@PathParam("customerId") int customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\" AND u.status = \"Preparing\"";
        return em.createQuery(query).getResultList();
    }

    //get all customer delivered orders
    @GET
    @Path("getAllCustomerDeliveredOrders/{customerId}")
    public List<OrderEntity> getAllCustomerDeliveredOrders(@PathParam("customerId") int customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\" AND u.status = \"Delivered\"";
        return em.createQuery(query).getResultList();
    }

    //cancel order
    @PUT
    @Path("cancelOrder/{orderId}/{customerId}")
    public String cancelOrder(@PathParam("orderId") int orderId,@PathParam("customerId") int customerId) {
        OrderEntity orderEntity = null;
        try{
            orderEntity = em.find(OrderEntity.class, orderId);
        } catch (Exception e){
            return "Order or Customer not found";
        }
        if(orderEntity.getCustomer().getId() != customerId){
            return "This order is not yours";
        }
        em.getTransaction().begin();
        orderEntity.setStatusCancelled();
        orderEntity.getRunner().setStatusAvailable();
        em.merge(orderEntity.getRunner());
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is cancelled";
    }

    //get orders of customer
    @GET
    @Path("getCustomerOrders/{customerId}")
    public List<OrderEntity> getCustomerOrders(@PathParam("customerId") int customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\"";
        return em.createQuery(query).getResultList();
    }

    //deliver order
    @PUT
    @Path("deliverOrder/{runnerId}")
    public String deliverOrder(@PathParam("runnerId") int runnerId) {
        OrderEntity orderEntity = null;
        RunnerEntity runnerEntity = null;
        try{
            runnerEntity = em.find(RunnerEntity.class, runnerId);
            orderEntity = runnerEntity.getOrderEntity();
        } catch (Exception e){
            return "Order or Runner not found";
        }
        em.getTransaction().begin();
        orderEntity.setStatusDelivered();
        runnerEntity.getCompletedOrders().add(orderEntity);
        runnerEntity.setStatusAvailable();
        em.merge(runnerEntity);
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is delivering";
    }

    //edit order to add meals
    @PUT
    @Path("editOrder/{orderId}/{customerId}")
    public String editOrderAdd(List<Integer> mealsIds, @PathParam("orderId") int orderId,@PathParam("customerId") int customerId) {
        OrderEntity orderEntity = null;
        try{
            orderEntity = em.find(OrderEntity.class, orderId);
        } catch (Exception e){
            return "Order or Customer not found";
        }
        if(orderEntity.getCustomer().getId() != customerId){
            return "This order is not yours";
        }
        if (orderEntity.getStatus() != OrderEntity.orderStatus.preparing){
            return "This order is either cancelled or delivered";
        }
        int totalPrice = 0;
        for(int mealId : mealsIds){
            MealEntity mealEntity = em.find(MealEntity.class, mealId);
            totalPrice += mealEntity.getPrice();
            if (!orderEntity.getMeals().contains(mealEntity)){
                orderEntity.getMeals().add(mealEntity);
            }else {
                return "This meal is already in your order, please recheck your order list";
            }
        }
        orderEntity.addTotalPrice(totalPrice);
        em.getTransaction().begin();
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is edited";
    }

    //edit order to remove meals
    @PUT
    @Path("editOrderRemove/{orderId}/{customerId}")
    public String editOrderRemove(List<Integer> mealsIds, @PathParam("orderId") int orderId,@PathParam("customerId") int customerId) {
        OrderEntity orderEntity = null;
        try{
            orderEntity = em.find(OrderEntity.class, orderId);
        } catch (Exception e){
            return "Order or Customer not found";
        }
        if(orderEntity.getCustomer().getId() != customerId){
            return "This order is not yours";
        }
        if (orderEntity.getStatus() != OrderEntity.orderStatus.preparing){
            return "This order is either cancelled or delivered";
        }
        int totalPrice = 0;
        for(int mealId : mealsIds){
            MealEntity mealEntity = em.find(MealEntity.class, mealId);
            totalPrice += mealEntity.getPrice();
            if (orderEntity.getMeals().contains(mealEntity)){
                orderEntity.getMeals().remove(mealEntity);
            }else {
                return "This meal is not in your order, please recheck your order again";
            }
        }
        orderEntity.subtractTotalPrice(totalPrice);
        em.getTransaction().begin();
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is edited";
    }
}
