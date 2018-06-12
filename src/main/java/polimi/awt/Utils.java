package polimi.awt;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
//        String auth = "w1";
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
