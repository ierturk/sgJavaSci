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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
public class XcosBlockTran extends Helpers {
	
	//public static Document docIn;
	//public int idCnt = 0;

	//private static Helpers helpXML;

	public XcosBlockTran(Document In) {
		// TODO Auto-generated constructor stub
		super(In);
	}
	
	public Element ParseXcosDiagram() throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ParserConfigurationException, TransformerException {
		
		return ParseSuperBlock(
								((NodeList) XPathFactory.newInstance().newXPath()
										.compile("/node()/mxGraphModel/root/mxCell[string-length(@parent)!=0]")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("id").getNodeValue()
							);
		}
	
	private Element ParseSuperBlock(String parentID) throws XPathExpressionException, ParserConfigurationException, TransformerException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		XPathExpression expBLK = XPathFactory.newInstance().newXPath().compile("//*[local-name()='BasicBlock' or local-name()='SuperBlock']"
																					+ "[@parent='" + parentID + "']");
		
		Element elementOut = docIn.createElement("SystemBlock");
		elementOut.setAttribute("type", "SubSystem");
		elementOut.setAttribute("name", "NoName");
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("directFeedThrough", "true");
		
		Element elementTemp = docIn.createElement("blocks");
		elementTemp.setAttribute("type", "gaxml:collection");
		elementOut.appendChild(elementTemp);
		
		NodeList resultNL = (NodeList) expBLK.evaluate(docIn, XPathConstants.NODESET);
		for (int i = 0; i < resultNL.getLength(); i++) {

			switch(resultNL.item(i).getNodeName()) {
				case "BasicBlock":
					elementOut.getFirstChild().appendChild(docIn.importNode(ParseBasicBlock(resultNL.item(i).getAttributes().getNamedItem("id").getNodeValue()), true));
					break;
					
				case "SuperBlock":
					elementOut.getFirstChild().appendChild(docIn.importNode(ParseSuperBlock(
																								((NodeList) XPathFactory.newInstance().newXPath()
																										.compile("//SuperBlock[@id='"
																												+ resultNL.item(i).getAttributes().getNamedItem("id").getNodeValue()
																												+ "']"
																												+ "/SuperBlockDiagram/mxGraphModel/root/mxCell[string-length(@parent)!=0]")
																										.evaluate(docIn, XPathConstants.NODESET))
																										.item(0).getAttributes().getNamedItem("id").getNodeValue()
																							), true));
					break;
					
				default:
					System.out.println("NODE[" + i + "]: " + resultNL.item(i).getNodeName());
			}
		}
		return elementOut;
	}
	
	private Element ParseBasicBlock(String blockID) throws ParserConfigurationException, XPathExpressionException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, DOMException, NoSuchMethodException {
		
		XPathExpression expBLK = XPathFactory.newInstance().newXPath().compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']");
		NodeList blockNL = (NodeList) expBLK.evaluate(docIn, XPathConstants.NODESET);
		
		Method parser;
		XcosBlockTran testClass = new XcosBlockTran(docIn);
		
		try {
			parser = testClass.getClass().getDeclaredMethod(blockNL.item(0).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue() + "_tran", String.class);
			parser.setAccessible(true);
			Element elementOut = (Element) parser.invoke(testClass, blockID);
			
			try {
				elementOut.appendChild(docIn.importNode(ParseInDataPort(blockID), true));	
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			try {
				elementOut.appendChild(docIn.importNode(ParseOutDataPort(blockID), true));
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			try {
				elementOut.appendChild(docIn.importNode(ParseGeometry(blockID), true));
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			return elementOut;
		} catch (NoSuchMethodException | NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		parser = testClass.getClass().getDeclaredMethod("UnSupported", String.class);
		parser.setAccessible(true);
		Element elementOut = (Element) parser.invoke(testClass, blockNL.item(0).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue());
		return elementOut;

	}

	@SuppressWarnings("unused")
	private Element UnSupported(String blockType) throws ParserConfigurationException {		
		Element elementOut = docIn.createElement("UnSupportedBlock");
		elementOut.setAttribute("type", blockType);
		return elementOut;
	}
	
	@SuppressWarnings("unused")
	private Element CONST_m_tran(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {		
		Element elementOut = docIn.createElement("SourceBlock");
		elementOut.setAttribute("type", "Constant");
		elementOut.setAttribute("name", "NoName");
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("directFeedThrough", "true");
		elementOut.setAttribute("sampletime", "-1");
		
		ArrayList<ArrayList<Object>> ParamArray = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> ParamList = new ArrayList<Object>();

		//Map<Object, Object> parameters = new HashMap<Object, Object>();

		//ParamList.clear();
		ParamList.add("Value");
		ParamList.add(true);
		try {
			ParamList.add(((NodeList) XPathFactory.newInstance().newXPath()
										.compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']"
													+ "/Array[@as='objectsParameters']/ScilabDouble/data")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("realPart").getNodeValue());		
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			ParamList.add(((NodeList) XPathFactory.newInstance().newXPath()
										.compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']"
													+ "/ScilabDouble[@as='realParameters']/data")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("realPart").getNodeValue());
		}
		
		ParamArray.add(ParamList);
		
		ParamList = new ArrayList<Object>();
		ParamList.add("OutDataTypeMode");
		ParamList.add(false);
		ParamList.add("Inherit from 'Constant value'");
		ParamArray.add(ParamList);

		ParamList = new ArrayList<Object>();
		ParamList.add("VectorParams1D");
		ParamList.add(false);
		ParamList.add("off");
		ParamArray.add(ParamList);
		
		elementOut.appendChild(docIn.importNode(ParseParameters(ParamArray), true));

		return elementOut;
	}
	
	@SuppressWarnings("unused")
	private Element DOLLAR_m_tran(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {		
		Element elementOut = docIn.createElement("SequentialBlock");
		elementOut.setAttribute("type", "UnitDelay");
		elementOut.setAttribute("name", "NoName");
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("directFeedThrough", "true");
		elementOut.setAttribute("sampletime", "-1");
		
		return elementOut;
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
