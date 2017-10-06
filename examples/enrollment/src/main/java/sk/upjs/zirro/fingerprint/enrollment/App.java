package sk.upjs.zirro.fingerprint.enrollment;

import sk.upjs.zirro.fpm10sensor.FingerprintSensor;
import sk.upjs.zirro.fpm10sensor.HumanActionListener;

public class App {

    public static void main(String[] args) {
        FingerprintSensor sensor = new FingerprintSensor("/dev/ttyUSB0");
        sensor.open();
        HumanActionListener hal = new HumanActionListener() {
            @Override
            public void putFinger() {
                System.out.println("Place finger on the sensor.");
            }

            @Override
            public void removeFinger() {
                System.out.println("Remove finger.");
            }

            @Override
            public void waitWhileDataIsTransferring() {
                System.out.println("Wait.");
            }
        };    

        try {
            int fingerprintId = 10;
            sensor.enrollActivity(fingerprintId, hal);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            sensor.close();
        }
    }
}
