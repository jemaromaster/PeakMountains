package polimi.awt.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import polimi.awt.Utils;
import polimi.awt.model.*;
import polimi.awt.repo.AlternativePeakNameRepository;
import polimi.awt.repo.CampaignRepository;
import polimi.awt.repo.PeakRepository;
import polimi.awt.repo.UserRepository;
import polimi.awt.storage.StorageException;
import polimi.awt.storage.StorageService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class CampaignLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    PeakRepository peakRepository;

    @Autowired
    AlternativePeakNameRepository alternativePeakNameRepository;

    @Autowired
    Utils utils;

    private final StorageService storageService;

    @Autowired
    public CampaignLogic(StorageService storageService) {
        this.storageService = storageService;
    }

    //the logic is controlled
    public Campaign createCampaign(Campaign campaign) throws Exception {

        //we get the user from the session, which could be only a manager
        UserPV userManager = utils.getUserFromSession();
        Set<Privilege> setP = userManager.getPrivileges();

        //verify is a manager who is creating a campaign
        Boolean isManager = false;
        for (Privilege p : setP) {
            if (p.getName().equals("manager")) {
                isManager = true;
                break;
            }
        }

        if (!isManager) {
            throw new AccessDeniedException(userManager.getUsername() + " does not have manager privileges.");
        }

        campaign = this.controlCampaing(campaign);

        //create the campaign start date
        campaign.setStatus("created");
        campaign.setCreatedDate(new Date());

        campaign.setUsrManager(userManager); //set the dao of the campaign
        return campaignRepository.save(campaign);

    }

    private Campaign controlCampaing(Campaign campaign) {
        //controls if the campaign name is not empty
        String name = campaign.getName();
        if (name == null || campaign.getName().isEmpty()) {
            throw new RuntimeException("Empty name is not valid");
        } else {
            campaign.setName(name.trim());//cut spaces
        }

        return campaign;
    }

    public Campaign updateCampaign(Campaign campaign) throws Exception {

        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Campaign oldCamp = campaignRepository.findOne(campaign.getId());
        if (oldCamp.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can update a campaign");
        }

        campaign = controlCampaing(campaign);

        //the campaign name is the only property that can be changed
        if (campaign.getName() != null)
            oldCamp.setName(campaign.getName());

        return campaignRepository.save(oldCamp);
    }

    public Page<Campaign> findAll(Integer page, Integer size) {
        return campaignRepository.findAll(new PageRequest(page, size));
    }

    public Page<Campaign> listCampaignByManager(String username, Integer page, Integer size) {
        //we get the user from the session
        UserPV manager = userRepository.findByUsername(username);
        if (manager == null) {
            throw new UsernameNotFoundException(username);
        }

        return campaignRepository.findCampaignByUsrManager(manager, new PageRequest(page, size));
    }

    public Campaign findCampaignById(Long findById) {
        //we get the user from the session
        return campaignRepository.findCampaignById(findById);
    }

    public ResponseEntity StoreAndProcessFile(MultipartFile file, Long campaignId, Boolean toAnnotate) {


        String fileName = "campaign_" + campaignId + "_DATA_PEAK.json";

        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Campaign campaignToUploadFile = campaignRepository.findOne(campaignId);
        if (campaignToUploadFile.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can update a campaign");
        }

        if (campaignToUploadFile.getStatus().equals("created")) {
            throw new RuntimeException("The campaign should be in STATUS:STARTED to upload a file. Please start the campaign first.");
        } else if (!campaignToUploadFile.getStatus().equals("started")) {
            throw new RuntimeException("The campaign should be in STATUS:STARTED to upload a file.");
        }
        ;

        try {
            storageService.store(file, fileName);
        } catch (StorageException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //process the file
        Path pathToFile = storageService.load(fileName);
        try {
            this.parseJsonFileAndSave(pathToFile, campaignToUploadFile, toAnnotate);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            return new ResponseEntity("Error parsing json file!", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity("File not uploaded correctly", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity("File uploaded and processed successfully!", HttpStatus.OK);
    }

    private void parseJsonFileAndSave(Path pathToFile, Campaign campaign, Boolean toAnnotate) throws Exception {
        JSONParser parser = new JSONParser();

        //Use JSONObject for simple JSON and JSONArray for array of JSON.
        JSONArray dataArray = (JSONArray) parser.parse(new FileReader(pathToFile.toString()));

        Peak peak;
        for (Object o : dataArray) {
            peak = new Peak();
            JSONObject peakJSON = (JSONObject) o;

            String name = (String) peakJSON.get("name");
            Double elevation = (Double) peakJSON.get("elevation");
            Double latitude = (Double) peakJSON.get("latitude");
            Double longitude = (Double) peakJSON.get("longitude");
            String provenance = (String) peakJSON.get("provenance");

            peak.setName(name);
            peak.setAltitude(elevation);
            peak.setLatitude(latitude);
            peak.setLongitude(longitude);
            peak.setProvenance(provenance);
            peak.setToBeAnnotated(toAnnotate); //if the peak is to be annotated
            peak.setCampaign(campaign);
            peak.setColor(toAnnotate ? "yellow" : "green");

            // process localized names
            JSONArray localizedNamesJSON = (JSONArray) peakJSON.get("localized_names");

            String lang;
            String alternativeName;
            List<AlternativePeakName> arrayListLN = null;
            AlternativePeakName altPN;
            if (localizedNamesJSON != null && !localizedNamesJSON.isEmpty()) {
                arrayListLN = new ArrayList<AlternativePeakName>();
                for (Object oneLN : localizedNamesJSON) {
                    JSONArray jsonA = (JSONArray) oneLN;
                    lang = (String) jsonA.get(0);
                    alternativeName = (String) jsonA.get(1);

                    altPN = new AlternativePeakName();
                    altPN.setPeak(peak);
                    altPN.setLang(lang);
                    altPN.setName(alternativeName);

                    arrayListLN.add(altPN);
                }
                peak.setLocalizedNames(arrayListLN);
            }

            peakRepository.save(peak);
            alternativePeakNameRepository.save(peak.getLocalizedNames());
        }

    }

    public Campaign startCampaign(Long campaignId) {

        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Campaign campaignToStart = campaignRepository.findOne(campaignId);
        if (campaignToStart.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can start a campaign");
        }

        if (campaignToStart.getStatus().equals("started")) {
            throw new RuntimeException("The campaign has already been started");
        } else if (campaignToStart.getStatus().equals("closed")) {
            throw new RuntimeException("The campaign has already been closed already");
        }
        campaignToStart.setStartDate(new Date());
        campaignToStart.setStatus("started");

        return campaignRepository.save(campaignToStart);
    }

    public Campaign closeCampaign(Long campaignId) {

        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Campaign campaignToClose = campaignRepository.findOne(campaignId);
        if (campaignToClose.getUsrManager().getId() != user.getId()) {
            throw new RuntimeException("Only the Campaign Manager can close the campaign");
        }

        if (campaignToClose.getStatus().equals("created")) {
            throw new RuntimeException("The campaign has not been started yet.");
        } else if (campaignToClose.getStatus().equals("closed")) {
            throw new RuntimeException("The campaign has already been closed already");
        }
        campaignToClose.setEndDate(new Date());
        campaignToClose.setStatus("closed");

        return campaignRepository.save(campaignToClose);
    }

    public Campaign suscribeToCampaign(Long campaignId) {

        //we get the user from the session
        UserPV user = utils.getUserFromSession();

        Campaign campaignToSuscribe = campaignRepository.findOne(campaignId);

        //control if the user is a worker
        if (!user.hasPriviliges("worker")) {
            throw new RuntimeException("User " + user.getUsername() + " has no worker rol");
        }

        //control if the campaign is started
        if (!campaignToSuscribe.getStatus().equals("started")) {
            throw new RuntimeException("The campaign is not in started mode");
        }

        campaignToSuscribe.addWorkerToCampaign(user);
        campaignRepository.save(campaignToSuscribe);

        return campaignToSuscribe;
    }

}
