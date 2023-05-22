package Restaurant;

import Meal.MealEntity;
import Meal.MealService;
import Order.OrderEntity;
import Order.OrderService;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/restaurant")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();


    @POST
    @Path("createRestaurant/{ownerId}")
    public String createRestaurant(RestaurantEntity restaurant,@PathParam("ownerId") int ownerId) {
        RestaurantEntity restaurantEntity = null;
        UserEntity owner = em.find(UserEntity.class, ownerId);
        try {
            String query = "SELECT u FROM RestaurantEntity u WHERE u.owner = \"" + owner + "\"";
            Query query1 = em.createQuery(query);
            restaurantEntity = (RestaurantEntity) query1.getSingleResult();
        } catch (Exception e) {
        }
        if (restaurantEntity == null) {
            em.getTransaction().begin();
            restaurant.setOwner(owner);
            em.persist(restaurant);
            em.getTransaction().commit();
            return "Restaurant Created Successfully";
        } else {
            return "You already Have A Restaurant";
        }
    }

    //add meal
    @POST
    @Path("createMeal/{restaurantId}")
    public String createMeal(MealEntity mealEntity, @PathParam("restaurantId") int restaurantId){
        MealService mealService = new MealService();
        return mealService.createMeal(mealEntity, restaurantId);
    }

    //add meal to restaurant menu
    @POST
    @Path("createRestaurantMenu/{restaurantId}")
    public String createRestaurantMenu(List<Integer> mealIds, @PathParam("restaurantId") int restaurantId){
        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        StringBuilder result = new StringBuilder();
        for (int mealId : mealIds) {
            try{
                MealEntity mealEntity = em.find(MealEntity.class, mealId);
                if(mealEntity.getRestaurantEntity().getId() != restaurantId){
                    result.append(mealEntity.getName()).append(" does not exist\n");
                }else if (mealEntity.getRestaurantEntity().getId() == restaurantId && restaurantEntity.getMenu().contains(mealEntity)){
                    result.append(mealEntity.getName()).append(" already exists\n");
                }else{
                    restaurantEntity.addMealToMenu(mealEntity);
                    result.append(mealEntity.getName()).append(" added successfully\n");
                }
            }catch (Exception e){
                result.append("fault at the loop does not exist\n");
            }
        }
        return result.toString();
    }

    //get menu
    @GET
    @Path("getMenu/{restaurantId}")
    public List<MealEntity> getMenu(@PathParam("restaurantId") int restaurantId){
        try{
            RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
            return restaurantEntity.getMenu();
        }catch (Exception e){
            return null;
        }
    }

    //remove meal from restaurant
    @DELETE
    @Path("removeMeal/{restaurantId}/{mealId}")
    public String removeMeal(@PathParam("mealId") int mealId, @PathParam("restaurantId") int restaurantId){
        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        MealEntity mealEntity = em.find(MealEntity.class, mealId);
        try{
            if(mealEntity.getRestaurantEntity().getId() != restaurantId){
                return mealEntity.getName() + " does not exist";
            }else if (mealEntity.getRestaurantEntity().getId() == restaurantId && !restaurantEntity.getMeals().contains(mealEntity)){
                return mealEntity.getName() + " does not exist";
            }else{
                restaurantEntity.removeMeal(mealEntity);
                em.remove(mealEntity);
                return mealEntity.getName() + " removed successfully";
            }
        }catch (Exception e){
            return "fault at the loop";
        }
    }

    //list restaurant meals
    @GET
    @Path("listMeals/{restaurantId}")
    public List<MealEntity> getMeals(@PathParam("restaurantId") int restaurantId){
        MealService mealService = new MealService();
        return mealService.getRestaurantMeals(restaurantId);
    }

    //get restaurant by id
    @GET
    @Path("getRestaurantById/{id}")
    public RestaurantEntity getRestaurantById(@PathParam("id") int id){
        return em.find(RestaurantEntity.class, id);
    }

    //update meal price
    @PUT
    @Path("updateMealPrice/{restaurantId}/{mealId}/{newPrice}")
    public String updateMealPrice(@PathParam("restaurantId") int restaurantId, @PathParam("mealId") int mealId, @PathParam("newPrice") double newPrice){
        MealService mealService = new MealService();
        try {
            mealService.getMeal(mealId, restaurantId);
            MealEntity mealEntity = mealService.getMeal(mealId, restaurantId);
            mealEntity.setPrice(newPrice);
            em.getTransaction().begin();
            em.merge(mealEntity);
            em.getTransaction().commit();
            return mealEntity.getName() + " Price Updated Successfully";
        }catch (Exception e){
            return "Meal does not exist";
        }
    }

    //list all restaurants
    @GET
    @Path("listAllRestaurants")
    public List<RestaurantEntity> listAllRestaurants(){
        try{
            String query = "SELECT u FROM RestaurantEntity u";
            Query query1 = em.createQuery(query);
            return query1.getResultList();
        }catch (Exception e){
            return null;
        }
    }

    //get all completed orders
    @GET
    @Path("getCompletedOrders/{restaurantId}")
    public List<OrderEntity> getAllRestaurantCompetedOrders(@PathParam("restaurantId") int restaurantId){
        String query = "SELECT u FROM OrderEntity u WHERE u.restaurantEntity.id = " + restaurantId + " AND u.status = \"Completed\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //get all cancelled orders
    @GET
    @Path("getCancelledOrders/{restaurantId}")
    public List<OrderEntity> getAllRestaurantCancelledOrders(@PathParam("restaurantId") int restaurantId){
        String query = "SELECT u FROM OrderEntity u WHERE u.restaurantEntity.id = " + restaurantId + " AND u.status = \"Cancelled\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //create restaurant report
    @GET
    @Path("createRestaurantReport/{restaurantId}")
    public String createReport(@PathParam("restaurantId") int restaurantId){
        double totalEarned = 0;
        List<OrderEntity> completedOrders = getAllRestaurantCompetedOrders(restaurantId);
        List<OrderEntity> cancelledOrders = getAllRestaurantCancelledOrders(restaurantId);
        for (OrderEntity completedOrder : completedOrders) {
            totalEarned += completedOrder.getTotalPrice();
        }
        return "Total Earned: " + totalEarned + "\n" +
                "Total Completed Orders: " + completedOrders.size() + "\n" +
                "Total Cancelled Orders: " + cancelledOrders.size() + "\n";
    }




}
