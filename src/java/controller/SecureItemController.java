package controller;

import bean.SecureItem;
import bean.User;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.SecureItemFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("secureItemController")
@SessionScoped
public class SecureItemController implements Serializable {

    @EJB
    private service.SecureItemFacade ejbFacade;
    @EJB
    private service.UserFacade userFacade;
    @EJB
    private service.SecureFacade secureFacade;
    private List<SecureItem> items = null;
    private SecureItem selected;

    public SecureItemController() {
    }

    //**** Add Secure Question ****//
    public void addButton() {
        SecureItem secureItem = ejbFacade.addSecureItem(selected);
        selected = null;
        if (secureItem != null) {
            int test = secureFacade.addSecure(secureItem);
            if (test > 0 ) {
                items.add(secureItem);
                System.out.println("AddButton Succeed");
            } else {
                System.out.println("Add Button unSucceed");
            }
        }

    }

    //*** Table Add Question ***//
    public boolean hasUserSecureItem() {
        User user = userFacade.findByUsername("User"); // Recommanded to do Session
        if (user != null) {
            if (user.getSecure() != null) {
                List<SecureItem> secureItems = ejbFacade.findBySecure(user.getSecure());
                if (!secureItems.isEmpty()) {
                    items = secureItems;
                    for(SecureItem item : items){
                        item.setResponse("");
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean moreThanThree() {
        if (items.size() >= 3) {
            return true;
        }
        return false;
    }

    public void minusSecure(SecureItem item) {
        try {
            if (!items.isEmpty()) {
                items.remove(items.indexOf(item));
                ejbFacade.remove(item);
            }
        } catch (Exception e) {
            System.out.println("item == " + item);
        }
    }

    public void validateSecureItem() {
        // Refers to another Page
        // Empty Table
        items.clear();
        items = new ArrayList<>();
    }

    //*** Table Add Question ***//
    //*************//
    //**** Add Secure Question ****//
    //*****************************//
    //**** Verification Secure Question ****//
    
    private List<SecureItem> elems;
    
    public void checkAnswer(SecureItem secureItem) {
        ejbFacade.checkAnswer(secureItem);
        notValid(secureItem);
    }
    
    public void notValid(SecureItem item) {
      
            if (!elems.isEmpty()) {
                elems.remove(elems.indexOf(item));
                //ejbFacade.remove(item);
            }
      
    }
    
    public boolean hasSecureItemVerification(){
        User user = userFacade.findByUsername("User"); // Recommanded to do Session
        if (user != null) {
            if (user.getSecure() != null) {
                List<SecureItem> secureItems = ejbFacade.findBySecure(user.getSecure());
                if (!secureItems.isEmpty()) {
                    elems = secureItems;
                    for(SecureItem item : elems){
                        item.setResponse("");
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public void validate() {
        if (elems.isEmpty()) {
            if (secureFacade.verification()) {
                // refers to a Seccuessful Page 
                System.out.println("Verification : Correct");
            }
            System.out.println("Not");
            elems = new ArrayList<>();
            // refers to a Failed Page
        }
    }
    public List<SecureItem> getElems() {
        if(elems == null){
            elems = new ArrayList<>();
        }
        return elems;
    }

    //**** Verification Secure Question ****//
    public void setElems(List<SecureItem> elems) {
        this.elems = elems;
    }

    //**************************************//
    public SecureItem getSelected() {
        if (selected == null) {
            selected = new SecureItem();
        }
        return selected;
    }

    public void setSelected(SecureItem selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SecureItemFacade getFacade() {
        return ejbFacade;
    }

    public SecureItem prepareCreate() {
        selected = new SecureItem();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SecureItemCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SecureItemUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SecureItemDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SecureItem> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public SecureItem getSecureItem(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<SecureItem> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SecureItem> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SecureItem.class)
    public static class SecureItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SecureItemController controller = (SecureItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "secureItemController");
            return controller.getSecureItem(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SecureItem) {
                SecureItem o = (SecureItem) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SecureItem.class.getName()});
                return null;
            }
        }

    }

}
