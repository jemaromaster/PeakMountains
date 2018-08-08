package polimi.awt;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;
import polimi.awt.repo.PrivilegeRepository;
import polimi.awt.repo.UserRepository;
import polimi.awt.security.SecurityConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class Utils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    SecurityConfiguration securityConfiguration;

    public UserPV getUserFromSession() {
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
//        String auth = "w1";
        UserPV user = userRepository.findByUsername(auth);
        if (user == null) {
            throw new UsernameNotFoundException(auth);
        }
        return user;
    }

    //    list the privileges
    public String getRolUserInSession(){
        UserPV userInSession = this.getUserFromSession();
        Set<Privilege> privileges = userInSession.getPrivileges();
        ArrayList<Privilege> privArray = new ArrayList<Privilege>(privileges);

        //get the role of the user. If the user has both privileges, it access to the manager only
        if (privArray.size() > 1 || privArray.get(0).getName().equals("manager")) {
            return privArray.get(0).getName();
        }else{
            return privArray.get(0).getName();
        }
    }


    public String encodePassword(String planePass) {
        //we encrypt the password
        BCryptPasswordEncoder encoder = (BCryptPasswordEncoder) securityConfiguration.encoder();
        String encodedPass = encoder.encode(planePass);
        return encodedPass;
    }

    public Boolean comparePlaneToEncodedPassword(String rawPass, String encodedToCompare) {
        BCryptPasswordEncoder encoder = (BCryptPasswordEncoder) securityConfiguration.encoder();
        return encoder.matches(rawPass,encodedToCompare);
    }

//    list the privileges
    public Set<Privilege> getPrivileges(Set<Privilege> roles){

        Set<Privilege> privilegeSet = new LinkedHashSet<>();

        for (Privilege rol: roles){
            privilegeSet.add(privilegeRepository.findByName(rol.getName()));
        }

        return privilegeSet;

    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

}
