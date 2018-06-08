package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date dateTimeCreated;

    @Expose
    private Boolean peakValidity= Boolean.TRUE;

    @Expose
    private Double elevation;

    @Expose
    private String name;

    @Column(name="type", length = 10)
    @Expose
    private String status = "VALID"; //valid or rejected

    @OneToMany(mappedBy = "annotation",fetch = FetchType.EAGER)
    @Expose
    private List<AlternativePeakAnnotationName> localizedNames = new ArrayList<AlternativePeakAnnotationName>();

    @ManyToOne(fetch = FetchType.EAGER)
    @Expose
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

    public Date getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(Date dateTimeCreated) {
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