
package pl.luxtech.movie;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import pl.luxtech.movie.client.common.AppCtx;
import pl.luxtech.movie.common.VideoConsts;
import pl.luxtech.movie.server.listeners.PerformListener;
import pl.luxtech.movie.server.utils.GuiUtils;
import pl.luxtech.movie.server.utils.ServerUtils;

@SpringBootApplication
public class VideoMainApplication {
    private final static Logger logger = LoggerFactory.getLogger(VideoMainApplication.class);

    private static ConfigurableApplicationContext context;
    private static Integer port = null;
    
    private static TrayIcon trayIcon;
    private static SystemTray tray;
    
    public static void main(String[] args) {
        port = createHttpPort();
              
        startup();
        
        GuiUtils.executeLater(new PerformListener() {
            @Override
            public void execute() {
                try {
                    
                    HashMap<String, Object> props = new HashMap<>();
                    props.put("server.port", port);
                    
                    SpringApplicationBuilder builder = new SpringApplicationBuilder(VideoMainApplication.class);

                    builder.properties(props);
                    builder.headless(false);
                    context = builder.run();
                    
                } catch (Exception e) {   
                    logger.error("An error occurred while running the application", e);
                    GuiUtils.showMessegeError(trayIcon, "An error occurred while running the application: " + e.getMessage());
                    return;
                }
                
                GuiUtils.showMessege(trayIcon, "The application has been started."); 
                trayIcon.setToolTip(VideoConsts.APP_TITLE);
                
                openVideoDownloader();
            }

        }, 500L);       
    }

    private static Integer createHttpPort() {
        Integer port = 8080;
        
        while (!ServerUtils.checkAvailablePort(port)) {
            port++;
        }
        return port;
    }

    private static void startup() {
        logger.info("Start application.");

        prepareAppSetings();
        
        try {
            tray = SystemTray.getSystemTray();
            Image trayImage = Toolkit.getDefaultToolkit().getImage(VideoMainApplication.class.getResource("/icons/download-16.png"));
            final PopupMenu popup = new PopupMenu();

            registerOpenVideoDownloader(popup);
            registerOpenDictionaryForVideoFiles(popup);
            popup.addSeparator();  
            registerExitAgent(popup);
            
            trayIcon = new TrayIcon(trayImage, VideoConsts.APP_TITLE, popup);
            tray.add(trayIcon);

            GuiUtils.showMessege(trayIcon, "The application is starting... (please wait)");
            
        } catch (Exception e) {
            logger.error("An error occurred while running the application", e);
            
        }
    }
    
    private static void prepareAppSetings() {
        if ( AppCtx.getDestinationDirectory() == null ) {
            AppCtx.setDownloadDirectory(Paths.get(".").toAbsolutePath().normalize().toString());              
        }
        
        if (AppCtx.isAutoDownloadVideo() == null) {
            AppCtx.setAutoDownloadVideo(true);
        }
    }

    private static void registerOpenVideoDownloader(PopupMenu popup) {
        ActionListener openListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openVideoDownloader();
            }
        };

        GuiUtils.addMenuItemToPopup(popup, "Open video downloader", openListener);           
    }

    protected static void openVideoDownloader() {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + port));
            
        } catch (Exception e) {
            logger.error("Open video downloader", e);
        }
        
    }

    private static void registerOpenDictionaryForVideoFiles(PopupMenu popup) {
        ActionListener openListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDictionaryForDownloadFiles();
            }
        };

        GuiUtils.addMenuItemToPopup(popup, "Open dictionary for video files", openListener);        
    }

    protected static void openDictionaryForDownloadFiles() {
        try {
            Desktop.getDesktop().open(new File( AppCtx.getDestinationDirectory() ));
        
        } catch (IOException e) {
            logger.error("Open dictionary for download files", e);
        }
    }

    public static void shutdown() {     
        GuiUtils.executeLater(new PerformListener(){
            @Override
            public void execute() {
                if ( context != null) {
                    VideoMainApplication.context.close();
                }
                
                if ( tray != null && trayIcon != null ) {
                    tray.remove(trayIcon);
                }          
                
                System.exit(0);
            }}
        
        , 500L);                                   
    };
       
    private static void registerExitAgent(PopupMenu popup) {
        ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        };

        GuiUtils.addMenuItemToPopup(popup, "Close", exitListener);
    }
    

}