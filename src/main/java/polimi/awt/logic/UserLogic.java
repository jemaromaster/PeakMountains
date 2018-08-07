package polimi.awt.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import polimi.awt.Utils;
import polimi.awt.model.Privilege;
import polimi.awt.model.UserPV;
import polimi.awt.repo.UserRepository;

import java.util.Set;

@Service
public class UserLogic {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    public UserPV createUser(UserPV newUser) {

        Set<Privilege> privilegeList = utils.getPrivileges(newUser.getPrivileges());
        newUser.setPrivileges(privilegeList);

        //we encrypt the password
        String passToEncrypt = newUser.getPassword();
        String encodedPass = utils.encodePassword(passToEncrypt);
        newUser.setPassword(encodedPass);

        newUser = this.controlUser(newUser);
        return userRepository.save(newUser);
    }

    private UserPV controlUser(UserPV user){
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Empty username is not valid");
        } else {
            user.setUsername(username.trim());//cut spaces
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Empty email is not valid");
        } else {
            user.setEmail(user.getEmail().trim());//cut spaces
        }
        return user;
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
        userToUpdate.setPassword(userOld.getPassword());
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

    public UserPV changePassword(String userName, UserPV userToUpdate, String new_password) {

        UserPV userOld = userRepository.findByUsername(userName);

        String encodedPass = utils.encodePassword(new_password);

        if (userOld==null){
            throw new RuntimeException("Username to update not found");
        }
        userToUpdate.setId(userOld.getId());

        if (userToUpdate.getPrivileges() != null) {
            Set<Privilege> privilegeList = utils.getPrivileges(userToUpdate.getPrivileges());
            userToUpdate.setPrivileges(privilegeList);
        }
        userToUpdate.setPassword(encodedPass);
        return userRepository.save(userToUpdate);
    }


    public Page<UserPV> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Iterable<UserPV> findAll() {
        return userRepository.findAll();
    }

}
