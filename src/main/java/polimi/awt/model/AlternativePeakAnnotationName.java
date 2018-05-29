package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

//This class is to save the alternative name for the annotation name
@Entity
public class AlternativePeakAnnotationName extends PeakNameBase {

    @ManyToOne(fetch = FetchType.LAZY)
    private Annotation annotation;

    @JsonIgnore
    public Annotation getAnnotation() {
        return annotation;
    }

    @JsonProperty
    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}