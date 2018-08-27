package polimi.awt.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import polimi.awt.Utils;
import polimi.awt.model.Campaign;
import polimi.awt.model.Peak;
import polimi.awt.repo.CampaignRepository;
import polimi.awt.repo.PeakRepository;
import polimi.awt.repo.UserRepository;

@Service
public class PeakLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PeakRepository peakRepository;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    Utils utils;

    public Page<Peak> findAll(Integer page, Integer size) {
        return peakRepository.findAll(new PageRequest(page, size));
    }

    public Page<Peak> findPeakByCampaign(Long campaignId, Integer page, Integer size) {
        Campaign campaign = campaignRepository.findCampaignById(campaignId);
        if (campaign == null) {
            throw new RuntimeException("Campaign " + campaignId + " not found");
        }
        return peakRepository.findPeakByCampaignAndAltitudeIsNotNullOrderByAltitudeDesc(campaign, new PageRequest(page, size));
    }

    public Peak findPeakById(Long findById) {
        //we get the user from the session
        Peak peak = peakRepository.findPeakByIdEquals(findById);
        return peak;
    }


    public Page<Peak> findPeakByCampaignAndColor(Campaign campaign, String color, Integer page, Integer size) {
        //we get the user from the session
        Page<Peak> peaks = peakRepository.findPeakByCampaignAndColor(campaign, color, new PageRequest(page, size));
        return peaks;
    }

    public Page<Peak> findPeakByCampaignAndConflicts(Campaign campaign, Boolean b, Integer page, Integer size) {
        //we get the user from the session
        Page<Peak> peaks = peakRepository.findPeakByCampaignAndConflicts(campaign, b, new PageRequest(page, size));
        return peaks;
    }

}
