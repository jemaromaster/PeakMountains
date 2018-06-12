package polimi.awt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import polimi.awt.Utils;
import polimi.awt.logic.AnnotationLogic;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.logic.PeakLogic;
import polimi.awt.logic.UserLogic;
import polimi.awt.model.Annotation;
import polimi.awt.model.Peak;

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
    public String getCampaigns(@PathVariable Long peakId, Model model) {
        Peak peak = peakLogic.findPeakById(peakId);
        Page<Annotation> annotations = annotationLogic.findAnnotationByPeak(peakId, 0, 100);

        model.addAttribute("peak", peak);
        model.addAttribute("annotations", annotations.getContent());
        model.addAttribute("annotationSelected", null);

        //we first serialize the peak to a JSON
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String peakJson = gson.toJson(peak);
        model.addAttribute("peakJson", peakJson);
        return "/peakDetails";
    }

}