package Meal;


import Restaurant.RestaurantEntity;
import Restaurant.RestaurantService;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/meal")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    //create meal
    @POST
    @Path("createMeal/{restaurantId}}")
    public String createMeal(MealEntity meal, @PathParam("restaurantId") int restaurantId) {
        MealEntity mealEntity = null;
        RestaurantEntity restaurantEntity = null;
        try{
            restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        }catch (Exception e){
            return "Restaurant does not exist";
        }
        try {
            String name = meal.getName();
            String query = "SELECT u FROM MealEntity u WHERE u.name = \"" + name + "\" AND u.restaurant = \"" + restaurantEntity + "\"";
            Query query1 = em.createQuery(query);
            mealEntity = (MealEntity) query1.getSingleResult();
        } catch (Exception e) {
        }
        if (mealEntity == null) {
            em.getTransaction().begin();
            meal.setRestaurantEntity(restaurantEntity);
            restaurantEntity.addMeal(meal);
            em.persist(meal);
            em.getTransaction().commit();
            return "Meal Created Successfully";
        } else {
            return "Meal Already Exists";
        }
    }

    //get meal by id
    @GET
    @Path("getMeal/{restaurantId}/{mealId}")
    public MealEntity getMeal(@PathParam("restaurantId") int restaurantId,@PathParam("mealId") int mealId) {
        MealEntity mealEntity = null;
        RestaurantEntity restaurantEntity = null;
        try {
            mealEntity = em.find(MealEntity.class, mealId);
            restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
            if (!(mealEntity.getRestaurantEntity() == restaurantEntity)) {
                throw new Exception("Meal does not belong to this restaurant");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mealEntity;
    }

    //get restaurant's meals
    @GET
    @Path("getRestaurantMeals/{restaurantId}")
    public List<MealEntity> getRestaurantMeals(@PathParam("restaurantId") int restaurantId) {
        List<MealEntity> mealEntities = null;
        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        try {
            String query = "SELECT u FROM MealEntity u WHERE u.restaurant = \"" + restaurantEntity + "\"";
            Query query1 = em.createQuery(query);
            mealEntities = query1.getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mealEntities;
    }

    //remove meal from restaurant
    @DELETE
    @Path("removeMeal/{mealId}/{restaurantId}")
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
            em.getTransaction().begin();
            em.remove(mealEntity);
            em.getTransaction().commit();
            return "Meal removed successfully";
        }else{
            return "Meal does not belong to this restaurant";
        }
    }
}
