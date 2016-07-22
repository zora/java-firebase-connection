package thermostats.base;

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
        FirebaseBridge firebaseBridge = new FirebaseBridge();
        firebaseBridge.initialize();

        IOBridge ioBridge = new MockIOBridge();
        TemperatureManager temperatureManager = new TemperatureManager(firebaseBridge, ioBridge);
        temperatureManager.initialize();
    }
}
