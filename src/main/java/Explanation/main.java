package Explanation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {


    public static void main(String[] args) throws FileNotFoundException {
        // Define path to the names resource file
        String path = "src/main/resources/hotels.txt";

        // Initialize Scanner with the file
        Scanner sc = new Scanner(new File(path));

        // Iterate through each line of the file and print it to the console
        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine());
        }


    }
}