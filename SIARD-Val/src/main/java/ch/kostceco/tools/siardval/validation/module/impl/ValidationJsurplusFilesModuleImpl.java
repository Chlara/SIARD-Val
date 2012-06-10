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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.kostceco.tools.siardval.exception.module.ValidationJsurplusFilesException;
import ch.kostceco.tools.siardval.service.ConfigurationService;
import ch.kostceco.tools.siardval.validation.ValidationModuleImpl;
import ch.kostceco.tools.siardval.validation.module.ValidationJsurplusFilesModule;

/**
 * Validierungsschritt J (Zus�tzliche Prim�rdateien) Enth�lt der content-Ordner
 * Dateien oder Ordner die nicht in metadata.xml beschrieben sind ? invalid -->
 * Zus�tzliche Ordner oder Dateien im content-Ordner
 * 
 * @author Ec Christian Eugster
 */

public class ValidationJsurplusFilesModuleImpl extends ValidationModuleImpl
		implements ValidationJsurplusFilesModule
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
			throws ValidationJsurplusFilesException
	{
		boolean valid = true;
		try {
			String pathToWorkDir = getConfigurationService().getPathToWorkDir();
			File metadataXml = new File( new StringBuilder( pathToWorkDir )
					.append( File.separator ).append( "header" )
					.append( File.separator ).append( "metadata.xml" )
					.toString() );
			InputStream fin = new FileInputStream( metadataXml );
			InputSource source = new InputSource( fin );
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( source );
			fin.close();

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			xPath.setNamespaceContext( new SiardNamespaceContext() );

			File content = new File( new StringBuilder( pathToWorkDir )
					.append( File.separator ).append( "content" ).toString() );
			File[] schemas = content.listFiles();
			for ( File schema : schemas ) {
				valid = valid && validateSchema( schema, xPath, doc );
			}
		} catch ( java.io.IOException e ) {
			valid = false;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_D )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ "IOException " + e.getMessage() );
		} catch ( XPathExpressionException e ) {
			valid = false;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_D )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ "XPathExpressionException " + e.getMessage() );
		} catch ( ParserConfigurationException e ) {
			valid = false;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_D )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ "ParserConfigurationException " + e.getMessage() );
		} catch ( SAXException e ) {
			valid = false;
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_D )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ "SAXException " + e.getMessage() );
		}

		return valid;
	}

	private boolean validateSchema( File schema, XPath xPath, Document doc )
			throws XPathExpressionException
	{
		boolean valid = true;
		XPathExpression expression = xPath
				.compile( "//siard:schema[siard:folder='" + schema.getName()
						+ "']/siard:folder/text()" );
		Node node = (Node) expression.evaluate( doc, XPathConstants.NODE );
		if ( node == null ) {
			valid = false;
			File content = new File( schema.getParent() );
			if ( schema.isFile() ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ content.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_FILE ) + " "
								+ schema.getName() + "." );
			} else if ( schema.isDirectory() ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ content.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_FOLDER ) + " "
								+ schema.getName() + "." );
			} else {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ content.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_ENTRY ) + " "
								+ schema.getName() + "." );
			}
		}
		if ( valid ) {
			File[] tables = schema.listFiles();
			for ( File table : tables ) {
				valid = valid && validateTable( table, xPath, doc );
			}
		}
		return valid;
	}

	private boolean validateTable( File table, XPath xPath, Document doc )
			throws XPathExpressionException
	{
		boolean valid = true;
		XPathExpression expression = xPath
				.compile( "//siard:table[siard:folder='" + table.getName()
						+ "']/siard:folder/text()" );
		Node node = (Node) expression.evaluate( doc, XPathConstants.NODE );
		if ( node == null ) {
			valid = false;
			File schema = new File( table.getParent() );
			if ( table.isFile() ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ schema.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_FILE ) + " "
								+ table.getName() + "." );
			} else if ( table.isDirectory() ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ schema.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_FOLDER ) + " "
								+ table.getName() + "." );
			} else {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_J )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ schema.getName()
								+ " "
								+ getTextResourceService().getText(
										MESSAGE_MODULE_J_INVALID_ENTRY ) + " "
								+ table.getName() + "." );
			}
		}
		return valid;
	}

	private class SiardNamespaceContext implements NamespaceContext
	{
		private static final String	SIARD_URI	= "http://www.bar.admin.ch/xmlns/siard/1.0/metadata.xsd";

		private static final String	DB_URI		= "http://db.apache.org/torque/4.0/templates/database";

		@Override
		public String getNamespaceURI( String prefix )
		{
			if ( prefix == null ) {
				throw new NullPointerException( "Null prefix is not allowed" );
			}
			if ( "siard".equals( prefix ) ) {
				return SIARD_URI;
			}
			if ( "db".equals( prefix ) ) {
				return DB_URI;
			}
			return XMLConstants.XML_NS_URI;
		}

		@Override
		public String getPrefix( String uri )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<String> getPrefixes( String uri )
		{
			throw new UnsupportedOperationException();
		}

	}
}
