package ndproofs.puzzle;

// Provides static methods for file IO.

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileIO {
    
    public static boolean saveStringToFile(String fileString, File file) {
        
        PrintWriter writer;
        try {
            writer = new PrintWriter(file, "UTF-8");
            
            writer.print(fileString);
            writer.close();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    public static String readFile(File file) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
            return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
        }
        catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Changes extension of path to "extension"<br>
     * Note: extension can include or exclude dot.<br>
     * <br>
     * EXAMPLE: C:\Users\Oh\Desktop\Test.txt<br>
     * <br>
     * @param path original path
     * @param extension extension can include or exclude dot. ".txt" and "txt" are ok
     * @return path after changing extension
     */
    public static String setExtension(String path, String extension) {
        
        // Add dot
        if (extension.charAt(0) != '.')
            extension = "." + extension;
        
        // already has correct extension. do nothing.
        if (path.endsWith(extension))
            return path;
        
        int lastDot = path.lastIndexOf('.');
        
        // no dot in path. simple
        if (lastDot == -1)
            return path + extension;
        
        // already has dot in path. check whether that dot is for an extension.
        int lastSlash = path.lastIndexOf('\\');
        int lastSlash2 = path.lastIndexOf('/');
        lastSlash = Math.max(lastSlash, lastSlash2);
        
        if (lastDot > lastSlash) { 
            // If the last dot occurs after the last slash.
            // Also if there is no slash in the path (lastSlash == -1)
            return path.substring(0,lastDot) + extension;
        }
        
        // That dot is probably in a folder name.
        return path + extension;
    }
    
    public static File setFileExtension(File file, String extension) {
        // Add dot
        if (extension.charAt(0) != '.')
            extension = "." + extension;
        
        // check if file path already ends with the extension
        if (file.getPath().endsWith(extension))
            return file;
        
        // If it does not...
        return new File(setExtension(file.getPath(), extension));
    }

    public static boolean hasFileExtension(File file) {
        String path = file.getName();
        
        int lastDot = path.lastIndexOf('.');
        
        // no dot in path. no extension.
        if (lastDot == -1)
            return false;
        return true;
        
        /*// already has dot in path. check whether that dot is for an extension.
        int lastSlash = path.lastIndexOf('\\');
        if (lastDot > lastSlash) { 
            // If the last dot occurs after the last slash.
            // Also if there is no slash in the path (lastSlash == -1)
            return path.substring(0,lastDot) + extension;
        }*/
    }
}
