package model;

public class SensorUnit {

    private String sensorName;
    private String sensorType;
    private float quantity;
    private String unitName;

    public SensorUnit(String sensorName, String sensorType, float quantity, String unitName) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.quantity = quantity;
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return sensorName + "(" + sensorType + "): with " + quantity + " " + unitName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnitName() {
        return unitName;
    }
}
