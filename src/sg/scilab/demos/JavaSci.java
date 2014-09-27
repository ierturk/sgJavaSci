/**
  ******************************************************************************
  * @file    Javasci.java
  * @author  ierturk @ StarGateInc <ierturk@ieee.org>
  * @version V0.0.0
  * @date    06-Sep-2014
  * @brief   Demo JavaSci Java/Scilab Binding
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

/**
 * Copyright (C) 2010 - DIGITEO - Sylvestre Ledru
 *
 * This file is released under the 3-clause BSD license. See COPYING-BSD.
 *
 */

package sg.scilab.demos;
  
import org.scilab.modules.javasci.Scilab;
import org.scilab.modules.types.ScilabType;
import org.scilab.modules.types.ScilabDouble;

/**
 * @author ierturk @ StarGateInc <ierturk@ieee.org>
 *
 */
public class JavaSci {

	/**
	 * @param args
	 */
	public void demoJavaSci() {
		// TODO Auto-generated method stub
        try {

            Scilab sci = new Scilab();

            if (sci.open()) {
                /* Send a Scilab instruction */
                sci.exec("foo = [ 2, 4, 6; 4, 0, 10; 6, 10, 12 ];");

                /* Retrieve the variable foo */
                ScilabType foo = sci.get("foo");

                /* Display the variable */
                System.out.println("Representation of  : " + foo);

                /* Get the data and retrieve the 2,2 value */
                double[][] aReal = ((ScilabDouble)foo).getRealPart();
                System.out.println("foo[1,1] = " + aReal[1][1]);

                /* Change the value of 2,2 */
                aReal[1][1] = Math.PI;

                /* Create a new variable */
                ScilabDouble bar = new ScilabDouble(aReal);

                /* Send it to Scilab */
                sci.put("bar", bar);

                /* Display it through Scilab */
                sci.exec("disp(bar)");

                sci.close();
            } else {
                System.out.println("Could not start Scilab ");
            }


            /* Can be improved by other exceptions: AlreadyRunningException,
             * InitializationException, UndefinedVariableException,
             * UnknownTypeException, etc
             */
        } catch (org.scilab.modules.javasci.JavasciException e) {
            System.err.println("An exception occurred: " + e.getLocalizedMessage());
        }

	}

}

/*** COPYRIGHT 2014 StarGate Inc <http://www.stargate-tr.com> *****END OF FILE****/
