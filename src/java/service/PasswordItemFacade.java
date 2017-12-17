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
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author BENIHOUD Youssef
 */
@Stateless
public class PasswordItemFacade extends AbstractFacade<PasswordItem> {

    @PersistenceContext(unitName = "SecureProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PasswordItemFacade() {
        super(PasswordItem.class);
    }

    //*** Tools ***//
    private List<PasswordItem> findbyPassword(Password password) {
        return em.createQuery("SELECT p FROM PasswordItem p WHERE p.password.id ='" + password.getId() + "'").getResultList();
    }

    private PasswordItem findByHashedPassword(String hashpass, Password password) {
        return (PasswordItem) em.createQuery("SELECT p FROM PasswordItem p WHERE p.password.id='" + password.getId() + "' AND p.pass ='" + hashpass + "'").getSingleResult();
    }
    //*** Tools ***//
    /////////////////

    //** PasswordItemOfPassword : convert the typed Password from String to Object PasswordItem **//
    public PasswordItem passwordItemOfPassword(String typedPassword) {
        if (!(typedPassword.isEmpty() || typedPassword.equals(""))) {
            PasswordItem passwordItem = new PasswordItem();
            passwordItem.setPass(HashageUtil.sha256(typedPassword));
            passwordItem.setValide(true);
            create(passwordItem);
            return passwordItem;
        }
        return null;
    }

    /**
     * check if the typed password exists in the DataBase and match with the
     * given user's password and check if this password is valid or not ( even
     * if it exists in DB )
     *
     * @param typedPassword
     * @param user
     * @return boolean
     */
    public boolean checkPassword(String typedPassword, User user) {
        if (!(typedPassword.isEmpty() || typedPassword.equals(""))) {
            typedPassword = HashageUtil.sha256(typedPassword);
            PasswordItem passwordItem = findByHashedPassword(typedPassword, user.getPassword());
            if (passwordItem != null && user != null) {
                if (passwordItem.getPassword().getId() == user.getPassword().getId()) { // check if the password exists
                    return passwordItem.getValide(); // check if a valid password
                }
            }
        }
        return false;
    }

    /**
     * Turn a given Password OFF
     *
     * @param oldPassword
     * @param password
     * @return Integer
     */
    public int turnPasswordOff(String oldPassword, Password password) {
        PasswordItem passwordItem = findByHashedPassword(HashageUtil.sha256(oldPassword), password);
        if (passwordItem != null) {
            passwordItem.setValide(false);
            edit(passwordItem);
            return 1; // Success the typed oldPassword is no longer valid
        }
        return -1; // oldPassword and Password are not found
    }

}
