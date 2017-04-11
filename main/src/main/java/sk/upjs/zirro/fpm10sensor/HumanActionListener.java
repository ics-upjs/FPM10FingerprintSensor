package sk.upjs.zirro.fpm10sensor;

public interface HumanActionListener {

	/**
	 * Instructs the human to put finger on the sensor.
	 */
	public void putFinger();

	/**
	 * Instructs the human to remove finger on the sensor.
	 */
	public void removeFinger();

}
