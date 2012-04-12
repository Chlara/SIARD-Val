/*== SIARD-Val ==================================================================================
The SIARD-Val application is used for validate SIARD-Files. 
Copyright (C) 2012 Claire Röthlisberger (KOST-CECO), Martin Kaiser (KOST-CECO), XYZ (xyz)
-----------------------------------------------------------------------------------------------
SIARD-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.tools.siardval.validation.module.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import ch.kostceco.tools.siardval.service.ConfigurationService;
import ch.kostceco.tools.siardval.exception.module.ValidationAzipException;
import ch.kostceco.tools.siardval.validation.ValidationModuleImpl;
import ch.kostceco.tools.siardval.validation.module.ValidationAzipModule;
import ch.enterag.utils.zip.Zip64File;
import ch.enterag.utils.zip.FileEntry;

/**
 * Validierungsschritt A (Lesbarkeit)
 * Kann die SIARD-Datei gelesen werden? 
 * valid --> lesbare und nicht passwortgeschützte ZIP-Datei oder ZIP64-Datei 
 * valid --> unkomprimierte ZIP64-Datei oder unkomprimierte ZIP-Datei
 * ==> Bei den Module A, B, C und D wird die Validierung abgebrochen, sollte das Resulat invalid sein!
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public class ValidationAzipModuleImpl extends ValidationModuleImpl implements ValidationAzipModule {
	
    private ConfigurationService configurationService;
    
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }


    @Override
    public boolean validate(File siardDatei) throws ValidationAzipException {
    	
        boolean valid = false;


        // Die Datei muss mit PK.. beginnen
        FileReader fr = null;
        try { 
        	fr = new FileReader(siardDatei);
            BufferedReader read = new BufferedReader(fr);
        	
        	// Hex 03 in Char umwandeln
        	String str3= "03";
        	int i3= Integer.parseInt(str3,16);
        	char c3 = (char)i3;
        	// Hex 04 in Char umwandeln
        	String str4= "04";
        	int i4= Integer.parseInt(str4,16);
        	char c4 = (char)i4;

        	// auslesen der ersten 4 Zeichen der Datei
        	int length;
        	int i;
        	char[] buffer = new char[4];
        	length=read.read(buffer);
        	for(i=0;i!=length;i++);
        	
        	// die beiden charArrays (soll und ist) mit einander vergleichen
        	char[] charArray1 = buffer;
        	char[] charArray2 = new char[]{'P','K',c3,c4};

            if (Arrays.equals(charArray1,charArray2)) {
            	// höchstwahrscheinlich ein ZIP da es mit 504B0304 respektive PK.. beginnt
                valid = true;
            } else{
                getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_A) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(ERROR_MODULE_A_INCORRECTFILEENDING));    
                //Die SIARD-Datei ist kein ZIP.            
                return false;
            }
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_A) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            return false;
        }
       
        // die Datei darf kein Directory sein
        if (siardDatei.isDirectory()) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_A) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(ERROR_MODULE_A_INCORRECTFILEENDING));    
            // Die SIARD-Datei ist kein ZIP.            
            return false;
        }
        
        // Das ZIP-Archiv darf nicht komprimiert sein
        Zip64File zf = null;
        try { 
        	Integer compressed = 0;
            // Versuche das ZIP file zu öffnen
            zf = new Zip64File(siardDatei);
            // auslesen der Komprimierungsmethode aus allen FileEntries der zip(64)-Datei
			List<FileEntry> fileEntryList = zf.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
              compressed = fileEntry.getMethod() + compressed;
              // Compression method for uncompressed entries = STORED = 0 
            }
            // und wenn es klappt, gleich wieder schliessen
            zf.close();
            if (compressed == 0) {
            	// erlaubtes unkomprimiertes ZIP
                valid = true;
            } else{
                getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_A) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(ERROR_MODULE_A_DEFLATED));    
                // Die SIARD-Datei ist komprimiert.            
                return false;
            }
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_A) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            return false;
        }

        return (valid);
    }
}
