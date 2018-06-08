package polimi.awt.controllerRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import polimi.awt.logic.AnnotationLogic;
import polimi.awt.model.Annotation;

import java.net.URI;

@RestController
@RequestMapping("rest/annotations")
public class AnnotationControllerREST {

    @Autowired
    AnnotationLogic annotationLogic;

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Annotation>> getAllAnnotations(@RequestParam(required = false) Long peakId,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            if (peakId != null) {//List all the peaks from a campaign
                Page<Annotation> pageAnnotation = annotationLogic.findAnnotationByPeak(peakId, page, size);
                return new ResponseEntity(pageAnnotation, HttpStatus.OK);
            } else { //list all the peaks
                Page<Annotation> pageAnnotation = annotationLogic.findAll(page, size);
                return new ResponseEntity(pageAnnotation, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/{annotationId}", produces = "application/json")
    public Annotation getAnnotationById(@PathVariable Long annotationId) {
        Annotation annotation = annotationLogic.findAnnotationById(annotationId);
        return annotation;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Annotation> createAnnotation(@RequestBody Annotation annotationRequestBody,
                                                     @RequestParam Long peakId) {

        try {
            Annotation annotationCreated = annotationLogic.createAnnotation(annotationRequestBody, peakId);

            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(annotationCreated.getId())
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

}
