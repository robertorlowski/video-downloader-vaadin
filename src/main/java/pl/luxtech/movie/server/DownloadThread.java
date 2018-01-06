package pl.luxtech.movie.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import pl.luxtech.movie.model.VideoItem;
import pl.luxtech.movie.server.services.YouTubeDewnloadServices;

public class DownloadThread extends Thread {

    private YouTubeDewnloadServices.DownloadListener downloadListener = new YouTubeDewnloadServices.DownloadListener() {

        @Override
        public void downloadStart(VideoItem videoItem) {
            for (ChangeListener changeListener : listeners) {
                changeListener.refreshItem(videoItem, 0);
            }
        }

        @Override
        public void downloadProgress(VideoItem videoItem, float progress) {
            videoItem.setProgress(progress);
            for (ChangeListener changeListener : listeners) {
                changeListener.refreshItem(videoItem, progress);
            }
        }

        @Override
        public void downloadFinished(VideoItem videoItem, Boolean terminate) {
            videoItem.setProgress(Boolean.TRUE.equals(terminate) ? -1f : 1f);

            for (ChangeListener listener : listeners) {
                listener.refreshItem(videoItem, videoItem.getProgress());
            }

            DownloadThread services = downloadThreadList.get(videoItem);
            if (services == null) {
                return;
            }
            downloadThreadList.put(videoItem, null);
            services.interrupt();
        }
    };

    public static LinkedHashMap<VideoItem, DownloadThread> downloadThreadList = new LinkedHashMap<>();
    public static ArrayList<ChangeListener> listeners = new ArrayList<>();

    public YouTubeDewnloadServices services = new YouTubeDewnloadServices(downloadListener);
    private VideoItem videoItem;

    public interface ChangeListener {
        void addItem(VideoItem item);

        void deleteItem(VideoItem item);

        void refreshItem(VideoItem item, double progress);
    };

    public DownloadThread(VideoItem item) {
        super();
        this.videoItem = item;
    }

    public void stopDownload() {
        services.stopYoutobe();
        interrupt();
    }

    @Override
    public void run() {
        services.downloadYoutobe(videoItem);
        super.run();
    }

    public static void addListenerEx(ChangeListener changeListener) {
        if (!listeners.contains(changeListener)) {
            listeners.add(changeListener);
        }
    }

    public static void removeListenerEx(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }

    public static void downloadVideoEx(VideoItem videoItem) {
        for (ChangeListener lll : listeners) {
            lll.refreshItem(videoItem, 0);
        }

        DownloadThread service = new DownloadThread(videoItem);
        downloadThreadList.put(videoItem, service);
        service.start();
    }

    public static void addVideoEx(VideoItem videoItem) {
        YouTubeDewnloadServices services = new YouTubeDewnloadServices(new YouTubeDewnloadServices.DownloadListener() {
            
            @Override
            public void downloadStart(VideoItem videoItem) {
            }
            
            @Override
            public void downloadProgress(VideoItem videoItem, float progress) {
            }
            
            @Override
            public void downloadFinished(VideoItem videoItem, Boolean terminate) {
            }
        });
        
        services.prepareYoutobe(videoItem);
        
        downloadThreadList.put(videoItem, null);

        for (ChangeListener lll : listeners) {
            lll.addItem(videoItem);
        }
    }

    public static void removeVideoEx(VideoItem videoItem) {
        if (videoItem == null) {
            return;
        }

        cancelVideoEx(videoItem);
        for (ChangeListener changeListener : listeners) {
            changeListener.deleteItem(videoItem);
        }
    }

    public static void cancelVideoEx(VideoItem videoItem) {
        if (videoItem == null) {
            return;
        }

        DownloadThread services = downloadThreadList.get(videoItem);
        if (services != null) {
            services.stopDownload();
        }
        downloadThreadList.remove(videoItem);

        for (ChangeListener changeListener : listeners) {
            changeListener.refreshItem(videoItem, -1);
        }
    }

    public static Set<VideoItem> getVideosEx() {
        return new HashSet<>(downloadThreadList.keySet());
    }

    public static void deleteAllEx() {
        for (VideoItem videoItem : getVideosEx()) {
            removeVideoEx(videoItem);
        }
    }

    public static void downloadAllEx() {
        for (VideoItem videoItem : getVideosEx()) {
            downloadVideoEx(videoItem);
        }
    }
}
