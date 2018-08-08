package polimi.awt.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import polimi.awt.logic.CampaignLogic;
import polimi.awt.model.Campaign;
import polimi.awt.storage.StorageException;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@RestController
@RequestMapping("campaigns")
public class CampaignControllerREST {

    @Autowired
    CampaignLogic campaignLogic;


    //receives a json campaign in the body
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaignRequestBody,
                                                   HttpServletResponse response) {

        try {
            Campaign campaignCreated = campaignLogic.createCampaign(campaignRequestBody);

            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(campaignCreated.getId())
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return new ResponseEntity(headers, HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{campaignId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Integer campaignId, @RequestBody Campaign campaign,
                                                   HttpServletResponse response) {
        try {
            //setting the ID if it was not sent in the body itself
            campaign.setId(campaignId);
            campaignLogic.updateCampaign(campaign);

            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(campaignId)
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(uri);
            return new ResponseEntity(headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Campaign>> getAllCampaigns(@RequestParam(required = false) String username,
                                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                                          @RequestParam(required = false, defaultValue = "20") Integer size) {
        if (username != null) {//List all the campaigns form an user
            Page<Campaign> pageCampaign = campaignLogic.listCampaignByManager(username, page, size);
            return new ResponseEntity(pageCampaign, HttpStatus.OK);
        } else { //list all the campaigns
            Page<Campaign> pageCampaign = campaignLogic.findAll(page, size);
            return new ResponseEntity(pageCampaign, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/{campaignId}", produces = "application/json")
    public ResponseEntity<Campaign> getCampaignsById(@PathVariable Long campaignId) {
        Campaign campaignToReturn = campaignLogic.findCampaignById(campaignId);
        return new ResponseEntity(campaignToReturn, HttpStatus.OK);
    }

    @PostMapping(value = "/{campaignId}/uploadFile")
    public ResponseEntity<String> handleFileUpload(@PathVariable Long campaignId,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("toAnnotate") Boolean toAnnotate) {

        try {
            return campaignLogic.StoreAndProcessFile(file,campaignId, toAnnotate);
        } catch (StorageException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{campaignId}/start")
    public ResponseEntity<Campaign> startCampaign(@PathVariable Long campaignId) {

        try {
            Campaign campToReturn =  campaignLogic.startCampaign(campaignId);
            return new ResponseEntity<Campaign>(campToReturn, HttpStatus.OK);
        } catch (StorageException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{campaignId}/subscribe")
    public ResponseEntity<Campaign> subscribeToCampaign(@PathVariable Long campaignId) {

        try {
            Campaign campToReturn =  campaignLogic.subscribeToCampaign(campaignId);
            return new ResponseEntity<Campaign>(campToReturn, HttpStatus.OK);
        } catch (StorageException e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
