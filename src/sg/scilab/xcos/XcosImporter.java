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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
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
import geneauto.launcher.GALauncherSCICOSOptImpl;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class XcosImporter extends ApplicationWindow {
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
	private static File sgWorkFolder;
	private Action actionNew;
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
		
	    /*   System.out.println("Working Directory = " +
	               System.getProperty("user.dir")); */
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

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			actionNew = new Action("New") {
				@Override 			
				public void run() {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					InDiagram = dialog.open();
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
					if(InDiagram != null){
						File file = new File(InDiagram);
						if(file.exists()){
							dialog.setFilterPath(file.getParentFile().getPath());
						}
					} else {
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
					}
					OutFolder = dialog.open();
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
			actionConvert = new Action("Convert to GA") {
				@Override 			
				public void run() {
					if(InDiagram == null) {
					       MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
					       mb.setText("sgJavaSci");
					       mb.setMessage("Please select output folder...");
					       mb.open();
					} else {
						sgWorkFolder = new File(OutFolder + "\\sgJavaSciWork\\"
													+ XcostoGA.docXcos.getFirstChild().getAttributes()
														.getNamedItem("title").getNodeValue().replace(" ", "_"));
						if (!sgWorkFolder.exists()) {
							//System.out.println("creating directory: " + sgWorkFolder.getPath());

							try{
								sgWorkFolder.mkdirs();
								new File(sgWorkFolder.getAbsolutePath() + "\\cfiles").mkdirs();
								new File(sgWorkFolder.getAbsolutePath() + "\\XML").mkdirs();
							} catch (SecurityException e){
								// TODO Auto-generated catch block
								//e.printStackTrace();							
							}        
						}
						
						try {
							Converter.ImportXcos();
							Converter.XMLtoTree(treeOut, XcostoGA.docGA);
							Converter.PrintXML(sgWorkFolder.getAbsolutePath() + "\\XML\\tmpGA.gsm.xml");
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
				public void run() {
				    /*
					String cmd = "C:\\works\\tools\\eclipseDevelopmentPackage\\ibm_sdk70\\bin\\java "
							+ "-classpath \"" + System.getProperty("user.dir") + "\\Thirdparty\\geneauto2" + "\\geneauto.galauncher-2.4.10.jar\" "
							+ "geneauto.launcher.GALauncherSCICOSOpt " + sgWorkFolder.getAbsolutePath() + "\\XML\\tmpGA.gsm.xml "
							+ "-O " + sgWorkFolder.getAbsolutePath() + "\\cfiles 2>geneauto.err";
					
					System.out.println(cmd);
				    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
				    pb.environment().put("GENEAUTO_HOME", System.getProperty("user.dir") + "\\Thirdparty\\geneauto2");
				    pb.inheritIO();

				    try {
				        Process p = pb.start();
				        String output = loadStream(p.getInputStream());
				        String error  = loadStream(p.getErrorStream());
				        int rc = p.waitFor();
				        System.out.println("Process ended with rc=" + rc);
				        System.out.println("\nStandard Output:\n");
				        System.out.println(output);
				        System.out.println("\nStandard Error:\n");
				        System.out.println(error);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
				    
				    //geneauto.launcher.GALauncherSCICOSOpt.class.newInstance();
				    GALauncherSCICOSOptImpl launcher = new GALauncherSCICOSOptImpl();
				    String[] args = {sgWorkFolder.getAbsolutePath() + "\\XML\\tmpGA.gsm.xml", "-O", sgWorkFolder.getAbsolutePath() + "\\cfiles"};
				    launcher.gaMain(args, "GALauncherSCICOSOpt");
					setStatus("C Code generation has been completed");
				}

			};
		}
	}
/*	
    private static String loadStream(InputStream s) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=br.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }
*/

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
			FileMenu.add(actionNew);
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
		{
			ToolBarManager toolBarManager = new ToolBarManager();
			coolBarManager.add(toolBarManager);
			toolBarManager.add(actionSelectInput);
			toolBarManager.add(actionOutputFolder);
		}
		{		
			ToolBarManager toolBarManager = new ToolBarManager();
			coolBarManager.add(toolBarManager);
			toolBarManager.add(actionImport);
			toolBarManager.add(actionConvert);
			toolBarManager.add(actionGenerate);
			return coolBarManager;
		}
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
		return new Point(650, 562);
	}
	
	protected Tree getTreeIn() {
		return treeIn;
	}

	protected Tree getTreeOut() {
		return treeOut;
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
