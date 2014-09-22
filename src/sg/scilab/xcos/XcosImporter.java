/**
  ******************************************************************************
  * @file    XcosImporter.java
  * @author  ierturk @ StarGateInc <ierturk@ieee.org>
  * @version V0.0.0
  * @date    06-Sep-2014
  * @brief   Xcos XML Diagram Importer into Java Tree
  ******************************************************************************
  * @attention
  *
  * COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com>
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
  */

package sg.scilab.xcos;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.action.Action;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import org.eclipse.swt.widgets.Tree;

import sg.scilab.xcos.codegen.XcostoGA;

public class XcosImporter extends ApplicationWindow {
	private Action actionOpenFile;
	private Action actionExit;
	private static Tree treeIn;
	private static Tree treeOut;
	private Action actionOutputFolder;
	private Action actionImport;
	private Action actionConvert;
	private Action actionGenerate;
	private Action actionSelectInput;
	private static String InDiagram;
	private static String OutFolder;
	XcostoGA Converter;
	/**
	 * Create the application window,
	 */
	public XcosImporter() {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();
		InDiagram = null;
		OutFolder = null;
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		setStatus("Ready to Import!");
		Composite container = new Composite(parent, SWT.NONE);
		{
			treeIn = new Tree(container, SWT.BORDER);
			treeIn.setBounds(2, 0, 306, 450);
		}
		{
			treeOut = new Tree(container, SWT.BORDER);
			treeOut.setBounds(310, 0, 322, 450);
		}
		
		/*
	    for (int loopIndex1 = 0; loopIndex1 < 5; loopIndex1++) {
	        TreeItem item0 = new TreeItem(tree, 0);
	        item0.setText("Level 0 Item " + loopIndex1);
	        for (int loopIndex2 = 0; loopIndex2 < 5; loopIndex2++) {
	          TreeItem item1 = new TreeItem(item0, 0);
	          item1.setText("Level 1 Item " + loopIndex2);
	          for (int loopIndex3 = 0; loopIndex3 < 5; loopIndex3++) {
	            TreeItem item2 = new TreeItem(item1, 0);
	            item2.setText("Level 2 Item " + loopIndex3);
	          }
	        }
	      }
	      */

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			actionOpenFile = new Action("Open") {
				@Override 			
				public void run() {
				} 
			};
		}
		{
			actionExit = new Action("Exit") {
				@Override 			
				public void run() { 				
					close(); 			
				}

			};
		}
		{
			actionSelectInput = new Action("Select Input Diagram ...") {
				@Override 			
				public void run() {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					InDiagram = dialog.open();
				}

			};
		}
		{
			actionOutputFolder = new Action("Select Output Folder ...") {
				@Override 			
				public void run() {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					OutFolder = dialog.open();
					//System.out.println(OutFolder);
				}
			};
		}
		{
			actionImport = new Action("Import Xcos Diagram") {
				@Override 			
				public void run() {
					if(InDiagram == null) {
					       MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
					       mb.setText("sgJavaSci");
					       mb.setMessage("Please select input diagram...");
					       mb.open();
					} else { 
						try {
							Converter = new XcostoGA(InDiagram);
							Converter.XMLtoTree(treeIn, XcostoGA.docXcos);
							setStatus("Diagram Imported");
						} catch (SAXException | IOException
								| ParserConfigurationException
								| TransformerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			};
		}
		{
			actionConvert = new Action("Convert to GA") {				@Override 			
				public void run() {
					if(InDiagram == null) {
					       MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
					       mb.setText("sgJavaSci");
					       mb.setMessage("Please select output folder...");
					       mb.open();
					} else {
						try {
							Converter.ImportXcos();
							Converter.XMLtoTree(treeOut, XcostoGA.docGA);
							Converter.PrintXML(OutFolder + "\\tmpGA.gsm.xml");
							setStatus("Diagram converted");
						} catch (XPathExpressionException | NoSuchMethodException
								| SecurityException | IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException | DOMException
								| InstantiationException
								| ParserConfigurationException
								| TransformerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					}
				}
			};
		}
		{
			actionGenerate = new Action("Generate C Code") {
				@Override 			
				public void run() {					try {
						Runtime.getRuntime().exec("cmd.exe /c set GENEAUTO_HOME=C:\\Users\\ierturk\\Desktop\\GeneAuto\\geneauto2 && "
								+ "C:\\works\\tools\\eclipseDevelopmentPackage\\ibm_sdk70\\bin\\java "
								+ "-classpath \"c:\\Users\\ierturk\\Desktop\\GeneAuto\\geneauto2\\geneauto.galauncher-2.4.10.jar;"
								+ "c:\\Users\\ierturk\\Desktop\\GeneAuto\\geneautoLib\\geneauto.pl_blocklibrary.jar\" geneauto.launcher.GALauncherSCICOSOpt "
								+ OutFolder + "\\tmpGA.gsm.xml"
								+ " -b c:\\Users\\ierturk\\Desktop\\GeneAuto\\geneautoLib\\pl_blocklibrary.gbl.xml 2>geneauto.err");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			};
		}
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		{
			MenuManager FileMenu = new MenuManager("File");
			menuManager.add(FileMenu);
			FileMenu.add(actionOpenFile);
			FileMenu.add(actionExit);
		}
		{
			MenuManager optionsMenu = new MenuManager("Options");
			menuManager.add(optionsMenu);
			optionsMenu.add(actionSelectInput);
			optionsMenu.add(actionOutputFolder);
		}
		{
			MenuManager toolsMenu = new MenuManager("Tools");
			menuManager.add(toolsMenu);
			toolsMenu.add(actionImport);
			toolsMenu.add(actionConvert);
			toolsMenu.add(actionGenerate);
		}
		return menuManager;
	}

	/**
	 * Create the coolbar manager.
	 * @return the coolbar manager
	 */
	@Override
	protected CoolBarManager createCoolBarManager(int style) {
		CoolBarManager coolBarManager = new CoolBarManager(style);
		return coolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			XcosImporter window = new XcosImporter();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Xcos Importer");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(650, 500);
	}
	
	protected Tree getTreeIn() {
		return treeIn;
	}

	protected Tree getTreeOut() {
		return treeOut;
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
