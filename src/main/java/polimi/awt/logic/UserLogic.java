package polimi.awt.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import polimi.awt.Utils;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;
import polimi.awt.repo.UserRepository;

import java.util.Set;

@Component
public class UserLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    public UserPV createUser(UserPV newUser) {

        Set<Privilege> privilegeList = utils.getPrivileges(newUser.getPrivileges());
        newUser.setPrivileges(privilegeList);

        return userRepository.save(newUser);
    }


    public UserPV updateUser(String userName, UserPV userToUpdate) {

        UserPV userOld = userRepository.findByUsername(userName);

        if (userOld==null){
            throw new RuntimeException("Username to update not found");
        }
        userToUpdate.setId(userOld.getId());

        if (userToUpdate.getPrivileges() != null) {
            Set<Privilege> privilegeList = utils.getPrivileges(userToUpdate.getPrivileges());
            userToUpdate.setPrivileges(privilegeList);
        }

        return userRepository.save(userToUpdate);
    }

    public Boolean loginUsernamePass(String username, String pass) {
        UserPV user = userRepository.findByUsername(username);

        if (user==null || !user.getPassword().equals(pass)) { //if not equals to pass
            return false;
        } else {
            return true;
        }
    }


    public Page<UserPV> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Iterable<UserPV> findAll() {
        return userRepository.findAll();
    }

}
