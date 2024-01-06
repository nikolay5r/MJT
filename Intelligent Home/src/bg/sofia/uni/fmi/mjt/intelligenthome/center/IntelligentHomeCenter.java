package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public class IntelligentHomeCenter {

    private DeviceStorage storage;

    public IntelligentHomeCenter(DeviceStorage storage) {
        this.storage = storage;
    }

    /**
     * Adds a @device to the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(IoTDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("The device you entered was null.");
        }

        if (storage.containsDevice(device.getId())) {
            throw new DeviceAlreadyRegisteredException("The device is already registered.");
        }

        storage.addDevice(device);
    }

    /**
     * Removes the @device from the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException in case the @device is not found.
     */
    public void unregister(IoTDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("The device you entered was null.");
        }

        if (!storage.containsDevice(device.getId())) {
            throw new DeviceNotFoundException("The device is not registered.");
        }

        storage.removeDevice(device);
    }

    /**
     * Returns a IoTDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null or blank.
     * @throws DeviceNotFoundException in case device with ID @id is not found.
     */
    public IoTDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("The id is null.");
        }

        if (id.isBlank()) {
            throw new IllegalArgumentException("The id is empty.");
        }

        if (!storage.containsDevice(id)) {
            throw new DeviceNotFoundException("The device is not registered.");
        }

        return storage.getDeviceById(id);
    }

    /**
     * Returns the total number of devices with type @type registered in IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("The device type is null.");
        }

        return storage.getDevicesQuantityByType(type);
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     *
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     *
     * If @n exceeds the total number of devices, return all devices available sorted by the gi ven criterion.
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be negative number.");
        }

        if (n > storage.size()) {
            return storage.getAllDevicesByName();
        }

        return storage.getTopNDevicesByPowerConsumption(n);
    }

    /**
     * Returns a collection of the first @n registered devices, i.e. the first @n that were added
     * in the IntelligentHomeCenter (registration != installation).
     *
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<IoTDevice> getFirstNDevicesByRegistration(int n)   {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be negative number.");
        }

        if (n > storage.size()) {
            return storage.getAllDevices();
        }

        return storage.getTopNDevicesByRegistrationTime(n);
    }
}