package Meal;


import Orders.OrderEntity;
import Restaurant.RestaurantEntity;
import Restaurant.RestaurantService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/meal")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealService {

    @PersistenceContext
    private EntityManager em;

    //create meal
    @POST
    @Path("createMeal/{restaurantId}")
    public String createMeal(MealEntity meal, @PathParam("restaurantId") long restaurantId) {
        RestaurantEntity restaurantEntity = null;
        restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        if (restaurantEntity.getMeals().contains(meal)) {
            return "Meal Already Exists";
        }
        meal.setRestaurantEntity(restaurantEntity);
        restaurantEntity.getMeals().add(meal);
        try {
            em.merge(restaurantEntity);
        } catch (Exception e) {
            return "Meal already exists";
        }
        return "Meal Created Successfully";
    }

    //get all meals
    @GET
    @Path("getAllMeals")
    public List<MealEntity> getAllMeals(){
        List<MealEntity> mealEntities = null;
        try{
            String query = "SELECT u FROM MealEntity u";
            Query query1 = em.createQuery(query);
            mealEntities = query1.getResultList();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return mealEntities;
    }

    //get meal by id
    @GET
    @Path("getMeal/{restaurantId}/{mealId}")
    public MealEntity getMeal(@PathParam("restaurantId") long restaurantId,@PathParam("mealId") long mealId) {
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

}
