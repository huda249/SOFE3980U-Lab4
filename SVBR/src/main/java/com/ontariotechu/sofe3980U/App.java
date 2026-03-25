package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        for (String file : files) {
            evaluateBinaryModel(file);
        }
    }

    public static void evaluateBinaryModel(String filePath) {
        List<String[]> allData;
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading " + filePath);
            return;
        }

        int n = allData.size();
        double bceSum = 0;
        int tp = 0, tn = 0, fp = 0, fn = 0;
        double epsilon = 1e-10;

        for (String[] row : allData) {
            int y = Integer.parseInt(row[0]);
            float y_hat = Float.parseFloat(row[1]);

            // 1. Binary Cross Entropy
            bceSum += y * Math.log10(y_hat + epsilon) + (1 - y) * Math.log10(1 - y_hat + epsilon);

            // 2. Confusion Matrix (Threshold = 0.5)
            int prediction = (y_hat >= 0.5) ? 1 : 0;
            if (y == 1 && prediction == 1) tp++;
            else if (y == 0 && prediction == 0) tn++;
            else if (y == 0 && prediction == 1) fp++;
            else if (y == 1 && prediction == 0) fn++;
        }

        double bce = -bceSum / n;
        double accuracy = (double) (tp + tn) / n;
        double precision = (double) tp / (tp + fp);
        double recall = (double) tp / (tp + fn);
        double f1 = 2 * (precision * recall) / (precision + recall);

        // 3. AUC-ROC Calculation (Simplified Trapezoidal)
        double auc = calculateAUC(allData);

        System.out.println("for " + filePath);
        System.out.println("\tBCE =" + bce);
        System.out.println("\tConfusion matrix\n\t\t\ty=1\ty=0\n\t\ty^=1\t" + tp + "\t" + fp + "\n\t\ty^=0\t" + fn + "\t" + tn);
        System.out.printf("\tAccuracy =%.4f\n", accuracy);
        System.out.printf("\tPrecision =%.8f\n", precision);
        System.out.printf("\tRecall =%.8f\n", recall);
        System.out.printf("\tf1 score =%.8f\n", f1);
        System.out.printf("\tauc roc =%.8f\n", auc);
    }

    public static double calculateAUC(List<String[]> data) {
        double auc = 0;
        double[] x = new double[101]; // FPR
        double[] y_coords = new double[101]; // TPR

        int totalPos = 0, totalNeg = 0;
        for (String[] row : data) {
            if (Integer.parseInt(row[0]) == 1) totalPos++;
            else totalNeg++;
        }

        for (int i = 0; i <= 100; i++) {
            double threshold = i / 100.0;
            int tp = 0, fp = 0;
            for (String[] row : data) {
                int actual = Integer.parseInt(row[0]);
                float pred = Float.parseFloat(row[1]);
                if (pred >= threshold) {
                    if (actual == 1) tp++;
                    else fp++;
                }
            }
            y_coords[i] = (double) tp / totalPos;
            x[i] = (double) fp / totalNeg;
        }

        for (int i = 1; i <= 100; i++) {
            auc += (y_coords[i - 1] + y_coords[i]) * Math.abs(x[i - 1] - x[i]) / 2.0;
        }
        return auc;
    }
}