/**
 * @Author: Shivam Patel
 * @Andrew_ID: shpatel
 * @Course: 95-771 Data Structures and Algorithms for Information Processing
 * @Project_Number: Project 4 - Part 3
 * @File: Main.java
 */

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    // Stores the list of crimes
    static LinkedList<String> crime_list;

    // Stores the minimum cycle length (for optimal path)
    static Double minCycleLength;

    // Stores the route for the best (optimal) path
    static int[] best_cycle;

    public static void main(String[] args) throws ParseException {

        // Scanner object to get input from the user
        Scanner s = new Scanner(System.in);

        System.out.print("Enter number of test cases: ");

        // Stores the number of test cases the user wants to check
        int test_cases = s.nextInt();
        s.nextLine();

        // Stores the content for the result.txt file
        String resultsFileContent = "shpatel";

        // Loop for the number of test cases
        for (int t = 1; t <= test_cases; t++) {

            // Stores the content for the KML file to the generated
            // Note: Separate KML files will be generated for all test cases
            String kmlFileContent = """
                    <?xml version="1.0" encoding="UTF-8" ?>
                    <kml xmlns="http://earth.google.com/kml/2.2">
                    <Document>
                    <name>Pittsburgh TSP</name><description>TSP on Crime</description><Style id="style6">
                    <LineStyle>
                    <color>73FF0000</color>
                    <width>5</width>
                    </LineStyle>
                    </Style>
                    <Style id="style5">
                    <LineStyle>
                    <color>507800F0</color>
                    <width>5</width>
                    </LineStyle>
                    </Style>
                    <Placemark>
                    <name>TSP Path</name>
                    <description>TSP Path</description>
                    <styleUrl>#style6</styleUrl>
                    <LineString>
                    <tessellate>1</tessellate>
                    <coordinates>
                    """;

            resultsFileContent = resultsFileContent + "\n\nTestCase" + t + "\n";

            // Initialise minimum cycle length for the optimal path to be the maximum value
            minCycleLength = Double.MAX_VALUE;

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

            crime_list = new LinkedList<>();

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

            resultsFileContent = resultsFileContent + "Hamiltonian Cycle" + "\n";

            // Print hamiltonian cycle
            System.out.println("\nHamiltonian Cycle (not necessarily optimum):");
            for (int i = 0; i < hamiltonianCycle.size(); i++) {
                System.out.print(hamiltonianCycle.get(i) + " ");
                resultsFileContent = resultsFileContent + hamiltonianCycle.get(i) + " ";

                String[] crime_data_split = crime_list.get(hamiltonianCycle.get(i)).split(",");
                kmlFileContent = kmlFileContent
                        + crime_data_split[8]
                        + ","
                        + crime_data_split[7]
                        + ",0.000000\n";
            }

            kmlFileContent = kmlFileContent + "</coordinates>\n" +
                    "</LineString>\n" +
                    "</Placemark>\n" +
                    "<Placemark>\n" +
                    "<name>Optimal Path</name>\n" +
                    "<description>Optimal Path</description>\n" +
                    "<styleUrl>#style5</styleUrl>\n" +
                    "<LineString>\n" +
                    "<tessellate>1</tessellate>\n" +
                    "<coordinates>\n";

            // Compute hamiltonian cycle length
            Double cycleLength = hamiltonianCycleLength(hamiltonianCycle, crime_list);
            System.out.println("\nLength Of cycle: " + cycleLength + " miles");
            resultsFileContent = resultsFileContent + "\nLength\n" + cycleLength + "\n";

            // Optimal Path calculations
            System.out.println("\nLooking at every permutation to find the optimal solution");
            int[] arr = new int[crime_data.length];

            // Generate array to perform permutations
            for (int i = 1; i < crime_data.length; i++) {
                arr[i] = i;
            }

            // Initialize best_cycle
            best_cycle = new int[arr.length];

            // On all permutations, perform optimal path calculations
            permuteArray(arr.length, arr);

            resultsFileContent = resultsFileContent + "Optimum path\n";

            // Display results for the optimal path
            System.out.println("The best permutation");
            for (int j : best_cycle) {
                System.out.print(j + " ");
                resultsFileContent = resultsFileContent + j + " ";
                String[] crime_data_split = crime_list.get(j).split(",");
                kmlFileContent = kmlFileContent
                        + (Double.parseDouble(crime_data_split[8]) - 0.0008)
                        + ","
                        + (Double.parseDouble(crime_data_split[7]) - 0.0008)
                        + ","
                        + "0.000000\n";
            }
            System.out.print("0");
            System.out.println("\n\nOptimal Cycle length = " + minCycleLength + " miles");
            resultsFileContent = resultsFileContent + "0\nLength\n" + minCycleLength;

            String[] crime_data_split = crime_list.get(0).split(",");
            kmlFileContent = kmlFileContent
                    + (Double.parseDouble(crime_data_split[8]) - 0.0008)
                    + ","
                    + (Double.parseDouble(crime_data_split[7]) - 0.0008)
                    + ","
                    + "0.000000\n"
                    + "</coordinates>\n" +
                    "</LineString>\n" +
                    "</Placemark>\n" +
                    "</Document>\n" +
                    "</kml>";

            // Generate KML file
            // Note: A new KML file will be generated for each Test Case
            generateKMLFile(kmlFileContent, t);
        }

        // Generate result.txt file
        generateResultsTxtFile(resultsFileContent);
    }

    /**
     * Function to generate KML file of the hamiltonian and optimal path
     * @param kmlFileContent Content of the KML file
     * @param test_case Test case for which the KML file is generated
     */
    public static void generateKMLFile(String kmlFileContent, int test_case) {
        // Source: https://stackoverflow.com/questions/18725039/java-create-a-kml-file-and-insert-elements-in-existing-file
        try {
            // Create a Writer object pointing to the file result.txt inside the src directory
            Writer fwriter = new FileWriter("src/PGHCrimesTestCase" + test_case + ".kml");

            // Write the kmlString to the file
            fwriter.write(kmlFileContent);

            // Flush the fwriter file
            fwriter.flush();

            // Close the file
            fwriter.close();
        }
        // Handles exceptions related to IO. Throws an exception if the file is not found (incorrect location)
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Function to generate result.txt file
     * @param resultsFileContent Content of the result.txt file
     */
    public static void generateResultsTxtFile(String resultsFileContent) {
        // Source: https://stackoverflow.com/questions/18725039/java-create-a-kml-file-and-insert-elements-in-existing-file
        try {
            // Create a Writer object pointing to the file result.txt inside the src directory
            Writer fwriter = new FileWriter("src/result.txt");

            // Write the kmlString to the file
            fwriter.write(resultsFileContent);

            // Flush the fwriter file
            fwriter.flush();

            // Close the file
            fwriter.close();
        }
        // Handles exceptions related to IO. Throws an exception if the file is not found (incorrect location)
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Function to generate all permutations of array
     * @param n Length of array
     * @param elements Array whose permutations are to be calculated
     */
    // Source: https://www.baeldung.com/java-array-permutations
    public static void permuteArray(int n, int[] elements) {

        // If n == 1 during processing (or initially during the function call)
        if(n == 1) {
            // Perform optimal path calculations
            processPath(elements);
        } else {
            // Perform permutations
            for(int i = 0; i < n-1; i++) {
                permuteArray(n - 1, elements);
                if(n % 2 == 0) {
                    swap(elements, i, n-1);
                } else {
                    swap(elements, 0, n-1);
                }
            }
            permuteArray(n - 1, elements);
        }
    }

    /**
     * Function to swap the values of two index in array
     * @param input Array in which the swapping is to be done
     * @param a Index 1
     * @param b Index 2
     */
    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    /***
     * Function to perform the shortest (optimal) distance computations
     * @param input
     */
    private static void processPath(int[] input) {

        // Create a copy of the input array
        int[] inputArrCopy = new int[input.length + 1];

        // Add 0 as the first element since we want to start from 0
        inputArrCopy[0] = 0;

        // Populate the remaining values of the copy array
        for (int i = 1; i < inputArrCopy.length; i++) {
            inputArrCopy[i] = input[i-1];
        }

        // Compute length of current array
        Double currentCycleLength = computeCycleLength(inputArrCopy);

        // If length is less than the minimum length found
        if (currentCycleLength < minCycleLength) {

            // Update minimum length
            minCycleLength = currentCycleLength;

            // Update best cycle
            for (int i = 0; i < input.length; i++) {
                best_cycle[i] = input[i];
            }
        }
    }

    /**
     * Function to compute cycle length while checking permutations
     * @param cycleArr Permuted array
     * @return Cycle length of permutated array
     */
    public static Double computeCycleLength(int[] cycleArr) {
        Double cycleLength = 0.0;
        Double x1, y1, x2, y2;
        String crime1, crime2;

        // Create a copy array
        int[] cycleArrCopy = Arrays.copyOf(cycleArr, cycleArr.length + 1);

        // Adding the last element as the first element to make a cycle
        cycleArrCopy[cycleArrCopy.length - 1] = cycleArr[0];

        // Loop over every adjacent pair in the array and update cycleLength
        for(int i = 0; i < cycleArrCopy.length - 1; i++) {
            crime1 = crime_list.get(cycleArrCopy[i]);
            crime2 = crime_list.get(cycleArrCopy[i+1]);
            x1 = Double.parseDouble(crime1.split(",")[0]);
            y1 = Double.parseDouble(crime1.split(",")[1]);
            x2 = Double.parseDouble(crime2.split(",")[0]);
            y2 = Double.parseDouble(crime2.split(",")[1]);

            cycleLength = cycleLength + (computeDistance(x1, y1, x2, y2) * 0.00018939);
        }

        // Return cycle length
        return cycleLength;
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