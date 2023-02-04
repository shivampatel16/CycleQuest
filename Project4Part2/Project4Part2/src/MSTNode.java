/**
 * @Author: Shivam Patel
 * @Andrew_ID: shpatel
 * @Course: 95-771 Data Structures and Algorithms for Information Processing
 * @Project_Number: Project 4 - Part 2
 * @File: MSTNode.java
 */

import java.util.LinkedList;

public class MSTNode {

    // Stores the index of the crime (0, 1, 2, etc)
    int index;

    // Stores the MSTNode children
    LinkedList<MSTNode> children;

    // Constructor to initialize the instance variables
    MSTNode(int index) {
        this.index = index;
        children = new LinkedList<>();
    }

    public int getIndex() {
        return index;
    }

    public LinkedList<MSTNode> getChildren() {
        return children;
    }
}
