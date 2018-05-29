package polimi.awt.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import polimi.awt.model.Campaign;
import polimi.awt.model.Peak;
import polimi.awt.repo.CampaignRepository;
import polimi.awt.repo.PeakRepository;
import polimi.awt.repo.UserRepository;

@Component
public class PeakLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PeakRepository peakRepository;

    @Autowired
    CampaignRepository campaignRepository;

    public Page<Peak> findAll(Integer page, Integer size) {
        return peakRepository.findAll(new PageRequest(page, size));
    }

    public Page<Peak> findPeakByCampaign(Long campaignId, Integer page, Integer size) {
        Campaign campaign = campaignRepository.findCampaignById(campaignId);
        if (campaign==null){
            throw new RuntimeException("Campaign " + campaignId + " not found");
        }
        return peakRepository.findPeakByCampaign(campaign, new PageRequest(page, size));
    }

    public Peak findPeakById(Long findById) {
        //we get the user from the session
        return peakRepository.findPeakByIdEquals(findById);
    }


}
