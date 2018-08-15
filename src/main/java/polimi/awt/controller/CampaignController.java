package polimi.awt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polimi.awt.Utils;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.PeakLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Campaign;
import polimi.awt.model.Peak;
import polimi.awt.model.UserPV;
import polimi.awt.storage.StorageException;
import polimi.awt.utils.Message;
import polimi.awt.utils.Statistics;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class CampaignController {

    @Autowired
    CampaignLogic campaignLogic;

    @Autowired
    PeakLogic peakLogic;

    @Autowired
    UserLogic userLogic;

    @Autowired
    Utils utils;

    //campaign list
    @GetMapping("/home")
    public String campaigns(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        UserPV userInSession = utils.getUserFromSession();
        String rolInSession = utils.getRolUserInSession();

        //get the role of the user. If the user has both privileges, it access to the manager only
        if (rolInSession.equals("manager")) {
            List<Campaign> campaignList = campaignLogic.listCampaignByManager(userInSession.getUsername(), 0, 100).getContent();
            model.addAttribute("campaignList", campaignList);
            return "/myCampaigns";
        } else { //the role is a worker. Display the worker home page

            List<Campaign> campaignsJoined = campaignLogic.listCampaignByWorkers(userInSession, 0, 100);
            model.addAttribute("campaignsJoined", campaignsJoined);

            Set<UserPV> set = new HashSet();
            set.add(userInSession);
            List<Campaign> allCampaigns = campaignLogic.listCampaignByWorkersJoinedIsNotAndStatusEquals(set, "started", 0, 100).getContent();
            model.addAttribute("allCampaignStarted", allCampaigns);

            return "/workerCampaigns";
        }

    }

    //campaign list
    @GetMapping("/campaign/{campaignId}")
    public String getCampaigns(@PathVariable Long campaignId, Model model) {
        Campaign campaign = campaignLogic.findCampaignById(campaignId);
        Page<Peak> listaPeaks = peakLogic.findPeakByCampaign(campaignId, 0, 50);
        model.addAttribute("campaign", campaign);
        List<Peak> list = listaPeaks.getContent();

        //if there is a worker in session
        UserPV userInSession = utils.getUserFromSession();
        String rolUserInSession = utils.getRolUserInSession();
        if (rolUserInSession.equals("worker")) {
            Boolean b = campaignLogic.isWorkerSubscribedToCampaign(campaign, userInSession);
            model.addAttribute("subscribedTo", b);
        } else {
            model.addAttribute("subscribedTo", null);
        }

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String jsonList = gson.toJson(list);

        model.addAttribute("listaPeaks", jsonList);

        if (rolUserInSession.equals("worker")) {
            // for worker profile
            if (campaignLogic.isWorkerSubscribedToCampaign(campaign, userInSession)) {
                model.addAttribute("isSuscribedToCampaign", true);
            } else {
                model.addAttribute("isSuscribedToCampaign", false);
                Message message = new Message("Warning", "You are not subscribed to this campaign. To start adding annotations, you must subscribe to it first!");
                model.addAttribute("message", message);
            }
            return "/campaignDetailsWorker";
        } else
            return "/campaignDetails";
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/campaign/new")
    public String createCampaign(Model model) {
        model.addAttribute("campaign", new Campaign());
        return "/newCampaign";
    }

    @PostMapping("/campaign/new")
    public ModelAndView createCampaign(@ModelAttribute(name = "campaign") Campaign campaign, Model model, RedirectAttributes redir,
                                       HttpServletRequest httpServletRequest) {
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
        modelAndView.setViewName("redirect:" + httpServletRequest.getContextPath() + "/home");
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    @GetMapping("/campaign/{campaignId}/uploadFile")
    public ModelAndView uploadPeakFileView(@PathVariable(name = "campaignId") Long campaignId, Model model, RedirectAttributes redirectAttributes) {
        Campaign campaign = campaignLogic.findCampaignById(campaignId);
        ModelAndView modelAndView = new ModelAndView();

        //in case that the user is trying to access the url without the manager privilege over his campaign
        UserPV userInSession = utils.getUserFromSession();
        if (campaign.getUsrManager().getId() != userInSession.getId()) {
            redirectAttributes.addFlashAttribute("message", new Message("Warning", "Only the campaign manager can upload files into the campaign"));
            modelAndView.setViewName("redirect:/home");
            return modelAndView;
        }
        model.addAttribute(campaign);
        model.addAttribute("toBeAnnotated", new Boolean(true));
        modelAndView.setViewName("/uploadPeakFile");

        return modelAndView;
    }

    @GetMapping("/campaign/{campaignId}/statistics")
    public ModelAndView viewPeakStatistics(@PathVariable(name = "campaignId") Long campaignId, Model model, RedirectAttributes redirectAttributes) {
        Campaign campaign = campaignLogic.findCampaignById(campaignId);
        ModelAndView modelAndView = new ModelAndView();

        //in case that the user tries to access the url without the manager privilege over the campaign
        UserPV userInSession = utils.getUserFromSession();
        if (campaign.getUsrManager().getId() != userInSession.getId()) {
            redirectAttributes.addFlashAttribute("message", new Message("Warning", "Only the campaign manager visualize the statistics in a campaign"));
            modelAndView.setViewName("redirect:/home");
            return modelAndView;
        }

        Statistics stat = campaignLogic.getStatistics(campaign);
        model.addAttribute(campaign);
        model.addAttribute("statistics", stat);
        modelAndView.setViewName("/campaignStatistics");

        return modelAndView;
    }

    @PostMapping("/campaign/{campaignId}/uploadFile")
    public ModelAndView handleFilePeakUpload(@PathVariable(name = "campaignId") Long campaignId,
                                             @RequestParam("file") MultipartFile file,
                                             @ModelAttribute(name = "toBeAnnotated") Boolean toAnnotate,
                                             RedirectAttributes redirectAttributes) {
        Message message = null;
        try {
            campaignLogic.StoreAndProcessFile(file, campaignId, toAnnotate);
            message = new Message("Success", "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (StorageException e) {
            e.printStackTrace();
            message = new Message("Error", "Error upload file. " + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            message = new Message("Error", "Error uploading file. " + e.getMessage());
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/campaign/" + campaignId);
        redirectAttributes.addFlashAttribute("message", message);
        return modelAndView;

    }

    @PostMapping(value = "/campaign/{campaignId}/start")
    public ModelAndView startCampaign(@PathVariable("campaignId") Long campaignId, Model model, RedirectAttributes redir) {
        Message message = null;
        try {
            Campaign campToReturn = campaignLogic.startCampaign(campaignId);
            message = new Message("Success", "The campaign " + campToReturn.getName() + " has been started successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem starting the campaign with ID=" + campaignId + ".");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/campaign/" + campaignId);
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    @PostMapping(value = "/campaign/{campaignId}/close")
    public ModelAndView closedCampaign(@PathVariable("campaignId") Long campaignId, Model model, RedirectAttributes redir) {
        Message message = null;
        try {
            Campaign campToReturn = campaignLogic.closeCampaign(campaignId);
            message = new Message("Success", "The campaign " + campToReturn.getName() + " has been closed successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem closing the campaign with ID=" + campaignId + ".");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/campaign/" + campaignId);
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    @PostMapping(value = "/campaign/{campaignId}/subscribe")
    public ModelAndView subscribeToCampaign(@PathVariable("campaignId") Long campaignId, @Param("from") String from, Model model, RedirectAttributes redir) {
        Message message = null;
        try {
            Campaign campToReturn = campaignLogic.subscribeToCampaign(campaignId);
            message = new Message("Success", "You have suscribed to " + campToReturn.getName() + " successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem suscribing to the campaign with ID=" + campaignId + ".");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        ModelAndView modelAndView = new ModelAndView();

        if (from != null && from.equals("home")) { //to know from which page the request came
            modelAndView.setViewName("redirect:/home");
        } else {
            modelAndView.setViewName("redirect:/campaign/" + campaignId);
        }
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

}