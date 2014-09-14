package ndproofs.swingwindow;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import ndproofs.puzzle.FileIO;

/**
 * Stores Config data for the program.
 * Also saves config data to an external file CONFIG_FILE
 * @author Oh
 */
public class NDConfig {
    private static final File CONFIG_FILE = new File("settings.cfg");
    
    private static final int NUM_CONFIG_DATA = 3;
    private static final boolean[] configData = new boolean[NUM_CONFIG_DATA];
    
    private static final int SPLASH_SCREEN = 0;
    private static final int INSTRUCTIONS = 1;
    private static final int LATEX_MODE = 2;
    
    private static final HashMap<String, Integer> nameMap = new HashMap<>(NUM_CONFIG_DATA*2);
    public static final String[] dataNames = new String[NUM_CONFIG_DATA];
    
    public static final void initialise() {
        dataNames[SPLASH_SCREEN] = "splashscreen";
        configData[SPLASH_SCREEN] = true;
        dataNames[INSTRUCTIONS] = "instructions";
        configData[INSTRUCTIONS] = true;
        dataNames[LATEX_MODE] = "latexmode";
        configData[LATEX_MODE] = true;
        
        for (int i=0; i<NUM_CONFIG_DATA; i++) {
            nameMap.put(dataNames[i], i);
        }
        
        loadConfig();
    }
    
    public static final void loadConfig() {
        String fileString = FileIO.readFile(CONFIG_FILE);
        if (fileString == null) {
            System.out.println("settings.cfg not detected");
            return;
        }
        
        Scanner sc = new Scanner(fileString);
        while (sc.hasNextLine())
            readConfigLine(sc.nextLine());
    }
    
    public static void saveConfig() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<NUM_CONFIG_DATA; i++) {
            addConfigLine(sb, dataNames[i], configData[i]);
            sb.append(System.lineSeparator());
        }
        FileIO.saveStringToFile(sb.toString(), CONFIG_FILE);
    }
    
    private static void addConfigLine(StringBuilder sb, String name, boolean value) {
        sb.append(name);
        sb.append(" = ");
        sb.append(value ? 1 : 0);
    }
    
    private static void readConfigLine(String line) {
        String[] argStrings = line.split("=");
        Integer dataIndex = nameMap.get(argStrings[0].trim());
        
        if (dataIndex != null) {
            String value = argStrings[1].trim();
            if (value.equals("1")) {
                configData[dataIndex] = true;
            }
            else if (value.equals("0")) {
                configData[dataIndex] = false;
            }
        }
    }
    
    private static void set(int index, boolean on) {;
        if (configData[index] != on) {
            configData[index] = on;
            saveConfig();
        }
    }
    
    public static boolean splashScreenOn() {return configData[SPLASH_SCREEN];}
    public static boolean instructionsOn() {return configData[INSTRUCTIONS];}
    public static boolean latexModeOn() {return configData[LATEX_MODE];}
    
    public static void setSplashScreenOn(boolean on) {
        set(SPLASH_SCREEN, on);
    }
    
    public static void setInstructionsOn(boolean on) {
        set(INSTRUCTIONS, on);
    }
    
    public static void setLatexModeOn(boolean on) {
        set(LATEX_MODE, on);
    }
    
}
