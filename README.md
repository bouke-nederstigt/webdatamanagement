webdatamanagement
=================

This repository contains the answers to chapter 5 of the webdatamanagement book. Each folder contains the answers to a certain exercise or the final report.

**Installation instructions MovieLookup & Shakespear**

The folders MovieLookup and Shakespear contain the sourcecode of the eXist XQuery applications. Besides this a xar file is also included. The easiest way to install the applications on eXist database is through the package manager on the eXist dashboard. Just click add a package and upload the application. After this the application can be launched from the eXist dashboard. *The applications were tested and working on eXist version 2.1*

**Installing the MusicXMLOnline application**

Installation of the MusicXMLOnline probably requires a few extra steps, which are described below. The application was tested on Apache Tomcat 8.0. 

**Option 1**
1.  Create a musicxmlcollection in the root directory of eXist
2. Edit the eXist uri and credentials at the top of Exist.java
3. Compile source code & deploy to Tomcat server
4. Make sure a folder "upload" is created in the webroot of the application, and that files can be written to it.
5. Make sure lilypond is installed and the commands musicxml2ly and lilypond can be called from the command line (add Lilypond to the $PATH)
6. Have fun checking out the application!

**Option 2**
Skip step 1-3 and deploy the .war file from the MusicXMLOnline directly to Tomcat. However this will only work if eXist is running on port 8899 and can be accessed with user "admin" password "admin"

**Option 3**
Use the .iml file from MusicXMLOnline folder to import all settings and run configurations into Intellij. That way the application can be loaded directly from Intellij, skipping the need for manual deployment. 
