package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Campaign;
import polimi.awt.model.UserPV;

import java.util.List;

@Controller
public class DefaultController {

    @Autowired
    CampaignLogic campaignLogic;

    @Autowired
    UserLogic userLogic;


    @GetMapping("/home")
    public String campaigns(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        List<Campaign> campaignList = campaignLogic.listCampaignByManager("john", 0, 100).getContent();
        model.addAttribute("campaignList", campaignList);
        return "/home";
    }


    @GetMapping("/admin")
    public String admin() {
        return "/admin";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/plain_page")
    public String user() {
        return "/plain_page";
    }

    @GetMapping("/about")
    public String about() {
        return "/about";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute(name = "user") UserPV userPV) {
        Boolean bool = userLogic.loginUsernamePass(userPV.getUsername(), userPV.getPassword());

        if (bool==false) { //if not equals to pass
            return "redirect:/403";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserPV());
        return "/login";
    }

    @GetMapping("/403")
    public String error403() {
        return "/error/page_403";
    }

}