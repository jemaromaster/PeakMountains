package polimi.awt.logic;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polimi.awt.Utils;
import polimi.awt.model.*;
import polimi.awt.repo.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class AnnotationLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PeakRepository peakRepository;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    Utils utils;

    @Autowired
    AnnotationRepository annotationRepository;

    @Autowired
    AlternativePeakAnnotationNameRepository nameForAnnotationRepository;

    @Autowired
    CampaignLogic campaignLogic;

    //the logic is controlled
    public Annotation createAnnotation(Annotation annotation, Long peakId) throws Exception {

        //we get the user from the session
        UserPV userFromSession = utils.getUserFromSession();
        Set<Privilege> setP = userFromSession.getPrivileges();

        //verify is a manager who is creating a campaign
        Boolean isWorker = false;
        for (Privilege p : setP) {
            if (p.getName().equals("worker")) {
                isWorker = true;
                break;
            }
        }

        if (!isWorker) {
            throw new AccessDeniedException(userFromSession.getUsername() + " does not have worker privileges.");
        }

        Peak peak = peakRepository.findPeakByIdEquals(peakId);
        if (peak == null) {
            throw new RuntimeException("Peak with id " + peakId + " could not be found");
        }

        if (!campaignLogic.isWorkerSubscribedToCampaign(peak.getCampaign(), userFromSession)) {
            throw new RuntimeException("You are not suscribed to this campaign.");
        }

        if (this.isAnnotatedByUser(peak, userFromSession)){
            throw new RuntimeException("You have already annotated this peak. A peak can be annotated only once per user.");
        }

        //controls if the campaign name is not empty
        String name = annotation.getName().trim();
        if (name != null) {
            annotation.setName(name.trim());//cut spaces
        }

        //create the campaign start date
        annotation.setDateTimeCreated(new Date());
        annotation.setPeak(peak);
        annotation.setUserPV(userFromSession);
        annotation.setCampaign(peak.getCampaign());


        List<AlternativePeakAnnotationName> list = annotation.getLocalizedNames();
        Annotation annotationToReturn = annotationRepository.save(annotation);
        if (list != null && list.size() > 0) {
            for (AlternativePeakAnnotationName an : list) {
//                check for annotation, if they are empty
                if (an.getName() != null) {
                    an.setAnnotation(annotationToReturn);
                    if (an.getName() != null && an.getName().trim().equals(""))break; else
                    an.setName(an.getName().trim());
                    if (an.getLang() != null && an.getLang().trim().equals("")) break;
                    else an.setLang(an.getLang().trim());
                    nameForAnnotationRepository.save(an);
                }
            }
        }

        //maintain number statistics in peak
        if (annotation.getPeakValidity()) {
            peak.addPositivePeaksValidity();
        } else {
            peak.addPositivePeaksValidity();
        }
        peak.checkConflict(); //update the conflict state

        peakRepository.save(peak);
        return annotationRepository.save(annotation);

    }

    @Transactional
    public Annotation rejectAnnotation(Long annotationId) {
        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Annotation annotationToReject = annotationRepository.findOne(annotationId);
        Hibernate.initialize(annotationToReject.getPeak());

        Peak peak = utils.initializeAndUnproxy(annotationToReject.getPeak());

        Campaign camp = utils.initializeAndUnproxy(peak.getCampaign());

        if (camp.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can reject an annotation");
        }

        if (annotationToReject.getStatus().equals("REJECTED")) {
            throw new RuntimeException("The annotation has already been rejected");
        }

        annotationToReject.setStatus("REJECTED");

        if (peak.getColor().equals("orange")) {
            peak.setColor("red");
        }
        peakRepository.save(peak);
        return annotationRepository.save(annotationToReject);
    }

    @Transactional
    public Annotation acceptAnnotation(Long annotationId) {
        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Annotation annotationToReject = annotationRepository.findOne(annotationId);
        Hibernate.initialize(annotationToReject.getPeak());

        Peak peak = utils.initializeAndUnproxy(annotationToReject.getPeak());

        Campaign camp = utils.initializeAndUnproxy(peak.getCampaign());

        if (camp.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can reaccept an annotation");
        }

        if (annotationToReject.getStatus().equals("VALID")) {
            throw new RuntimeException("The annotation has already been reaccepted");
        }

        annotationToReject.setStatus("VALID");

        Long qAnnotation = null;
        Long qAnnotationRejected = null;
        //maintain color
        if (peak.getToBeAnnotated() == false) {
            peak.setColor("green");
        } else { //the peak is to be annotated. It could be yellow, orange or red
            qAnnotation = annotationRepository.countAnnotationByPeak(peak);
            qAnnotationRejected = annotationRepository.countAnnotationByPeakAndStatusEquals(peak, "REJECTED");
            if (qAnnotation == 0l) {
                peak.setColor("yellow");
            } else {
                if (qAnnotationRejected == 0l) {
                    peak.setColor("orange");
                } else {
                    peak.setColor("red");
                }
            }
        }

        peakRepository.save(peak);
        return annotationRepository.save(annotationToReject);
    }

    public Page<Annotation> findAll(Integer page, Integer size) {
        return annotationRepository.findAll(new PageRequest(page, size));
    }

    public Page<Annotation> findAnnotationByPeak(Long peakId, Integer page, Integer size) {
        //we get the user from the session
        Peak peak = peakRepository.findPeakByIdEquals(peakId);
        if (peak == null) {
            throw new RuntimeException("Peak id " + peakId + " is not valid");
        }
        return annotationRepository.findAnnotationByPeak(peak, new PageRequest(page, size));
    }

    public Annotation createAnnotationByPeak(Peak peak, UserPV userPV) {
        //we get the user from the session
        Annotation ann = new Annotation(peak, userPV);
        return ann;
    }


    public Annotation findAnnotationById(Long findById) {
        return annotationRepository.findAnnotationById(findById);
    }

    public Boolean isAnnotatedByUser(Peak peak, UserPV user){
        List<Annotation> p = annotationRepository.findAnnotationByPeakAndUserPV(peak,user);
        return p.size()>0;
    }

}
