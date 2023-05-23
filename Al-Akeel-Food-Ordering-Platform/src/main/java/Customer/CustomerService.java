package Customer;

import Meal.MealEntity;
import Orders.OrderEntity;
import Orders.OrderService;
import Restaurant.RestaurantEntity;
import Restaurant.RestaurantService;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/customer")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    //list of customers
    @GET
    @Path("listOfCustomers")
    public List<UserEntity> listOfCustomers() {
        List<UserEntity> userEntities = null;
        try{
            String query = "SELECT u FROM UserEntity u WHERE u.role = \"customer\"";
            userEntities = em.createQuery(query).getResultList();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return userEntities;
    }

    //list all restaurants
    @GET
    @Path("listOfRestaurants")
    public List<RestaurantEntity> listOfRestaurants() {
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.listAllRestaurants();
    }

    //get restaurant by id
    @GET
    @Path("getRestaurant/{restaurantId}")
    public RestaurantEntity getRestaurant(@PathParam("restaurantId") long restaurantId) {
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getRestaurantById(restaurantId);
    }

    //get meals of restaurant
    @GET
    @Path("getMeals/{restaurantId}")
    public List<MealEntity> getMenu(@PathParam("restaurantId") long restaurantId) {
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getMenu(restaurantId);
    }

    //create order
    @POST
    @Path("createOrder/{restaurantId}/{customerId}")
    public String createOrder(List<Integer> mealsIds,@PathParam("customerId") long customerId,@PathParam("restaurantId") long restaurantId){
        OrderService orderService = new OrderService();
        return orderService.createOrder(mealsIds, customerId, restaurantId);
    }

    //cancel order
    @POST
    @Path("cancelOrder/{orderId}/{customerId}")
    public String cancelOrder(@PathParam("orderId") long orderId,@PathParam("customerId") long customerId){
        OrderService orderService = new OrderService();
        return orderService.cancelOrder(orderId, customerId);
    }

    //get all orders of customer
    @GET
    @Path("getAllOrders/{customerId}")
    public List<OrderEntity> getAllOrders(@PathParam("customerId") long customerId){
        OrderService orderService = new OrderService();
        return orderService.getCustomerOrders(customerId);
    }

    // edit order to add meals
    @POST
    @Path("editOrder/{orderId}/{customerId}")
    public String editOrderAdd(List<Integer> mealsIds,@PathParam("orderId") long orderId,@PathParam("customerId") long customerId){
        OrderService orderService = new OrderService();
        return orderService.editOrderAdd(mealsIds, orderId, customerId);
    }

    //edit order to remove meals
    @POST
    @Path("removeMeals/{orderId}/{customerId}")
    public String editOrderRemove(List<Integer> mealsIds,@PathParam("orderId") long orderId,@PathParam("customerId") long customerId){
        OrderService orderService = new OrderService();
        return orderService.editOrderRemove(mealsIds, orderId, customerId);
    }
}
