package Owner;

import Meal.MealEntity;
import Restaurant.RestaurantEntity;
import Restaurant.RestaurantService;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/Owner")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OwnerService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    //create restaurant
    @POST
    @Path("createRestaurant/{ownerId}")
    public String createRestaurant(RestaurantEntity restaurant,@PathParam("ownerId") int ownerId) {
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.createRestaurant(restaurant,ownerId);
    }

    //add meal
    @POST
    @Path("createMeal/{ownerId}")
    public String createMeal(MealEntity mealEntity, @PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.createMeal(mealEntity, restaurantId);
    }

    //add meals to restaurant menu
    @POST
    @Path("addMealToMenu/{ownerId}")
    public String addMealToMenu(List<Integer> mealIds, @PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.createRestaurantMenu(mealIds, restaurantId);
    }

    //remove meal from restaurant
    @DELETE
    @Path("removeMeal/{ownerId}/{mealId}")
    public String removeMeal(@PathParam("ownerId") int ownerId, @PathParam("mealId") int mealId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.removeMeal(mealId, restaurantId);
    }

    //get menu
    @GET
    @Path("getMenu/{ownerId}")
    public List<MealEntity> getMenu(@PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getMenu(restaurantId);
    }

    //list restaurants meals
    @GET
    @Path("listMeals/{ownerId}")
    public List<MealEntity> listMeals(@PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getMeals(restaurantId);
    }

    //update meal price
    @PUT
    @Path("updateMealPrice/{ownerId}/{mealId}/{newPrice}")
    public String updateMealPrice(@PathParam("ownerId") int ownerId, @PathParam("mealId") int mealId, @PathParam("newPrice") double newPrice){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.updateMealPrice(mealId, restaurantId, newPrice);
    }

    //get all completed orders
    @GET
    @Path("getCompletedOrders/{ownerId}")
    public List<Order.OrderEntity> getCompletedOrders(@PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getAllRestaurantCompetedOrders(restaurantId);
    }

    //get all cancelled orders
    @GET
    @Path("getCancelledOrders/{ownerId}")
    public List<Order.OrderEntity> getCancelledOrders(@PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.getAllRestaurantCancelledOrders(restaurantId);
    }

    //create restaurant report
    @GET
    @Path("createReport/{ownerId}")
    public String createReport(@PathParam("ownerId") int ownerId){
        UserEntity owner = em.find(UserEntity.class, ownerId);
        int restaurantId = (int) owner.getRestaurantEntity().getId();
        RestaurantService restaurantService = new RestaurantService();
        return restaurantService.createReport(restaurantId);
    }


}
