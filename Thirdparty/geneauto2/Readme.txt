
*******************************************************************************

                            Gene-Auto Readme
                            
*******************************************************************************

			http://www.geneauto.org
			http://geneauto.krates.ee

This readme contains following sections:

    * What is Gene-Auto?
    * Who develops it?
    * How is Gene-Auto licensed?
    * How to obtain Gene-Auto
    * How to install Gene-Auto?
    * Contributing to Gene-Auto
    * Where to find more information?


----------------------------------------------------------------------
    What is Gene-Auto?
----------------------------------------------------------------------

Gene-Auto is an open-source toolset for converting Simulink, Stateflow, 
Scicos and Xcos models to executable program code. Output to C code is 
currently available. A prototype exists also for Ada output. Gene-Auto 
has been implemented mostly in Java, but one module has been implemented
and verified with the Coq proof-assistant.

The creation of the toolset was initiated by the needs of the safety critical 
industries (automotive, avionic and space) and thus the development has 
followed the most stringent and common reference in this domain - the 
DO-178B/ED-12B avionic software development guideline. Still, as Gene-Auto 
has grown out of a research project, its maturation and qualification has not 
been fully completed. However, it has been verified in a number of realistic 
industrial case studies with good results. Gene-Auto is also being used as a
code generation backend in the Scilab/Xcos based Equalis Coder and Equalis
Embedded Coder (http://www.equalis.com/?CodeGenerationModule) tools 
developed by the company Equalis (www.equalis.com).

As of current state Gene-Auto active maintenance is limited. A successor 
to the Gene-Auto toolset is developed in the following European projects:
    Project P: http://www.open-do.org/projects/p/
    Hi-MoCo: http://www.eurekanetwork.org/project/-/id/6037
The successor toolset will be largely compatible with Gene-Auto, but it will 
use Ada as the implementation language and is expected to raise the level
of maturity and qualification to the one required by DO-178C/ED-12C in a 
few years.

Gene-Auto has been and is currently also used in several research projects.
There exists a separate toolset Gene-Auto Eclipse tools that adds Eclipse 
Modeling Framework (EMF) compatibility to Gene-Auto and a minimal Eclipse
user interface. You can download these and find more information from the 
websites mentioned below.


----------------------------------------------------------------------
    Who develops it?
----------------------------------------------------------------------

Gene-Auto was originally developed by the Gene-Auto consortium with 
support from ITEA (www.itea2.org) and national public funding bodies 
in France, Estonia, Belgium and Israel.

The members of the original Gene-Auto consortium are
    Continental SAS
    Airbus France SAS
    Thales Alenia Space France SAS
    ASTRIUM SAS
    IB KRATES OU
    Barco NV
    Israel Aircraft Industries
    Alyotech
    Tallinn University of Technology
    FERIA - Institut National Polytechnique de Toulouse
    INRIA - Institut National de Recherche en Informatique et en Automatique

The main developers of the Gene-Auto toolset are IB Krates OU (www.krates.ee),
Alyotech (www.alyotech.fr) and FERIA-INPT (www.enseeiht.fr/~pantel). Gene-
Auto welcomes also external contributions! Recently there have been several 
contributions from Equalis LLC (www.equalis.com), who develops Scilab/Xcos
based modules for numerical computations for engineering and science and who
is using Gene-Auto as a code generation backend.

The copyright of the different components belongs to their respective developers.


----------------------------------------------------------------------
    How is Gene-Auto licensed?
----------------------------------------------------------------------

The Gene-Auto toolset is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License as published 
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but 
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>


----------------------------------------------------------------------
    How to obtain Gene-Auto?
----------------------------------------------------------------------

Gene-Auto is freely downloadable in source and binary forms from:
	www.geneauto.org.

If you plan to work with the sources it will probably be useful to checkout
Gene-Auto directly from the git source repository, which is located at:

	http://geneauto.gforge.enseeiht.fr/geneauto.git

The repository is read-only for anonymous users. If you want to propose 
and submit changes, then please follow the instructions below.

----------------------------------------------------------------------
    How to install Gene-Auto?
----------------------------------------------------------------------

You can find detailed installation instructions from:

    http://www.geneauto.org/?page=documents
    
    D2.55 Gene-Auto Installation and Usage Manual
    
For the binary installation simply download the latest binary 
distribution (geneauto-2.x.x-bin.zip) from:
    
    http://www.geneauto.org/?page=downloads
    
and unzip the file contents to a separate folder (the location is not 
important).

For installing the source release and for other information please consult
the installation manual.

----------------------------------------------------------------------
    Contributing to Gene-Auto
----------------------------------------------------------------------

Gene-Auto welcomes contributions either in the form of feedback on the
forum or as concrete source code improvement proposals. If you want
to submit a source code change, then please make the commits on your
local repository following the following good practices:

	* use the coding style conventions as explained in the user manual
	* include your full name and email address in git config
	* make separate commits for different functional changes
	* add a reasonable commit comment

Generate patches for sending by e-mail. Normally calling:

	git format-patch origin

should produce exactly the patch files that are needed. 

Please package the patch files to a common zip archive to avoid text 
manipulations during sending the e-mail and e-mail them to 

	geneauto@krates.ee 

Please include either in the e-mail or as a separate document an overview 
of the changes that you have made. Also, please write the exact credentials 
of your organisation or individuals that you would like to be accredited for 
the changes.


----------------------------------------------------------------------
    Where to find more information?
----------------------------------------------------------------------

You can find more technical or background information and the latest 
toolset releases on two websites:
                www.geneauto.org and geneauto.krates.ee
                
There is a lot of useful information in the Gene-Auto FAQ:
                 http://geneauto.krates.ee/faq
    
You are also welcome to contact the Gene-Auto team through the web-sites
to share your experience, ask for new features and/or contribute to 
the development. We hope you will find Gene-Auto useful!

                        The Gene-Auto team