/*== SIARD-Val ===================================================================================
The SIARD-Val v1.0.0 application is used for validate SIARD-Files. 
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

package ch.kostceco.tools.siardval;

import java.io.File;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.kostceco.tools.siardval.controller.Controller;
import ch.kostceco.tools.siardval.logging.LogConfigurator;
import ch.kostceco.tools.siardval.logging.Logger;
import ch.kostceco.tools.siardval.logging.MessageConstants;
import ch.kostceco.tools.siardval.service.ConfigurationService;
import ch.kostceco.tools.siardval.service.TextResourceService;
import ch.kostceco.tools.siardval.util.Util;

/**
 * Dies ist die Starter-Klasse, verantwortlich f�r das Initialisieren des
 * Controllers, des Loggings und das Parsen der Start-Parameter.
 */
public class SIARDVal implements MessageConstants
{

	private static final Logger		LOGGER	= new Logger( SIARDVal.class );

	private TextResourceService		textResourceService;
	private ConfigurationService	configurationService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	/**
	 * Die Minimaleingabe besteht aus Parameter 1: Pfad zum SIARD-File Parameter
	 * 2: Pfad zum Logging-Verzeichnis
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/applicationContext.xml" );

		SIARDVal SIARDVal = (SIARDVal) context.getBean( "SIARDVal" );

		// Ist die Anzahl Parameter (2) korrekt?
		if ( args.length < 2 ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			System.exit( 1 );
		}

		File siardDatei = new File( args[0] );

		LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
				MESSAGE_SIARDVALIDATION, siardDatei.getName() ) );

		// die Anwendung muss mindestens unter Java 6 laufen
		String javaRuntimeVersion = System.getProperty( "java.vm.version" );
		if ( javaRuntimeVersion.compareTo( "1.6.0" ) < 0 ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_WRONG_JRE ) );
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 2. Parameters (Log-Verzeichnis)
		File directoryOfLogfile = new File( args[1] );
		if ( !directoryOfLogfile.exists() ) {
			directoryOfLogfile.mkdir();
		}

		// Im Logverzeichnis besteht kein Schreibrecht
		if ( !directoryOfLogfile.canWrite() ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NOTWRITABLE, directoryOfLogfile ) );
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		if ( !directoryOfLogfile.isDirectory() ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NODIRECTORY ) );
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Informationen zum Arbeitsverzeichnis holen
		String pathToWorkDir = SIARDVal.getConfigurationService()
				.getPathToWorkDir();
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

		// Im workverzeichnis besteht kein Schreibrecht
		if ( !tmpDir.canWrite() ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_WORKDIRECTORY_NOTWRITABLE, tmpDir ) );
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 1. Parameters (SIARD-Datei): existiert die Datei?
		if ( !siardDatei.exists() ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_SIARDFILE_FILENOTEXISTING ) );
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		String originalSiardName = siardDatei.getAbsolutePath();

		// Ueberpr�fung der anzahl Parameter (3. und mehr)
		if ( args.length > 2 ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			System.exit( 1 );
		}

		// Konfiguration des Loggings, ein File Logger wird zus�tzlich erstellt
		LogConfigurator logConfigurator = (LogConfigurator) context
				.getBean( "logconfigurator" );
		String logFileName = logConfigurator.configure(
				directoryOfLogfile.getAbsolutePath(), siardDatei.getName() );
		LOGGER.logError( SIARDVal.getTextResourceService().getText(
				MESSAGE_SIARDVALIDATION, siardDatei.getName() ) );

		Controller controller = (Controller) context.getBean( "controller" );
		boolean okMandatory = controller.executeMandatory( siardDatei );
		boolean ok = false;

		// die Validierungen A-D sind obligatorisch, wenn sie bestanden wurden,
		// k�nnen die restlichen
		// Validierungen, welche nicht zum Abbruch der Applikation f�hren,
		// ausgef�hrt werden.
		if ( okMandatory ) {

			ok = controller.executeOptional( siardDatei );

			// Ausf�hren der optionalen Schritte

		}

		ok = (ok && okMandatory);

		LOGGER.logInfo( "" );
		if ( ok ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_TOTAL_VALID, siardDatei.getAbsolutePath() ) );
		} else {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_TOTAL_INVALID, siardDatei.getAbsolutePath() ) );
		}
		LOGGER.logInfo( "" );

		LOGGER.logInfo( "" );
		LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
				MESSAGE_FOOTER_SIARD, originalSiardName ) );
		LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
				MESSAGE_FOOTER_LOG, logFileName ) );
		LOGGER.logInfo( "" );

		if ( okMandatory ) {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_FINISHED ) );
		} else {
			LOGGER.logInfo( SIARDVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
		}

		// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
		File workDir = new File( pathToWorkDir );
		if ( workDir.exists() ) {
			Util.deleteDir( workDir );
		}
		if ( ok ) {
			System.exit( 0 );
			// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}
		} else {
			System.exit( 2 );
			// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}
		}
	}
}
