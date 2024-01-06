package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.util.Collection;

public interface DeviceStorage {
    void addDevice(IoTDevice device);

    void removeDevice(IoTDevice device);

    boolean containsDevice(String id);

    IoTDevice getDeviceById(String id);

    int getDevicesQuantityByType(DeviceType type);

    int size();

    Collection<IoTDevice> getAllDevices();

    Collection<String> getAllDevicesByName();

    Collection<String> getTopNDevicesByPowerConsumption(int n);

    Collection<IoTDevice> getTopNDevicesByRegistrationTime(int n);
}
