package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {
    @Mock
    private DeviceStorage storage;

    @InjectMocks
    private IntelligentHomeCenter intelligentHomeCenter;

    @Test
    void testRegisterWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.register(null),
                "When device is passed as null, register should throw IllegalArgumentException."
        );
    }

    @Test
    void testRegisterWithAlreadyRegisteredDevice() {
        IoTDevice device = mock(IoTDevice.class);

        when(storage.containsDevice(device.getId())).thenReturn(true);

        assertThrows(
                DeviceAlreadyRegisteredException.class,
                () -> intelligentHomeCenter.register(device),
                "When device is already registered, register should throw DeviceAlreadyRegisteredException."
        );

        verify(storage, times(1)).containsDevice(device.getId());
    }

    @Test
    void testRegisterWithUnregisteredDevice() throws DeviceAlreadyRegisteredException {
        IoTDevice device = mock(IoTDevice.class);

        when(storage.containsDevice(device.getId())).thenReturn(false);

        doNothing().when(storage).addDevice(device);

        intelligentHomeCenter.register(device);

        verify(storage, times(1)).addDevice(device);
    }

    @Test
    void testUnregisterWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.unregister(null),
                "When device is passed as null, unregister should throw IllegalArgumentException."
        );
    }

    @Test
    void testUnregisterWithUnregisteredDevice() {
        IoTDevice device = mock(IoTDevice.class);

        when(storage.containsDevice(device.getId())).thenReturn(false);

        assertThrows(
                DeviceNotFoundException.class,
                () -> intelligentHomeCenter.unregister(device),
                "When device is not registered, unregister should throw DeviceNotFoundException."
        );

        verify(storage, times(1)).containsDevice(device.getId());
    }

    @Test
    void testUnregisterWithAlreadyRegisteredDevice() throws DeviceNotFoundException {
        IoTDevice device = mock(IoTDevice.class);

        when(storage.containsDevice(device.getId())).thenReturn(true);

        doNothing().when(storage).removeDevice(device);

        intelligentHomeCenter.unregister(device);

        verify(storage, times(1)).removeDevice(device);
    }

    @Test
    void testGetDeviceByIdWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceById(null),
                "When id is passed as null, getDeviceById should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetDeviceByIdWithBlankString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceById(""),
                "When id is passed empty, getDeviceById should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetDeviceByIdWithUnregisteredDevice() {
        when(storage.containsDevice("1")).thenReturn(false);

        assertThrows(
                DeviceNotFoundException.class,
                () -> intelligentHomeCenter.getDeviceById("1"),
                "When device is not registered, getDeviceById should throw DeviceNotFoundException."
        );

        verify(storage, times(1)).containsDevice("1");
    }

    @Test
    void testGetDeviceByIdWithAlreadyRegisteredDevice() throws DeviceNotFoundException {
        IoTDevice device = mock(IoTDevice.class);

        when(storage.containsDevice("1")).thenReturn(true);
        when(storage.getDeviceById("1")).thenReturn(device);

        assertEquals(
                device,
                intelligentHomeCenter.getDeviceById("1"),
                "When device is registered, getDeviceById should return the right device."
        );

        verify(storage, times(1)).containsDevice("1");
        verify(storage, times(1)).getDeviceById("1");
    }

    @Test
    void testGetDeviceQuantityPerTypeWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceQuantityPerType(null),
                "When deviceType is null, getDeviceQuantityPerType should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetDeviceQuantityPerTypeWithValidDeviceType() {
        when(storage.getDevicesQuantityByType(DeviceType.BULB)).thenReturn(2);

        assertEquals(
                2,
                intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.BULB),
                "When deviceType is valid, getDeviceQuantityPerType should return the right quantity."
        );

        verify(storage, times(1)).getDevicesQuantityByType(DeviceType.BULB);
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionWithNegativeNum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.getTopNDevicesByPowerConsumption(-1),
                "When number is negative, getTopNDevicesByPowerConsumption should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionWithNumberBiggerThanTheSize() {
        Collection<String> devices = mock(Collection.class);

        when(storage.size()).thenReturn(2);
        when(storage.getAllDevicesByName()).thenReturn(devices);

        assertEquals(
                devices,
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(3),
                "When number is bigger than stored devices, getTopNDevicesByPowerConsumption should return all the devices."
        );

        verify(storage, times(1)).getAllDevicesByName();
        verify(storage, times(1)).size();
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionWithNumberLessThanTheSize() {
        Collection<String> devices = mock(Collection.class);

        when(storage.size()).thenReturn(4);
        when(storage.getTopNDevicesByPowerConsumption(3)).thenReturn(devices);

        assertEquals(
                devices,
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(3),
                "When number is less than stored devices," +
                        " getTopNDevicesByPowerConsumption should return top n from the devices stored by" +
                        " power consumption."
        );

        verify(storage, times(1)).getTopNDevicesByPowerConsumption(3);
        verify(storage, times(1)).size();
    }

    @Test
    void testGetFirstNDevicesByRegistrationWithNegativeNum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> intelligentHomeCenter.getFirstNDevicesByRegistration(-1),
                "When number is negative, " +
                        "getFirstNDevicesByRegistration should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetFirstNDevicesByRegistrationWithNumberBiggerThanTheSize() {
        Collection<IoTDevice> devices = mock(Collection.class);

        when(storage.size()).thenReturn(2);
        when(storage.getAllDevices()).thenReturn(devices);

        assertEquals(
                devices,
                intelligentHomeCenter.getFirstNDevicesByRegistration(3),
                "When number is less than stored devices, " +
                        "getFirstNDevicesByRegistration should return first n from " +
                        "the devices stored by power consumption."
        );

        verify(storage, times(1)).getAllDevices();
        verify(storage, times(1)).size();
    }

    @Test
    void testGetFirstNDevicesByRegistrationWithNumberLessThanTheSize() {
        Collection<IoTDevice> devices = mock(Collection.class);

        when(storage.size()).thenReturn(4);
        when(storage.getTopNDevicesByRegistrationTime(3)).thenReturn(devices);

        assertEquals(
                devices,
                intelligentHomeCenter.getFirstNDevicesByRegistration(3),
                "When number is less than stored devices, " +
                        "getTopNDevicesByPowerConsumption should " +
                        "return top n power consumption."
        );

        verify(storage, times(1)).getTopNDevicesByRegistrationTime(3);
        verify(storage, times(1)).size();
    }
}
