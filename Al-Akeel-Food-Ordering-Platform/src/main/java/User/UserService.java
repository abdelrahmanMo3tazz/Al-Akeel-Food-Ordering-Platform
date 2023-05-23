package User;

import Runner.RunnerEntity;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/user")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    @PersistenceContext
    EntityManager em;

    //done
    //register user
    @POST
    @Path("register")
    public String register(UserEntity user){
        UserEntity userEntity = null;
        RunnerEntity runnerEntity = new RunnerEntity();
        try{
            String name = user.getUsername();
            String query = "SELECT u FROM UserEntity u WHERE u.username = \"" + name + "\"";
            Query query1 = em.createQuery(query);
            userEntity = (UserEntity) query1.getSingleResult();
        }catch (Exception e){
//            return e.getMessage();
        }
        if(userEntity == null){
//            em.getTransaction().begin();
            if(user.getRole() != UserEntity.Role.runner){
                user.setFees(0);
//                em.persist(user);
            } else if (user.getRole() == UserEntity.Role.runner && user.getFees() == 0) {
                user.setFees(10);
                runnerEntity.setName(user.getUsername());
                runnerEntity.setDeliveryFees(10);
                runnerEntity.setStatus(RunnerEntity.Status.available);
                runnerEntity.setPassword(user.getPassword());
                em.persist(runnerEntity);
            } else {
                runnerEntity.setName(user.getUsername());
                runnerEntity.setDeliveryFees(user.getFees());
                runnerEntity.setStatus(RunnerEntity.Status.available);
                runnerEntity.setPassword(user.getPassword());
                em.persist(runnerEntity);
            }
            em.persist(user);
//            em.getTransaction().commit();
            return "User Registered Successfully";
        }
        else{
            return "User Already Exists";
        }
    }

    //test
    //login user
    @POST
    @Path("login")
    public String login(UserEntity user){
        UserEntity userEntity = null;
        try{
            String name = user.getUsername();
            String password = user.getPassword();
            String query = "SELECT u FROM UserEntity u WHERE u.username = \"" + name + "\" AND u.password = \"" + password + "\"";
            userEntity = (UserEntity) em.createQuery(query).getSingleResult();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(userEntity == null){
            return "User Not Found";
        }
        else{
            return "User Logged In Successfully";
        }
    }

    //done
    @GET
    @Path("userList")
    public List<UserEntity> userList(){
        List<UserEntity> userEntities = null;
        try{
            String query = "SELECT u FROM UserEntity u";
            userEntities = em.createQuery(query).getResultList();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return userEntities;

    }
}
