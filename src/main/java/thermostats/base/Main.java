package thermostats.base;

import thermostats.manager.*;
import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.bridge.mock.MockIOBridge;

/**
 * Created by Zoranor on 2016-07-04.
 */
public class Main {

    private static final String deviceID = "device1";
    private static final String TAG = "main";

    public static void main(String[] args) {
        System.out.println("Starting application...");
        FirebaseBridge firebaseBridge = new FirebaseBridge(deviceID);
        firebaseBridge.initialize();

        Log.initialize(firebaseBridge);
        Log.i(TAG, "Application Started on device: " + deviceID);

        IOBridge ioBridge = new MockIOBridge();
        TemperatureManager temperatureManager = new TemperatureManager(firebaseBridge, ioBridge);
        temperatureManager.initialize();
        HumidityManager humidityManager = new HumidityManager(firebaseBridge, ioBridge);
        humidityManager.initialize();
        CpuUsageManager cpuUsageManager = new CpuUsageManager(firebaseBridge, ioBridge);
        cpuUsageManager.initialize();
        MemoryUsageManager memoryUsageManager = new MemoryUsageManager(firebaseBridge, ioBridge);
        memoryUsageManager.initialize();
        DiskUsageManager diskUsageManager = new DiskUsageManager(firebaseBridge, ioBridge);
        diskUsageManager.initialize();

        Log.i(TAG, "All Sensors initialized for device: " + deviceID);
    }
}
