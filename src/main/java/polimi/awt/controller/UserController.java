package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polimi.awt.Utils;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;
import polimi.awt.utils.Message;

import java.util.LinkedHashSet;


@Controller
public class UserController {
    // Inyecta: instancia de la clase en el container.
    @Autowired
    UserLogic userLogic;

    @Autowired
    Utils utils;

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

    // Leugo de logear recuperamos los datos de UserPV y userType de la vez anterior.
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

    // spring magic esto no lo hace
    @PostMapping("/login")
    public String loginPost(@ModelAttribute(name = "user") UserPV userPV) {
        Boolean bool = userLogic.loginUsernamePass(userPV.getUsername(), userPV.getPassword());

        if (bool == false) { //if not equals to pass
            return "redirect:/403";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        UserPV userInSession = utils.getUserFromSession();
        model.addAttribute("userInSession", userInSession);

        return "/profile";
    }

    @PostMapping("/profile")
    public ModelAndView profilePut(@ModelAttribute(name = "userInSession") UserPV userPV,
                                   Model model,
                                   RedirectAttributes redir) {

        Message message = null;

        try {
            userLogic.updateUser(userPV.getUsername(), userPV);
            message = new Message("Success", "The new information was changed successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem changing the personal information.");
            model.addAttribute("message", message);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/home/");
        redir.addFlashAttribute("message", message);

        return modelAndView;

    }

    @GetMapping("/password")
    public String password(Model model) {
        UserPV userInSession = utils.getUserFromSession();
        model.addAttribute("oldPass", "");
        model.addAttribute("pass1", "");
        model.addAttribute("pass2", "");
        model.addAttribute("userInSession", userInSession);
        return "/password";
    }

    // please remember that request param take from the name so we don't have to create an object
    @PostMapping("/password")
    public ModelAndView passwordPost(@RequestParam String pass1,
                                    @RequestParam String pass2,
                                    @RequestParam String oldPass,
                                    Model model,
                                    RedirectAttributes redir) {
        Message message = null;
        UserPV userPV = utils.getUserFromSession();

        if (!utils.comparePlaneToEncodedPassword(oldPass, userPV.getPassword())){
            message = new Message("Warning", "Incorrect old password. The password was not changed.");
            model.addAttribute("message", message);
        }else {
            if (pass1.equals(pass2)) {
                try {
                    userLogic.changePassword(userPV.getUsername(), userPV, pass1);
                    message = new Message("Success", "The new password was changed successfully.");
                    model.addAttribute("message", message);
                } catch (Exception e) {
                    e.printStackTrace();
                    message = new Message("Warning", "There was a problem changing the password.");
                    model.addAttribute("message", message);
                }
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/profile/");
        redir.addFlashAttribute("message", message);

        return modelAndView;
    }
}
