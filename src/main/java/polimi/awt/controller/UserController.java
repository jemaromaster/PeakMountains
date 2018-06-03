package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;

import java.util.LinkedHashSet;

@Controller
public class UserController {

    @Autowired
    UserLogic userLogic;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute(name = "user") UserPV userPV, @ModelAttribute(name = "userType") String userType) {
//        try {
        //we create a privilege
        LinkedHashSet ls = new LinkedHashSet<Privilege>();
        ls.add(new Privilege(userType));
        userPV.setPrivileges(ls);

        UserPV userCreated = userLogic.createUser(userPV);

        return "/login";
//        } catch(DataIntegrityViolationException constraintException){
//            return new ResponseEntity("Username " + userBody.getUsername() + " already exist.",
//                    HttpStatus.CONFLICT);
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            return new ResponseEntity(e.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

}
