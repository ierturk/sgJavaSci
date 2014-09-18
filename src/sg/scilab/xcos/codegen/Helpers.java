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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
	protected static ArrayList<ArrayList<Object>> PortAsscs;
	
	public Helpers(Document In) {
		// TODO Auto-generated constructor stub
		docIn = In;
	}
	
	public Element ParseXcosDiagram() throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ParserConfigurationException, TransformerException {
		
		return ParseBlocks(
								((NodeList) XPathFactory.newInstance().newXPath()
										.compile("/node()/mxCell[@as='defaultParent']")
										.evaluate(docIn, XPathConstants.NODESET))
										.item(0).getAttributes().getNamedItem("id").getNodeValue()
							);
		}

	
	private Element ParseBlocks(String parentID) throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DOMException, ParserConfigurationException, InstantiationException, TransformerException{
		PortAsscs = new ArrayList<ArrayList<Object>>();
		NodeList blockNL = (NodeList) XPathFactory.newInstance().newXPath()
												.compile("//*[@parent='" + parentID + "']"
														+ "[string-length(@interfaceFunctionName)!=0 "
														+ "or name()='SuperBlock']")
												.evaluate(docIn, XPathConstants.NODESET);

		Element elementOut = docIn.createElement("blocks");
		elementOut.setAttribute("type", "gaxml:collection");
		
		Method Translator;
		XcosBlockTran TranClass = new XcosBlockTran(docIn);
		String blockID;
		String blockIFN;
		Element elementTmp;
		
		for (int i = 0; i < blockNL.getLength(); i++) {
			
			blockID = blockNL.item(i).getAttributes().getNamedItem("id").getNodeValue();
						
			try {
				if(blockNL.item(i).getNodeName().equals("SuperBlock")) {					
					elementTmp = (Element) docIn.importNode(ParseSuperBlock(blockID), true);		
				} else {
					Translator = TranClass.getClass().getDeclaredMethod(
										blockNL.item(i).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue() + "_tran", String.class);
					Translator.setAccessible(true);
					elementTmp = (Element) Translator.invoke(TranClass, blockID);
				}
				
				try {
					elementTmp.appendChild(docIn.importNode(ParseInDataPort(blockID), true));	
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				try {
					elementTmp.appendChild(docIn.importNode(ParseOutDataPort(blockID), true));
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

				try {
					elementTmp.appendChild(docIn.importNode(ParseInControlPort(blockID), true));	
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				try {
					elementTmp.appendChild(docIn.importNode(ParseOutControlPort(blockID), true));
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				
				try {
					elementTmp.appendChild(docIn.importNode(ParseGeometry(blockID), true));
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				elementOut.appendChild(elementTmp);
				continue;
			} catch (NoSuchMethodException | NullPointerException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

			Translator = TranClass.getClass().getDeclaredMethod("UnSupported", String.class);
			Translator.setAccessible(true);
			elementTmp = (Element) Translator.invoke(TranClass, blockNL.item(i).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue());
			
			//try {
				elementOut.appendChild(elementTmp);

				//} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
		}
		
		return elementOut;
	}
		
	private Element ParseSuperBlock(String blockID) throws XPathExpressionException, ParserConfigurationException, TransformerException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {		
		String parentID = ((NodeList) XPathFactory.newInstance().newXPath()
								.compile("//SuperBlock[@id='"
											+ blockID 
											+ "']/SuperBlockDiagram"
											+ "/mxCell[@as='defaultParent']")
								.evaluate(docIn, XPathConstants.NODESET))
								.item(0).getAttributes().getNamedItem("id").getNodeValue();
		
		Element elementOut = docIn.createElement("SystemBlock");
		elementOut.setAttribute("type", "SubSystem");
		elementOut.setAttribute("name", "NoName");
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("directFeedThrough", "true");
		
		elementOut.appendChild(docIn.importNode(ParseBlocks(parentID), true));
		elementOut.appendChild(docIn.importNode(ParseSignals(parentID), true));
		return elementOut;
	}
	
	private Element ParseInDataPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		Element elementOut = null;
		NodeList PortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//ExplicitInputPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		if(PortNL.getLength() > 0) {
			elementOut = docIn.createElement("inDataPorts");
			elementOut.setAttribute("type", "gaxml:collection");
		}
		
		for (int i = 0; i < PortNL.getLength(); i++) {			
			Element elementTemp = docIn.createElement("InDataPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			elementTemp.setAttribute("portNumber", PortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);
			
			ArrayList<Object> PortAssc = new ArrayList<Object>(); 
			PortAssc.add(PortNL.item(i).getAttributes().getNamedItem("id").getNodeValue());
			PortAssc.add(idCnt);
			PortAsscs.add(PortAssc);
		}

		return elementOut;
	}
	
	private Element ParseOutDataPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		Element elementOut = null;
		NodeList PortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//ExplicitOutputPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		if(PortNL.getLength() > 0) {		
			elementOut = docIn.createElement("outDataPorts");
			elementOut.setAttribute("type", "gaxml:collection");
		}
		
		for (int i = 0; i < PortNL.getLength(); i++) {			
			Element elementTemp = docIn.createElement("OutDataPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			elementTemp.setAttribute("portNumber", PortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);
			
			ArrayList<Object> PortAssc = new ArrayList<Object>(); 
			PortAssc.add(PortNL.item(i).getAttributes().getNamedItem("id").getNodeValue());
			PortAssc.add(idCnt);
			PortAsscs.add(PortAssc);
		}

		return elementOut;
	}
	
	private Element ParseInControlPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		Element elementOut = null;
		NodeList PortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//ControlPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		if(PortNL.getLength() > 0) {
			elementOut = docIn.createElement("inControlPorts");
			elementOut.setAttribute("type", "gaxml:collection");
		}
		
		for (int i = 0; i < PortNL.getLength(); i++) {			
			Element elementTemp = docIn.createElement("InControlPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			//elementTemp.setAttribute("portNumber", outPortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementTemp.setAttribute("relatedToInportBlock", "false");
			elementTemp.setAttribute("resetStates", "false");
			elementTemp.setAttribute("periodicSampleTime", "false");
			elementOut.appendChild(elementTemp);
			
			ArrayList<Object> PortAssc = new ArrayList<Object>(); 
			PortAssc.add(PortNL.item(i).getAttributes().getNamedItem("id").getNodeValue());
			PortAssc.add(idCnt);
			PortAsscs.add(PortAssc);
		}

		return elementOut;
	}
	
	private Element ParseOutControlPort(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		Element elementOut = null;
		NodeList PortNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//CommandPort[@parent='" + blockID + "']")
											.evaluate(docIn, XPathConstants.NODESET);
		if(PortNL.getLength() > 0) {
			elementOut = docIn.createElement("outControlPorts");
			elementOut.setAttribute("type", "gaxml:collection");
		}
		
		for (int i = 0; i < PortNL.getLength(); i++) {			
			Element elementTemp = docIn.createElement("OutControlPort");
			elementTemp.setAttribute("id", Integer.toString(++idCnt));
			elementTemp.setAttribute("portNumber", PortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);
			
			ArrayList<Object> PortAssc = new ArrayList<Object>(); 
			PortAssc.add(PortNL.item(i).getAttributes().getNamedItem("id").getNodeValue());
			PortAssc.add(idCnt);
			PortAsscs.add(PortAssc);
		}

		return elementOut;
	}
	
	private Element ParseSignals(String parentID) throws XPathExpressionException{
		Element elementOut = null;
		elementOut = docIn.createElement("signals");
		elementOut.setAttribute("type", "gaxml:collection");
		Element elementTemp = null;
		Element elementTemp0 = null;
		NodeList inportNL = null;
		String outportID = null;
		String inportID = null;


		NodeList blockNL = (NodeList) XPathFactory.newInstance().newXPath()
										.compile("//*[@parent='" + parentID + "']"
													+ "[string-length(@interfaceFunctionName)!=0 "
													+ "or name()='SuperBlock']")
										.evaluate(docIn, XPathConstants.NODESET);
				
		for (int i = 0; i < blockNL.getLength(); i++) {
			inportNL = (NodeList) XPathFactory.newInstance().newXPath()
									.compile("//*[@parent='" + blockNL.item(i).getAttributes().getNamedItem("id").getNodeValue() + "']"
												+ "[local-name()='ExplicitInputPort' or local-name()='ControlPort']")
									.evaluate(docIn, XPathConstants.NODESET);
			
			if(inportNL.getLength() == 0) {continue;}

			for (int ii = 0; ii < inportNL.getLength(); ii++) {
				inportID = inportNL.item(ii).getAttributes().getNamedItem("id").getNodeValue();
				outportID = ParseSignal(inportID);
				
				elementTemp = docIn.createElement("Signal");
				elementTemp.setAttribute("id", Integer.toString(++idCnt));
				for(ArrayList<Object> portPortAssc : PortAsscs){
					
					if(portPortAssc.get(0).equals(outportID)) {
						elementTemp0 = docIn.createElement("srcPort");
						elementTemp0.setAttribute("type", "gaxml:pointer");
						elementTemp0.setTextContent(Integer.toString((int) portPortAssc.get(1)));			
						elementTemp.appendChild(elementTemp0);
					}

					if(portPortAssc.get(0).equals(inportID)) {
						elementTemp0 = docIn.createElement("dstPort");
						elementTemp0.setAttribute("type", "gaxml:pointer");
						elementTemp0.setTextContent(Integer.toString((int) portPortAssc.get(1)));
						elementTemp.appendChild(elementTemp0);
					}
					elementOut.appendChild(elementTemp);
				}
				/*
				NodeList outblockNL = (NodeList) XPathFactory.newInstance().newXPath().compile("//*[@id='" + outportID + "']")
																						.evaluate(docIn, XPathConstants.NODESET);
				System.out.println("Out Block: " + outblockNL.item(0).getNodeName());
				NodeList outparentNL = (NodeList) XPathFactory.newInstance().newXPath()
															.compile("//*[@id='" + outblockNL.item(0).getAttributes().getNamedItem("parent").getNodeValue() + "']")
															.evaluate(docIn, XPathConstants.NODESET);				
				System.out.println("Out Parent: " + outparentNL.item(0).getNodeName());
				*/
			}
		}
		
		return elementOut;
	}
	
	private String ParseSignal(String inportID) throws XPathExpressionException{
		String outportID = null;

		NodeList signalNL = (NodeList) XPathFactory.newInstance().newXPath()
								.compile("//*[@target='" + inportID + "']"
											+ "[local-name()='CommandControlLink' or local-name()='ExplicitLink']")
								.evaluate(docIn, XPathConstants.NODESET);
		
		NodeList outportNL = (NodeList) XPathFactory.newInstance().newXPath()
								.compile("//*[@id='" + signalNL.item(0).getAttributes().getNamedItem("source").getNodeValue() + "']"
											+ "[local-name()='ExplicitOutputPort' or local-name()='CommandPort']")
								.evaluate(docIn, XPathConstants.NODESET);
		
		NodeList outparentNL = (NodeList) XPathFactory.newInstance().newXPath()
								.compile("//*[@id='" + outportNL.item(0).getAttributes().getNamedItem("parent").getNodeValue() + "']")
								.evaluate(docIn, XPathConstants.NODESET);
		
		
		if("SplitBlock".equals(outparentNL.item(0).getNodeName())) {
			NodeList inportNL = (NodeList) XPathFactory.newInstance().newXPath()
											.compile("//*[@parent='" + outparentNL.item(0).getAttributes().getNamedItem("id").getNodeValue() + "']"
														+ "[local-name()='ExplicitInputPort' or local-name()='ControlPort']")
											.evaluate(docIn, XPathConstants.NODESET);
			
			outportID = ParseSignal(inportNL.item(0).getAttributes().getNamedItem("id").getNodeValue());
		} else {
			outportID = outportNL.item(0).getAttributes().getNamedItem("id").getNodeValue();
		}
		
		return outportID;
	}
		
	private Element ParseGeometry(String blockID) throws ParserConfigurationException, DOMException, XPathExpressionException {
		
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
