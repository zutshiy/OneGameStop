package com.game.lib;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.game.utils.Utility;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadGameLibraries
{
    private static final int SCENE_HEIGHT_INCREASE = 57;
    private static final int SCENE_HEIGHT_DECREASE = 57;
    private static final String FOUND = "Found!";

    private static final String STEAM_DIRECTORY = "C:\\Program Files (x86)\\Steam\\steamapps\\common";
    private static final String ORIGIN_DIRECTORY = "C:\\Program Files (x86)\\Origin Games";
    private static final String GOG_DIRECTORY = "C:\\Program Files (x86)\\GOG Galaxy\\Games";
    private static final String GAME_LIBRARIES_TXT = "GameLibraries.txt";

    private Stage window;
    private static double sceneHeight = 600;
    private boolean libsLoaded = false;

    private List<GameLibraryFieldUI> gameLibFields = new ArrayList<GameLibraryFieldUI>();

    public void loadScene()
    {
        window = new Stage();

        window.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                window.setMaximized(false);
        });

        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinHeight(220);
        window.setScene(initScene());
        window.setTitle("Load Game Libraries");
        window.setResizable(false);
        window.showAndWait();
    }

    public Scene initScene()
    {
        VBox fieldsContainerBox = new VBox();
        fieldsContainerBox.setSpacing(8);

        createPreLoadedPaths(fieldsContainerBox);

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(10, 10, 10, 10));
        bottomBox.setSpacing(10);

        initContextButtons(bottomBox, fieldsContainerBox);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 0, 0, 0));
        borderPane.setCenter(fieldsContainerBox);
        borderPane.setBottom(bottomBox);

        Scene loadGamePathsScene = new Scene(borderPane, 700, sceneHeight);
        loadGamePathsScene.getStylesheets().add(Utility.getResourceFile(this, "LoadGameLibrary.css").toExternalForm());

        return loadGamePathsScene;
    }

    private void initContextButtons(HBox bottomBox, VBox containerBox)
    {
        JFXButton loadButton = new JFXButton("Load The Libraries");
        setMousePressAnimation(loadButton);
        loadButton.setDefaultButton(true);
        loadButton.setOnAction(e -> saveGameLibsToFile());
        loadButton.getStyleClass().add("button-raised");

        JFXButton addButton = new JFXButton("Add New Library");
        setMousePressAnimation(addButton);
        addButton.setOnAction(e -> createGameLibFieldUI(containerBox, null));
        addButton.getStyleClass().add("button-raised");

        JFXButton closeButton = new JFXButton("Close");
        setMousePressAnimation(closeButton);
        closeButton.setCancelButton(true);
        closeButton.setOnAction(e -> window.close());
        closeButton.getStyleClass().add("button-raised");

        bottomBox.getChildren().addAll(loadButton, addButton, closeButton);
    }

    private void setMousePressAnimation(JFXButton loadButton)
    {
        loadButton.setOnMousePressed(e -> loadButton.setTranslateY(2.0));
        loadButton.setOnMouseReleased(e -> loadButton.setTranslateY(-2.0));
    }

    public void createPreLoadedPaths(VBox gridPane)
    {
        List<String> preLoadedPaths = initDefaultPaths();
        preLoadedPaths.forEach(p -> createGameLibFieldUI(gridPane, p));
    }

    private List<String> initDefaultPaths()
    {
        List<String> preLoadedPaths = new ArrayList<String>();

        File file = new File(GAME_LIBRARIES_TXT);
        String savedGameLibsString = (Utility.readFromFile(file));

        if (!Utility.isEmptyString(savedGameLibsString))
        {
            preLoadedPaths.addAll(Utility.splitLinesFromString(savedGameLibsString));
        }
        else
        {
            preLoadedPaths.addAll(Arrays.asList(STEAM_DIRECTORY, ORIGIN_DIRECTORY, GOG_DIRECTORY));
        }

        return preLoadedPaths;
    }

    private void createGameLibFieldUI(VBox containerBox, String preLoadedPath)
    {
        GameLibraryFieldUI gameLibField = new GameLibraryFieldUI();
        gameLibField.setPath(preLoadedPath);
        gameLibField.setFoundInd(Utility.EMPTY_STRING);
        gameLibField.setEditable(true);
        gameLibField.buildUIObject();
        initGameLibFieldsGridPos(gameLibField);
        gameLibFields.add(gameLibField);

        TextField pathField = gameLibField.getPathField();

        validateAndAddPath(gameLibField, null, gameLibField.getPath());

        pathField.textProperty().addListener((v, oldVal, newVal) -> {
            if (newVal != oldVal)
            {
                validateAndAddPath(gameLibField, oldVal, newVal);
            }
        });

        updateWindowHeight(SCENE_HEIGHT_INCREASE);
        containerBox.getChildren().add((gameLibField.getGridPane()));
    }

    private void validateAndAddPath(GameLibraryFieldUI gameLibField, String oldPath, String newPath)
    {
        String pathFoundInd = Utility.EMPTY_STRING;

        if (Utility.isEmptyString(newPath))
        {
            gameLibField.setFoundInd(pathFoundInd);
            return;
        }

        gameLibField.setPath(newPath);
        newPath = newPath.trim();

        if (Files.isDirectory(Paths.get(newPath)))
        {
            gameLibField.updateFoundInd(true);
        }
        else
        {
            gameLibField.updateFoundInd(false);
        }
    }

    private void initGameLibFieldsGridPos(GameLibraryFieldUI gameLibField)
    {
        gameLibField.getBrowseButton().setOnAction(e -> addBrowsePath(gameLibField));
        gameLibField.getRemoveButton().setOnAction(e -> removeGameFieldLibUI(gameLibField));
    }

    private void addBrowsePath(GameLibraryFieldUI gameLibField)
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose Library");
        dirChooser.setInitialDirectory(null);

        File selectedDir = dirChooser.showDialog(window);

        if (selectedDir != null)
        {
            gameLibField.setPath(selectedDir.getPath());
        }
    }

    private void removeGameFieldLibUI(GameLibraryFieldUI gameLibraryField)
    {
        Button removeButton = gameLibraryField.getRemoveButton();
        if (removeButton != null)
        {
            if (gameLibFields.size() == 1)
            {
                gameLibFields.get(0).setPath("");
                return;
            }

            VBox parentGridPane = (VBox) gameLibraryField.getGridPane().getParent();
            parentGridPane.getChildren().removeAll(gameLibraryField.getAllNodes());

            updateWindowHeight(-SCENE_HEIGHT_DECREASE);
            gameLibFields.remove(gameLibraryField);
        }

    }

    private void updateWindowHeight(double diff)
    {
        sceneHeight = window.getHeight() + diff;
        window.setHeight(sceneHeight);
    }

    private void saveGameLibsToFile()
    {
        String gameLibPathString = getPathString();
        boolean writeSuccessful = Utility.writeStringToFile(gameLibPathString, "GameLibraries.txt");
        showConfirmationDialogBox(writeSuccessful);

        if (writeSuccessful)
        {
            setLibsLoaded(true);
        }
    }

    private String getPathString()
    {
        String gameLibOptional = gameLibFields.stream()
                .filter(g -> isValidPath(g))
                .reduce(new StringJoiner("\n"), (s, p) -> s.add(p.getPath()), (s1, s2) -> s1.merge(s2)).toString();

        return gameLibOptional;
    }

    private boolean isValidPath(GameLibraryFieldUI g)
    {
        return FOUND.equals(g.getFoundInd()) && !Utility.isEmptyString(g.getPath());
    }

    private void showConfirmationDialogBox(boolean writeSuccessful)
    {
        JFXDialogLayout jfxDiag = new JFXDialogLayout();
        JFXAlert<Void> jfxAlert = new JFXAlert<>(window);

        String message = Utility.EMPTY_STRING;
        String subMessage = Utility.EMPTY_STRING;

        if (writeSuccessful)
        {
            message = "Successfully Loaded The Libraries";
            subMessage = "(The main window will refresh when you close this window.)";
        }
        else
        {
            message = "Could Not Load The Libraries!";
        }

        Label label = new Label(message);
        label.getStyleClass().add("label-dialog");

        Label subLabel = new Label(subMessage);
        subLabel.getStyleClass().add("label-dialog");
        subLabel.setStyle("-fx-font-size : 12px");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, subLabel);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.BASELINE_CENTER);

        jfxDiag.setBody(vBox);
        jfxDiag.setOnMouseClicked(e -> jfxAlert.close());
        jfxDiag.getStyleClass().add("jfx-dialog");
        JFXDialogLayout.setAlignment(label, Pos.TOP_CENTER);
        jfxAlert.setContent(jfxDiag);
        jfxAlert.setOverlayClose(true);
        jfxAlert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        jfxAlert.show();
    }

    public boolean isLibsLoaded()
    {
        return libsLoaded;
    }

    public void setLibsLoaded(boolean libsLoaded)
    {
        this.libsLoaded = libsLoaded;
    }

}