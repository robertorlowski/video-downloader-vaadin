/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luxtech.movie.server.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.vget.vhs.YouTubeInfo.StreamCombined;
import com.github.axet.vget.vhs.YouTubeInfo.StreamInfo;
import com.github.axet.vget.vhs.YouTubeMPGParser;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;
import com.github.axet.wget.info.ex.DownloadInterruptedError;

import pl.luxtech.movie.model.VideoItem;

public class YouTubeDewnloadServices {

    private VideoInfo videoinfo;
    private VideoItem videoItem;
    private long last;
    private int errorCount = 0;
    private DownloadListener downloadListener;
    private AtomicBoolean stop = new AtomicBoolean(false);

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public interface DownloadListener {
        void downloadStart(final VideoItem videoItem);

        void downloadProgress(final VideoItem videoItem, final float progress);

        void downloadFinished(final VideoItem videoItem, Boolean terminate);
    }

    public YouTubeDewnloadServices() {
        this.downloadListener = new DownloadListener() {

            @Override
            public void downloadStart(VideoItem videoItem) {
            }

            @Override
            public void downloadProgress(VideoItem videoItem, float progress) {
            }

            @Override
            public void downloadFinished(VideoItem videoItem, Boolean terminate) {
            }
        };
    }

    public YouTubeDewnloadServices(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void stopYoutobe() {
        stop.set(true);

        if (Boolean.TRUE.equals(stop.get())) {
            downloadListener.downloadFinished(videoItem, true);
        }

    }

    public void downloadYoutobe(VideoItem videoItem) {
        try {
            runYoutobe(videoItem, true);
        } catch (DownloadInterruptedError | InterruptedException | MalformedURLException ex) {
        }
    }

    public void prepareYoutobe(VideoItem videoItem) {
        try {
            runYoutobe(videoItem, false);

        } catch (DownloadInterruptedError | InterruptedException | MalformedURLException ex) {
        }
    }

    private void runYoutobe(VideoItem videoItem, Boolean download) throws DownloadInterruptedError, InterruptedException, MalformedURLException {
        stop.set(false);
        this.videoItem = videoItem;

        Runnable notify = () -> {

            List<VideoFileInfo> list = videoinfo.getInfo();

            switch (videoinfo.getState()) {
            case EXTRACTING_DONE:
                if (videoinfo instanceof YouTubeInfo) {
                    YouTubeInfo i = (YouTubeInfo) videoinfo;

                    videoItem.setInfo(i);

                    // downloadListener.extracted(videoItem);
                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());

                } else if (videoinfo instanceof VimeoInfo) {
                    VimeoInfo i = (VimeoInfo) videoinfo;

                    videoItem.setTitle(videoinfo.getTitle());
                    // downloadListener.extracted(videoItem);

                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());

                } else {
                    System.out.println("downloading unknown quality");
                }

                break;

            case ERROR:
            case RETRYING:
                errorCount++;
                System.out.println(videoinfo.getState() + " ERROR COUNT: " + errorCount + " - " + videoItem.getTitle());

                if (errorCount > 10) {
                    throw new RuntimeException("There is an error during getting your video information.");
                }
                break;

            case DOWNLOADING:

                long now = System.currentTimeMillis();
                if (now - 1000 > last) {
                    last = now;

                    String parts = "";

                    for (VideoFileInfo dinfo : list) {
                        List<Part> pp = dinfo.getParts();
                        if (pp != null) {
                            // multipart download
                            for (Part p : pp) {
                                if (p.getState().equals(States.DOWNLOADING)) {
                                    parts += String.format("part#%d(%.2f) ", p.getNumber(), p.getCount() / (float) p.getLength());
                                }
                            }
                        }

                        System.out.println(String.format("file:%d - %s %.2f %s ", list.indexOf(dinfo), videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts));

                        downloadListener.downloadProgress(videoItem, dinfo.getCount() / (float) dinfo.getLength());
                    }
                }
                break;

            case DONE:
                downloadListener.downloadFinished(videoItem, stop.get());
                System.out.println(videoinfo.getState() + " -" + videoItem.getTitle());
                break;
            case EXTRACTING:
                break;
            case QUEUE:
                break;
            case STOP:
                break;
            default:
                break;

            }
        };

        URL web = new URL(videoItem.getUrl());
        VGetParser user = null;

        user = new YouTubeMPGParser() {

            @Override
            public void filter(List<VideoDownload> sNextVideoURL, String itag, URL url) {
                Integer i = Integer.decode(itag);
                StreamInfo vd = itagMap.get(i);

                if (vd instanceof StreamCombined) {
                    super.filter(sNextVideoURL, itag, url);
                }

            }
        };

        // create proper videoinfo to keep specific video information
        videoinfo = user.info(web);

        VGet v = new VGet(videoinfo, new File(videoItem.getFolder()));
        // [OPTIONAL] call v.extract() only if you d like to get video title
        // or download url link
        // before start download. or just skip it.
        v.extract(user, stop, notify);

        // System.out.println("Title: " + info.getTitle());
        // System.out.println("Download URL: " + info.getInfo().getSource());

        if (download) {
            downloadListener.downloadStart(videoItem);
            v.download(user, stop, notify);
        }
    }
}
