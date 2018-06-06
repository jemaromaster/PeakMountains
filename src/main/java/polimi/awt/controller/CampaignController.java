package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polimi.awt.Utils;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Campaign;
import polimi.awt.model.UserPV;
import polimi.awt.storage.StorageException;
import polimi.awt.utils.Message;

import java.util.List;

@Controller
public class CampaignController {

    @Autowired
    CampaignLogic campaignLogic;

    @Autowired
    UserLogic userLogic;

    @Autowired
    Utils utils;

    //campaign list
    @GetMapping("/home")
    public String campaigns(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        UserPV userInSession = utils.getUserFromSession();
        List<Campaign> campaignList = campaignLogic.listCampaignByManager(userInSession.getUsername(), 0, 100).getContent();
        model.addAttribute("campaignList", campaignList);
        return "/myCampaigns";
    }

    //campaign list
    @GetMapping("/campaignDetails")
    public String getCampaigns(@RequestParam Long campaignId, Model model) {
        Campaign campaign = campaignLogic.findCampaignById(campaignId);
        model.addAttribute("campaign", campaign);
        return "/campaignDetails";
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

//    @PostMapping("/login")
//    public String loginPost(@ModelAttribute(name = "user") UserPV userPV) {
//        Boolean bool = userLogic.loginUsernamePass(userPV.getUsername(), userPV.getPassword());
//
//        if (bool==false) { //if not equals to pass
//            return "redirect:/403";
//        } else {
//            return "redirect:/";
//        }
//    }


    @GetMapping("/newCampaign")
    public String createCampaign(Model model) {
        model.addAttribute("campaign", new Campaign());
        return "/newCampaign";
    }

    @PostMapping("/newCampaign")
    public ModelAndView createCampaign(@ModelAttribute(name = "campaign") Campaign campaign, Model model, RedirectAttributes redir) {
        Message message = null;
        try {
            campaignLogic.createCampaign(campaign);
            message = new Message("Success", "The new campaign created successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem creating the campaign.");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:home");
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    @GetMapping("/uploadPeakFile")
    public ModelAndView uploadPeakFileView(@RequestParam Long campaignId, Model model, RedirectAttributes redirectAttributes) {
        Campaign campaign = campaignLogic.findCampaignById(campaignId);
        ModelAndView modelAndView = new ModelAndView();

        //in case that the user is trying to access the url without the manager privilege over his campaign
        UserPV userInSession = utils.getUserFromSession();
        if (campaign.getUsrManager().getId() != userInSession.getId()) {
            redirectAttributes.addFlashAttribute("message", new Message("warning", "Only the campaign manager can upload files into the campaign"));
            modelAndView.setViewName("redirect:home");
            return modelAndView;
        }
        model.addAttribute("campaign", campaign);
        modelAndView.setViewName("/uploadPeakFile");

        return modelAndView;
    }

    @PostMapping("/uploadPeakFile")
    public ModelAndView handleFilePeakUpload(@ModelAttribute(name = "campaign") Campaign campaign,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("toAnnotate") Boolean toAnnotate,
                                             RedirectAttributes redirectAttributes) {
        Message message = null;
        try {
            campaignLogic.StoreAndProcessFile(file, campaign.getId(), toAnnotate);
            message = new Message("Success", "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (StorageException e) {
            e.printStackTrace();
            message = new Message("Error", "Error upload file. " + e.getMessage());
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/campaignDetails" + "?campaignId=" + campaign.getId());
        redirectAttributes.addFlashAttribute("message", message);
        return modelAndView;

    }

}