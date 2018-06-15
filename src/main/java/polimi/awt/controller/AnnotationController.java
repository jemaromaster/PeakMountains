package polimi.awt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import polimi.awt.Utils;
import polimi.awt.logic.AnnotationLogic;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.PeakLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Annotation;
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

}