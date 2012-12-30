package de.knewcleus.openradar.gui.setup;

import java.util.Properties;

public class RunwayData {

    private volatile boolean repaintNeeded = true;

    private String rwCode;

    private double extCenterlineStart = 0;
    private double extCenterlineLength = 100;

    private double majorDMStart = 10;
    private double majorDMEnd = 20;
    private double majorDMInterval = 5;
    private double majorDMTickLength = 1;

    private double minorDMStart = 10;
    private double minorDMEnd = 20;
    private double minorDMInterval = 1;
    private double minorDMTickLength = 0.5;

    private double leftVectoringCLStart = 10;
    private double leftVectoringAngle = 30;
    private double leftVectoringLength = 3;
    private double leftBaselegLength = 5;

    private double rightVectoringCLStart = 10;
    private double rightVectoringAngle = 30;
    private double rightVectoringLength = 3;
    private double rightBaselegLength = 5;

    private boolean symetric = true;
    private boolean rightBaseEnabled = true;
    private boolean leftBaseEnabled = true;

    public RunwayData(String rwCode) {
        this.rwCode = rwCode;
    }

    public boolean isRepaintNeeded() {
        return repaintNeeded;
    }

    public void setRepaintNeeded(boolean repaintNeeded) {
        this.repaintNeeded = repaintNeeded;
    }

    public double getExtCenterlineStart() {
        return extCenterlineStart;
    }

    public void setExtCenterlineStart(double extCenterlineStart) {
        this.extCenterlineStart = extCenterlineStart;
    }

    public double getExtCenterlineLength() {
        return extCenterlineLength;
    }

    public void setExtCenterlineLength(double extCenterlineLength) {
        this.extCenterlineLength = extCenterlineLength;
    }

    public double getMajorDMStart() {
        return majorDMStart;
    }

    public void setMajorDMStart(double majorDMStart) {
        this.majorDMStart = majorDMStart;
    }

    public double getMajorDMEnd() {
        return majorDMEnd;
    }

    public void setMajorDMEnd(double majorDMEnd) {
        this.majorDMEnd = majorDMEnd;
    }

    public double getMajorDMInterval() {
        return majorDMInterval;
    }

    public void setMajorDMInterval(double majorDMInterval) {
        this.majorDMInterval = majorDMInterval;
    }

    public double getMajorDMTickLength() {
        return majorDMTickLength;
    }

    public void setMajorDMTickLength(double majorDMTickLength) {
        this.majorDMTickLength = majorDMTickLength;
    }

    public double getMinorDMStart() {
        return minorDMStart;
    }

    public void setMinorDMStart(double minorDMStart) {
        this.minorDMStart = minorDMStart;
    }

    public double getMinorDMEnd() {
        return minorDMEnd;
    }

    public void setMinorDMEnd(double minorDMEnd) {
        this.minorDMEnd = minorDMEnd;
    }

    public double getMinorDMInterval() {
        return minorDMInterval;
    }

    public void setMinorDMInterval(double minorDMInterval) {
        this.minorDMInterval = minorDMInterval;
    }

    public double getMinorDMTickLength() {
        return minorDMTickLength;
    }

    public void setMinorDMTickLength(double minorDMTickLength) {
        this.minorDMTickLength = minorDMTickLength;
    }

    public double getLeftVectoringCLStart() {
        return leftVectoringCLStart;
    }

    public void setLeftVectoringCLStart(double leftVectoringCLStart) {
        this.leftVectoringCLStart = leftVectoringCLStart;
    }

    public double getLeftVectoringAngle() {
        return leftVectoringAngle;
    }

    public void setLeftVectoringAngle(double leftVectoringAngle) {
        this.leftVectoringAngle = leftVectoringAngle;
    }

    public double getLeftVectoringLength() {
        return leftVectoringLength;
    }

    public void setLeftVectoringLength(double leftVectoringLength) {
        this.leftVectoringLength = leftVectoringLength;
    }

    public double getLeftBaselegLength() {
        return leftBaselegLength;
    }

    public void setLeftBaselegLength(double leftBaselegLength) {
        this.leftBaselegLength = leftBaselegLength;
    }

    public double getRightVectoringCLStart() {
        return rightVectoringCLStart;
    }

    public void setRightVectoringCLStart(double rightVectoringCLStart) {
        this.rightVectoringCLStart = rightVectoringCLStart;
    }

    public double getRightVectoringLength() {
        return rightVectoringLength;
    }

    public void setRightVectoringLength(double rightVectoringLength) {
        this.rightVectoringLength = rightVectoringLength;
    }

    public double getRightVectoringAngle() {
        return rightVectoringAngle;
    }

    public void setRightVectoringAngle(double rightVectoringAngle) {
        this.rightVectoringAngle = rightVectoringAngle;
    }

    public double getRightBaselegLength() {
        return rightBaselegLength;
    }

    public void setRightBaselegLength(double rightBaselegLength) {
        this.rightBaselegLength = rightBaselegLength;
    }

    public boolean isSymetric() {
        return symetric;
    }

    public void setSymetric(boolean symetric) {
        this.symetric = symetric;
    }

    public boolean isRightBaseEnabled() {
        return rightBaseEnabled;
    }

    public void setRightBaseEnabled(boolean rightBaseEnabled) {
        this.rightBaseEnabled = rightBaseEnabled;
    }

    public boolean isLeftBaseEnabled() {
        return leftBaseEnabled;
    }

    public void setLeftBaseEnabled(boolean leftBaseEnabled) {
        this.leftBaseEnabled = leftBaseEnabled;
    }

    public void addValuesToProperties(Properties p) {
        p.setProperty("rwd." + rwCode + ".extCenterlineStart", Double.toString(extCenterlineStart));
        p.setProperty("rwd." + rwCode + ".extCenterlineLength", Double.toString(extCenterlineLength));

        p.setProperty("rwd." + rwCode + ".majorDMStart", Double.toString(majorDMStart));
        p.setProperty("rwd." + rwCode + ".majorDMEnd", Double.toString(majorDMEnd));
        p.setProperty("rwd." + rwCode + ".majorDMInterval", Double.toString(majorDMInterval));
        p.setProperty("rwd." + rwCode + ".majorDMTickLength", Double.toString(majorDMTickLength));

        p.setProperty("rwd." + rwCode + ".minorDMStart", Double.toString(minorDMStart));
        p.setProperty("rwd." + rwCode + ".minorDMEnd", Double.toString(minorDMEnd));
        p.setProperty("rwd." + rwCode + ".minorDMInterval", Double.toString(minorDMInterval));
        p.setProperty("rwd." + rwCode + ".minorDMTickLength", Double.toString(minorDMTickLength));

        p.setProperty("rwd." + rwCode + ".leftVectoringCLStart", Double.toString(leftVectoringCLStart));
        p.setProperty("rwd." + rwCode + ".leftVectoringAngle", Double.toString(leftVectoringAngle));
        p.setProperty("rwd." + rwCode + ".leftVectoringLength", Double.toString(leftVectoringLength));
        p.setProperty("rwd." + rwCode + ".leftBaselegLength", Double.toString(leftBaselegLength));

        p.setProperty("rwd." + rwCode + ".rightVectoringCLStart", Double.toString(rightVectoringCLStart));
        p.setProperty("rwd." + rwCode + ".rightVectoringAngle", Double.toString(rightVectoringAngle));
        p.setProperty("rwd." + rwCode + ".rightVectoringLength", Double.toString(rightVectoringLength));
        p.setProperty("rwd." + rwCode + ".rightBaselegLength", Double.toString(rightBaselegLength));

        p.setProperty("rwd." + rwCode + ".symetric", symetric ? "true" : "false");
        p.setProperty("rwd." + rwCode + ".rightBaseEnabled", rightBaseEnabled ? "true" : "false");
        p.setProperty("rwd." + rwCode + ".leftBaseEnabled", leftBaseEnabled ? "true" : "false");
    }

    public void setValuesFromProperties(Properties p) {
        extCenterlineStart = Double.parseDouble(p.getProperty("rwd." + rwCode + ".extCenterlineStart"));
        extCenterlineLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".extCenterlineLength"));

        majorDMStart = Double.parseDouble(p.getProperty("rwd." + rwCode + ".majorDMStart"));
        majorDMEnd = Double.parseDouble(p.getProperty("rwd." + rwCode + ".majorDMEnd"));
        majorDMInterval = Double.parseDouble(p.getProperty("rwd." + rwCode + ".majorDMInterval"));
        majorDMTickLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".majorDMTickLength"));

        minorDMStart = Double.parseDouble(p.getProperty("rwd." + rwCode + ".minorDMStart"));
        minorDMEnd = Double.parseDouble(p.getProperty("rwd." + rwCode + ".minorDMEnd"));
        minorDMInterval = Double.parseDouble(p.getProperty("rwd." + rwCode + ".minorDMInterval"));
        minorDMTickLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".minorDMTickLength"));

        leftVectoringCLStart = Double.parseDouble(p.getProperty("rwd." + rwCode + ".leftVectoringCLStart"));
        leftVectoringAngle = Double.parseDouble(p.getProperty("rwd." + rwCode + ".leftVectoringAngle"));
        leftVectoringLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".leftVectoringLength"));
        leftBaselegLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".leftBaselegLength"));

        rightVectoringCLStart = Double.parseDouble(p.getProperty("rwd." + rwCode + ".rightVectoringCLStart"));
        rightVectoringAngle = Double.parseDouble(p.getProperty("rwd." + rwCode + ".rightVectoringAngle"));
        rightVectoringLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".rightVectoringLength"));
        rightBaselegLength = Double.parseDouble(p.getProperty("rwd." + rwCode + ".rightBaselegLength"));

        symetric = "true".equals(p.getProperty("rwd." + rwCode + ".symetric"));
        rightBaseEnabled = "true".equals(p.getProperty("rwd." + rwCode + ".rightBaseEnabled"));
        leftBaseEnabled = "true".equals(p.getProperty("rwd." + rwCode + ".leftBaseEnabled"));
        
    }
}
