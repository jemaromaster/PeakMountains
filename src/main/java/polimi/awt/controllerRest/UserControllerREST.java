package polimi.awt.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.UserPV;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@RestController
@RequestMapping("users")
public class UserControllerREST {

    @Autowired
    UserLogic userLogic;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserPV userBody,
                                                     HttpServletResponse response) {
        try {
            UserPV userCreated = userLogic.createUser(userBody);

            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(userCreated.getId())
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return new ResponseEntity(headers, HttpStatus.CREATED);
        } catch(DataIntegrityViolationException constraintException){
            return new ResponseEntity("Username " + userBody.getUsername() + " already exist.",
                    HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{userName}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserPV> updateUser(@PathVariable String userName, @RequestBody UserPV userBody) {
        try {
            UserPV userUpdated = userLogic.updateUser(userName, userBody);

            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(userUpdated.getId())
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return new ResponseEntity("User " + userName + " updated correctly. ", headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get all the users
    @GetMapping(produces = "application/json")
    public Iterable<UserPV> findAllUsers() {
        return userLogic.findAll();
    }

    @PostMapping(value = "/login/{userName}", consumes = "application/json", produces = "application/json")
    public ResponseEntity login(@PathVariable String userName,
                        @RequestParam String pass) {
        try {
            Boolean result = userLogic.loginUsernamePass(userName, pass);
            if (result) {
                return new ResponseEntity("Logged as "+ userName, HttpStatus.OK);
            } else {
                return new ResponseEntity("Username and password failed to authenticate", HttpStatus.UNAUTHORIZED);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
