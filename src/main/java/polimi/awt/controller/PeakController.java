package polimi.awt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import polimi.awt.Utils;
import polimi.awt.logic.AnnotationLogic;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.PeakLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Annotation;
import polimi.awt.model.Peak;
import polimi.awt.model.UserPV;
import polimi.awt.utils.Message;

@Controller
public class PeakController {

    @Autowired
    CampaignLogic campaignLogic;

    @Autowired
    AnnotationLogic annotationLogic;

    @Autowired
    PeakLogic peakLogic;

    @Autowired
    UserLogic userLogic;

    @Autowired
    Utils utils;

    //peak details
    @GetMapping("/peaks/{peakId}")
    public String peakDetails(@PathVariable Long peakId, Model model) {
        Peak peak = peakLogic.findPeakById(peakId);
        Page<Annotation> annotations = annotationLogic.findAnnotationByPeak(peakId, 0, 100);
        ModelAndView modelAndView = new ModelAndView();
        model.addAttribute("peak", peak);

        //for the manager profile
        model.addAttribute("annotations", annotations.getContent());
        model.addAttribute("annotationSelected", null);

        UserPV userInSession = utils.getUserFromSession();
        String rolInSession = utils.getRolUserInSession();

        // for worker profile
        if (rolInSession.equals("worker")){
            if (campaignLogic.isWorkerSubscribedToCampaign(peak.getCampaign(), userInSession)){
                model.addAttribute("isSuscribedToCampaign", true);
            }else{
                model.addAttribute("isSuscribedToCampaign", false);
                Message message = new Message("Warning", "You are not subscribed to this campaign. To start adding annotations, you must subscribe to it first!");
                model.addAttribute("message", message);
            }
        }

        //we first serialize the peak to a JSON
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String peakJson = gson.toJson(peak);
        model.addAttribute("peakJson", peakJson);

        return "/peakDetails";
    }

}