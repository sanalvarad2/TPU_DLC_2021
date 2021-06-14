/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DLC.TPI.Utils;

/**
 *
 * @author Santiago
 */
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.nio.file.Files;


/**
 *
 * @author Santiago
 */
public class FileToBase64 {

    public static String encodeFileToBase64(File file) {
        String retVal = "";
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            retVal = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("No se puede leer el archivo: " + file, e);
        }
        catch(OutOfMemoryError e){
            retVal = Base64.getEncoder().encodeToString(("No se pudo almacenar el archivo").getBytes());
        }
        return retVal;
    }

}

