/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Password;
import bean.PasswordItem;
import bean.User;
import controller.util.HashageUtil;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author BENIHOUD Youssef
 */
@Stateless
public class PasswordFacade extends AbstractFacade<Password> {

    @EJB
    private PasswordItemFacade passwordItemFacade;

    @PersistenceContext(unitName = "SecureProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PasswordFacade() {
        super(Password.class);
    }

    public Password passwordOfUser(PasswordItem passwordItem) {
        if (passwordItem != null) {
            Password password = new Password();
            passwordItem.setPassword(password);
            create(password);
            passwordItemFacade.edit(passwordItem);
            return password;
        }
        return null;
    }

    public int changePlusAddPassword(String oldPassword, String newPassword, User user) {
        if (oldPassword != null && newPassword != null && user != null) { // check if all is not null
            if (!oldPassword.equals(newPassword)) { // check if oldPassword doesn't equal to newPassword
                if (passwordItemFacade.checkPassword(oldPassword, user)) { //Check if the oldPassword is a valid Password
                   PasswordItem passwordItem = new PasswordItem();
                   passwordItem.setPass(HashageUtil.sha256(newPassword));
                   passwordItem.setValide(true);
                   passwordItem.setPassword(user.getPassword());
                   passwordItemFacade.create(passwordItem);
                   passwordItemFacade.turnPasswordOff(oldPassword, user.getPassword());
                   return 1; // add new Password with success
                }
                return -1; // oldPassword is not valid
            }
            return -2; // oldPassword == newPassword
        }
        return -3; // one/all of the objects is/are null 
    }

}
