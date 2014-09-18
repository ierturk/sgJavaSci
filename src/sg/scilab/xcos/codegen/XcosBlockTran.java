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
	
	public XcosBlockTran(Document In) {
		// TODO Auto-generated constructor stub
		super(In);
	}
	
	
	@SuppressWarnings("unused")
	private Element UnSupported(String blockType) throws ParserConfigurationException {
		Element elementOut = docIn.createElement("UnSupportedBlock");
		elementOut.setAttribute("type", blockType);
		System.out.println("\tUnSupportedBlock: " + blockType);
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
		
		ArrayList<ArrayList<Object>> ParamArray = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> ParamList = new ArrayList<Object>();

		ParamList.add("InitialValue");
		ParamList.add(true);
		try {
			ParamList.add(((NodeList) XPathFactory.newInstance().newXPath()
										.compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']"
												+ "/Array[@as='oDState']/ScilabDouble/data")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("realPart").getNodeValue());	
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			ParamList.add(((NodeList) XPathFactory.newInstance().newXPath()
										.compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']"
													+ "/ScilabDouble[@as='dState']/data")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("realPart").getNodeValue());
		}
		
		ParamArray.add(ParamList);		
		elementOut.appendChild(docIn.importNode(ParseParameters(ParamArray), true));
		
		return elementOut;
	}
	
	@SuppressWarnings("unused")
	private Element IFTHEL_f_tran(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {		
		Element elementOut = docIn.createElement("CombinatorialBlock");
		elementOut.setAttribute("type", "If");
		elementOut.setAttribute("name", "NoName");
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("directFeedThrough", "true");
		elementOut.setAttribute("sampletime", "-1");
		
		ArrayList<ArrayList<Object>> ParamArray = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> ParamList = new ArrayList<Object>();
		
		return elementOut;
	}
}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
