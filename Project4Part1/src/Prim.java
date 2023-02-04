/**
 * @Author: Shivam Patel
 * @Andrew_ID: shpatel
 * @Course: 95-771 Data Structures and Algorithms for Information Processing
 * @Project_Number: Project 4 - Part 1
 * @File: Prim.java
 */

public class Prim {

    // Stores the parents in the prim computation
    int[] parent;

    // Stores the keys (distances) in the prim computation
    Double[] key;

    // Stores boolean values to represent set of vertices included in MST
    Boolean[] mstSet;

    // Constructor to initialize instance variables
    Prim(Double[][] adjacencyMatrix) {
        parent = new int[adjacencyMatrix.length];
        key = new Double[adjacencyMatrix.length];
        mstSet = new Boolean[adjacencyMatrix.length];
    }

    /***
     * Function to construct Prim MST from adjacencyMatrix
     * @param adjacencyMatrix Adjacency matrix of distance of every point from every other point
     * @return Prim MST result (in terms of the parent list)
     */
    // Source: https://www.geeksforgeeks.org/prims-minimum-spanning-tree-mst-greedy-algo-5/
    public int[] generatePrimMST(Double[][] adjacencyMatrix) {

        // Initialize all keys as INFINITE
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            key[i] = Double.MAX_VALUE;
            mstSet[i] = false;
        }

        // For including the first vertex in the MST
        key[0] = 0.0;
        parent[0] = -1;

        // Loop over all vertices
        for (int count = 0; count < adjacencyMatrix.length - 1; count++) {

            // Find the minimum key from the list of vertices not yet in the MST
            int u = minKey(key, mstSet);

            // Add the selected vertex to MST
            mstSet[u] = true;

            // Considering only those vertices which are yet not included in MST
            // Updating the key and parent of adjacent vertices of the picked vertex
            for (int v = 0; v < adjacencyMatrix.length; v++) {

                // adjacencyMatrix[u][v] is non-zero only for adjacent vertices of m
                // mstSet[v] is false for vertices not yet included in MST
                // Update the key only if adjacencyMatrix[u][v] is smaller than key[v]
                if (adjacencyMatrix[u][v] != 0
                        && !mstSet[v]
                        && adjacencyMatrix[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = adjacencyMatrix[u][v];
                }
            }
        }

        // Return list of parent
        return parent;
    }

    /***
     * Function to find the vertex with minimum key value, from the set of vertices
     * not yet included in MST
     * @param key Key set in the Prim MST
     * @param mstSet List of vertex still in MST
     * @return index of minimum key
     */
    // A utility function to find the vertex with minimum
    // key value, from the set of vertices not yet included
    // in MST
    public int minKey(Double[] key, Boolean[] mstSet) {
        // Initialize min value
        Double min = Double.MAX_VALUE;
        int min_index = -1;

        // Find the minimum value
        for (int v = 0; v < key.length; v++) {
            if (!mstSet[v] && key[v] < min) {
                min = key[v];
                min_index = v;
            }
        }

        // Index of minimum key
        return min_index;
    }
}