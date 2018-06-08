package polimi.awt.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Column(name="type", length = 10) //CREATED STARTED CLOSED
    private String status;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToOne(fetch = FetchType.EAGER,
            cascade =  CascadeType.ALL)
    private UserPV usrManager;

//    @OneToMany(mappedBy = "campaign")
//    private List<Peak> peaksInCampaign = new ArrayList<Peak>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="workers_campaigns",
            joinColumns = { @JoinColumn(name = "campaign_id") },
            inverseJoinColumns = { @JoinColumn(name = "userpv_id")})
    private Set<UserPV> workersJoined;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public UserPV getUsrManager() {
        return usrManager;
    }

    public void setUsrManager(UserPV usrManager) {
        this.usrManager = usrManager;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

//    @JsonIgnore
//    public List<Peak> getPeaksInCampaign() {
//        return peaksInCampaign;
//    }

//    @JsonProperty
//    public void setPeaksInCampaign(List<Peak> peaksInCampaign) {
//        this.peaksInCampaign = peaksInCampaign;
//    }

    public Set<UserPV> getWorkersJoined() {
        return workersJoined;
    }

    public void setWorkersJoined(Set<UserPV> workersJoined) {
        this.workersJoined = workersJoined;
    }

    public void addWorkerToCampaign(UserPV user){
        this.workersJoined.add(user);
    }
}