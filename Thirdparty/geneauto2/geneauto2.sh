#!/bin/sh

SCRIPT=`readlink -f $0`
export GENEAUTO_HOME=`dirname $SCRIPT`"/"

java -Xmx1024m -cp "$GENEAUTO_HOME/geneauto.galauncher-2.4.10.jar" geneauto.launcher.GALauncher $1 $2 $3 $4 $5 $6 $7 $8 $9
