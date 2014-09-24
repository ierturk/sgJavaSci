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
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	protected static ArrayList<ArrayList<Object>> PortAsscs = new ArrayList<ArrayList<Object>>();
;
	
	public Helpers(Document In) {
		// TODO Auto-generated constructor stub
		docIn = In;
	}
	
	public Element ParseXcosDiagram() throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ParserConfigurationException, TransformerException {
		
		String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
		Element elementOut = docIn.createElement("GASystemModel");
		elementOut.setAttribute("type", "gaxml:model");
		elementOut.setAttribute("modelVersion", "6.3");
		elementOut.setAttribute("modelType", "GASystemModel");
		elementOut.setAttribute("modelName", docIn.getFirstChild().getAttributes().getNamedItem("title").getNodeValue());
		elementOut.setAttribute("lastSavedOn", timestamp);
		elementOut.setAttribute("lastSavedBy", "sgJavaSci-0.1");
		elementOut.setAttribute("lastSavedBy", "sgJavaSci-0.1");
		elementOut.setAttribute("xmlns:gaxml", "http://www.geneauto.org/GAXML");
		elementOut.setAttribute("xmlns:gadt", "http://www.geneauto.org/GADataType");
		elementOut.setAttribute("xmlns", "http://www.geneauto.org/GASystemModel");
		
		Element elementTmp = docIn.createElement("TransformationHistory");
		elementTmp.setAttribute("type", "gaxml:history");

		Element elementTmp0 = docIn.createElement("Transformation");
		elementTmp0.setAttribute("writeTime", timestamp);
		elementTmp0.setAttribute("toolName", "sgJavaSci");
		elementTmp0.setAttribute("readTime", timestamp);
		elementTmp.appendChild(elementTmp0);
		elementOut.appendChild(elementTmp);
		
		String parentID = ((NodeList) XPathFactory.newInstance().newXPath()
												.compile("/node()/mxCell[@as='defaultParent']")
												.evaluate(docIn, XPathConstants.NODESET))
												.item(0).getAttributes().getNamedItem("id").getNodeValue();
		elementOut.appendChild(docIn.importNode(ParseSuperBlock(parentID), true));
		
		return elementOut;
	}

	private Element ParseSuperBlock(String parentID) throws XPathExpressionException, ParserConfigurationException, TransformerException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {		
		Element elementOut = docIn.createElement("SystemBlock");
		elementOut.setAttribute("type", "SubSystem");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		
		try {
			NodeList parentNL = (NodeList) XPathFactory.newInstance().newXPath()
																.compile("//*[./mxCell[@as='defaultParent' and @id='"+ parentID + "']]")
																.evaluate(docIn, XPathConstants.NODESET);
			//System.out.println("\tParent type: "+ parentNL.item(0).getNodeName());

			if("XcosDiagram".equals(parentNL.item(0).getNodeName()))
				elementOut.setAttribute("name", docIn.getFirstChild().getAttributes().getNamedItem("title").getNodeValue());
			else
				elementOut.setAttribute("name", getBlockName(parentNL.item(0).getParentNode().getAttributes().getNamedItem("id").getNodeValue()));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			elementOut.setAttribute("name", "UnTitled");
		}
		
		elementOut.setAttribute("isVirtual", "false");
		elementOut.setAttribute("directFeedThrough", "true");
		
		elementOut.appendChild(docIn.importNode(ParseBlocks(parentID), true));
		elementOut.appendChild(docIn.importNode(ParseSignals(parentID), true));
		return elementOut;
	}
	
	private Element ParseBlocks(String parentID) throws XPathExpressionException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DOMException, ParserConfigurationException, InstantiationException, TransformerException{
		NodeList blockNL = (NodeList) XPathFactory.newInstance().newXPath()
												.compile("//*[@parent='" + parentID + "']")
												.evaluate(docIn, XPathConstants.NODESET);
		
		Element elementOut = docIn.createElement("blocks");
		elementOut.setAttribute("type", "gaxml:collection");
		
		Method Translator;
		XcosBlockTran TranClass = new XcosBlockTran(docIn);
		String blockID;
		Element elementTmp = null;
		
		for (int i = 0; i < blockNL.getLength(); i++) {
			
			blockID = blockNL.item(i).getAttributes().getNamedItem("id").getNodeValue();
						
			try {
				
				switch(blockNL.item(i).getNodeName()) {
				case "SuperBlock":
					elementTmp = (Element) docIn.importNode(ParseSuperBlock(((NodeList) XPathFactory.newInstance().newXPath()
																									.compile("//SuperBlock[@id='"
																												+ blockID 
																												+ "']/SuperBlockDiagram"
																												+ "/mxCell[@as='defaultParent']")
																									.evaluate(docIn, XPathConstants.NODESET))
																									.item(0).getAttributes().getNamedItem("id").getNodeValue()),
																									true);
					ArrayList<ArrayList<Object>> ParamArray = new ArrayList<ArrayList<Object>>();
					ArrayList<Object> ParamList = new ArrayList<Object>();
					
					ParamList.add("RTWSystemCode");
					ParamList.add("String");
					ParamList.add("auto");
					ParamArray.add(ParamList);
					
					ParamList = new ArrayList<Object>();
					ParamList.add("AtomicSubSystem");
					ParamList.add("String");
					ParamList.add("off");
					ParamArray.add(ParamList);		
					elementTmp.appendChild(docIn.importNode(ParseParameters(ParamArray), true));
					break;

				case "EventInBlock":
					Translator = TranClass.getClass().getDeclaredMethod("ActionPort_tran", String.class);
					Translator.setAccessible(true);
					elementTmp = (Element) Translator.invoke(TranClass, blockID);
					//elementTmp.appendChild(docIn.importNode(ParseGeometry(blockID), true));
					elementOut.appendChild(elementTmp);
					continue;
	
				case "ExplicitOutBlock":
					Translator = TranClass.getClass().getDeclaredMethod("OutDataPort_tran", String.class);
					Translator.setAccessible(true);
					elementTmp = (Element) Translator.invoke(TranClass, blockID);
					break;
					
				case "BasicBlock":
				case "Summation":
					Translator = TranClass.getClass().getDeclaredMethod(
										blockNL.item(i).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue() + "_tran", String.class);
					Translator.setAccessible(true);
					elementTmp = (Element) Translator.invoke(TranClass, blockID);
					break;
					
				default:
					Translator = TranClass.getClass().getDeclaredMethod("UnSupported", String.class);
					Translator.setAccessible(true);
					Translator.invoke(TranClass, blockNL.item(i).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue());
					continue;
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
					if(blockNL.item(i).getNodeName() == "SuperBlock")
						elementTmp.appendChild(docIn.importNode(ParseInControlPort(blockID), true));
					//System.out.println("\t" + blockNL.item(i).getNodeName());
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

				try {
						elementTmp.appendChild(docIn.importNode(ParseOutControlPort(blockID), true));
						//System.out.println("\t" + blockNL.item(i).getAttributes().getNamedItem("interfaceFunctionName").getNodeValue());
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
		}
		
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
			elementTemp.setAttribute("relatedToInportBlock", "true");
			elementTemp.setAttribute("portNumber", PortNL.item(i).getAttributes().getNamedItem("ordering").getNodeValue());
			elementOut.appendChild(elementTemp);
			
			ArrayList<Object> PortAssc = new ArrayList<Object>();
			PortAssc.add(PortNL.item(i).getAttributes().getNamedItem("id").getNodeValue());
			PortAssc.add(idCnt);
			PortAssc.add("InData");
			PortAssc.add(((NodeList) XPathFactory.newInstance().newXPath()
													.compile("//*[@id='" + blockID + "']")
													.evaluate(docIn, XPathConstants.NODESET))
													.item(0).getAttributes().getNamedItem("parent").getNodeValue());		
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
			PortAssc.add("OutData");
			PortAssc.add(((NodeList) XPathFactory.newInstance().newXPath()
					.compile("//*[@id='" + blockID + "']")
					.evaluate(docIn, XPathConstants.NODESET))
					.item(0).getAttributes().getNamedItem("parent").getNodeValue());
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
			PortAssc.add("InControl");
			PortAssc.add(((NodeList) XPathFactory.newInstance().newXPath()
					.compile("//*[@id='" + blockID + "']")
					.evaluate(docIn, XPathConstants.NODESET))
					.item(0).getAttributes().getNamedItem("parent").getNodeValue());
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
			PortAssc.add("OutControl");
			PortAssc.add(((NodeList) XPathFactory.newInstance().newXPath()
					.compile("//*[@id='" + blockID + "']")
					.evaluate(docIn, XPathConstants.NODESET))
					.item(0).getAttributes().getNamedItem("parent").getNodeValue());
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
		
		for(ArrayList<Object> portIn : PortAsscs){
			if((("InData".equals(portIn.get(2))) | ("InControl".equals(portIn.get(2))))
					& parentID.equals(portIn.get(3))) {

				String outportID = ParseSignal((String) portIn.get(0));
				for(ArrayList<Object> portOut : PortAsscs){
					if(outportID.equals((String) portOut.get(0))) {
						elementTemp = docIn.createElement("Signal");
						elementTemp.setAttribute("id", Integer.toString(++idCnt));
						elementTemp0 = docIn.createElement("dstPort");
						elementTemp0.setAttribute("type", "gaxml:pointer");
						elementTemp0.setTextContent(Integer.toString((int) portIn.get(1)));
						elementTemp.appendChild(elementTemp0);
						elementTemp0 = docIn.createElement("srcPort");
						elementTemp0.setAttribute("type", "gaxml:pointer");
						elementTemp0.setTextContent(Integer.toString((int) portOut.get(1)));			
						elementTemp.appendChild(elementTemp0);
						elementOut.appendChild(elementTemp);
					}
				}
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
								.compile("//*[@id='" + blockID + "']/mxGeometry")
								.evaluate(docIn, XPathConstants.NODESET)).item(0).getAttributes();

		Element elementOut = docIn.createElement("diagramInfo");
		elementOut.setAttribute("type", "gaxml:object");
		
		Element elementTemp = docIn.createElement("DiagramInfo");
		elementTemp.setAttribute("sizeX", geoNM.getNamedItem("width").getNodeValue());
		elementTemp.setAttribute("sizeY", geoNM.getNamedItem("height").getNodeValue());
		elementTemp.setAttribute("positionX", geoNM.getNamedItem("x").getNodeValue());
		elementTemp.setAttribute("positionY", geoNM.getNamedItem("y").getNodeValue());
		elementOut.appendChild(elementTemp);		

		return elementOut;
	}
	
	protected String getBlockName(String blockID) throws XPathExpressionException {
		try {
			String returnLabel = ((NodeList) XPathFactory.newInstance().newXPath()
																.compile("//*[@id='" + blockID + "#identifier']")
																.evaluate(docIn, XPathConstants.NODESET)).item(0)
																.getAttributes().getNamedItem("value").getNodeValue();
			if(returnLabel.equals("")) return "Block" + Integer.toString(idCnt);
			return returnLabel;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Block" + Integer.toString(idCnt);
		}
	}
	
	protected Element ParseParameters(ArrayList<ArrayList<Object>> paramArray) throws NoSuchMethodException, SecurityException, DOMException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Element elementOut = docIn.createElement("parameters");
		elementOut.setAttribute("type", "gaxml:collection");
		Element elementTmp = null;
		
		Helpers HelpClass = new Helpers(docIn);
	    for(ArrayList<Object> pList : paramArray){
	    	//System.out.println((String) pList.get(0));
	    	
			elementTmp = docIn.createElement("BlockParameter");
			elementTmp.setAttribute("name", (String) pList.get(0));
			elementTmp.setAttribute("id", Integer.toString(++idCnt));
			elementOut.appendChild(elementTmp);
			
			elementTmp = docIn.createElement("value");
			elementTmp.setAttribute("type", "gaxml:object");
			elementOut.getLastChild().appendChild(elementTmp);
	    	
			Method paramParser = HelpClass.getClass().getDeclaredMethod( "Parse" + ((String) pList.get(1)) + "Param", ArrayList.class);
			paramParser.setAccessible(true);
			elementOut.getLastChild().getFirstChild().appendChild(docIn.importNode((Element) paramParser.invoke(HelpClass, pList), true));
	    }

		return elementOut;
	}
	
	protected Element ParseExpressionParam(ArrayList<Object> paramList) throws NoSuchMethodException, SecurityException, DOMException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Helpers HelpClass = new Helpers(docIn);
		Element elementOut = docIn.createElement("ExpressionValue");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		
		Element elementTemp = docIn.createElement("value");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);
		
    	//System.out.println((String) paramList.get(2));
		Method paramParser = HelpClass.getClass().getDeclaredMethod( "Parse" + ((String) paramList.get(2)) + "Param", String.class);
		paramParser.setAccessible(true);
		elementOut.getFirstChild().appendChild(docIn.importNode((Element) paramParser.invoke(HelpClass, paramList.get(3)), true));
		
		return elementOut;
	}
	
	protected Element ParseRealExpParam(String pvalue) {
		
		Double value = Double.parseDouble((String) pvalue);
		Element elementOut = docIn.createElement("RealExpression");
		elementOut.setAttribute("id", Integer.toString(++idCnt));	

		elementOut.setAttribute("litValue", Integer.toString(value.intValue()));
		elementOut.setAttribute("integerPart", Integer.toString(value.intValue()));
		elementOut.setAttribute("scientificValue", "false");
		elementOut.setAttribute("fractionalPart", Integer.toString((int) (Math.abs(value - value.intValue()))));
		elementOut.setAttribute("exponent", Integer.toString(0));	
		
		Element elementTemp = docIn.createElement("dataType");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);
		
		elementTemp = docIn.createElement("TRealDouble");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementOut.getFirstChild().appendChild(elementTemp);

		return elementOut;
	}
	
	protected Element ParseIntegerExpParam(String pvalue) {
		
		Pattern pattern = Pattern.compile(">");
		String[] split = pattern.split(pvalue);
		
		Integer value = Integer.parseInt(split[0]);
		
		Element elementOut = docIn.createElement("IntegerExpression");
		elementOut.setAttribute("id", Integer.toString(++idCnt));	

		elementOut.setAttribute("litValue", split[0]);
		elementOut.setAttribute("integerPart", Integer.toString(Math.abs(value)));
		if(value>=0) elementOut.setAttribute("isNegative", "false");
		else elementOut.setAttribute("isNegative", "true");
		elementOut.setAttribute("hexValue", "false");
		
		Element elementTemp = docIn.createElement("dataType");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);
		
		elementTemp = docIn.createElement("TRealInteger");
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementTemp.setAttribute("signed", "true");
		elementTemp.setAttribute("nBits", split[1]);
		elementOut.getFirstChild().appendChild(elementTemp);

		return elementOut;
	}
	
	protected Element ParseBinaryExpParam(String pvalue) {
		
		Element elementOut = docIn.createElement("BinaryExpression");
		elementOut.setAttribute("id", Integer.toString(++idCnt));

		Pattern pattern = Pattern.compile(">");
		String[] split = pattern.split(pvalue);
		
		Element elementTemp = docIn.createElement("leftArgument");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);	
		elementTemp = docIn.createElement("VariableExpression");
		elementTemp.setAttribute("name", split[0]);
		elementTemp.setAttribute("id", Integer.toString(++idCnt));
		elementOut.getLastChild().appendChild(elementTemp);
		
		elementTemp = docIn.createElement("rightArgument");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);	
		elementOut.getLastChild().appendChild(docIn.importNode(ParseRealExpParam(split[1]), true));

		elementTemp = docIn.createElement("operator");
		elementTemp.setAttribute("type", "gaxml:object");
		elementOut.appendChild(elementTemp);	
		elementTemp = docIn.createElement("BinaryOperator");
		elementTemp.setAttribute("type", "gaxml:enum");
		elementTemp.setAttribute("name", "GT_OPERATOR");
		elementTemp.setAttribute("precedence", "10");
		elementTemp.setAttribute("ordinal", "14");
		elementOut.getLastChild().appendChild(elementTemp);

		return elementOut;
	}

	
	protected Element ParseStringParam(ArrayList<Object> paramList) {		
		Element elementOut = docIn.createElement("StringValue");
		elementOut.setAttribute("id", Integer.toString(++idCnt));
		elementOut.setAttribute("value", (String) paramList.get(2));
		
		return elementOut;
	}

}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
