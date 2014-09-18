/**
  ******************************************************************************
  * @file    XcostoGA.java
  * @author  ierturk @ StarGateInc <ierturk@ieee.org>
  * @version V0.0.0
  * @date    06-Sep-2014
  * @brief   Xcos XML Diagram parser
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

package sg.scilab.xcos.codegen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author ierturk
 *
 */
public class XcostoGA {
	
	public static Document docXcos;
	public static Document docGA;
	//public static Document docLib;
	
	public XcostoGA(String dName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
		docXcos = DocLoad(dName);
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		docBuilderFactory.setNamespaceAware(true);
		
		// Initialize input file
		docGA = docBuilder.newDocument();

		// Initialize output file
		//docGA.appendChild(docGA.createElement("SystemBlock"));
		//docGA.getFirstChild().appendChild(docGA.createElement("blocks"));
		//docGA.setXmlVersion("1.0");

		// Initialize library file
		//docLib = docBuilder.parse(new File("Resource/BlockLibrary.xml"));
	}
	
	@SuppressWarnings("unused")
	private void PrintXML(Document docIn) throws TransformerException {
        //for output to file, console
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //for pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(docGA);

        //write to console or file
        StreamResult console = new StreamResult(System.out);
        //StreamResult file = new StreamResult(new File(".xml"));

        //write data
        transformer.transform(source, console);
        //transformer.transform(source, file);
        //System.out.println("DONE");
	}
	
	private Document DocLoad(String dName)
			throws SAXException, IOException, ParserConfigurationException {
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		return docBuilder.parse (new File(dName));		
	}

	public void XMLtoTree(Tree tree, Document doc) {
		TreeItem treeItem = new TreeItem(tree, 0);
		treeItem.setText("root");
		XMLtoTree(treeItem, doc.getFirstChild());
	}
	
	private void XMLtoTree(TreeItem treeItem, Node node) {
		
		if((node.getNodeType() == 1) | (node.getNodeType() == 9)){			
			NamedNodeMap attributes = node.getAttributes();
			String strAttributes = "";
			for (int i = 0; i < attributes.getLength(); i++) {
		        Node theAttribute = attributes.item(i);
		        strAttributes += theAttribute.getNodeName() + "=\"" + theAttribute.getNodeValue() + "\" ";
		      }
			
			TreeItem tItem = new TreeItem(treeItem, 0);
			tItem.setText(node.getNodeName() + " " + strAttributes /*+ " text: " + node.getTextContent()*/);
			for(int i = 0; i<node.getChildNodes().getLength();i++) {
				XMLtoTree(tItem, node.getChildNodes().item(i));
			}
		}
	}
	
	public void ImportXcos() throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParserConfigurationException, TransformerException, DOMException, InstantiationException {
		
		XcosBlockTran Xcos2GA = new XcosBlockTran(docXcos);
		
		docGA.appendChild(docGA.importNode(Xcos2GA.ParseXcosDiagram(), true));
		//PrintXML(docGA);
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/

