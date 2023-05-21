package Meal;


import Restaurant.RestaurantEntity;
import Restaurant.RestaurantService;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/meal")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    //create meal
    @POST
    @Path("createMeal")
    public String createMeal(MealEntity meal, int restaurantId) {
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
            em.persist(meal);
            em.getTransaction().commit();
            return "Meal Created Successfully";
        } else {
            return "Meal Already Exists";
        }
    }
}
