# Run with following command

hadoop jar WDMGiraph.jar org.apache.giraph.GiraphRunner com.hadoop.webdatamanagement.TriangleCounterInGiraph -vif org.apache.giraph.io.formats.LongLongNullTextInputFormat -vip /input/graph.txt -vof org.apache.giraph.io.formats.IdWithValueTextOutputFormat -op /output -w 1;

# Where /output is the output directory and /input/graph.txt has form

vertexIndex1 neighborIndex1 neighborIndex2 ...
vertexIndex2 neighborIndex1 neighborIndex2 ...
.
.
.

# This example should have 4 triangles (3 in vertex 2 and 1 in vertex 1) 

1 6 2
2 3 4 5 6
3 2 4
4 2 5 3
5 2 6
6 1 5 2