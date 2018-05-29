package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

//Class to store the alternative name (different languages) of a Peak
@Entity
public class AlternativePeakName extends PeakNameBase {

    @ManyToOne(fetch = FetchType.LAZY)
    private Peak peak;

    @JsonIgnore
    public Peak getPeak() {
        return peak;
    }

    @JsonProperty
    public void setPeak(Peak peak) {
        this.peak = peak;
    }
}