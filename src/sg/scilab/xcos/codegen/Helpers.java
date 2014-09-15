/**
  ******************************************************************************
  * @file    Helpers.java
  * @author  ierturk @ StarGateInc <ierturk@ieee.org>
  * @version V0.0.0
  * @date    06-Sep-2014
  * @brief   Helpers Block Translator from Xcos to GA
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

import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author ierturk
 *
 */
public class Helpers {
	
	protected static Document docIn;
	protected static int idCnt;
	
	public Helpers(Document In) {
		// TODO Auto-generated constructor stub
		docIn = In;
	}
	
	public Element ParseOutDataPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {

		Element elementOut = null;

		NodeList outPortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//ExplicitOutputPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		
		for (int i = 0; i < outPortNL.getLength(); i++) {
			elementOut = docIn.createElement("outDataPorts");
			elementOut.setAttribute("type", "gaxml:collection");
			
			Element elementTemp = docIn.createElement("OutDataPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			elementTemp.setAttribute("portNumber", outPortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);		
		}

		return elementOut;
	}
	
	public Element ParseInDataPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {

		Element elementOut = null;

		NodeList outPortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//ExplicitInputPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		
		for (int i = 0; i < outPortNL.getLength(); i++) {
			elementOut = docIn.createElement("inDataPorts");
			elementOut.setAttribute("type", "gaxml:collection");
			
			Element elementTemp = docIn.createElement("InDataPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			elementTemp.setAttribute("portNumber", outPortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);		
		}

		return elementOut;
	}
	
	public Element ParseGeometry(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		
		NamedNodeMap geoNM = ((NodeList) XPathFactory.newInstance().newXPath()
								.compile("//*[local-name()='BasicBlock']" + "[@id='" + blockID + "']/mxGeometry")
								.evaluate(docIn, XPathConstants.NODESET)).item(0).getAttributes();

		Element elementOut = docIn.createElement("diagramInfo");
		elementOut.setAttribute("type", "gaxml:collection");
		
		Element elementTemp = docIn.createElement("DiagramInfo");
		elementTemp.setAttribute("sizeX", geoNM.getNamedItem("width").getNodeValue());
		elementTemp.setAttribute("sizeY", geoNM.getNamedItem("height").getNodeValue());
		elementTemp.setAttribute("positionX", geoNM.getNamedItem("x").getNodeValue());
		elementTemp.setAttribute("positionY", geoNM.getNamedItem("y").getNodeValue());
		elementOut.appendChild(elementTemp);		

		return elementOut;
	}
	
	public Element ParseParameters(ArrayList<ArrayList<Object>> paramArray) {
		Element elementOut = docIn.createElement("parameters");
		elementOut.setAttribute("type", "gaxml:collection");
		
	    for(ArrayList<Object> pList : paramArray){
	        //System.out.println(pList.get(0) + " -- " + pList.get(1) + " -- " + pList.get(2));
			//elementOut.appendChild(docIn.importNode(ParseParameters(ParamArray), true));
	        
	        switch (pList.get(1).toString()) {
				case "true":
					//System.out.println(1);
					elementOut.appendChild(docIn.importNode(ParseValueParam(pList), true));
					break;
					
				case "false":
					//System.out.println(0);
					elementOut.appendChild(docIn.importNode(ParseStringParam(pList), true));
					break;
					
				default:
					break;
			}

	    }

		return elementOut;
	}
	
	public Element ParseValueParam(ArrayList<Object> paramList) {
		Element elementOut = docIn.createElement("BlockParameter");
		elementOut.setAttribute("name", (String) paramList.get(0));
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		
		Element elementTemp = docIn.createElement("value");
		elementTemp.setAttribute("type", "gaxml:collection");
		elementOut.appendChild(elementTemp);
		
		elementTemp = docIn.createElement("ExpressionValue");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementOut.getFirstChild().appendChild(elementTemp);
		
		elementTemp = docIn.createElement("value");
		elementTemp.setAttribute("type", "gaxml:collection");
		elementOut.getFirstChild().getFirstChild().appendChild(elementTemp);
		
		Double value = Double.parseDouble((String) paramList.get(2));
		//System.out.println( "This is list item 3 : " + value);
		elementTemp = docIn.createElement("RealExpression");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));	
		elementTemp.setAttribute("litValue", Double.toString(value.floatValue()));
		elementTemp.setAttribute("integerPart", Integer.toString(value.intValue()));
		elementTemp.setAttribute("scientificValue", "false");
		elementTemp.setAttribute("fractionalPart", Double.toString((Math.abs(value - value.intValue()))));
		elementTemp.setAttribute("exponent", Integer.toString(0));	
		elementOut.getFirstChild().getFirstChild().getFirstChild().appendChild(elementTemp);
		
		elementTemp = docIn.createElement("dataType");
		elementTemp.setAttribute("type", "gaxml:collection");
		elementOut.getFirstChild().getFirstChild().getFirstChild().getFirstChild().appendChild(elementTemp);
		
		elementTemp = docIn.createElement("TRealDouble");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementOut.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getFirstChild().appendChild(elementTemp);
		
		return elementOut;
	}
	
	public Element ParseStringParam(ArrayList<Object> paramList) {
		Element elementOut = docIn.createElement("BlockParameter");
		elementOut.setAttribute("name", (String) paramList.get(0));
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		
		Element elementTemp = docIn.createElement("value");
		elementTemp.setAttribute("type", "gaxml:collection");
		elementOut.appendChild(elementTemp);
		
		elementTemp = docIn.createElement("StringValue");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementTemp.setAttribute("value", (String) paramList.get(2));
		elementOut.getFirstChild().appendChild(elementTemp);
		
		return elementOut;
	}

}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
