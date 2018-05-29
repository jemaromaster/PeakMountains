package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateTimeCreated;

    private Boolean peakValidity= Boolean.TRUE;
    private Double elevation;
    private String name;

    @Column(name="type", length = 10)
    private String status = "VALID"; //valid or rejected

    @OneToMany(mappedBy = "annotation",fetch = FetchType.EAGER)
    private List<AlternativePeakAnnotationName> localizedNames = new ArrayList<AlternativePeakAnnotationName>();

    @ManyToOne(fetch = FetchType.EAGER)
    private UserPV userPV;

    @ManyToOne(fetch = FetchType.LAZY)
    private Peak peak;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campaign campaign;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Calendar dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Boolean getPeakValidity() {
        return peakValidity;
    }

    public void setPeakValidity(Boolean peakValidity) {
        this.peakValidity = peakValidity;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AlternativePeakAnnotationName> getLocalizedNames() {
        return localizedNames;
    }

    public void setLocalizedNames(List<AlternativePeakAnnotationName> localizedNames) {
        this.localizedNames = localizedNames;
    }

    public UserPV getUserPV() {
        return userPV;
    }

    public void setUserPV(UserPV userPV) {
        this.userPV = userPV;
    }

    @JsonIgnore
    public Peak getPeak() {
        return peak;
    }

    @JsonProperty
    public void setPeak(Peak peak) {
        this.peak = peak;
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