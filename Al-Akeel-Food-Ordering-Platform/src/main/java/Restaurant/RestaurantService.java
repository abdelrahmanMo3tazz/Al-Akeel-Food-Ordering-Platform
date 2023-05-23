package Restaurant;

import Meal.MealEntity;
import Meal.MealService;
import Orders.OrderEntity;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/restaurant")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantService {

    @PersistenceContext
    private EntityManager em;



    @POST
    @Path("createRestaurant/{ownerId}")
    public String createRestaurant(RestaurantEntity restaurant,@PathParam("ownerId") long ownerId) {
        RestaurantEntity restaurantEntity = null;
        UserEntity owner = em.find(UserEntity.class, ownerId);
        if(owner.getRestaurantEntity() != null){
            return "You already have a restaurant";
        }
        restaurant.setEarns(0.0);
        restaurant.setOwner(owner);
        owner.setRestaurantEntity(restaurant);
        em.merge(owner);
        try {
            em.persist(restaurant);
        } catch (Exception e) {
            return "Restaurant already exists";
        }
        return "Restaurant Created Successfully";
//        UserEntity owner = em.find(UserEntity.class, ownerId);
//        restaurant.setOwner(owner);
//        owner.setRestaurantEntity(restaurant);
//        em.merge(owner);
//        em.persist(restaurant);
//        return "Restaurant Created Successfully";
    }

//    add meal
    @POST
    @Path("createMeal/{restaurantId}")
    public String createMeal(MealEntity mealEntity, @PathParam("restaurantId") long restaurantId){
        MealService mealService = new MealService();
        return mealService.createMeal(mealEntity, restaurantId);
    }


    //get menu
    @GET
    @Path("getMenu/{restaurantId}")
    public List<MealEntity> getMenu(@PathParam("restaurantId") long restaurantId){
        List<MealEntity> mealEntities = null;
//        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        try {
            String query = "SELECT u FROM MealEntity u WHERE u.restaurantEntity.id = \"" + restaurantId + "\"";
            Query query1 = em.createQuery(query);
            mealEntities = query1.getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mealEntities;
    }

    //remove meal from restaurant
    @DELETE
    @Path("removeMeal/{restaurantId}/{mealId}")
    public String removeMeal(@PathParam("mealId") int mealId, @PathParam("restaurantId") int restaurantId){
        MealEntity mealEntity = null;
        RestaurantEntity restaurantEntity = null;
        try{
            mealEntity = em.find(MealEntity.class, mealId);
            restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        }catch (Exception e){
            return "Meal or Restaurant does not exist";
        }
        if(mealEntity.getRestaurantEntity().equals(restaurantEntity)){
            restaurantEntity.getMeals().remove(mealEntity);
//            em.getTransaction().begin();
            em.remove(mealEntity);
//            em.getTransaction().commit();
            return "Meal removed successfully";
        }else{
            return "Meal does not belong to this restaurant";
        }
    }

    //get restaurant by id
    @GET
    @Path("getRestaurantById/{id}")
    public RestaurantEntity getRestaurantById(@PathParam("id") long id){
        return em.find(RestaurantEntity.class, id);
    }

    //update meal price
    @PUT
    @Path("updateMealPrice/{restaurantId}/{mealId}/{newPrice}")
    public String updateMealPrice(@PathParam("restaurantId") long restaurantId, @PathParam("mealId") long mealId, @PathParam("newPrice") double newPrice){
        MealService mealService = new MealService();
        try {
            MealEntity mealEntity = em.find(MealEntity.class, mealId);
            mealEntity.setPrice(newPrice);
            em.merge(mealEntity);
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
    public List<OrderEntity> getAllRestaurantCompetedOrders(@PathParam("restaurantId") long restaurantId){
        String query = "SELECT u FROM OrderEntity u WHERE u.restaurant.id = " + restaurantId + " AND u.status = \"Completed\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //get all cancelled orders
    @GET
    @Path("getCancelledOrders/{restaurantId}")
    public List<OrderEntity> getAllRestaurantCancelledOrders(@PathParam("restaurantId") long restaurantId){
        String query = "SELECT u FROM OrderEntity u WHERE u.restaurant.id = " + restaurantId + " AND u.status = \"Cancelled\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //create restaurant report
    @GET
    @Path("createRestaurantReport/{restaurantId}")
    public String createReport(@PathParam("restaurantId") long restaurantId){
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
