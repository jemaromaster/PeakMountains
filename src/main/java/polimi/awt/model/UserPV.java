package polimi.awt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Set;

@Entity
public class UserPV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //the username should be unique
    @Column(unique = true)
    private String username;

    private String password;

    //the email should be unique
    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "workersJoined", cascade = CascadeType.ALL)
    private Set<Campaign> campaignsJoined;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password){
        //we encrypt the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
        String encodedPass = encoder.encode(password);
        this.password = encodedPass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    @JsonIgnore
    public Set<Campaign> getCampaignsJoined() {
        return campaignsJoined;
    }

    @JsonProperty
    public void setCampaignsJoined(Set<Campaign> campaignsJoined) {
        this.campaignsJoined = campaignsJoined;
    }

    public Boolean hasPriviliges(String priv) {
        for (Privilege p : this.getPrivileges()) {
            if (p.getName().equals(priv)){
                return true;
            }
        }
        return false;
    }

    public void joinToCampaign(Campaign campaign) {
        this.campaignsJoined.add(campaign);
    }
}