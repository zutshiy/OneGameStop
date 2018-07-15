package com.main.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.lib.LoadGameLibraries;
import com.game.utils.Utility;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LaunchWindow extends Application
{
    private static final String GAME_LIBRARIES_TXT = "GameLibraries.txt";
    private static final String LOAD_GAME_LIBRARIES = "ADD LIBRARIES";
    private List<GameDirectory> gameDirList = new ArrayList<GameDirectory>();
    private Map<String, String> gameDirMap = new HashMap<String, String>();

    private BorderPane layout;
    private Stage window;

    @Override
    public void start(Stage window)
    {
        this.window = window;
        layout = new BorderPane();

        loadFilePaths();
        makeContainerPane();

        JFXButton loadLibrariesButton = new JFXButton(LOAD_GAME_LIBRARIES);
        loadLibrariesButton.requestFocus();
        loadLibrariesButton.setOnAction(e -> launchGameLibWindow());
        loadLibrariesButton.setId("load-button");
        HBox bottomBox = new HBox();
        bottomBox.getChildren().add(loadLibrariesButton);
        bottomBox.setAlignment(Pos.BASELINE_CENTER);
        bottomBox.setPadding(new Insets(0, 0, 0, 0));
        bottomBox.setId("bottom-box");
        HBox.setHgrow(loadLibrariesButton, Priority.ALWAYS);

        layout.setBottom(bottomBox);

        Scene scene = new Scene(layout, 1200, 600);
        scene.getStylesheets().add(Utility.getResourceFile(this, "application.css").toExternalForm());

        window.setScene(scene);
        window.setMaximized(true);
        window.setTitle("Game Library");
        window.show();

        loadLibrariesButton.setPrefWidth(window.getWidth());
    }

    private void makeGameIcons(JFXMasonryPane iconContainerPane)
    {
        if (gameDirList.size() == 0)
        {
            return;
        }

        sortGameLists();
        for (GameDirectory gameDirectory : gameDirList)
        {
            iconContainerPane.getChildren().add(gameDirectory.getGameIcon());
        }
        return;
    }

    private void sortGameLists()
    {
        gameDirList.sort(new Comparator<GameDirectory>()
        {
            @Override
            public int compare(GameDirectory o1, GameDirectory o2)
            {
                return o1.getDirName().compareTo(o2.getDirName());
            }
        });
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    public void launchGameLibWindow()
    {
        this.layout.setEffect(new GaussianBlur());
        LoadGameLibraries loadGameLib = new LoadGameLibraries();
        loadGameLib.loadScene();
        this.layout.setEffect(null);

        if (loadGameLib.isLibsLoaded())
        {
            loadFilePaths();
            makeContainerPane();
            window.hide();
            window.show();
        }
    }

    public void makeContainerPane()
    {
        JFXMasonryPane iconContainerPane = new JFXMasonryPane();
        makeGameIcons(iconContainerPane);
        iconContainerPane.setHSpacing(10);
        iconContainerPane.setVSpacing(20);
        iconContainerPane.setPadding(new Insets(80, 20, 20, 120));

        JFXScrollPane scrollPane = new JFXScrollPane();
        scrollPane.setContent(iconContainerPane);

        Text headerTextOne = new Text("One");
        Text headerTextGame = new Text("Game");
        Text headerTextStop = new Text("Stop");

        headerTextOne.setId("text-header-one");
        headerTextGame.setId("text-header-game");
        headerTextStop.setId("text-header-stop");

        HBox hbox = new HBox(headerTextOne, headerTextGame, headerTextStop);
        hbox.setAlignment(Pos.CENTER);

        headerTextOne.getStyleClass().add("text-header");
        headerTextGame.getStyleClass().add("text-header");
        headerTextStop.getStyleClass().add("text-header");

        scrollPane.getMainHeader().getChildren().add(hbox);

        JFXScrollPane.smoothScrolling((ScrollPane) scrollPane.getChildren().get(0));
        layout.setCenter(scrollPane);
    }

    public void loadFilePaths()
    {
        gameDirList.clear();
        gameDirMap.clear();

        File file = new File(GAME_LIBRARIES_TXT);
        String savedGameLibsString = (Utility.readFromFile(file));

        if (!Utility.isEmptyString(savedGameLibsString))
        {
            List<String> savedGameLibs = Utility.splitLinesFromString(savedGameLibsString);

            for (String path : savedGameLibs)
            {
                File dir = new File(path);
                String[] files = dir.list();

                for (String directoryName : files)
                {
                    if (gameDirMap.get(directoryName) != null)
                    {
                        continue;
                    }

                    String gameDirPath = path + "\\" + directoryName;

                    if (Files.isDirectory(Paths.get(gameDirPath)))
                    {
                        GameDirectory gameDirectory = new GameDirectory(directoryName, gameDirPath);
                        if (gameDirectory.build())
                        {
                            gameDirList.add(gameDirectory);
                            gameDirMap.put(gameDirectory.getDirName(), gameDirPath);
                        }
                    }
                }
            }
        }
    }
}
