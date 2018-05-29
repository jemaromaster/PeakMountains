package polimi.awt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;
import polimi.awt.repo.PrivilegeRepository;
import polimi.awt.repo.UserRepository;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class Utils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    public UserPV getUserFromSession() {
//        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        String auth = "lauri";
        UserPV user = userRepository.findByUsername(auth);
        if (user == null) {
            throw new UsernameNotFoundException(auth);
        }
        return user;
    }

//    list the privileges
    public Set<Privilege> getPrivileges(Set<Privilege> roles){

        Set<Privilege> privilegeSet = new LinkedHashSet<>();

        for (Privilege rol: roles){
            privilegeSet.add(privilegeRepository.findByName(rol.getName()));
        }

        return privilegeSet;

    }

}
