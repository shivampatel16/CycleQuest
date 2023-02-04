/**
 * @Author: Shivam Patel
 * @Andrew_ID: shpatel
 * @Course: 95-771 Data Structures and Algorithms for Information Processing
 * @Project_Number: Project 4 - Part 1
 * @File: Main.java
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ParseException {

        // Scanner object to get input from the user
        Scanner s = new Scanner(System.in);

        System.out.print("Enter number of test cases: ");
        
        // Stores the number of test cases the user wants to check
        int test_cases = s.nextInt();
        s.nextLine();

        // Loop for the number of test cases
        for (int t = 1; t <= test_cases; t++) {

            // Enter start and end dates for test case
            System.out.println("\n\n******** Enter details for Test Case " + t + " ********\n\n");

            System.out.println("Enter start date:");
            String start_date = s.nextLine();

            System.out.println("Enter end date:");
            String end_date = s.nextLine();

            // Format the start and end dates
            // Source: https://www.baeldung.com/java-string-to-date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
            Date startDate = formatter.parse(start_date);
            Date endDate = formatter.parse(end_date);

            // Stores the location of the CSV which has the crime data
            String crimeDataLocation = "src/CrimeLatLonXY1990.csv";

            // Stores data of one crime record in one combined String
            String[] crime_data;

            // Stores all the crime data between the given dates (both included)
            LinkedList<String> crime_list = new LinkedList<>();

            System.out.println("\nCrime records between " + start_date + " and " + end_date);

            // Populate the crime list
            // Source to read files: https://www.w3schools.com/java/java_files_read.asp
            try {
                // Get the location of the file from the user
                String user_file = crimeDataLocation;

                // Create and object for the user file
                File myObj = new File(user_file);

                // Create a scanner object
                Scanner myReader1 = new Scanner(myObj);

                // Ignoring the first line of the CSV file (header line)
                myReader1.nextLine();

                // Read each line from the user file and store them to a TwoDTreeNode to form the 2D tree
                while (myReader1.hasNextLine()) {

                    // Read the next line the CSV file
                    String data = myReader1.nextLine();

                    if (formatter.parse(data.split(",")[5]).compareTo(startDate) >= 0 &&
                            formatter.parse(data.split(",")[5]).compareTo(endDate) <= 0) {
                        crime_list.add(data);
                        System.out.println(data);
                    }
                }
                myReader1.close();
            }
            // Throws an exception if the file is not found (incorrect location)
            catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            crime_data = crime_list.toArray(new String[0]);

            // Generate adjacency matrix
            Double[][] adjacencyMatrix = generateAdjacencyMatrix(crime_data);

            // Create an object for Prim class
            Prim primObj = new Prim(adjacencyMatrix);

            // Compute MST using Prim
            int[] primParents = primObj.generatePrimMST(adjacencyMatrix);

            // Create an object for MST class and create the MST from the results of Prim
            MST mstObj = new MST(primParents);
            MSTNode root = mstObj.generateMST(primParents);

            // Perform preOrderTraversal on the MST
            mstObj.preOrderTraversal(root);

            // Generate Hamiltonian cycle
            LinkedList<Integer> hamiltonianCycle = mstObj.getPreOrderTreeWalk();

            // Add 0 to the cycle to complete the cycle
            hamiltonianCycle.add(0);

            // Print hamiltonian cycle
            System.out.println("\nHamiltonian Cycle (not necessarily optimum):");
            for (int i = 0; i < hamiltonianCycle.size(); i++) {
                System.out.print(hamiltonianCycle.get(i) + " ");
            }

            // Compute hamiltonian cycle length
            Double cycleLength = hamiltonianCycleLength(hamiltonianCycle, crime_list);

            System.out.println("\nLength Of cycle: " + cycleLength + " miles");
        }
    }

    /***
     * Compute distance between two points in the X-Y coordinate system
     * @param x1 X-Coordinate of point 1
     * @param y1 Y-Coordinate of point 1
     * @param x2 X-Coordinate of point 2
     * @param y2 Y-Coordinate of point 2
     * @return
     */
    public static Double computeDistance(Double x1, Double y1, Double x2, Double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /***
     * Generate adjacency matrix for the crime data
     * @param crime_data List of crime data whose adjacency matrix is to be generated
     * @return Adjacency matrix of the crime data
     */
    public static Double[][] generateAdjacencyMatrix(String[] crime_data) {

        // Store the adjacency matrix
        Double[][] adjacencyMatrix = new Double[crime_data.length][crime_data.length];

        Double x1, y1, x2, y2;

        // Loop over every point, calculate its distance with every other point and
        // populate the adjacency matrix
        for (int i = 0; i < crime_data.length; i++) {
            for (int j = 0; j < crime_data.length; j++) {
                x1 = Double.valueOf(crime_data[i].split(",")[0]);
                y1 = Double.valueOf(crime_data[i].split(",")[1]);
                x2 = Double.valueOf(crime_data[j].split(",")[0]);
                y2 = Double.valueOf(crime_data[j].split(",")[1]);

                adjacencyMatrix[i][j] = computeDistance(x1, y1, x2, y2);
            }
        }

        // Return adjacency matrix
        return adjacencyMatrix;
    }

    /***
     * Calculate length of the hamiltonian cycle
     * @param hamiltonianCycle Points in the hamiltonian cycle
     * @param crime_list List of crime data
     * @return Length of the hamiltonian cycle
     */
    public static Double hamiltonianCycleLength(LinkedList<Integer> hamiltonianCycle,
                                                LinkedList<String> crime_list) {
        Double cycleLength = 0.0;
        Double x1, y1, x2, y2;

        // Loop over every adjacent pair in the hamiltonianCycle and update cycleLength
        for(int i = 0; i < hamiltonianCycle.size() - 1; i++) {
            String crime1 = crime_list.get(hamiltonianCycle.get(i));
            String crime2 = crime_list.get(hamiltonianCycle.get(i+1));
            x1 = Double.valueOf(crime1.split(",")[0]);
            y1 = Double.valueOf(crime1.split(",")[1]);
            x2 = Double.valueOf(crime2.split(",")[0]);
            y2 = Double.valueOf(crime2.split(",")[1]);

            cycleLength = cycleLength + (computeDistance(x1, y1, x2, y2) * 0.00018939);
        }

        // Return hamiltonian cycle length
        return cycleLength;
    }
}