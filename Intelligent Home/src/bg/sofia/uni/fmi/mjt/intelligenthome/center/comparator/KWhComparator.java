package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.LocalDateTime;
import java.util.Comparator;

public class KWhComparator implements Comparator<IoTDevice> {
    @Override
    public int compare(IoTDevice first, IoTDevice second) {
        int timeDiffFirst = LocalDateTime.now().getHour() - first.getInstallationDateTime().getHour();
        int timeDiffSecond = LocalDateTime.now().getHour() - second.getInstallationDateTime().getHour();

        return Double.compare(
                first.getPowerConsumption() * timeDiffFirst,
                second.getPowerConsumption() * timeDiffSecond
        );
    }
}
