/**
  ******************************************************************************
  * @file    XcosBlockTran.java
  * @author  ierturk @ StarGateInc <ierturk@ieee.org>
  * @version V0.0.0
  * @date    06-Sep-2014
  * @brief   Block Translator from Xcos to GA
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author ierturk
 *
 */
public class XcosBlockTran {
	
	private static Document docIn;
	//private static Map<String,String> BlockLib = new HashMap<String, String>();

	public XcosBlockTran(Document In) {
		// TODO Auto-generated constructor stub
		docIn = In;
		
		//BlockLib.put("GAINBLK", "Gain");
		//BlockLib.put("SUMMATION", "Sum");
	}
	
	public Node ParseBasicBlock(String blockID) throws ParserConfigurationException, XPathExpressionException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, DOMException, NoSuchMethodException {
		
		XPathExpression expBLK = XPathFactory.newInstance().newXPath().compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']");
		NodeList blockNL = (NodeList) expBLK.evaluate(docIn, XPathConstants.NODESET);
		
		Method parser;
		XcosBlockTran testClass = new XcosBlockTran(docIn);
		
		//System.out.println(BlockLib.get("GAINBLK"));
		
		try {
			parser = testClass.getClass().getMethod(blockNL.item(0).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue() + "_tran", String.class);
			Node nodeOut = (Node) parser.invoke(testClass, blockID);
			return nodeOut;
		} catch (NoSuchMethodException | NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		parser = testClass.getClass().getMethod("UnSupported", String.class);
		Node nodeOut = (Node) parser.invoke(testClass, blockNL.item(0).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue());
		return nodeOut;

	}


	public Node UnSupported(String blockType) throws ParserConfigurationException {		
		Document nodeOut = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element typeE = nodeOut.createElement("UnSupportedBlock");
		typeE.setAttribute("type", blockType);
		nodeOut.appendChild(typeE);
		return (Node) nodeOut.getFirstChild();
	}
	
	public Node GAINBLK_tran(String blockID) throws ParserConfigurationException {		
		Document nodeOut = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element typeE = nodeOut.createElement("CombinatorialBlock");
		typeE.setAttribute("type", "Gain");
		nodeOut.appendChild(typeE);
		return (Node) nodeOut.getFirstChild();
	}

	public Node SUMMATION_tran(String blockID) throws ParserConfigurationException {		
		Document nodeOut = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element typeE = nodeOut.createElement("CombinatorialBlock");
		typeE.setAttribute("type", "Sum");
		nodeOut.appendChild(typeE);
		return (Node) nodeOut.getFirstChild();
	}
	
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
