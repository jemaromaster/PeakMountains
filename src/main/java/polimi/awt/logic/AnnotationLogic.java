package polimi.awt.logic;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import polimi.awt.Utils;
import polimi.awt.model.*;
import polimi.awt.repo.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
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
        //controls if the campaign name is not empty
        String name = annotation.getName();
        if (name != null) {
            annotation.setName(name.trim());//cut spaces
        }

        //create the campaign start date
        annotation.setDateTimeCreated(new Date());
        annotation.setPeakValidity(true);
        annotation.setPeak(peak);
        annotation.setUserPV(userFromSession);

        List<AlternativePeakAnnotationName> list = annotation.getLocalizedNames();
        Annotation annotationToReturn = annotationRepository.save(annotation);
        if (list != null) {
            for (AlternativePeakAnnotationName an : list) {
                an.setAnnotation(annotationToReturn);
                nameForAnnotationRepository.save(an);
            }
        }
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

        Long qAnnotation=null;
        Long qAnnotationRejected = null;
        //maintain color
        if (peak.getToBeAnnotated()==false){
            peak.setColor("green");
        }else{ //the peak is to be annotated. It could be yellow, orange or red
            qAnnotation = annotationRepository.countAnnotationByPeak(peak);
            qAnnotationRejected = annotationRepository.countAnnotationByPeakAndStatusEquals(peak, "REJECTED");
            if(qAnnotation==0l){
                peak.setColor("yellow");
            }else{
                if (qAnnotationRejected==0l){
                    peak.setColor("orange");
                }else{
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

    public Annotation findAnnotationById(Long findById) {
        return annotationRepository.findAnnotationById(findById);
    }

}
