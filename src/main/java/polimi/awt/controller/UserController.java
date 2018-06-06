package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import polimi.awt.logic.UserLogic;
import polimi.awt.utils.Message;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;

import java.util.LinkedHashSet;

@Controller
public class UserController {

    @Autowired
    UserLogic userLogic;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute(name = "user") UserPV userPV, @ModelAttribute(name = "userType") String userType,
                               Model model) {
        //we create a privilege
        LinkedHashSet ls = new LinkedHashSet<Privilege>();
        ls.add(new Privilege(userType));
        userPV.setPrivileges(ls);

        UserPV userCreated = userLogic.createUser(userPV);

        //this is used for displaying the messages
        Message message = new Message("Success", "User registered correctly. Now login into your account to start.");
        model.addAttribute(message);

        return "/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserPV());
        model.addAttribute("userType", new String());
        return "/login";
    }

    @GetMapping("/403")
    public String error403() {

        return "/error/page_403";
    }
}
