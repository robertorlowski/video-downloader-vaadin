package pl.luxtech.movie.client.view;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import pl.luxtech.movie.client.common.AppCtx;

@SuppressWarnings("serial")
public class SettingsView extends VerticalLayout implements View {

    public SettingsView() {
        setSizeFull();
        setSpacing(false);
        setMargin(new MarginInfo(false, true));

        Label title = new Label("Settings");
        title.addStyleName(ValoTheme.LABEL_H3);
        addComponent(title);

        Component directory = buildDirectory();

        addComponent(directory);
        setExpandRatio(directory, 1);
    }

    private Panel buildDirectory() {
        Panel p = new Panel("Destination directory for video files");
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField directory = new TextField();
        directory.setPlaceholder("Directory for video files.");
        directory.setWidth(100, Unit.PERCENTAGE);
        directory.setValue(AppCtx.getDestinationDirectory());
        content.addComponent(directory);

        CheckBox autoDownload = new CheckBox("Auto download video");
        autoDownload.setValue(AppCtx.isAutoDownloadVideo());
        content.addComponent(autoDownload);        
        
        Button saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setWidth(150, Unit.PIXELS);
        saveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                AppCtx.setDownloadDirectory(directory.getValue());
                AppCtx.setAutoDownloadVideo( autoDownload.getValue() );
            }
        });

        content.addComponent(saveButton);
        content.setComponentAlignment(saveButton, Alignment.BOTTOM_LEFT);

        p.setContent(content);
        return p;
    }

}
