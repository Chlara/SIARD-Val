/*== SIARD-Val ==================================================================================
The SIARD-Val application is used for validate SIARD-Files. 
Copyright (C) 2012 Claire R�thlisberger (KOST-CECO), Martin Kaiser (KOST-CECO), XYZ (xyz)
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

import java.io.File;
import java.util.List;

import ch.kostceco.tools.siardval.exception.module.ValidationBprimaryStructureException;
import ch.kostceco.tools.siardval.validation.ValidationModuleImpl;
import ch.kostceco.tools.siardval.validation.module.ValidationBprimaryStructureModule;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * Validierungsschritt B (prim�re Verzeichnisstruktur)
 * Besteht eine korrekte prim�re Verzeichnisstruktur? 
 * valid --> [Name].siard/header und [Name].siard/content
 * invalid --> [Name].siard/[Name]/header und [Name].siard/[Name]/content
 * invalid --> Andere Ordner oder Dateien im Toplevel-Ordner
 * ==> Bei den Module A, B, C und D wird die Validierung abgebrochen, sollte das Resulat invalid sein!
 * @author Rc Claire R�thlisberger, KOST-CECO
 */

public class ValidationBprimaryStructureModuleImpl extends ValidationModuleImpl implements
        ValidationBprimaryStructureModule {

    @Override
    public boolean validate(File siardDatei) throws ValidationBprimaryStructureException {
    	
        Integer bExistsHeaderFolder = 0;
        Integer bExistsContentFolder = 0;
        
        String toplevelDir = siardDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);
        
        try {
           
            Zip64File zipfile = new Zip64File(siardDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
            	
            	// nur valid wenn es mit header oder content anf�ngt
                // dies schliesst auch [Name].siard/[Name]/header und [Name].siard/[Name]/content mit ein
                String name = fileEntry.getName();
                if (name.startsWith("content/")) {
                	// erlaubter Inhalt content/...
                	bExistsContentFolder = 1;
                } else {
                	if (name.startsWith("header/")) {
                		// erlaubter Inhalt header/...
                		bExistsHeaderFolder = 1;
                	} else {
                    	// keines der beiden validen M�glichkeiten -> Fehler
                    	getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_B) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_B_NOTALLOWEDFILE, name));
                        // SIARD enthaelt ein File, das sich nicht dort befinden duerfte: {0}
                        return false;
                	}
                }
            }
            zipfile.close();
            if (bExistsContentFolder == 0){
            	getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_B) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(MESSAGE_MODULE_B_CONTENT));
                // SIARD enthaelt kein content-Ordner
                return false;
            }
            if (bExistsHeaderFolder == 0){
            	getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_B) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(MESSAGE_MODULE_B_HEADER));
                // SIARD enthaelt kein header-Ordner
                return false;
            }
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_B) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            return false;
        }
        return true;
    }
}
