package sk.upjs.zirro.fingerprint.search;

import sk.upjs.zirro.fpm10sensor.FingerprintSensor;
import sk.upjs.zirro.fpm10sensor.FingerprintSensor.SearchResult;
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
            SearchResult result = sensor.searchActivity(hal);

            if (result == null) {
                System.out.println("No match!");
            } else {
                System.out.println("Found id : " + result.getId() + ", matchScore : " + result.getMatchScore());
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            sensor.close();
        }
    }
}

