import java.io.*;
import java.util.*;

/**
 * Created by ane on 6/18/14.
 */

/**
 * Simple edge class with source and target vertices (can be in any order)
 */
class Edge implements Comparable {
    public long sourceVertex;
    public long targetVertex;

    public Edge(long v1, long v2) {
        this.sourceVertex = v1;
        this.targetVertex = v2;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge))
            return false;
        Edge w = (Edge) o;
        return (sourceVertex == w.sourceVertex) &&
                (targetVertex == w.targetVertex);
    }

    @Override
    public int compareTo(Object o) {
        Edge w = (Edge) o;
        if (sourceVertex != w.sourceVertex)
            return new Long(sourceVertex).compareTo(w.sourceVertex);
        return new Long(targetVertex).compareTo(w.targetVertex);
    }

    @Override
    public String toString() {
        return "(" + sourceVertex + "," + targetVertex + ")";
    }
}

/**
 * Tests the two triangle counting methods (Multi-way Join and Giraph) against the trivial counter
 */
public class TriangleCountingTester {

    /**
     * Generates a random graph
     * @param noVertices the number of vertices for the desired graph
     * @param density the density between 0 (no edges) and 1 (maximum number of edges)
     * @return map containing the set of adjacent vertices to each vertex (symmetric)
     */
    public static Map<Long, TreeSet<Long>> generateGraph(long noVertices, double density) {
        Map<Long, TreeSet<Long>> result = new TreeMap<Long, TreeSet<Long>>();
        for (long i = 0; i < noVertices; i++) {
            result.put(i, new TreeSet<Long>());
        }
        Random rand = new Random(System.currentTimeMillis());
        double d = 0;
        int m = 0;
        double ratio = density * (noVertices - 1) / 2;
        while (d < ratio) {
            long source = Math.abs(rand.nextLong()) % noVertices;
            long target = Math.abs(rand.nextLong()) % noVertices;
            while (target == source || result.get(source).contains(target)) {
                target = Math.abs(rand.nextLong()) % noVertices;
                source = Math.abs(rand.nextLong()) % noVertices;
            }
            result.get(source).add(target);
            result.get(target).add(source);
            m++;
            d = m / noVertices;
        }
        return result;
    }

    /**
     * Counts triangles according the trivial algorithm (6 x triangles)
     * @param edges list of Edge objects of the graph
     * @return the number of triangles
     */
    public static long countTriangles(List<Edge> edges) {
        long count = 0;
        for (Edge e1 : edges) {
            for (Edge e2 : edges) {
                if (e1.targetVertex == e2.sourceVertex)
                    if (edges.contains(new Edge(e1.sourceVertex, e2.targetVertex))) {
                        count++;
                    }
            }
        }
        return count / 6;
    }

    /**
     * Writes a generated graph to a given file
     * @param graph the graph as a map of adjacent vertices
     * @param filename the name of the file
     * @throws IOException
     */
    public static void writeGraphToFile(Map<Long, TreeSet<Long>> graph, String filename) throws IOException {
        File out = new File(filename);
        System.out.println(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        for (Long source : graph.keySet()) {
            Set<Long> neighbors = graph.get(source);
            writer.write("" + source);
            for (Long target : neighbors) {
                writer.write(" " + target);
            }
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    /**
     * Reads a graph from a given file (must be in adjacency list format)
     * @param filename the name of the file
     * @return the list with the graph's edges (edges occur twice)
     * @throws IOException
     */
    public static List<Edge> readGraphFromFile(String filename) throws IOException {
        List<Edge> edges = new ArrayList<Edge>();
        File in = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            Scanner scanner = new Scanner(line);
            scanner.useDelimiter((" "));
            long v1 = scanner.nextLong(), v2;
            while (scanner.hasNextLong()) {
                v2 = scanner.nextLong();
                Edge e = new Edge(v1, v2);
                edges.add(e);
            }
        }
        reader.close();
        return edges;
    }

    /**
     * Reads the output of the last Hadoop process (under /output) and sums up the triangles found
     * @return the number of triangles counted by the process
     * @throws IOException
     */
    public static long readHadoopOutput() throws IOException {
        Process p = Runtime.getRuntime().exec("hadoop fs -cat /output/p*");
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;

        long count = 0;

        while ((line = reader.readLine()) != null) {
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNextLong())
                return -1;
            scanner.nextLong();
            if (!scanner.hasNextLong())
                return -1;
            count += scanner.nextLong();
        }
        return count;
    }

    /**
     * Tests the two ways of counting the triangles of a graph (Multi-way Join and Giraph) and compares them to the
     * trivial method; prints "[method] FAILED!" if the process's count and the trivial count differ
     * @param baseDir the path to the base test directory
     * @param graphName the name of the file containing the test graph
     * @throws IOException
     * @throws InterruptedException
     */
    public static void testGraph(String baseDir, String graphName) throws IOException, InterruptedException {
        String filename = baseDir + "input/" + graphName;
        List<Edge> edges = readGraphFromFile(filename);
        System.out.println("----TEST " + filename);
        long count1 = countTriangles(edges), count2, count3;
        System.out.println("Expected: " + count1);
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("hadoop fs -rmr /input");
        p.waitFor();
        p = r.exec(" hadoop fs -put " + filename + " /input/" + graphName);
        p.waitFor();
        p = r.exec("hadoop fs -rmr /output");
        p.waitFor();
        p = r.exec("hadoop jar " + baseDir + "WDMHadoop.jar com.hadoop.webdatamanagement.TriangleCounterInHadoop /input /output");
        p.waitFor();
        count2 = readHadoopOutput();
        System.out.println("Multi-way Join: " + count2);
        p = r.exec("hadoop fs -rmr /output");
        p.waitFor();
        p = r.exec("hadoop jar " + baseDir + "WDMGiraph.jar org.apache.giraph.GiraphRunner com.hadoop.webdatamanagement.TriangleCounterInGiraph -vif org.apache.giraph.io.formats.LongLongNullTextInputFormat -vip /input/" + graphName + " -vof org.apache.giraph.io.formats.IdWithValueTextOutputFormat -op /output -w 1");
        p.waitFor();
        count3 = readHadoopOutput();
        System.out.println("Giraph: " + count3);
        if (count1 != count2)
            System.out.println("MULTI-WAY FAILED!");
        if (count1 != count3)
            System.out.println("GIRAPH FAILED!");
    }

    /**
     * Generates a set of graphs according to the given parameters
     * @param baseDir the base testing directory
     * @param noVertices a list containing the various desired numbers of vertices of the graphs
     * @param densities a list containing the various desired densities of edges of the graphs;
     *                  densities are between 0 (no edges) to 1 (maximum number of edges)
     * @throws InterruptedException
     * @throws IOException
     */
    public static void generateGraphs(String baseDir, long[] noVertices, double[] densities) throws InterruptedException, IOException {
        Runtime.getRuntime().exec(new String[] { "sh", "-c", "rm "+baseDir+ "input/*"});
        int count = 0;
        for (long n : noVertices) {
            for (double d : densities) {
                String graphName = String.format("graph_%03d_%d_%d", count++, n, (int)( d * 100));
                writeGraphToFile(generateGraph(n, d), baseDir+"input/"+graphName);
            }
        }
    }

    /**
     * Tests all the graphs in the "input" directory of the base testing directory
     * @param baseDir the base testing directory
     * @throws InterruptedException
     * @throws IOException
     */
    public static void testGraphs(String baseDir) throws InterruptedException, IOException {
        File dir = new File(baseDir + "input/");
        if (!dir.isDirectory())
            throw new RuntimeException("Base directory path incorrect");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        });
        for (File input : files) {
            testGraph(baseDir, input.getName());
        }
    }


    public static void main(String[] args) throws Exception {
        String baseDir = "/Users/ane/Desktop/testGraphs/";
        generateGraphs(baseDir, new long[]{10L, 20L, 30L, 50L, 100L}, new double[]{0.4, 0.6, 0.8});
        testGraphs(baseDir);
    }
}
