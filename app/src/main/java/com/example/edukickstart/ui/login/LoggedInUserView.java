package com.example.edukickstart.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    //... data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        try {
            if (displayName.isEmpty()) throw new Exception("Display name empty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}