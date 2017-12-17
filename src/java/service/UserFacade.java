/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Password;
import bean.User;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author BENIHOUD Youssef
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @EJB
    private PasswordFacade passwordFacade;

    @PersistenceContext(unitName = "SecureProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    //***Tools***//
    
    public User findByUsername(String username){
        return (User) em.createQuery("SELECT u FROM User u WHERE u.username ='"+username+"'").getSingleResult();
    }
    //***Tools***//
    //**********//

    public void inscriptionUser(User user, Password password) {
        if (password != null && user != null) {

            user.setPassword(password);
            password.setUser(user);
            edit(user);
            passwordFacade.edit(password);

        }

    }

}
