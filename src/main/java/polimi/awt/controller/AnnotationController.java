package polimi.awt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
public class AnnotationController {

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

    //annotation details
    @GetMapping("/annotations/{annotationId}")
    public String getAnnotationDetails(@PathVariable Long annotationId, Model model) {
        Annotation annotation = annotationLogic.findAnnotationById(annotationId);

        model.addAttribute("annotationSelected", annotation);
        return "/fragments/modalRejectPeak :: modal-body";
    }

    @PostMapping("/annotations/{annotationId}/reject")
    public ModelAndView rejectAnnotation(@PathVariable Long annotationId, Model model, RedirectAttributes redir) {

        Message message = null;
        ModelAndView modelAndView = new ModelAndView();
        Annotation ann = annotationLogic.findAnnotationById(annotationId);
        modelAndView.setViewName("redirect:/peaks/" + ann.getPeak().getId());
        try {
            //reject annotation
            annotationLogic.rejectAnnotation(annotationId);
            message = new Message("Success", "The annotation has been rejected successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem rejecting the campaign with ID=" + ann.getId() + "." + e.getMessage() + ". ");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    //annotation details
    @PostMapping("/annotations/{annotationId}/accept")
    public ModelAndView acceptAnnotation(@PathVariable Long annotationId, Model model, RedirectAttributes redir) {

        Message message = null;
        ModelAndView modelAndView = new ModelAndView();
        Annotation ann = annotationLogic.findAnnotationById(annotationId);
        modelAndView.setViewName("redirect:/peaks/" + ann.getPeak().getId());
        try {
            //accept annotation
            annotationLogic.acceptAnnotation(annotationId);
            message = new Message("Success", "The annotation has been accepted successfully.");
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", "There was a problem accepting the campaign with ID=" + ann.getId() + "." + e.getMessage() + ". ");
            model.addAttribute("message", message);
        }

        //in order to redirect to a previous page, and add a message
        redir.addFlashAttribute("message", message);
        return modelAndView;
    }

    //peak details
    @GetMapping("/peaks/{peakId}/annotate")
    public String peakEdit(@PathVariable Long peakId, Model model) {
        Peak peak = peakLogic.findPeakById(peakId);
        Page<Annotation> annotations = annotationLogic.findAnnotationByPeak(peakId, 0, 100);

        Annotation ann = null;
        UserPV userInSession = utils.getUserFromSession();
        String rolInSession = utils.getRolUserInSession();

        if (annotations.getTotalElements() == 0l) {
            ann = annotationLogic.createAnnotationByPeak(peak, userInSession);
        } else {
            ann = annotations.getContent().get(0);
        }

        ModelAndView modelAndView = new ModelAndView();
        model.addAttribute("annotation", ann);
        model.addAttribute("peak", peak);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String jsonListLocalizedNames = gson.toJson(ann.getLocalizedNames());

        model.addAttribute("listLocalizedNames", jsonListLocalizedNames);

        // for worker profile
        if (rolInSession.equals("worker")) {
            if (campaignLogic.isWorkerSubscribedToCampaign(peak.getCampaign(), userInSession)) {
                model.addAttribute("isSuscribedToCampaign", true);
            } else {
                model.addAttribute("isSuscribedToCampaign", false);
                Message message = new Message("Warning", "You are not subscribed to this campaign. To start adding annotations, you must subscribe to it first!");
                model.addAttribute("message", message);
                return "/home";
            }
        }

        return "/peakEdit";
    }

    @PostMapping("/annotations")
    public String peakAnnotate(@ModelAttribute(name = "annotation") Annotation annotation,
                               @RequestParam(name = "peakId", required = true) Long peakId,
                               Model model) {
        Message message = null;
        ModelAndView modelAndView = new ModelAndView();

        try {
            annotationLogic.createAnnotation(annotation, peakId);
            message = new Message("Success", "Annotation created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message("Warning", e.getMessage());
            model.addAttribute("message", message);
            return "redirect:home";
        }

        return "redirect:home";
    }
}