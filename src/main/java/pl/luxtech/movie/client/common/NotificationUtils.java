package pl.luxtech.movie.client.common;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class NotificationUtils {

    public static void error(String message, Page page) {
        Notification notification = new Notification("Error", message);
        notification.setStyleName("error bar");
        notification.setPosition(Position.MIDDLE_CENTER);
        notification.setDelayMsec(2000);
        notification.show(page);
    }

    public static void error(Throwable throwable, Page page) {
        Throwable ttt = throwable.getCause();
        error(ttt == null ? throwable.getMessage() : ttt.getMessage(), page);
    }

    public static void error(Throwable throwable) {
        Throwable ttt = throwable.getCause();
        error(ttt == null ? throwable.getMessage() : ttt.getMessage(), UI.getCurrent().getPage());
    }

    public static void error(String message) {
        error(message);
    }

}
