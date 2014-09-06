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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.jface.action.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.eclipse.swt.widgets.Tree;

public class XcosImporter extends ApplicationWindow {
	private Action actionOpenFile;
	private Action actionExit;
	private static Tree tree;

	/**
	 * Create the application window,
	 */
	public XcosImporter() {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		{
			tree = new Tree(container, SWT.BORDER);
			tree.setBounds(0, 0, 434, 346);
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
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					try {
						DocLoad(dialog.open());
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
		return new Point(450, 457);
	}
	
	/**
	 * XML Document loader.
	 * @param DocName
	 */
	private static void DocLoad(String DocName)
			throws SAXException, IOException, ParserConfigurationException {
		
		// Use DOM API to get full XML document
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse (new File(DocName));
		tree.removeAll();
		TreeItem treeItem = new TreeItem(tree, 0);
		treeItem.setText("root");
		XMLtoTree(treeItem, doc.getFirstChild());
	}
	
	/**
	 * XML to Tree.
	 * @param treeItem
	 * @param node
	 * 
	 */
	private static void XMLtoTree(TreeItem treeItem, Node node) {
		
		if((node.getNodeType() == 1) | (node.getNodeType() == 9)){
			TreeItem tItem = new TreeItem(treeItem, 0);
			tItem.setText(node.getNodeName());
			
			for(int i = 0; i<node.getChildNodes().getLength();i++) {
				XMLtoTree(tItem, node.getChildNodes().item(i));
			}
		}
	}
	protected Tree getTree() {
		return tree;
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
