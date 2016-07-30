package thermostats.base;

import thermostats.manager.HumidityManager;
import thermostats.manager.TemperatureManager;
import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.bridge.mock.MockIOBridge;

/**
 * Created by Zoranor on 2016-07-04.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting application...");
        FirebaseBridge firebaseBridge = new FirebaseBridge("device1");
        firebaseBridge.initialize();

        Log.initialize(firebaseBridge);
        Log.i("main", "Application Started.");

        IOBridge ioBridge = new MockIOBridge();
        TemperatureManager temperatureManager = new TemperatureManager(firebaseBridge, ioBridge);
        temperatureManager.initialize();
        HumidityManager humidityManager = new HumidityManager(firebaseBridge, ioBridge);
        humidityManager.initialize();

        Log.i("main", "Sensors initialized.");
    }
}
