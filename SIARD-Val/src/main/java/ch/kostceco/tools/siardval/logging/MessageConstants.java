/*== SIARD-Val ===================================================================================
The SIARD-Val application is used for validate SIARD-Files. 
Copyright (C) 2012 Claire R�thlisberger (KOST-CECO), Christian Eugster, Olivier Debenath, 
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

package ch.kostceco.tools.siardval.logging;

/**
 * SIARDVal / controller -->
 * 
 * Interface f�r den Zugriff auf Resourcen aus dem ResourceBundle.
 * 
 * @author Rc Claire R�thlisberger, KOST-CECO
 */
public interface MessageConstants
{

	// Initialisierung und Parameter-Ueberpruefung
	String	ERROR_PARAMETER_USAGE							= "error.parameter.usage";
	String	ERROR_LOGDIRECTORY_NODIRECTORY					= "error.logdirectory.nodirectory";
	String	ERROR_LOGDIRECTORY_NOTWRITABLE					= "error.logdirectory.notwritable";
	String	ERROR_SIARDFILE_FILENOTEXISTING					= "error.siardfile.filenotexisting";
	String	ERROR_LOGGING_NOFILEAPPENDER					= "error.logging.nofileappender";

	String	ERROR_WRONG_JRE									= "error.wrong.jdk";

	String	MESSAGE_TOTAL_VALID								= "message.total.valid";
	String	MESSAGE_TOTAL_INVALID							= "message.total.invalid";

	String	MESSAGE_FOOTER_LOG								= "message.footer.log";
	String	MESSAGE_FOOTER_SIARD							= "message.footer.siard";

	// Globale Meldungen
	String	MESSAGE_SIARDVALIDATION							= "message.siardvalidation";
	String	MESSAGE_VALIDATION_INTERRUPTED					= "message.validation.interrupted";
	String	MESSAGE_VALIDATION_FINISHED						= "message.validation.finished";
	String	MESSAGE_MODULE_VALID							= "message.module.valid";
	String	MESSAGE_MODULE_INVALID							= "message.module.invalid";
	String	MESSAGE_MODULE_INVALID_2ARGS					= "message.module.invalid.2args";

	String	MESSAGE_MODULE_A								= "message.module.a";
	String	MESSAGE_MODULE_B								= "message.module.b";
	String	MESSAGE_MODULE_C								= "message.module.c";
	String	MESSAGE_MODULE_D								= "message.module.d";
	String	MESSAGE_MODULE_E								= "message.module.e";
	String	MESSAGE_MODULE_F								= "message.module.f";
	String	MESSAGE_MODULE_G								= "message.module.g";
	String	MESSAGE_MODULE_H								= "message.module.h";
	String	MESSAGE_MODULE_I								= "message.module.i";
	String	MESSAGE_MODULE_J								= "message.module.j";
	String	MESSAGE_MODULE_K								= "message.module.k";
	String	MESSAGE_MODULE_L								= "message.module.l";

	String	MESSAGE_STEPERGEBNIS_A							= "message.stepergebnis.a";
	String	MESSAGE_STEPERGEBNIS_B							= "message.stepergebnis.b";
	String	MESSAGE_STEPERGEBNIS_C							= "message.stepergebnis.c";
	String	MESSAGE_STEPERGEBNIS_D							= "message.stepergebnis.d";
	String	MESSAGE_STEPERGEBNIS_E							= "message.stepergebnis.e";
	String	MESSAGE_STEPERGEBNIS_F							= "message.stepergebnis.f";
	String	MESSAGE_STEPERGEBNIS_G							= "message.stepergebnis.g";
	String	MESSAGE_STEPERGEBNIS_H							= "message.stepergebnis.h";
	String	MESSAGE_STEPERGEBNIS_I							= "message.stepergebnis.i";
	String	MESSAGE_STEPERGEBNIS_J							= "message.stepergebnis.j";
	String	MESSAGE_STEPERGEBNIS_K							= "message.stepergebnis.k";
	String	MESSAGE_STEPERGEBNIS_L							= "message.stepergebnis.l";

	String	MESSAGE_DASHES									= "message.dashes";

	String	MESSAGE_CONFIGURATION_ERROR_1					= "message.configuration.error.1";
	String	MESSAGE_CONFIGURATION_ERROR_2					= "message.configuration.error.2";
	String	MESSAGE_CONFIGURATION_ERROR_3					= "message.configuration.error.3";

	String	ERROR_UNKNOWN									= "error.unknown";

	// Modul A Meldungen
	String	ERROR_MODULE_A_NOFILE							= "error.module.a.nofile";
	String	ERROR_MODULE_A_INCORRECTFILEENDING				= "error.module.a.incorrectfileending";
	String	ERROR_MODULE_A_DEFLATED							= "error.module.a.deflated";

	// Modul B Meldungen
	String	MESSAGE_MODULE_B_NOTALLOWEDFILE					= "message.module.b.notallowedfile";
	String	MESSAGE_MODULE_B_CONTENT						= "message.module.b.content";
	String	MESSAGE_MODULE_B_HEADER							= "message.module.b.header";

	// Modul C Meldungen
	String	MESSAGE_MODULE_C_NOMETADATAFOUND				= "message.module.c.nometadatafound";
	String	MESSAGE_MODULE_C_NOMETADATAXSD					= "message.module.c.nometadataxsd";
	String	MESSAGE_MODULE_C_METADATA_ERRORS				= "message.module.c.metadata.errors";
	String	MESSAGE_MODULE_C_METADATA_ORIGERRORS			= "message.module.c.metadata.origerrors";

	// Modul D Meldungen
	String	MESSAGE_MODULE_D_INVALID_FOLDER					= "message.module.d.invalid.folder";
	String	MESSAGE_MODULE_D_MISSING_FOLDER					= "message.module.d.missing.folder";
	String	MESSAGE_MODULE_D_INVALID_FILE					= "message.module.d.invalid.file";
	String	MESSAGE_MODULE_D_MISSING_FILE					= "message.module.d.missing.file";

	// Modul E Meldungen
	String	MESSAGE_MODULE_E_INVALID_VALIDATION_CONTEXT		= "message.module.e.missing.properties";
	String	MESSAGE_MODULE_E_PROPERTIES_ERROR				= "message.module.e.properties.error";
	String	MESSAGE_MODULE_E_PATH_ERROR						= "message.module.e.path.error";
	String	MESSAGE_MODULE_E_EXTRACT_ERROR					= "message.module.e.extract.error";
	String	MESSAGE_MODULE_E_METADATA_ACCESS_ERROR			= "message.module.e.metadata.access.error";
	String	MESSAGE_MODULE_E_XML_ACCESS_ERROR				= "message.module.e.xml.access.error";
	String	MESSAGE_MODULE_E_PREVALIDATION_ERROR			= "message.module.e.prevalidation.error";
	String	MESSAGE_MODULE_E_MISSING_SIARD_TABLES			= "message.module.e.missing.siard.tables";
	String	MESSAGE_MODULE_E_INVALID_ATTRIBUTE_COUNT		= "message.module.e.attribute.count.validation.failed";
	String	MESSAGE_MODULE_E_INVALID_ATTRIBUTE_OCCURRENCE	= "message.module.e.attribute.occurrence.validation.failed";
	String	MESSAGE_MODULE_E_INVALID_ATTRIBUTE_SEQUENCE		= "message.module.e.attribute.sequence.validation.failed";
	String	MESSAGE_MODULE_E_INVALID_ATTRIBUTE_TYPE			= "message.module.e.attribute.type.validation.failed";
	String	MESSAGE_MODULE_E_VALIDATION_SKIPPED				= "message.module.e.validation.skipped";
	
	// Modul F Meldungen
	String	MESSAGE_MODULE_F_INVALID_VALIDATION_CONTEXT		= "message.module.f.missing.properties";
	String	MESSAGE_MODULE_F_PROPERTIES_ERROR				= "message.module.f.properties.error";
	String	MESSAGE_MODULE_F_PATH_ERROR						= "message.module.f.path.error";
	String	MESSAGE_MODULE_F_EXTRACT_ERROR					= "message.module.f.extract.error";
	String	MESSAGE_MODULE_F_METADATA_ACCESS_ERROR			= "message.module.f.metadata.access.error";
	String	MESSAGE_MODULE_F_XML_ACCESS_ERROR				= "message.module.f.xml.access.error";
	String	MESSAGE_MODULE_F_PREVALIDATION_ERROR			= "message.module.f.prevalidation.error";
	String	MESSAGE_MODULE_F_MISSING_SIARD_TABLES			= "message.module.f.missing.siard.tables";
	String  MESSAGE_MODULE_F_INVALID_TABLE_XML_FILES		= "message.module.f.invalid.table.xml.files";
	String  MESSAGE_MODULE_F_INVALID_TABLE_XSD_FILES		= "message.module.f.invalid.table.xsd.files";
	
	
	// Modul G Meldungen
	String	MESSAGE_MODULE_G_DUPLICATE_SCHEMA				= "message.module.g.duplicate.schema";
	String	MESSAGE_MODULE_G_DUPLICATE_TABLE				= "message.module.g.duplicate.table";
	String	MESSAGE_MODULE_G_DUPLICATE_COLUMN				= "message.module.g.duplicate.column";

	// Modul H Meldungen
	String	MESSAGE_MODULE_H_INVALID_FOLDER					= "message.module.h.invalid.folder";
	String	MESSAGE_MODULE_H_INVALID_XML					= "message.module.h.invalid.xml";
	String	MESSAGE_MODULE_H_INVALID_ERROR					= "message.module.h.invalid.error";
	String  MESSAGE_MODULE_H_TABLE_NOT_VALIDATED			= "message.module.h.table.not.validated";

	// Modul I Meldungen
	String	MESSAGE_MODULE_I_NOTALLOWEDPUID					= "message.module.i.notallowedpuid";
	String	MESSAGE_MODULE_I_NOTALLOWEDEXT					= "message.module.i.notallowedext";

	// Modul J Meldungen
	String	MESSAGE_MODULE_J_INVALID_FOLDER					= "message.module.j.invalid.folder";
	String	MESSAGE_MODULE_J_INVALID_FILE					= "message.module.j.invalid.file";
	String	MESSAGE_MODULE_J_INVALID_ENTRY					= "message.module.j.invalid.entry";
}
