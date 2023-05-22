package Runner;

import Order.OrderEntity;
import Order.OrderService;
import Restaurant.RestaurantEntity;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Random;

@Path("/runner")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RunnerService {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");

    private final EntityManager em = entityManagerFactory.createEntityManager();

    @GET
    @Path("getAvailableRunners")
    public List<RunnerEntity> getAvailableRunners() {
        String query = "SELECT u FROM RunnerEntity u WHERE u.status = \"Available\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //get busy runners
    @GET
    @Path("getBusyRunners")
    public List<RunnerEntity> getBusyRunners() {
        String query = "SELECT u FROM RunnerEntity u WHERE u.status = \"Busy\"";
        Query query1 = em.createQuery(query);
        return query1.getResultList();
    }

    //get runner by id
    @GET
    @Path("getRunner/{runnerId}")
    public RunnerEntity getRunner(@PathParam("runnerId") int runnerId) {
        RunnerEntity runnerEntity = null;
        try {
            runnerEntity = em.find(RunnerEntity.class, runnerId);
        } catch (Exception e) {
            throw new NotFoundException("Runner not found");
        }
        return runnerEntity;
    }


    //get random available runner
    @GET
    @Path("getRandomAvailableRunner")
    public RunnerEntity getRandomAvailableRunner() {
        String query = "SELECT u FROM RunnerEntity u WHERE u.status = \"Available\"";
        Query query1 = em.createQuery(query);
        List<RunnerEntity> runnerEntities = query1.getResultList();
        if(runnerEntities.size() > 0){
            Random random = new Random();
            int randomIndex = random.nextInt(runnerEntities.size());
            return runnerEntities.get(randomIndex);
        }else{
            throw new NotFoundException("No available runners");
        }
    }

    //deliever order
    @PUT
    @Path("deliverOrder/{runnerId}")
    public String deliverOrder(@PathParam("runnerId") int runnerId) {
        OrderService orderService = new OrderService();
        return orderService.deliverOrder(runnerId);
    }

    //list Completed Orders
    @GET
    @Path("listCompletedOrders/{runnerId}")
    public List<OrderEntity> listCompletedOrders(@PathParam("runnerId") int runnerId) {
        return getRunner(runnerId).getCompletedOrders();
    }

}
