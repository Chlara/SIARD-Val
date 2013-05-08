/*== SIARD-Val ===================================================================================
The SIARD-Val application is used for validate SIARD-Files. 
Copyright (C) 2012-2013 Claire R�thlisberger (KOST-CECO), Christian Eugster, Olivier Debenath, 
Peter Schneider (Staatsarchiv Aargau)
--------------------------------------------------------------------------------------------------
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
==================================================================================================*/

package ch.kostceco.tools.siardval.validation.module.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import ch.kostceco.tools.siardval.service.ConfigurationService;
import ch.kostceco.tools.siardval.exception.module.ValidationCheaderException;
import ch.kostceco.tools.siardval.validation.ValidationModuleImpl;
import ch.kostceco.tools.siardval.validation.module.ValidationCheaderModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * Validierungsschritt C (Header-Validierung) Ist der header-Ordner valid? valid
 * --> metadata.xml valid zu metadata.xsd und beides vorhanden Bemerkung -->
 * zus�tzliche Ordner oder Dateien wie z.B. metadata.xls sind im header-Ordner
 * erlaubt ==> Bei den Module A, B, C und D wird die Validierung abgebrochen,
 * sollte das Resulat invalid sein!
 * 
 * @author Rc Claire R�thlisberger, KOST-CECO
 */

public class ValidationCheaderModuleImpl extends ValidationModuleImpl implements
		ValidationCheaderModule
{

	public ConfigurationService	configurationService;

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	@Override
	public boolean validate( File siardDatei )
			throws ValidationCheaderException
	{

		// Sind im Header-Ordner metadata.xml und metadata.xsd vorhanden?
		FileEntry metadataxml = null;
		FileEntry metadataxsd = null;

		try {
			Zip64File zipfile = new Zip64File( siardDatei );
			List<FileEntry> fileEntryList = zipfile.getListFileEntries();
			for ( FileEntry fileEntry : fileEntryList ) {
				if ( fileEntry.getName().equals( "header/" + METADATA ) ) {
					metadataxml = fileEntry;
				}
				if ( fileEntry.getName().equals( "header/" + XSD_METADATA ) ) {
					metadataxsd = fileEntry;
				}
			}
			if ( metadataxml == null ) {
				// keine metadata.xml = METADATA in der SIARD-Datei gefunden
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_C )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ getTextResourceService().getText(
										MESSAGE_MODULE_C_NOMETADATAFOUND ) );
				return false;
			}
			if ( metadataxsd == null ) {
				// keine metadata.xsd = XSD_METADATA in der SIARD-Datei gefunden
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_C )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ getTextResourceService().getText(
										MESSAGE_MODULE_C_NOMETADATAXSD ) );
				return false;
			}
		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_C )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ e.getMessage() + " xml und xsd" );
			return false;
		}

		// Validierung metadata.xml mit metadata.xsd
		File xmlToValidate = null;
		File xsdToValidate = null;
		String toplevelDir = siardDatei.getName();
		int lastDotIdx = toplevelDir.lastIndexOf( "." );
		toplevelDir = toplevelDir.substring( 0, lastDotIdx );

		try {
			// Arbeitsverzeichnis zum Entpacken des Archivs erstellen
			String pathToWorkDir = getConfigurationService().getPathToWorkDir();
			/*
			 * Nicht vergessen in
			 * "src/main/resources/config/applicationContext-services.xml" beim
			 * entsprechenden Modul die property anzugeben: <property
			 * name="configurationService" ref="configurationService" />
			 */
			File tmpDir = new File( pathToWorkDir );
			if ( !tmpDir.exists() ) {
				tmpDir.mkdir();
			}

			// Das metadata.xml und sein xsd m�ssen in das Filesystem extrahiert
			// werden, weil bei
			// bei Verwendung eines Inputstreams bei der Validierung ein Problem
			// mit
			// den xs:include Statements besteht, die includes k�nnen so nicht
			// aufgel�st werden.
			// Es werden hier jedoch nicht nur diese Files extrahiert, sondern
			// gleich die ganze Zip-Datei,
			// weil auch sp�tere Validierungen nur mit den extrahierten Files
			// arbeiten k�nnen.
			Zip64File zipfile = new Zip64File( siardDatei );
			List<FileEntry> fileEntryList = zipfile.getListFileEntries();
			for ( FileEntry fileEntry : fileEntryList ) {
				if ( !fileEntry.isDirectory() ) {
					byte[] buffer = new byte[8192];
					// Scheibe die Datei an den richtigen Ort respektive in den
					// richtigen Ordner der ggf angelegt werden muss.
					EntryInputStream eis = zipfile
							.openEntryInputStream( fileEntry.getName() );
					File newFile = new File( tmpDir, fileEntry.getName() );
					File parent = newFile.getParentFile();
					if ( !parent.exists() ) {
						parent.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream( newFile );
					for ( int iRead = eis.read( buffer ); iRead >= 0; iRead = eis
							.read( buffer ) ) {
						fos.write( buffer, 0, iRead );
					}
					eis.close();
					fos.close();
					// Festhalten von metadata.xml und metadata.xsd
					if ( newFile.getName().endsWith( METADATA ) ) {
						xmlToValidate = newFile;
					}
					if ( newFile.getName().endsWith( XSD_METADATA ) ) {
						xsdToValidate = newFile;
					}

				} else {
					// Scheibe den Ordner wenn noch nicht vorhanden an den
					// richtigen Ort respektive in den
					// richtigen Ordner der ggf angelegt werden muss.
					// Dies muss gemacht werden, damit auch leere Ordner ins
					// Work geschrieben werden. Diese werden danach im J als
					// Fehler angegeben
					EntryInputStream eis = zipfile
							.openEntryInputStream( fileEntry.getName() );
					File newFolder = new File( tmpDir, fileEntry.getName() );
					if ( !newFolder.exists() ) {
						 File parent = newFolder.getParentFile(); if (
						 !parent.exists() ) { parent.mkdirs(); }
						 newFolder.mkdirs();
					}
					eis.close();
				}

			}
			if ( xmlToValidate != null && xsdToValidate != null ) {
				// der andere Fall wurde bereits oben abgefangen
				try {
					// Validierung von metadata.xml und metadata.xsd mit dem
					// (private class) Validator
					System.setProperty(
							"javax.xml.parsers.DocumentBuilderFactory",
							"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" );
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					factory.setNamespaceAware( true );
					factory.setValidating( true );
					factory.setAttribute(
							"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
							"http://www.w3.org/2001/XMLSchema" );
					factory.setAttribute(
							"http://java.sun.com/xml/jaxp/properties/schemaSource",
							xsdToValidate.getAbsolutePath() );
					DocumentBuilder builder = factory.newDocumentBuilder();
					Validator handler = new Validator();
					builder.setErrorHandler( handler );
					builder.parse( xmlToValidate.getAbsolutePath() );
					if ( handler.validationError == true ) {
						return false;
					}
				} catch ( java.io.IOException ioe ) {
					getMessageService().logError(
							getTextResourceService().getText( MESSAGE_MODULE_C )
									+ getTextResourceService().getText(
											MESSAGE_DASHES ) + "IOException "
									+ ioe.getMessage() );
				} catch ( SAXException e ) {
					getMessageService().logError(
							getTextResourceService().getText( MESSAGE_MODULE_C )
									+ getTextResourceService().getText(
											MESSAGE_DASHES ) + "SAXException "
									+ e.getMessage() );
				} catch ( ParserConfigurationException e ) {
					getMessageService().logError(
							getTextResourceService().getText( MESSAGE_MODULE_C )
									+ getTextResourceService().getText(
											MESSAGE_DASHES )
									+ "ParserConfigurationException "
									+ e.getMessage() );
				}
			}
			zipfile.close();
		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_C )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ e.getMessage() );
			return false;
		}
		return true;
	}

	private class Validator extends DefaultHandler
	{
		public boolean				validationError		= false;
		public SAXParseException	saxParseException	= null;

		public void error( SAXParseException exception ) throws SAXException
		{
			validationError = true;
			saxParseException = exception;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_C )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ getTextResourceService().getText(
									MESSAGE_MODULE_C_METADATA_ERRORS,
									saxParseException.getLineNumber(),
									saxParseException.getColumnNumber(),
									saxParseException.getMessage() ) );
		}

		public void fatalError( SAXParseException exception )
				throws SAXException
		{
			validationError = true;
			saxParseException = exception;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_C )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ getTextResourceService().getText(
									MESSAGE_MODULE_C_METADATA_ERRORS,
									saxParseException.getLineNumber(),
									saxParseException.getColumnNumber(),
									saxParseException.getMessage() ) );
		}

		public void warning( SAXParseException exception ) throws SAXException
		{
			// Warnungen werden nicht ausgegeben
		}
	}
}
