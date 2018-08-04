package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Peak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private long id;

    @Expose
    private Double latitude;
    @Expose
    private Double longitude;

    @Expose
    private Double altitude;
    @Expose
    private String name;

    @OneToMany(mappedBy = "peak", fetch = FetchType.EAGER)
    private List<AlternativePeakName> localizedNames = new ArrayList<AlternativePeakName>();

    @Column(name = "provenance", length = 30)
    @Expose
    private String provenance;

    @Column(nullable = false, columnDefinition = "boolean DEFAULT TRUE")
    @Expose
    private Boolean toBeAnnotated;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

    @Column(name = "color", length = 10)
    @Expose
    private String color;

    @Column
    @Expose
    private Long positivePeaksValidity=0l; //when a worker set the annotation to VALID add 1

    @Column
    @Expose
    private Long negativePeaksValidity=0l; //when a worker set the annotation to NOT VALID add 1

    @Column
    @Expose
    private Boolean conflicts=false;


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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getPositivePeaksValidity() {
        return positivePeaksValidity;
    }

    public void setPositivePeaksValidity(Long positivePeaksValidity) {
        this.positivePeaksValidity = positivePeaksValidity;
    }

    public Long getNegativePeaksValidity() {
        return negativePeaksValidity;
    }

    public void setNegativePeaksValidity(Long negativePeaksValidity) {
        this.negativePeaksValidity = negativePeaksValidity;
    }

    public Boolean getConflicts() {
        return conflicts;
    }

    public void setConflicts(Boolean conflicts) {
        this.conflicts = conflicts;
    }
}