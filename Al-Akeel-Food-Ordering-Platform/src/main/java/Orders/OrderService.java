package Orders;

import Meal.MealEntity;
import Restaurant.RestaurantEntity;
import Runner.RunnerEntity;
import Runner.RunnerService;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/orders")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderService {

    @PersistenceContext
    private EntityManager em;

    //create order
    @POST
    @Path("createOrder/{customerId}/{restaurantId}")
    public String createOrder(List<Integer> mealsIds,@PathParam("customerId") long customerId,@PathParam("restaurantId") long restaurantId) {
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
        runnerEntity.setStatus(RunnerEntity.Status.busy);
        OrderEntity orderEntity = new OrderEntity();
        for (MealEntity mealEntity : mealEntities){
            mealEntity.setOrderEntity(orderEntity);
        }
        runnerEntity.setOrderEntity(orderEntity);
        restaurant.getOrders().add(orderEntity);
        orderEntity.setMeals(mealEntities);
        orderEntity.setCustomer(customer);
        orderEntity.setRestaurant(restaurant);
        orderEntity.setRunner(runnerEntity);
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setStatus(OrderEntity.orderStatus.preparing);
        em.persist(orderEntity);
        return "Order Created Successfully";
    }

    //get all customer orders
    @GET
    @Path("getAllCustomerOrders/{customerId}")
    public List<OrderEntity> getAllCustomerOrders(@PathParam("customerId") long customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\"";
        return em.createQuery(query).getResultList();
    }

    //get all customer preparing orders
    @GET
    @Path("getAllCustomerPreparingOrders/{customerId}")
    public List<OrderEntity> getAllCustomerPreparingOrders(@PathParam("customerId") long customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\" AND u.status = \"Preparing\"";
        return em.createQuery(query).getResultList();
    }

    //get all customer delivered orders
    @GET
    @Path("getAllCustomerDeliveredOrders/{customerId}")
    public List<OrderEntity> getAllCustomerDeliveredOrders(@PathParam("customerId") long customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\" AND u.status = \"Delivered\"";
        return em.createQuery(query).getResultList();
    }

    //cancel order
    @PUT
    @Path("cancelOrder/{orderId}/{customerId}")
    public String cancelOrder(@PathParam("orderId") long orderId,@PathParam("customerId") long customerId) {
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
        orderEntity.setStatus(OrderEntity.orderStatus.cancelled);
        orderEntity.getRunner().setStatus(RunnerEntity.Status.available);
        em.merge(orderEntity.getRunner());
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is cancelled";
    }

    //get orders of customer
    @GET
    @Path("getCustomerOrders/{customerId}")
    public List<OrderEntity> getCustomerOrders(@PathParam("customerId") long customerId) {
        String query = "SELECT u FROM OrderEntity u WHERE u.customer.id = \"" + customerId + "\"";
        return em.createQuery(query).getResultList();
    }

    //deliver order
    @PUT
    @Path("deliverOrder/{runnerId}")
    public String deliverOrder(@PathParam("runnerId") long runnerId) {
        OrderEntity orderEntity = null;
        RunnerEntity runnerEntity = null;
        try{
            runnerEntity = em.find(RunnerEntity.class, runnerId);
            orderEntity = runnerEntity.getOrderEntity();
        } catch (Exception e){
            return "Order or Runner not found";
        }
        em.getTransaction().begin();
        orderEntity.setStatus(OrderEntity.orderStatus.delivered);
        runnerEntity.getCompletedOrders().add(orderEntity);
        runnerEntity.setStatus(RunnerEntity.Status.available);
        em.merge(runnerEntity);
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is delivering";
    }

    //edit order to add meals
    @PUT
    @Path("editOrder/{orderId}/{customerId}")
    public String editOrderAdd(List<Integer> mealsIds, @PathParam("orderId") long orderId,@PathParam("customerId") long customerId) {
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
        double old = orderEntity.getTotalPrice();
        orderEntity.setTotalPrice(old + totalPrice);
        em.getTransaction().begin();
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is edited";
    }

    //edit order to remove meals
    @PUT
    @Path("editOrderRemove/{orderId}/{customerId}")
    public String editOrderRemove(List<Integer> mealsIds, @PathParam("orderId") long orderId,@PathParam("customerId") long customerId) {
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
        double old = orderEntity.getTotalPrice();
        orderEntity.setTotalPrice(old - totalPrice);
        em.getTransaction().begin();
        em.merge(orderEntity);
        em.getTransaction().commit();
        return "Order is edited";
    }
}
