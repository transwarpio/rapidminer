package com.rapidminer.core.user;

import javax.swing.*;

/**
 * Created by fan on 17-3-12.
 */
public class ButtonManager {
    private static JButton login;
    private static JButton logout;

    public static void setLogin(LoginButton login) {
        ButtonManager.login = login;
    }

    public static void setLogout(LogoutButton logout) {
        ButtonManager.logout = logout;
    }

    public static void setLoginVisible(Boolean flag) {
        if (ButtonManager.login != null) {
            ButtonManager.login.setVisible(flag);
        }
    }

    public static void setLogoutVisible(Boolean flag) {
        if (ButtonManager.logout != null) {
            ButtonManager.logout.setVisible(flag);
        }
    }
}
