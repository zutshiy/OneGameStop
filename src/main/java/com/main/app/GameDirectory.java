package com.main.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.game.utils.Utility;
import com.jfoenix.controls.JFXButton;

import javafx.scene.effect.ColorAdjust;

public class GameDirectory
{
    private static final String GAME_DIRECTORY_PATHS = "GameDirectoryPaths.txt";
    private static final String ICON_COLOR_CODES = "IconColorCodes.txt";
    private String dirName;
    private String dirPath;

    private JFXButton gameIcon;
    private String executableFile;

    public GameDirectory(String name, String path)
    {
        dirName = name;
        dirPath = path;
    }

    public boolean build()
    {
        gameIcon = new JFXButton(dirName);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.5);
        colorAdjust.setHue(0.5);
        colorAdjust.setBrightness(0.5);
        colorAdjust.setSaturation(0.5);
        gameIcon.setEffect(colorAdjust);

        gameIcon.setStyle("-fx-background-color : " + getRandomIconColor());
        gameIcon.setId("button-icon");
        gameIcon.setWrapText(true);
        gameIcon.setEffect(colorAdjust);
        if (bindGameToExecutable())
        {
            gameIcon.setOnAction(e -> executeGameFile());
            return true;
        }

        return false;
    }

    private void executeGameFile()
    {
        InputStream is = null;
        try
        {
            File file = new File(executableFile);
            File parentDir = file.getParentFile();

            File logFile = new File("Error.log");

            ProcessBuilder processBuilder = new ProcessBuilder(executableFile);
            processBuilder.directory(parentDir);
            processBuilder.redirectError(logFile);

            Process process = processBuilder.start();
            is = process.getInputStream();

            int exitVal = process.waitFor();

            if (exitVal != 0)
            {
                String log = "Error executing file : " + executableFile;
                Files.write(logFile.toPath(), log.getBytes());
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean bindGameToExecutable()
    {

        try
        {
            if (findExecutableFile(dirPath))
            {
                return true;
            }

            InputStream inputStream = Utility.getResourceAsStream(this, GAME_DIRECTORY_PATHS);
            List<String> possiblePaths = Utility.splitLinesFromString(IOUtils.toString(inputStream));

            for (String possiblePath : possiblePaths)
            {
                String path = dirPath + possiblePath.trim();
                if (findExecutableFile(path))
                {
                    return true;
                }
            }

            if (findExecutableFileRecursively(dirPath))
            {
                return true;
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private boolean findExecutableFile(String path)
    {
        if (!Files.isDirectory(Paths.get(path)))
        {
            return false;
        }

        File file = new File(path);
        File[] gameFiles = file.listFiles();
        Map<String, String> eligibleGameExecFiles = new HashMap<String, String>();

        for (File gameFile : gameFiles)
        {
            if (gameFile.canExecute())
            {
                String absolutePath = gameFile.getAbsolutePath();
                if (isGameExecutableFile(absolutePath))
                {
                    eligibleGameExecFiles.put(gameFile.getName(), absolutePath);
                }
            }
        }

        for (Entry<String, String> entry : eligibleGameExecFiles.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();

            if (Utility.isEmptyString(executableFile))
            {
                executableFile = value;
                continue;
            }

            if (key.toLowerCase().contains("launcher"))
            {
                executableFile = value;
                break;
            }

            if (key.toLowerCase().equalsIgnoreCase(this.getDirName()))
            {
                executableFile = value;
                break;
            }
        }

        if (!Utility.isEmptyString(executableFile))
        {
            return true;
        }

        return false;
    }

    private boolean findExecutableFileRecursively(String path)
    {
        File file = new File(path);
        File[] gameFiles = file.listFiles();

        if (gameFiles == null || gameFiles.length == 0)
        {
            return false;
        }
        for (File gameFile : gameFiles)
        {
            String absolutePath = gameFile.getAbsolutePath();
            if (findExecutableFile(absolutePath))
            {
                return true;
            }
            else
            {
                return findExecutableFileRecursively(absolutePath);
            }
        }
        return false;
    }

    private boolean isGameExecutableFile(String absolutePath)
    {
        if (!absolutePath.substring(absolutePath.length() - 3, absolutePath.length()).equals("exe"))
        {
            return false;
        }

        String[] gameNameArray = dirName.split(" ");

        for (String word : gameNameArray)
        {
            if (absolutePath.substring(absolutePath.lastIndexOf('\\'), absolutePath.length()).toLowerCase()
                    .contains(word.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    private String getRandomIconColor()
    {
        try
        {
            InputStream inputStream = Utility.getResourceAsStream(this, ICON_COLOR_CODES);
            List<String> colorCodes = Utility.splitLinesFromString(IOUtils.toString(inputStream));
            Random rand = new Random();
            int r = rand.nextInt(colorCodes.size());

            return colorCodes.get(r);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String getDirName()
    {
        return dirName;
    }

    public void setDirName(String dirName)
    {
        this.dirName = dirName;
    }

    public String getDirPath()
    {
        return dirPath;
    }

    public void setDirPath(String dirPath)
    {
        this.dirPath = dirPath;
    }

    public JFXButton getGameIcon()
    {
        return gameIcon;
    }

    public void setGameIcon(JFXButton gameIcon)
    {
        this.gameIcon = gameIcon;
    }

    public String getExecutableName()
    {
        return executableFile;
    }

    public void setExecutableName(String executableName)
    {
        this.executableFile = executableName;
    }
}
