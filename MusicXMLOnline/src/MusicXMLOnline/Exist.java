package MusicXMLOnline;

/**
 * Created by bouke on 26/05/2014.
 */

import org.exist.xmldb.EXistResource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Exist {

    private static String URI = "xmldb:exist://localhost:8899/exist/xmlrpc/db/musicxmlonline";
    private static String user = "admin";
    private static String password = "admin";

    /**ÐÐÐÐÐ
     * @param xQuery Should be the XQuery to execute
     */
    public List<String> query(String xQuery) throws Exception {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection col = null;
        try {
            col = DatabaseManager.getCollection(URI, user, password);
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query(xQuery);
            ResourceIterator i = result.getIterator();
            Resource res = null;
            List<String> returnValue = new ArrayList<String>();
            while (i.hasMoreResources()) {
                try {
                    res = i.nextResource();
                    returnValue.add((String) res.getContent());
                } finally {
                    //dont forget to cleanup resources
                    try {
                        ((EXistResource) res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }

            return returnValue;

        } finally {
            //dont forget to cleanup
            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        }
    }

    /**
     * args[0] Should be the path of the file to read and store in the collection
     * args[1] Should be the name of the file (excluding path)
     */
    public String addDocument(String args[]) throws Exception {

        if (args.length < 2) {
            return "usage: StoreExample collection-path document";
        }

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
        Collection col = null;
        XMLResource res = null;
        try {
            col = DatabaseManager.getCollection(URI, user, password);
            if(col == null){
                return "unable to load connection: " + URI;
            }
            // create new XMLResource; an id will be assigned to the new resource
            res = (XMLResource) col.createResource(args[1], "XMLResource");
            File f = new File(args[0]);
            if (!f.canRead()) {
                return "cannot read file " + args[0];
            }

            res.setContent(f);
            //System.out.print("storing document " + res.getId() + "...");
            col.storeResource(res);
            return "Stored document with id " + res.getId();
        } finally {
            //dont forget to cleanup
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }

            if (col != null) {
                try {
                    col.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        }
    }
}
