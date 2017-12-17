package controller.util;


import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionUtil {

    private static final SessionUtil instance = new SessionUtil();

    private SessionUtil() {
    }

//    public static void attachUserToCommune(User user) {
//        Commune myCommune = user.getCommune();
//        if (myCommune.getUsers().indexOf(user) == -1) {
//            myCommune.getUsers().add(user);
//        }
//        registerUser(user);
//    }
    public static void registerUser(Object user) {
        setAttribute("user", user);
    }

    public static Object getConnectedUser() {
        return (Object) getAttribute("user");
    }

//    public static void registerRedevable(Redevable redevable) {
//        setAttribute("redevable", redevable);
//    }
//
//    public static Redevable getConnectedRedevable() {
//        return (Redevable) getAttribute("redevable");
//    }
//
//    public static Commune getCurrentCommune() {
//        User user = getConnectedUser();
//        if (user != null) {
//            return user.getCommune();
//        }
//        return new Commune();
//    }
    public static SessionUtil getInstance() {
        return instance;
    }

    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }

    public static void redirect(String pagePath) throws IOException {
        if (!pagePath.endsWith(".xhtml")) {
            pagePath += ".xhtml";
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(pagePath);

    }

    private static boolean isContextOk(FacesContext fc) {
        boolean res = (fc != null
                && fc.getExternalContext() != null
                && fc.getExternalContext().getSession(false) != null);
        return res;
    }

    private static HttpSession getSession(FacesContext fc) {
        return (HttpSession) fc.getExternalContext().getSession(false);
    }

    public static Object getAttribute(String cle) {
        FacesContext fc = FacesContext.getCurrentInstance();
        Object res = null;
        if (isContextOk(fc)) {
            res = getSession(fc).getAttribute(cle);
        }
        return res;
    }

    public static void setAttribute(String cle, Object valeur) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null && fc.getExternalContext() != null) {
            getSession(fc).setAttribute(cle, valeur);
        }
    }

    public static void deconnectUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null && fc.getExternalContext() != null) {
            getSession(fc).removeValue("user");
            getSession(fc).removeAttribute("user");
        }
    }
    public static void deconnectSelectedUser(String cle) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null && fc.getExternalContext() != null) {
            getSession(fc).removeValue(cle);
            getSession(fc).removeAttribute(cle);
        }
    }

    
}
