/**
 * Copyright (C) 2012,2013 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.setup;

import java.util.Properties;

public class RunwayData {

    private volatile boolean repaintNeeded = true;

    private String rwCode;

    private boolean landingEnabled = true;
    private boolean startingEnabled = true;
    
    private double extCenterlineStart = 0;
    private double extCenterlineLength = 100;

    private double majorDMStart = 5;
    private double majorDMEnd = 20;
    private double majorDMInterval = 5;
    private double majorDMTickLength = 1;

    private double minorDMStart = 1;
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

    public boolean isLandingEnabled() {
        return landingEnabled;
    }

    public void setLandingEnabled(boolean landingEnabled) {
        this.landingEnabled = landingEnabled;
    }

    public boolean isStartingEnabled() {
        return startingEnabled;
    }

    public void setStartingEnabled(boolean startingEnabled) {
        this.startingEnabled = startingEnabled;
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
        p.setProperty("rwd." + rwCode + ".landingEnabled", landingEnabled ? "true" : "false");
        p.setProperty("rwd." + rwCode + ".startingEnabled", startingEnabled ? "true" : "false");

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
        if(p.getProperty("rwd." + rwCode + ".extCenterlineStart")!=null) {
            // not existing records default to true!
            landingEnabled = !"false".equals(p.getProperty("rwd." + rwCode + ".landingEnabled"));
            startingEnabled = !"false".equals(p.getProperty("rwd." + rwCode + ".startingEnabled"));

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
    /** returns true if the runway is set to be enabled either for landing or starting */
    public boolean isEnabledAtAll() {
        return landingEnabled || startingEnabled;
    }

    public void copyDataFrom(RunwayData sourceRwd) {
//        landingEnabled = sourceRwd.isLandingEnabled();
//        startingEnabled = sourceRwd.isStartingEnabled();

        extCenterlineStart = sourceRwd.getExtCenterlineStart();
        extCenterlineLength = sourceRwd.getExtCenterlineLength();

        majorDMStart = sourceRwd.getMajorDMStart();
        majorDMEnd = sourceRwd.getMajorDMEnd();
        majorDMInterval = sourceRwd.getMajorDMInterval();
        majorDMTickLength = sourceRwd.getMajorDMTickLength();

        minorDMStart = sourceRwd.getMinorDMStart();
        minorDMEnd = sourceRwd.getMinorDMEnd();
        minorDMInterval = sourceRwd.getMinorDMInterval();
        minorDMTickLength = sourceRwd.getMinorDMTickLength();

        leftVectoringCLStart = sourceRwd.getLeftVectoringCLStart();
        leftVectoringAngle = sourceRwd.getLeftVectoringAngle();
        leftVectoringLength = sourceRwd.getLeftVectoringLength();
        leftBaselegLength = sourceRwd.getLeftBaselegLength();

        rightVectoringCLStart = sourceRwd.getRightVectoringCLStart();
        rightVectoringAngle = sourceRwd.getRightVectoringAngle();
        rightVectoringLength = sourceRwd.getRightVectoringLength();
        rightBaselegLength = sourceRwd.getRightBaselegLength();

//        symetric = sourceRwd.isSymetric();
//        rightBaseEnabled = sourceRwd.isRightBaseEnabled();
//        leftBaseEnabled = sourceRwd.isLeftBaseEnabled();
    }
}
