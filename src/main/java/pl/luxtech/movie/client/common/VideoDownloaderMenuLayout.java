package pl.luxtech.movie.client.common;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class VideoDownloaderMenuLayout extends HorizontalLayout {

    VerticalLayout contentArea = new VerticalLayout();

    public VideoDownloaderMenuLayout() {
        setSizeFull();
        setSpacing(false);

        contentArea.setPrimaryStyleName("valo-content");
        contentArea.addStyleName("v-scrollable");
        contentArea.setSizeFull();
    }

    public ComponentContainer getContentContainer() {
        return contentArea;
    }

    public void addMenu(Component menu) {
        addComponents(menu, contentArea);
        setExpandRatio(contentArea, 1);
    }

}
