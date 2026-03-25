package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

public class App 
{
    public static void main( String[] args )
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Variables to track the best model for each metric
        String bestMSEModel = "";
        double minMSE = Double.MAX_VALUE;

        String bestMAEModel = "";
        double minMAE = Double.MAX_VALUE;

        String bestMAREModel = "";
        double minMARE = Double.MAX_VALUE;

        for (String filePath : files) {
            System.out.println("Results for " + filePath + ":");
            
            // Calculate metrics for the current file
            double[] results = evaluateModel(filePath);
            
            if (results != null) {
                double currentMSE = results[0];
                double currentMAE = results[1];
                double currentMARE = results[2];

                // Update trackers
                if (currentMSE < minMSE) { minMSE = currentMSE; bestMSEModel = filePath; }
                if (currentMAE < minMAE) { minMAE = currentMAE; bestMAEModel = filePath; }
                if (currentMARE < minMARE) { minMARE = currentMARE; bestMAREModel = filePath; }
            }
            System.out.println("-----------------------------------");
        }

        // Print Final Recommendations
        System.out.println("According to MSE, The best model is " + bestMSEModel);
        System.out.println("According to MAE, The best model is " + bestMAEModel);
        System.out.println("According to MARE, The best model is " + bestMAREModel);
    }

    public static double[] evaluateModel(String filePath) {
        List<String[]> allData;
        try {
            FileReader filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        } catch(Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return null;
        }

        double sumMSE = 0, sumMAE = 0, sumMARE = 0;
        double epsilon = 1e-10;
        int n = allData.size();

        for (String[] row : allData) { 
            float y_true = Float.parseFloat(row[0]);
            float y_predicted = Float.parseFloat(row[1]);

            sumMSE += Math.pow(y_true - y_predicted, 2);
            sumMAE += Math.abs(y_true - y_predicted);
            sumMARE += Math.abs(y_true - y_predicted) / (Math.abs(y_true) + epsilon);
        }

        double avgMSE = sumMSE / n;
        double avgMAE = sumMAE / n;
        double avgMARE = (sumMARE / n); // This is represented as a decimal, multiply by 100 for %

        System.out.printf("\tMSE  = %.8f\n", avgMSE);
        System.out.printf("\tMAE  = %.8f\n", avgMAE);
        System.out.printf("\tMARE = %.8f\n", avgMARE);

        return new double[]{avgMSE, avgMAE, avgMARE};
    }
}