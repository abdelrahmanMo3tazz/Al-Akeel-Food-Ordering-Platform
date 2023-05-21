package Restaurant;

import Meal.MealEntity;
import User.UserEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
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

    //add meal to restaurant
    @POST
    @Path("addMeal/{restaurantId}")
    public String addMeal(List<Integer> mealIds, @PathParam("restaurantId") int restaurantId){
        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, restaurantId);
        StringBuilder result = new StringBuilder();
        for (int mealId : mealIds) {
            try{
                MealEntity mealEntity = em.find(MealEntity.class, mealId);
                if(mealEntity.getRestaurantEntity().getId() != restaurantId){
                    result.append(mealEntity.getName()).append(" does not exist\n");
                }else if (mealEntity.getRestaurantEntity().getId() == restaurantId && restaurantEntity.getMeals().contains(mealEntity)){
                    result.append(mealEntity.getName()).append(" already exists\n");
                }else{
                    restaurantEntity.addMeal(mealEntity);
                    result.append(mealEntity.getName()).append(" added successfully\n");
                }
            }catch (Exception e){
                result.append("fault at the loop does not exist\n");
            }
        }
        return result.toString();
    }

    //remove meal from restaurant
    @PUT
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
                return mealEntity.getName() + " removed successfully";
            }
        }catch (Exception e){
            return "fault at the loop";
        }
    }

    //list meals
    @GET
    @Path("listMeals")
    public List<MealEntity> getMeals(){
        String query = "SELECT u FROM MealEntity u";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //get restaurant by id
    @GET
    @Path("getRestaurantById/{id}")
    public RestaurantEntity getRestaurantById(@PathParam("id") int id){
        return em.find(RestaurantEntity.class, id);
    }

    //create restaurant report
//    @GET
//    @Path("createRestaurantReport/{id}")
//    public String createRestaurantReport(@PathParam("id") int id){
//        RestaurantEntity restaurantEntity = em.find(RestaurantEntity.class, id);
//        StringBuilder result = new StringBuilder();
//        result.append("Restaurant Name: ").append(restaurantEntity.getName()).append("\n");
//        result.append("Restaurant Owner: ").append(restaurantEntity.getOwner().getUsername()).append("\n");
//        result.append("Restaurant Completed Orders: ").
//        return result.toString();
//    }
}
