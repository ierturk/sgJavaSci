SET GENEAUTO_HOME=%~dp0/%
java -Xmx1024m -cp "%~dp0geneauto.GALauncher-2.4.10.jar" geneauto.launcher.GALauncherOpt %1 %2 %3 %4 %5 %6 %7 %8 %9
pause
