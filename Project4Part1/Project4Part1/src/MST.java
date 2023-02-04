/**
 * @Author: Shivam Patel
 * @Andrew_ID: shpatel
 * @Course: 95-771 Data Structures and Algorithms for Information Processing
 * @Project_Number: Project 4 - Part 1
 * @File: MST.java
 */

import java.util.LinkedList;

public class MST {

    // Stores the parents list from the Prim MST results
    int[] parent;

    // Stores the pre-order tree walk of the MST tree
    private final LinkedList<Integer> preOrderTreeWalk = new LinkedList<>();

    // Constructor to initialize instance variables
    MST (int[] parent) {
        this.parent = parent;
    }

    /***
     * Function to generate MST tree from the Prim MST results
     * @param parent List of parents from the output of Prim MST
     * @return root node of generated tree
     */
    public MSTNode generateMST(int[] parent){

        // Temporary helper list to store the vertices whose children are yet to be added in the tree
        LinkedList<Integer> nodesToBeChecked = new LinkedList<>();

        // Initially add root
        nodesToBeChecked.add(0);
        MSTNode root = new MSTNode(0);

        // Until children of all nodes are added
        while (!nodesToBeChecked.isEmpty()) {
            int currentIndex = nodesToBeChecked.get(0);

            // Create new MSTNode for the node to be added
            MSTNode parentNode = getMSTNodeByIndex(root, currentIndex);

            // Add all children of node to tree
            for (int i = 0; i < parent.length; i++) {
                if (parent[i] == currentIndex) {
                    MSTNode newMSTNode = new MSTNode(i);
                    parentNode.getChildren().add(newMSTNode);
                    nodesToBeChecked.add(i);
                }
            }
            // Remove node whose children have been added
            nodesToBeChecked.remove(0);
        }
        // Return root of the generated tree
        return root;
    }

    /***
     * Function to get the MST Node pointed to by the index
     * @param root Root of the MST tree
     * @param index Index of the MST Node
     * @return MST Node at index
     */
    public MSTNode getMSTNodeByIndex(MSTNode root, int index) {

        if (root.getIndex() == index) {
            return root;
        }
        else {
            for (int i = 0; i < root.getChildren().size(); i++) {
                getMSTNodeByIndex(root.getChildren().get(i), index);
            }
        }
        return root;
    }

    /***
     * Function to perform pre-order traversal on the MST tree
     * @param root Root of the MST tree on which the pre-order traversal is to be performed
     */
    public void preOrderTraversal(MSTNode root) {

        // Initially traverse root of the tree
        preOrderTreeWalk.add(root.index);

        // Traverse all other nodes of the tree
        for (int i = 0; i < root.getChildren().size(); i++) {
            preOrderTraversal(root.getChildren().get(i));
        }
    }

    /***
     * Function to get the preOrder traversal list of vertex in MST
     * @return PreOrder traversal list of vertex in MST
     */
    protected LinkedList<Integer> getPreOrderTreeWalk() {
        return preOrderTreeWalk;
    }
}
