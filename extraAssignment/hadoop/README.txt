# Run with (where /graphInput is the input directory and /output the output directory)
hadoop jar WDMHadoop.jar com.hadoop.webdatamanagement.TriangleCounterInHadoop /graphInput /output;

# Optional parameter for number of buckets for the m-way algorithm
hadoop jar WDMHadoop.jar com.hadoop.webdatamanagement.TriangleCounterInHadoop /graphInput /output 3;

#Possible optimization from http://infolab.stanford.edu/~ullman/mmds/book.pdf