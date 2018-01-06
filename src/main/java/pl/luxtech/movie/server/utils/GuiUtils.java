package pl.luxtech.movie.server.utils;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import pl.luxtech.movie.common.VideoConsts;
import pl.luxtech.movie.server.listeners.PerformListener;

public class GuiUtils {

    public static MenuItem addMenuItemToPopup(PopupMenu popup, String label, ActionListener actionListener) {
        MenuItem menu = new MenuItem(label);
        menu.addActionListener(actionListener);
        popup.add(menu);
        
        return menu;
    } 
    
    public static void executeLater( final PerformListener performListener , Long delayTime ) {
        new Timer().schedule(new TimerTask() {                
            @Override
            public void run() {
                performListener.execute();                
            }
        }, delayTime);
    }
    
    public static void showMessegeError(TrayIcon trayIcon, String message) {
        showMessege(trayIcon, message, TrayIcon.MessageType.ERROR);
        trayIcon.setToolTip(message);
    }
    
    public static void showMessege(TrayIcon trayIcon, String message) {
        showMessege(trayIcon, message, TrayIcon.MessageType.INFO);
        trayIcon.setToolTip(message);
    }

    public static void showMessege(TrayIcon trayIcon, String message, TrayIcon.MessageType messageType) {
        trayIcon.displayMessage(VideoConsts.APP_TITLE, message, messageType);
        trayIcon.setToolTip(message);
    }
}
