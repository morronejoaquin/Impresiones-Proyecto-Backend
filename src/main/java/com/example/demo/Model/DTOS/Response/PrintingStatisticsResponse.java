package com.example.demo.Model.DTOS.Response;

public class PrintingStatisticsResponse {
    private double colorPercentage;
    private double bwPercentage;
    private double ringedPercentage;
    private double stapledPercentage;
    private double noBindingPercentage;
    private int totalSheets;
    private int colorSheets;
    private int bwSheets;

    public PrintingStatisticsResponse(double colorPercentage, double bwPercentage, 
                                  double ringedPercentage, double stapledPercentage, 
                                  double noBindingPercentage, int totalSheets, 
                                  int colorSheets, int bwSheets) {
        this.colorPercentage = colorPercentage;
        this.bwPercentage = bwPercentage;
        this.ringedPercentage = ringedPercentage;
        this.stapledPercentage = stapledPercentage;
        this.noBindingPercentage = noBindingPercentage;
        this.totalSheets = totalSheets;
        this.colorSheets = colorSheets;
        this.bwSheets = bwSheets;
    }

    public double getColorPercentage() { return colorPercentage; }
    public double getBwPercentage() { return bwPercentage; }
    public double getRingedPercentage() { return ringedPercentage; }
    public double getStapledPercentage() { return stapledPercentage; }
    public double getNoBindingPercentage() { return noBindingPercentage; }
    public int getTotalSheets() { return totalSheets; }
    public int getColorSheets() { return colorSheets; }
    public int getBwSheets() { return bwSheets; }
}
