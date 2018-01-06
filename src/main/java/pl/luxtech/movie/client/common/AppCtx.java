package pl.luxtech.movie.client.common;

import com.vaadin.ui.UI;

import pl.luxtech.movie.common.VideoViewEnum;

public class AppCtx {
    
    private static String downloadDirectory = null;
    private static Boolean autoDownloadVideo = null;
    
    public static String getDestinationDirectory() {
        return AppCtx.downloadDirectory;
    }

    public static Boolean isAutoDownloadVideo() {
        return AppCtx.autoDownloadVideo;
    }
    
    public static void setDownloadDirectory(String downloadDirectory) {
        AppCtx.downloadDirectory = downloadDirectory;
    }

    public static void setAutoDownloadVideo(Boolean autoDownloadVideo) {
        AppCtx.autoDownloadVideo = autoDownloadVideo;
    }
    
    public static void showSettingView() {
        UI.getCurrent().getNavigator().navigateTo(VideoViewEnum.SETTINGS.getName());        
    }
    
}
