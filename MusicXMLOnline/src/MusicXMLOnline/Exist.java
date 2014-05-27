package MusicXMLOnline;

/**
 * Created by bouke on 26/05/2014.
 */

import org.exist.xmldb.EXistResource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Exist {

    private static String URI = "xmldb:exist://localhost:8899/exist/xmlrpc";

    /**
     * args[0] Should be the name of the collection to access
     * args[1] Should be the XQuery to execute
     */
    public List<String> query(String args[]) throws Exception {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection col = null;
        try {
            col = DatabaseManager.getCollection(URI + args[0], "admin", "admin");
            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query(args[1]);
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
     * args[0] Should be the name of the collection to access
     * args[1] Should be the name of the file to read and store in the collection
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
            col = getOrCreateCollection("/" + args[0]);
            if(col == null){
                return "unable to load connection: " + URI + "/" + args[0];
            }
            // create new XMLResource; an id will be assigned to the new resource
            res = (XMLResource) col.createResource(args[2], "XMLResource");
            File f = new File(args[1]);
            if (!f.canRead()) {
                return "cannot read file " + args[1];
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

    private static Collection getOrCreateCollection(String collectionUri) throws XMLDBException {
        return getOrCreateCollection(collectionUri, 0);
    }

    private static Collection getOrCreateCollection(String collectionUri, int  pathSegmentOffset) throws XMLDBException {

        Collection col = DatabaseManager.getCollection(URI + collectionUri, "admin", "admin");
        if(col == null) {
            if(collectionUri.startsWith("/")) {
                collectionUri = collectionUri.substring(1);
            }
            String pathSegments[] = collectionUri.split("/");
            if(pathSegments.length > 0) {
                StringBuilder path = new StringBuilder();
                for(int i = 0; i <= pathSegmentOffset; i++) {
                    path.append("/" + pathSegments[i]);
                }
                Collection start = DatabaseManager.getCollection(URI + path, "admin", "admin");
                if(start == null) {
                    //collection does not exist, so create
                    String parentPath = path.substring(0, path.lastIndexOf("/"
                    ));
                    Collection parent = DatabaseManager.getCollection(URI + parentPath, "admin", "admin");
                    CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
                    col = mgt.createCollection(pathSegments[pathSegmentOffset]);
                    col.close();
                    parent.close();
                } else {
                    start.close();
                }
            }
            return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
        } else {
            return col;
        }
    }
}
