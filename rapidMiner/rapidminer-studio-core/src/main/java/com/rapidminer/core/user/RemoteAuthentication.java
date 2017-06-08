package com.rapidminer.core.user;

import io.transwarp.midas.remote.RemoteExecutor;

/**
 * Created by fan on 17-3-12.
 */
public class RemoteAuthentication {
    private static RemoteExecutor executor = new RemoteExecutor();
    static String checkConfiguration(String username, String password) {
        if (executor.login(username, password)) {
            return null;
        } else {
            return "login failed";
        }
    }

    public static void releaseToken() {
        if (executor.logout()) {
            ButtonManager.setLogoutVisible(false);
            ButtonManager.setLoginVisible(true);
        }
    }
}
