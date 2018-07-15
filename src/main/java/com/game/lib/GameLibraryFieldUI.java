package com.game.lib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.game.utils.Utility;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;

public class GameLibraryFieldUI
{
    private static final String GAMEPAD_ICON = "3-gamepad.svg";
    private static final String DEFAULT_PROMPT_TEXT = "Enter game library path here...";
    private static final String NOT_FOUND = "Not Found!";
    private static final String FOUND = "Found!";

    private GridPane gridPane;

    private String name;
    private String foundInd;
    private String path;
    private String promptText;
    private boolean isEditable = true;

    private Label nameLabel;
    private JFXTextField pathField;
    private Label foundIndLabel;
    private JFXButton removeButton;
    private JFXButton browseButton;
    private SVGGlyph gamepadIcon;

    private List<Node> allNodes;

    public Button getBrowseButton()
    {
        return browseButton;
    }

    public void setBrowseButton(JFXButton browseButton)
    {
        this.browseButton = browseButton;
    }

    public GameLibraryFieldUI()
    {
        promptText = DEFAULT_PROMPT_TEXT;
        this.nameLabel = new Label();
        this.pathField = new JFXTextField();
        this.foundIndLabel = new Label();
        this.browseButton = new JFXButton(Utility.BROWSE);
        this.removeButton = new JFXButton(Utility.REMOVE);
        this.setGridPane(new GridPane());
        this.allNodes = new ArrayList<Node>();
    }

    public GameLibraryFieldUI(String fieldName)
    {
        promptText = DEFAULT_PROMPT_TEXT;
        this.name = fieldName;
        this.nameLabel = new Label(fieldName);
        this.pathField = new JFXTextField();
        this.foundIndLabel = new Label(Utility.EMPTY_STRING);
        this.removeButton = new JFXButton(Utility.REMOVE);
        this.browseButton = new JFXButton(Utility.BROWSE);
    }

    public void buildUIObject()
    {
        try
        {
            gamepadIcon = SVGGlyphLoader.loadGlyph(Utility.getResourceFile(this, GAMEPAD_ICON));
            gamepadIcon.setSize(30);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        nameLabel.setPrefWidth(80);

        foundIndLabel.setText(foundInd);
        foundIndLabel.setPrefWidth(80);
        foundIndLabel.setAlignment(Pos.BOTTOM_CENTER);

        browseButton.getStyleClass().add("button-flat");
        browseButton.setMinHeight(32);
        browseButton.setMinWidth(50);

        removeButton.getStyleClass().add("button-flat");
        removeButton.setMinHeight(32);
        removeButton.setMinWidth(50);

        nameLabel.getStyleClass().add("label-name");

        this.pathField = Utility.createTextField(this.promptText, this.path, this.isEditable);

        gridPane.setHgap(20);

        initAllNodes();
    }

    public void initAllNodes()
    {
        GridPane.setConstraints(gamepadIcon, 1, 0);
        GridPane.setConstraints(pathField, 2, 0);
        GridPane.setConstraints(foundIndLabel, 3, 0);
        GridPane.setConstraints(browseButton, 4, 0);
        GridPane.setConstraints(removeButton, 5, 0);

        getGridPane().getChildren().addAll(gamepadIcon, pathField, foundIndLabel, browseButton, removeButton);

        allNodes.add(getGridPane());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String fieldName)
    {
        this.name = fieldName;
        nameLabel.setText(name);
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
        this.pathField.setText(path);
    }

    public String getFoundInd()
    {
        return foundInd;
    }

    public void setFoundInd(String foundIndicator)
    {
        this.foundInd = foundIndicator;
        this.foundIndLabel.setText(foundIndicator);
    }

    public void updateFoundInd(boolean found)
    {
        if (found)
        {
            this.foundInd = FOUND;
            this.foundIndLabel.setText(FOUND);
            this.foundIndLabel.setTextFill(Paint.valueOf("#009F1B"));
        }
        else
        {
            this.foundInd = NOT_FOUND;
            this.foundIndLabel.setText(NOT_FOUND);
            this.foundIndLabel.setTextFill(Paint.valueOf("#D80000"));
        }
    }

    public Label getNameLabel()
    {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel)
    {
        this.nameLabel = nameLabel;
    }

    public JFXTextField getPathField()
    {
        return pathField;
    }

    public void setPathField(JFXTextField pathField)
    {
        this.pathField = pathField;
    }

    public Label getFoundIndLabel()
    {
        return foundIndLabel;
    }

    public void setFoundIndLabel(Label foundIndLabel)
    {
        this.foundIndLabel = foundIndLabel;
    }

    public String getPromptText()
    {
        return promptText;
    }

    public void setPromptText(String promptText)
    {
        this.promptText = promptText;
        this.setPromptText(promptText);
    }

    public boolean isEditable()
    {
        return isEditable;
    }

    public void setEditable(boolean isEditable)
    {
        this.isEditable = isEditable;
    }

    public Button getRemoveButton()
    {
        return removeButton;
    }

    public void setRemoveButton(JFXButton removeButton)
    {
        this.removeButton = removeButton;
    }

    public List<Node> getAllNodes()
    {
        return allNodes;
    }

    public void setAllNodes(List<Node> allNodes)
    {
        this.allNodes = allNodes;
    }

    public SVGGlyph getGamepadIcon()
    {
        return gamepadIcon;
    }

    public void setGamepadIcon(SVGGlyph gamepadIcon)
    {
        this.gamepadIcon = gamepadIcon;
    }

    public GridPane getGridPane()
    {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane)
    {
        this.gridPane = gridPane;
    }

}
