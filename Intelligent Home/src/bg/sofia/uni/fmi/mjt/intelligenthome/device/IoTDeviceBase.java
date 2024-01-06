package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public abstract class IoTDeviceBase implements IoTDevice {
    protected String id;
    protected String name;
    protected double powerConsumption;
    protected LocalDateTime installationDateTime;
    protected DeviceType type;
    private static int numberOfCreatedDevices;

    static {
        numberOfCreatedDevices = 1;
    }

    protected IoTDeviceBase(String name, double powerConsumption, LocalDateTime installationDateTime, DeviceType type) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.type = type;

        this.id = type.getShortName() + "-" + name + "-" + numberOfCreatedDevices;
        incrementNumberOfCreatedDevices();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPowerConsumption() {
        return powerConsumption;
    }

    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    public DeviceType getType() {
        return type;
    }

    private void incrementNumberOfCreatedDevices() {
        numberOfCreatedDevices++;
    }
}
