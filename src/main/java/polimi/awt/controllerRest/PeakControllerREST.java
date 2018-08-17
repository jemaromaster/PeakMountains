package polimi.awt.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import polimi.awt.Utils;
import polimi.awt.logic.AnnotationLogic;
import polimi.awt.logic.PeakLogic;
import polimi.awt.model.Peak;
import polimi.awt.model.UserPV;

@RestController
@RequestMapping("peaks")
public class PeakControllerREST {

    @Autowired
    PeakLogic peakLogic;

    @Autowired
    AnnotationLogic annotationLogic;

    @Autowired
    Utils utils;


    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Peak>> getAllPeaksFromCampaign(@RequestParam(required = false) Long campaignId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            if (campaignId != null) {//List all the peaks from a campaign
                Page<Peak> pageCampaign = peakLogic.findPeakByCampaign(campaignId, page, size);
                return new ResponseEntity(pageCampaign, HttpStatus.OK);
            } else { //list all the peaks
                Page<Peak> pagePeaks = peakLogic.findAll(page, size);
                return new ResponseEntity(pagePeaks, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/{peakId}", produces = "application/json")
    public Peak getPeakById(@PathVariable Long peakId) {
        Peak peakFound = peakLogic.findPeakById(peakId);
        return peakFound;
    }

    @GetMapping(value = "/{peakId}/isAnnotatedByUser", produces = "application/json")
    public Boolean isPeakAnnotatedByUser(@PathVariable Long peakId) {
        Peak peak = peakLogic.findPeakById(peakId);
        UserPV getUserFromSession = utils.getUserFromSession();
        Boolean toR = annotationLogic.isAnnotatedByUser(peak, getUserFromSession);
        return toR;
    }

}
