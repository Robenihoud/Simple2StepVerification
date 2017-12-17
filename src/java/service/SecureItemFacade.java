/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Secure;
import bean.SecureItem;
import bean.User;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author BENIHOUD Youssef
 */
@Stateless
public class SecureItemFacade extends AbstractFacade<SecureItem> {

    @PersistenceContext(unitName = "SecureProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SecureFacade secureFacade;
    @EJB
    private UserFacade userFacade;

    public SecureItemFacade() {
        super(SecureItem.class);
    }

    //*** TOOLs ***//
    private SecureItem findById(SecureItem secureItem) {
        return (SecureItem) em.createQuery("SELECT s FROM SecureItem s WHERE s.id ='" + secureItem.getId() + "'").getSingleResult();
    }
    
    public List<SecureItem> findBySecure(Secure secure){
        return em.createQuery("SELECT s FROM SecureItem s WHERE s.secure.id ='"+secure.getId()+"'").getResultList();
    }
    //*** TOOLs ***//
    //*** ***** ***//

    public SecureItem addSecureItem(SecureItem selected) {
        if (selected != null) {
            SecureItem secureItem = new SecureItem();
            secureItem.setQuestion(selected.getQuestion());
            secureItem.setResponse(selected.getResponse());
            secureItem.setValide(selected.isValide());
            create(secureItem);
            return secureItem;
        }
        return null;
    }

    public int checkAnswer(SecureItem secureItem) {
        if (secureItem != null) {
            SecureItem secureItemTest = findById(secureItem);
            if (secureItemTest != null) {
                if (secureItemTest.getResponse().equals(secureItem.getResponse())) {
                    Secure secure = secureFacade.findById(secureItem.getSecure());
                    secure.setKorrekt(true);
                    if (!secureItemTest.isValide()) {
                        secure.setKorrekt(false);
                    }
                    secureFacade.edit(secure);
                    return 1;
                }

                return -1; // this secureItem is not valid or Response is not correct
            }
            return -2; // secureItemTest is not found in DB
        }
        return -3; // secureItem is null
    }
    
    
    
    public List<SecureItem> secureItemOfConnectedUser(){
        User user = userFacade.findByUsername("User"); // This is just a Test, Session Recommanded
        if(user != null && user.getSecure() != null ){
            List<SecureItem> results = new ArrayList<>();
            results = findBySecure(user.getSecure());
            if(results != null && !results.isEmpty()){
                return results;
            }
        }
        
        return new ArrayList<>();
    }

}
