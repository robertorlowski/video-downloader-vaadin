package pl.luxtech.movie.client;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.annotation.WebServlet;

import com.github.appreciated.material.MaterialTheme;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import pl.luxtech.movie.client.common.NotificationUtils;
import pl.luxtech.movie.client.common.VideoDownloaderMenuLayout;
import pl.luxtech.movie.client.view.HistoryView;
import pl.luxtech.movie.client.view.SettingsView;
import pl.luxtech.movie.client.view.YouTubeView;
import pl.luxtech.movie.common.VideoConsts;
import pl.luxtech.movie.common.VideoViewEnum;

@SuppressWarnings({ "serial" })
@Theme("luxtech")
@Title("Video Downloader")
@PreserveOnRefresh
@Viewport("user-scalable=no,initial-scale=1.0")
@SpringUI
public class VideoApplication extends UI implements Serializable {

    @WebServlet(
            urlPatterns = "/*", 
            name = "VideoApplicationServlet", 
            asyncSupported = true
    )
    @VaadinServletConfiguration(
            ui = VideoApplication.class, 
            productionMode = false, 
            widgetset = "pl.luxtech.movie.widgetset.VideoDowmloaderWidgetset", 
            closeIdleSessions = true, 
            heartbeatInterval = 300
    )
    public static class VideoApplicationServlet extends VaadinServlet {        
    }

    public VideoApplication() {
        this.setSizeFull();
    }

    private VideoDownloaderMenuLayout root = new VideoDownloaderMenuLayout();
    private CssLayout menu = new CssLayout();
    private VerticalLayout menuItemsLayout = new VerticalLayout();
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<>();

    @Override
    protected void init(VaadinRequest request) {
        this.getPage().setTitle(VideoConsts.APP_TITLE);
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(99999999);

        UI.getCurrent().setErrorHandler(new ErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable exception = event.getThrowable();

                if (exception.getCause() != null && exception.getCause().getMessage() != null) {
                    exception = exception.getCause();
                }

                if (exception instanceof MalformedURLException) {
                    NotificationUtils.error("Invalid YouTobe address.");

                } else {
                    NotificationUtils.error(exception);

                }
            }
        });

        if (browserCantRenderFontsConsistently()) {
            
            UI.getCurrent().getPage().getStyles().add(".v-app.v-app.v-app {font-family: Sans-Serif;}");
        }

        UI.getCurrent().getPage().addBrowserWindowResizeListener(new BrowserWindowResizeListener() {

            @Override
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                setMenuVisible();
            }
        });

        menu.setStyleName(ValoTheme.PANEL_WELL);
        menu.setHeight(100, Unit.PERCENTAGE);
        if (UI.getCurrent().getPage().getWebBrowser().isIE() && UI.getCurrent().getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }

        Responsive.makeResponsive(this);
        root.setWidth("100%");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setHeight(53, Unit.PIXELS);
        top.setMargin(false);
        top.setSpacing(true);
        top.setPrimaryStyleName(ValoTheme.MENU_TITLE);

        Button showMenu = new Button("", new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                menu.setVisible(!menu.isVisible());
            }
        });

        showMenu.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        showMenu.addStyleName(ValoTheme.BUTTON_QUIET);
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.setHeight(53, Unit.PIXELS);
        showMenu.setIcon(VaadinIcons.LINES_LIST);
        top.addComponent(showMenu);

        Label title = new Label("<h2>Video <strong> Downloader</strong></h2>", ContentMode.HTML);
        title.setSizeUndefined();

        top.addComponent(title);
        top.setExpandRatio(title, 1);

        VerticalLayout displayLayout = new VerticalLayout();
        displayLayout.setSizeFull();
        displayLayout.setMargin(false);
        displayLayout.setSpacing(false);
        displayLayout.addComponent(top);
        displayLayout.addComponent(root);
        displayLayout.setExpandRatio(root, 1);

        setContent(displayLayout);
        root.addMenu(buildMenu());

        navigator = new Navigator(this, root.getContentContainer());

        navigator.addView(VideoViewEnum.YOUTOBE.getName(), YouTubeView.class);
        navigator.addView(VideoViewEnum.SETTINGS.getName(), SettingsView.class);
        navigator.addView(VideoViewEnum.HISTORY.getName(), HistoryView.class);

        String f = Page.getCurrent().getUriFragment();
        if (f == null || f.equals("")) {
            navigator.navigateTo("youtobe");
        }

        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                for (Iterator<Component> it = menuItemsLayout.iterator(); it.hasNext();) {
                    it.next().removeStyleName("selected");
                }

                for (Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (Iterator<Component> it = menuItemsLayout.iterator(); it.hasNext();) {
                            Component c = it.next();
                            if (c.getCaption() != null && c.getCaption().startsWith(item.getValue())) {
                                c.addStyleName("selected");
                                break;
                            }
                        }
                        break;
                    }
                }
                setMenuVisible();
            }
        });

    }

    private boolean browserCantRenderFontsConsistently() {
        return UI.getCurrent().getPage().getWebBrowser().getBrowserApplication().contains("PhantomJS");
    }

    private CssLayout buildMenu() {

        menuItems.put(VideoViewEnum.YOUTOBE.getName(), "YouTobe");
        //menuItems.put(VideoViewEnum.HISTORY.getName(), "History");
        menuItems.put(VideoViewEnum.SETTINGS.getName(), "Settings");

        menu.addComponent(menuItemsLayout);
        menu.setWidth("200px");

        menuItemsLayout.setMargin(new MarginInfo(true, true));
        menuItemsLayout.setSpacing(true);

        for (final Entry<String, String> item : menuItems.entrySet()) {

            Button b = new Button(item.getValue(), new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(item.getKey());
                }
            });

            b.setHeight(37, Unit.PIXELS);
            b.setCaptionAsHtml(true);
            b.setStyleName(ValoTheme.BUTTON_DANGER);
            b.addStyleName(MaterialTheme.BUTTON_FLAT);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);

            // https://icons8.com
            if (item.getKey().equals("youtobe")) {
                b.setIcon(new ThemeResource("images/film-24.png"));

            } else if (item.getKey().equals("settings")) {
                b.setIcon(new ThemeResource("images/settings-24.png"));

            } else if (item.getKey().equals("history")) {
                b.setIcon(new ThemeResource("images/history-24.png"));

            } else {
            }
            menuItemsLayout.addComponent(b);
            menuItemsLayout.setExpandRatio(b, 1);
        }

        return menu;
    }

    private void setMenuVisible() {
        if (320 > UI.getCurrent().getPage().getBrowserWindowWidth()) {
            menu.setVisible(false);
            menu.setWidth(UI.getCurrent().getPage().getBrowserWindowWidth(), Unit.PIXELS);

        } else {
            menu.setWidth(200, Unit.PIXELS);
        }
    }

}
