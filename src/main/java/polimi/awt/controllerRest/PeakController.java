package polimi.awt.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import polimi.awt.logic.PeakLogic;
import polimi.awt.model.Peak;

@RestController
@RequestMapping("peaks")
public class PeakController {

    @Autowired
    PeakLogic peakLogic;


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

}
