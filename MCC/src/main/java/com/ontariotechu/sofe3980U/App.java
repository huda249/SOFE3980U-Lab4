package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        List<String[]> allData;
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading CSV");
            return;
        }

        int n = allData.size();
        double ceSum = 0;
        int[][] confusionMatrix = new int[5][5]; // 5x5 for 5 classes

        for (String[] row : allData) {
            int y_actual = Integer.parseInt(row[0]); // Class 1-5
            float[] y_probs = new float[5];
            int y_pred = 0;
            float maxProb = -1;

            for (int j = 0; j < 5; j++) {
                y_probs[j] = Float.parseFloat(row[j + 1]);
                if (y_probs[j] > maxProb) {
                    maxProb = y_probs[j];
                    y_pred = j + 1; // Class is index + 1
                }
            }

            // Cross Entropy: -log(probability of the TRUE class)
            ceSum += Math.log(y_probs[y_actual - 1] + 1e-10);
            
            // Confusion Matrix: [predicted-1][actual-1]
            confusionMatrix[y_pred - 1][y_actual - 1]++;
        }

        System.out.printf("CE =%.7f\n", (-ceSum / n));
        System.out.println("Confusion matrix");
        System.out.println("\t\ty=1\ty=2\ty=3\ty=4\ty=5");
        for (int i = 0; i < 5; i++) {
            System.out.print("\ty^=" + (i + 1) + "\t");
            for (int j = 0; j < 5; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
}