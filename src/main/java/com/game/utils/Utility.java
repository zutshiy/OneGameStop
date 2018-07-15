package com.game.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXTextField;

public class Utility
{

    public static final String GAME_LIBRARY = "Game Library";
    public static final String EMPTY_STRING = "";
    public static final String BROWSE = "Browse";
    public static final String REMOVE = "Remove";

    public static JFXTextField createTextField(String promptText, String fieldText, boolean editable)
    {
        JFXTextField pathTextField = new JFXTextField();

        pathTextField.setPrefWidth(350);
        pathTextField.setPromptText(promptText);
        pathTextField.setEditable(editable);

        if (fieldText != null)
        {
            pathTextField.setText(fieldText);
        }

        return pathTextField;
    }

    public static boolean isEmptyString(String val)
    {
        return val == null || val.isEmpty();
    }

    public static boolean writeStringToFile(String data, String fileName)
    {
        FileOutputStream fileOutputStream = null;
        File file;
        try
        {
            file = new File(fileName);

            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.trim().getBytes());

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                fileOutputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static URL getResourceFile(Object obj, String resource)
    {
        return obj.getClass().getClassLoader().getResource(resource);
    }
    
    public static InputStream getResourceAsStream(Object obj, String resource)
    {
        try
        {
            return obj.getClass().getClassLoader().getResource(resource).openStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFromFile(File file)
    {
        String content = null;
        try
        {
            if (Files.isRegularFile(Paths.get(file.toURI())))
            {
                content = new String(Files.readAllBytes(Paths.get(file.toURI())));
                return content.trim();
            }
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> splitLinesFromString(String value)
    {
        if (value.length() == 0 || (value.length() == 1 && Utility.isEmptyString(value)))
            return new ArrayList<String>();

        String[] savedPathsList = value.split("\n");

        return Arrays.asList(savedPathsList);
    }

}
