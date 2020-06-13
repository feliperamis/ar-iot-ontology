package org.upc.edu;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

class WwtpDomain {

    private static WwtpDomain domain;

    public static WwtpDomain getInstance() {
        if (domain == null) {
            try {
                domain = OntologyParser.parse();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WwtpDomain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return domain;
    }

    private float maximumProduction, industryStorageAvailability, wastePerProduction, chanceOfDetectingIllegalDischarge,
            releasedWaterPerTimeUnit, waterReceivedVolume, waterReceivedSolidsConcentration, suspendedSolidsTreated,
            plantStorageAvailability, foodSuspendedSolidsGeneration;
    private int profitPerTonProduced, sanctionPerTonDischarged, costPerCubicMeterTreated, costPerTonOfPollutantTreated,
            timeUnit;

    @Override
    public String toString() {
        return "WwtpDomain{"
                + "maximumProduction=" + maximumProduction
                + ", industryStorageAvailability=" + industryStorageAvailability
                + ", wastePerProduction=" + wastePerProduction
                + ", chanceOfDetectingIllegalDischarge=" + chanceOfDetectingIllegalDischarge
                + ", releasedWaterPerTimeUnit=" + releasedWaterPerTimeUnit
                + ", waterReceivedVolume=" + waterReceivedVolume
                + ", waterReceivedSolidsConcentration=" + waterReceivedSolidsConcentration
                + ", suspendedSolidsTreated=" + suspendedSolidsTreated
                + ", plantStorageAvailability=" + plantStorageAvailability
                + ", profitPerTonProduced=" + profitPerTonProduced
                + ", sanctionPerTonDischarged=" + sanctionPerTonDischarged
                + ", costPerCubicMeterTreated=" + costPerCubicMeterTreated
                + ", costPerTonOfPollutantTreated=" + costPerTonOfPollutantTreated
                + ", timeUnit=" + timeUnit
                + ", foodSuspendedSolidsGeneration=" + foodSuspendedSolidsGeneration
                + '}';
    }

    public float getMaximumProduction() {
        return maximumProduction;
    }

    public void setMaximumProduction(float maximumProduction) {
        this.maximumProduction = maximumProduction;
    }

    public int getProfitPerTonProduced() {
        return profitPerTonProduced;
    }

    public void setProfitPerTonProduced(int profitPerTonProduced) {
        this.profitPerTonProduced = profitPerTonProduced;
    }

    public float getIndustryStorageAvailability() {
        return industryStorageAvailability;
    }

    public void setIndustryStorageAvailability(float industryStorageAvailability) {
        this.industryStorageAvailability = industryStorageAvailability;
    }

    public float getWastePerProduction() {
        return wastePerProduction;
    }

    public void setWastePerProduction(float wastePerProduction) {
        this.wastePerProduction = wastePerProduction;
    }

    public int getSanctionPerTonDischarged() {
        return sanctionPerTonDischarged;
    }

    public void setSanctionPerTonDischarged(int sanctionPerTonDischarged) {
        this.sanctionPerTonDischarged = sanctionPerTonDischarged;
    }

    public float getChanceOfDetectingIllegalDischarge() {
        return chanceOfDetectingIllegalDischarge;
    }

    public void setChanceOfDetectingIllegalDischarge(float chanceOfDetectingIllegalDischarge) {
        this.chanceOfDetectingIllegalDischarge = chanceOfDetectingIllegalDischarge;
    }

    public float getReleasedWaterPerTimeUnit() {
        return releasedWaterPerTimeUnit;
    }

    public void setReleasedWaterPerTimeUnit(float releasedWaterPerTimeUnit) {
        this.releasedWaterPerTimeUnit = releasedWaterPerTimeUnit;
    }

    public float getWaterReceivedVolume() {
        return waterReceivedVolume;
    }

    public void setWaterReceivedVolume(float waterReceivedVolume) {
        this.waterReceivedVolume = waterReceivedVolume;
    }

    public float getWaterReceivedSolidsConcentration() {
        return waterReceivedSolidsConcentration;
    }

    public void setWaterReceivedSolidsConcentration(float waterReceivedSolidsConcentration) {
        this.waterReceivedSolidsConcentration = waterReceivedSolidsConcentration;
    }

    public float getSuspendedSolidsTreated() {
        return suspendedSolidsTreated;
    }

    public void setSuspendedSolidsTreated(float suspendedSolidsTreated) {
        this.suspendedSolidsTreated = suspendedSolidsTreated;
    }

    public float getPlantStorageAvailability() {
        return plantStorageAvailability;
    }

    public void setPlantStorageAvailability(float plantStorageAvailability) {
        this.plantStorageAvailability = plantStorageAvailability;
    }

    public int getCostPerTonOfPollutantTreated() {
        return costPerTonOfPollutantTreated;
    }

    public void setCostPerTonOfPollutantTreated(int costPerTonOfPollutantTreated) {
        this.costPerTonOfPollutantTreated = costPerTonOfPollutantTreated;
    }

    public int getCostPerCubicMeterTreated() {
        return costPerCubicMeterTreated;
    }

    public void setCostPerCubicMeterTreated(int costPerCubicMeterTreated) {
        this.costPerCubicMeterTreated = costPerCubicMeterTreated;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public float getFoodSuspendedSolidsGeneration() {
        return foodSuspendedSolidsGeneration;
    }

    public void setFoodSuspendedSolidsGeneration(float foodSuspendedSolidsGeneration) {
        this.foodSuspendedSolidsGeneration = foodSuspendedSolidsGeneration;
    }

}
