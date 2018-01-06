package pl.luxtech.movie.client.view;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import pl.luxtech.movie.client.common.AppCtx;
import pl.luxtech.movie.client.view.component.YouTubeViewItem;
import pl.luxtech.movie.model.VideoItem;
import pl.luxtech.movie.server.DownloadThread;

@SuppressWarnings({ "serial" })
public class YouTubeView extends VerticalLayout implements View, ClickListener {

    private TextField url;
    private Button addButton;
    private Button deleteAllButton;
    private Button downloadAllButton;
    private Button downloadDirectory;
    private VerticalLayout contentArea = new VerticalLayout();
    private ProgressBar progressPrepare = new ProgressBar();

    private DownloadThread.ChangeListener changeListener = new DownloadThread.ChangeListener() {

        @Override
        public void deleteItem(VideoItem item) {
            deleteYouTubeViewItem(item);

        }

        @Override
        public void addItem(VideoItem item) {
            addYouTubeViewItem(item);

        }

        @Override
        public void refreshItem(VideoItem item, double progress) {
            refreshYouTubeViewItem(item, progress);
        }
    };

    public YouTubeView() {
        this.setSizeFull();
        this.setSpacing(true);
        this.setMargin(new MarginInfo(false, true));

        Label title = new Label("YouTobe downloader");
        title.addStyleName(ValoTheme.LABEL_H3);
        addComponent(title);

        Component directory = buildURL();
        Component table = buildGrid();

        addComponent(directory);
        addComponent(table);        

        setExpandRatio(table, 1);
    }

    private void addYouTubeViewItem(VideoItem item) {
        YouTubeViewItem xxx = new YouTubeViewItem(item);
        contentArea.addComponent(xxx);
        contentArea.setComponentAlignment(xxx, Alignment.TOP_LEFT);
        contentArea.setExpandRatio(xxx, 1);

        System.out.println("Item: " + item.getTitle() + " add ");
    }

    private void addAllYouTubeViewItems() {
        for (VideoItem videoItem : DownloadThread.getVideosEx()) {
            addYouTubeViewItem(videoItem);
        }
    }

    private YouTubeViewItem findYouTubeViewItem(VideoItem item) {
        for (int xxx = 0; xxx < contentArea.getComponentCount(); xxx++) {
            if (contentArea.getComponent(xxx) instanceof YouTubeViewItem) {
                YouTubeViewItem ooo = (YouTubeViewItem) contentArea.getComponent(xxx);
                if (ooo.getVideoItem().getID().equals(item.getID())) {
                    return ooo;
                }
            }
        }
        return null;
    }

    private Boolean refreshYouTubeViewItem(VideoItem item, Double progress) {
        YouTubeViewItem ooo = findYouTubeViewItem(item);
        if (ooo == null) {
            return true;
        }
        ooo.markAsDirty();
        ooo.setProgress(progress.floatValue());

        System.out.println("Item: " + item.getTitle() + " progress: " + progress);

        return true;
    }

    private void deleteYouTubeViewItem(VideoItem item) {

        YouTubeViewItem ooo = findYouTubeViewItem(item);
        if (ooo == null) {
            return;
        }
        contentArea.removeComponent(ooo);
    }

    private Panel buildURL() {
        Panel p = new Panel();
        VerticalLayout content = new VerticalLayout();

        url = new TextField();
        url.setCaption("YouTobe address:");
        url.setPlaceholder("https://www.youtube.com");
        url.setWidth(100, Unit.PERCENTAGE);

        url.addShortcutListener(new ShortcutListener("YouTobe address", KeyCode.ENTER, null ) {

            @Override
            public void handleAction(Object sender, Object target) {
                addButton.click();
            }
        });
        
        content.addComponent(url);       
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(false);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        
        addButton = new Button("ADD VIDEO");
        addButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addButton.setWidth(150, Unit.PIXELS);
        addButton.setIcon(VaadinIcons.PLUS_CIRCLE);
        addButton.addClickListener(this);
        buttonLayout.addComponent(addButton);
        buttonLayout.setComponentAlignment(addButton, Alignment.BOTTOM_LEFT);

        deleteAllButton = new Button("DELETE ALL");
        deleteAllButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        deleteAllButton.setWidth(150, Unit.PIXELS);
        deleteAllButton.setIcon(VaadinIcons.CLOSE_CIRCLE);
        deleteAllButton.addClickListener(this);
        buttonLayout.addComponent(deleteAllButton);
        buttonLayout.setComponentAlignment(deleteAllButton, Alignment.BOTTOM_LEFT);


        downloadAllButton = new Button("DOWNLOAD ALL");
        downloadAllButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        downloadAllButton.setWidth(190, Unit.PIXELS);
        downloadAllButton.setIcon(VaadinIcons.DOWNLOAD);
        downloadAllButton.addClickListener(this);
        buttonLayout.addComponent(downloadAllButton);
        buttonLayout.setComponentAlignment(downloadAllButton, Alignment.BOTTOM_LEFT);     
        
        if ( "".equals(AppCtx.getDestinationDirectory())  ) {
            downloadDirectory = new Button("SET VIDEO DIRECTORY");
            downloadDirectory.setDescription("Destination directory for download files");
            downloadDirectory.setStyleName(ValoTheme.BUTTON_BORDERLESS);
            downloadDirectory.addStyleName(ValoTheme.BUTTON_DANGER);
            
            
        } else {
            downloadDirectory = new Button("VIDEO DIRECTORY");
            downloadDirectory.setDescription("Destination directory for download files: " + AppCtx.getDestinationDirectory());
            downloadDirectory.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
            
        }
        
        downloadDirectory.setIcon(VaadinIcons.FOLDER);
        downloadDirectory.addClickListener(this);
        buttonLayout.addComponent(downloadDirectory);
        buttonLayout.setComponentAlignment(downloadDirectory, Alignment.BOTTOM_RIGHT); 
        buttonLayout.setExpandRatio(downloadDirectory, 1);
        
        content.addComponent(buttonLayout);
        
        
        p.setContent(content);
        return p;
    }

    private Component buildGrid() {
        contentArea.setPrimaryStyleName("valo-content");
        contentArea.addStyleName("v-scrollable");
        contentArea.setWidth(100, Unit.PERCENTAGE);
        contentArea.setSpacing(false);
        return contentArea;
    }

    @Override
    public void buttonClick(ClickEvent event) {

        if (addButton.equals(event.getButton())) {
            addVideo();

        } else if (deleteAllButton.equals(event.getButton())) {
            DownloadThread.deleteAllEx();
        
        } else if (downloadAllButton.equals(event.getButton())) {
            DownloadThread.downloadAllEx();
        
        } else if (downloadDirectory.equals(event.getButton())) {
            AppCtx.showSettingView();
            
        } else {
            
        }
    }

    private void addVideo() {
        if (url.getValue() == null || url.getValue() == "") {
            return;
        }

        progressPrepare.setVisible(true);
        VideoItem videoItem = new VideoItem(url.getValue(), AppCtx.getDestinationDirectory() );

        UI.getCurrent().access(new Runnable() {

            @Override
            public void run() {
                try {
                    DownloadThread.addVideoEx(videoItem);

                } catch (Exception ee) {
                    progressPrepare.setVisible(false);
                    url.selectAll();
                    url.focus();

                    throw ee;

                } finally {
                    progressPrepare.setVisible(false);

                }
            }
        });

        url.setValue("");
    }

    @Override
    public void attach() {
        super.attach();
        DownloadThread.addListenerEx(changeListener);
        addAllYouTubeViewItems();
    }

    @Override
    public void detach() {
        DownloadThread.removeListenerEx(changeListener);
        super.detach();
    }
}
