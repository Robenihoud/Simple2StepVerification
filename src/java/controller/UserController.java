package controller;

import bean.Password;
import bean.PasswordItem;
import bean.User;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.UserFacade;

import java.io.Serializable;
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

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private service.UserFacade ejbFacade;
    @EJB
    private service.PasswordFacade passwordFacade;
    @EJB
    private service.PasswordItemFacade passwordItemFacade;
    private List<User> items = null;
    private User selected;

    public UserController() {
    }

    //*** Inscription JSF Page ***//
    private String typedPassword;
    private String message;

    public void inscription() {
        User user = new User();
        user.setUsername(selected.getUsername());
        selected = null;
        if (user != null) {
            PasswordItem passwordItem = passwordItemFacade.passwordItemOfPassword(typedPassword);
            typedPassword = "";
            if (passwordItem != null) {
                Password password = passwordFacade.passwordOfUser(passwordItem);
                if (password != null) {
                    ejbFacade.inscriptionUser(user, password);
                    message = "Your Inscription is done successfuly!";
                } else {
                    message = "Object Password is null";
                }
            } else {
                message = "Object PasswordItem is null!";
            }
        } else {
            message = "Object User is null!";
        }
    }

    public String getTypedPassword() {
        if (typedPassword == null) {
            typedPassword = "";
        }
        return typedPassword;
    }

    public void setTypedPassword(String typedPassword) {
        this.typedPassword = typedPassword;
    }

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //*** Inscription JSF Page ***//
    //***************************//
    //*** Connection ***//
    public void connectionUser() {
        String password = typedPassword;
        typedPassword = "";
        if (selected != null) {
            User user = ejbFacade.findByUsername(selected.getUsername());
            selected = null;
            if (user != null) {
                if (passwordItemFacade.checkPassword(password, user)) {
                    message = "Username and Password Correct";
                } else {
                    message = "Username and Password not Correct";
                }
            } else {
                message = "Username not correct";
            }

        } else {
            message = "selected username is null";
        }

    }

    //*** Connection ***//
    //******************//
    //*** Change Password ***//
    private String oldPassword;

    public void changePassword() {
        User user = ejbFacade.findByUsername(selected.getUsername());// You can use Session in this, this is only a test
        selected = null;

        if (user != null) {
            int test = passwordFacade.changePlusAddPassword(oldPassword, typedPassword, user);
            oldPassword = null;
            typedPassword = null;
            System.out.println("Test == " + test);
            if (test == 1) {
                message = "Success";
            } else if (test == -1) {
                message = "oldPassword is not valid";
            } else if (test == -2) {
                message = "Old == New ";
            } else {
                message = "One or All object is null";
            }

        }
    }

    public String getOldPassword() {
        if (oldPassword != null) {
            oldPassword = "";
        }
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    //*** Change Password ***//
    //***********************//
    public User getSelected() {
        if (selected == null) {
            selected = new User();
        }
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
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

    public User getUser(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
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
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }

}
