package com.hex.pc.network;

import javax.swing.JOptionPane;

public class DialogBoxes {

    public static boolean amIHost() {

        Object[] options = { "HostGame", "Connect to server" };
        byte b = (byte) JOptionPane.showOptionDialog(null, "are you sure you want to reset all your options\n all your personal settings will be lost",
                "Are you sure", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if(b == 0) {
            return true;
        }
        else return false;
    }

    public static String getIP() {
        // TODO make box
        return "127.0.0.1";
    }
}
