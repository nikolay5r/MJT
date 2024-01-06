package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

//import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.LocalDateTime;
/*import java.util.ArrayList;
import java.util.Collection;*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MapDeviceStorage implements DeviceStorage {
    private Map<LocalDateTime, IoTDevice> devices;

    public MapDeviceStorage() {
        devices = new TreeMap<>();
    }

    @Override
    public int size() {
        return devices.size();
    }

    @Override
    public Collection<String> getAllDevicesByName() {
        throw new UnsupportedOperationException("Needs implementation.");
    }

    @Override
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        throw new UnsupportedOperationException("Needs implementation.");
    }

    @Override
    public Collection<IoTDevice> getTopNDevicesByRegistrationTime(int n) {
        throw new UnsupportedOperationException("Needs implementation.");
    }

    @Override
    public void addDevice(IoTDevice device) {
        devices.put(LocalDateTime.now(), device);
    }

    @Override
    public void removeDevice(IoTDevice device) {
        for (Map.Entry<LocalDateTime, IoTDevice> entry: devices.entrySet()) {
            if (entry.getValue().getId().equals(device.getId())) {
                devices.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean containsDevice(String id) {
        for (IoTDevice device: devices.values()) {
            if (device.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IoTDevice getDeviceById(String id) {
        for (IoTDevice device: devices.values()) {
            if (device.getId().equals(id)) {
                return device;
            }
        }

        return null;
    }

    @Override
    public int getDevicesQuantityByType(DeviceType type) {
        int count = 0;

        for (IoTDevice device: devices.values()) {
            if (device.getType().equals(type)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public Collection<IoTDevice> getAllDevices() {
        return new ArrayList<>(devices.values());
    }
}
