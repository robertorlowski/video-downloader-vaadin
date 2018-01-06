package pl.luxtech.movie.client.view.component;

import com.github.appreciated.material.MaterialTheme;
import com.github.axet.vget.info.VideoFileInfo;
import com.vaadin.annotations.Push;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import pl.luxtech.movie.client.common.AppCtx;
import pl.luxtech.movie.model.VideoItem;
import pl.luxtech.movie.server.DownloadThread;

@SuppressWarnings("serial")
@Push
public class YouTubeViewItem extends HorizontalLayout {

    private ProgressBar downloadingBar = new ProgressBar(0f);
    private Label progressLabel = new Label("", ContentMode.HTML);
    private Button download = new Button("DOWNLOAD");
    private Button cancel = new Button("CANCEL");
    private Button play = new Button("PLAY");
    private Button delete = new Button("DELETE");
    private VideoItem item;

    public VideoItem getVideoItem() {
        return item;
    }

    public void setProgress(Float progress) {
        cancel.setVisible(progress != -1 && progress != 1);
        download.setVisible(progress == -1 || progress == 1);
        
        downloadingBar.setEnabled(progress > -1);
        progressLabel.setEnabled(progress > -1);
        downloadingBar.setValue(progress==-1?0:progress);
    }

    public YouTubeViewItem(VideoItem videoItem) {
        super();

        UI.getCurrent().setPollInterval(1000);
        UI.getCurrent().addPollListener(new PollListener() {
            
            @Override
            public void poll(PollEvent event) {
                progressLabel.setValue("Progress: " + ((Float)(downloadingBar.getValue() * 100 )).intValue()  + "%");     
            }
        });
        
        this.item = videoItem;
        this.addStyleName("videoitem");

        ExternalResource res = new ExternalResource(item.getIcon().toString());
        Image image = new Image(null, res);
        image.setWidth(160, Unit.PIXELS);
        image.setHeight(96, Unit.PIXELS);
        image.setDescription(item.getUrl());

        downloadingBar.setWidth(333, Unit.PIXELS);
        downloadingBar.setHeight(10, Unit.PIXELS);
        //downloadingBar.setHeightUndefined();
        downloadingBar.addStyleName("download");
        
        progressLabel.addStyleName(ValoTheme.LABEL_TINY);
        progressLabel.setWidth(100, Unit.PERCENTAGE);
        progressLabel.setHeight(10, Unit.PIXELS);

        VerticalLayout dataImage = new VerticalLayout();
        dataImage.setMargin(false);
        dataImage.setSpacing(false);
        dataImage.setHeight(120, Unit.PIXELS);
        dataImage.setWidth(170, Unit.PIXELS);
        dataImage.addComponent(image);
        dataImage.setComponentAlignment(image, Alignment.MIDDLE_LEFT);

        Label title = new Label(item.getTitle(), ContentMode.HTML);
        title.setWidth(100, Unit.PERCENTAGE);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.setDescription(item.getUrl());

        //HorizontalLayout videoInfo = new HorizontalLayout();
        //videoInfo.setMargin(false);
        //videoInfo.setSpacing(false);
        //videoInfo.addComponent(downloadingBar);
        
        HorizontalLayout videoBuuton = new HorizontalLayout();
        videoBuuton.setWidth(100, Unit.PERCENTAGE);
        videoBuuton.setMargin(false);
        videoBuuton.setSpacing(true);

        play.setIcon(VaadinIcons.PLAY_CIRCLE);
        play.addStyleName(MaterialTheme.BUTTON_ICON_ONLY);
        play.addStyleName(MaterialTheme.BUTTON_FLAT);
        play.addStyleName(MaterialTheme.BUTTON_BORDER);
        play.addStyleName(MaterialTheme.BUTTON_PRIMARY);
        play.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                for (VideoFileInfo fileInfo : item.getInfo().getInfo()) {
                    getUI().getPage().open(fileInfo.getSource().toString(), "_blank");
                    break;
                }
            }
        });

        download.setIcon(VaadinIcons.DOWNLOAD);
        download.addStyleName(MaterialTheme.BUTTON_ICON_ONLY);
        download.addStyleName(MaterialTheme.BUTTON_FLAT);
        download.addStyleName(MaterialTheme.BUTTON_BORDER);
        download.addStyleName(MaterialTheme.BUTTON_PRIMARY);

        download.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                DownloadThread.downloadVideoEx(item);
                setProgress(0.01f);
            }
        });

        cancel.setIcon(VaadinIcons.STOP);
        cancel.addStyleName(MaterialTheme.BUTTON_ICON_ONLY);
        cancel.addStyleName(MaterialTheme.BUTTON_FLAT);
        cancel.addStyleName(MaterialTheme.BUTTON_BORDER);
        cancel.addStyleName(MaterialTheme.BUTTON_FRIENDLY);

        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                DownloadThread.cancelVideoEx(item);
            }
        });

        delete.setIcon(VaadinIcons.CLOSE_CIRCLE);
        delete.addStyleName(MaterialTheme.BUTTON_ICON_ONLY);
        delete.addStyleName(MaterialTheme.BUTTON_FLAT);
        delete.addStyleName(MaterialTheme.BUTTON_BORDER);
        delete.addStyleName(MaterialTheme.BUTTON_PRIMARY);
        delete.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                DownloadThread.removeVideoEx(item);
            }
        });

        videoBuuton.addComponent(play);
        videoBuuton.addComponent(delete);
        videoBuuton.addComponent(download);
        videoBuuton.addComponent(cancel);
        videoBuuton.setExpandRatio(cancel, 1);

        VerticalLayout data = new VerticalLayout();
        data.addStyleName("videoinfo");
        data.setSpacing(false);
        data.setMargin(false);
        data.addComponent(title);
        data.addComponent(progressLabel);
        data.addComponent(downloadingBar);
        data.addComponent(videoBuuton);
        data.setComponentAlignment(videoBuuton, Alignment.BOTTOM_LEFT);
        //data.setExpandRatio(videoBuuton, 1);

        HorizontalLayout details = new HorizontalLayout();
        details.setMargin(false);
        details.setSpacing(false);
        details.setSizeFull();

        details.addComponent(dataImage);
        details.addComponent(data);
        details.setExpandRatio(data, 1);

        this.addComponent(details);
        setProgress(videoItem.getProgress());

        if ( AppCtx.isAutoDownloadVideo() && !"".equals(AppCtx.getDestinationDirectory()) &&  -1 == videoItem.getProgress() ) {
            download.click();
        }

    }
}
