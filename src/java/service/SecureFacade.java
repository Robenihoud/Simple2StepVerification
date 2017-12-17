/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Secure;
import bean.SecureItem;
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
public class SecureFacade extends AbstractFacade<Secure> {

    @PersistenceContext(unitName = "SecureProjectPU")
    private EntityManager em;

    @EJB
    private UserFacade userFacade;
    @EJB
    private SecureItemFacade secureItemFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SecureFacade() {
        super(Secure.class);
    }

    //*** TOOLS ***//
    private Secure findByUser(User user) {
        return (Secure) em.createQuery("SELECT s FROM Secure s WHERE s.user.id ='" + user.getId() + "'").getSingleResult();
    }
    
    public Secure findById(Secure secure){
        return (Secure) em.createQuery("SELECT s FROM Secure s WHERE s.id ='"+secure.getId()+"'").getSingleResult();
    }
    //*** TOOLS ***//
    //*************//

    public int addSecure(SecureItem secureItem) {
        try {
            User user = userFacade.findByUsername("User"); // This is just a Test, You can use Session, recommended in Facade for more effecient result
            if (user != null) {
                Secure secure = new Secure();
                if (user.getSecure() == null) {
                    secure.setUser(user);
                    create(secure);
                    user.setSecure(secure);
                    edit(secure);
                    userFacade.edit(user);
                }

                secure = user.getSecure();
                secure.setKorrekt(false);
                edit(secure);
                secureItem.setSecure(secure);
                secureItemFacade.edit(secureItem);

                return 1;
            }
            return -1; // user is null
        } catch (Exception e) {
            System.out.println("e" + e);
        }
        return -1; // user is null
    }

    public boolean verification() {
        User user = userFacade.findByUsername("User"); // This is just a Test, You can use Session ( Recommended )
        if (user != null) {
            return user.getSecure().isKorrekt();
        }
        return false;
    }

}
