package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Peak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Double latitude;
    private Double longitude;

    private Double altitude;
    private String name;

    @OneToMany(mappedBy = "peak", fetch = FetchType.EAGER)
    private List<AlternativePeakName> localizedNames = new ArrayList<AlternativePeakName>();

    @Column(name = "provenance", length = 30)
    private String provenance;

    @Column(nullable = false, columnDefinition = "boolean DEFAULT TRUE")
    private Boolean toBeAnnotated;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

//    private String color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AlternativePeakName> getLocalizedNames() {
        return localizedNames;
    }

    public void setLocalizedNames(List<AlternativePeakName> localizedNames) {
        this.localizedNames = localizedNames;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Boolean getToBeAnnotated() {
        return toBeAnnotated;
    }

    public void setToBeAnnotated(Boolean toBeAnnotated) {
        this.toBeAnnotated = toBeAnnotated;
    }

    @JsonIgnore
    public Campaign getCampaign() {
        return campaign;
    }

    @JsonProperty
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

}