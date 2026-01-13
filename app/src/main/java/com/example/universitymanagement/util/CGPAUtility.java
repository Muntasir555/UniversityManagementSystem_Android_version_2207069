package com.example.universitymanagement.util;

import com.example.universitymanagement.models.Result;
import java.util.List;
import java.util.Map;

public class CGPAUtility {

    public static double calculateCGPA(List<Result> results, Map<String, Integer> subjectCredits) {
        double totalGradePoints = 0;
        double totalCredits = 0;

        for (Result result : results) {
            String subjectId = result.getSubjectId();
            if (subjectCredits.containsKey(subjectId)) {
                int credit = subjectCredits.get(subjectId);
                double gradePoint = calculateGradePoint(result.getGrade());

                totalGradePoints += (gradePoint * credit);
                totalCredits += credit;
            }
        }

        if (totalCredits == 0)
            return 0.0;
        return totalGradePoints / totalCredits;
    }

    public static double calculateGradePoint(String grade) {
        switch (grade) {
            case "A+":
                return 4.00;
            case "A":
                return 3.75;
            case "A-":
                return 3.50;
            case "B+":
                return 3.25;
            case "B":
                return 3.00;
            case "B-":
                return 2.75;
            case "C+":
                return 2.50;
            case "C":
                return 2.25;
            case "D":
                return 2.00;
            case "F":
                return 0.00;
            default:
                return 0.00;
        }
    }

    public static String calculateGrade(double marks) {
        if (marks >= 80)
            return "A+";
        if (marks >= 75)
            return "A";
        if (marks >= 70)
            return "A-";
        if (marks >= 65)
            return "B+";
        if (marks >= 60)
            return "B";
        if (marks >= 55)
            return "B-";
        if (marks >= 50)
            return "C+";
        if (marks >= 45)
            return "C";
        if (marks >= 40)
            return "D";
        return "F";
    }
}
