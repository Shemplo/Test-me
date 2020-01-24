package ru.shemplo.tm.gui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import ru.shemplo.tm.entity.HistoryLogEntry;
import ru.shemplo.tm.entity.HistoryLogger;

@RequiredArgsConstructor
public class HistoryWindow {
    
    private final AppWindow parentWindow;
    
    private Scene scene;
    
    public void show () {
        final Stage stage = new Stage ();
        
        scene = new Scene (createLayout ());
        
        Optional.ofNullable (parentWindow.getWindowIcon ()).ifPresent (icon -> {
            stage.getIcons ().add (icon);
        });
        
        stage.initModality (Modality.WINDOW_MODAL);
        stage.initOwner (parentWindow.getStage ());
        stage.setTitle ("History log");
        stage.setResizable (false);
        stage.setScene (scene);
        stage.sizeToScene ();
        stage.show ();
    }
    
    private Parent createLayout () {
        final VBox mainContainer = new VBox (8);
        mainContainer.getStylesheets ().add ("/css/history.css");
        mainContainer.setPadding (new Insets (8));
        
        final HistoryLogger historyLogger = parentWindow.getHistoryLogger ();
        
        final ListView <HistoryLogEntry> historyList = new ListView <> ();
        historyList.setCellFactory (__ -> new HistoryCell (this));
        historyList.setItems (historyLogger.getObservableHistory ());
        historyList.setEditable (false);
        historyList.setMinWidth (512);
        mainContainer.getChildren ().add (historyList);
        
        return mainContainer;
    }
    
}
