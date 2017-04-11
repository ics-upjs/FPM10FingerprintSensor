package sk.upjs.zirro.fpm10sensor;

public class FingerprintSensorException extends RuntimeException {

	private static final long serialVersionUID = -4901294379612450370L;

	public FingerprintSensorException() {
		super();
	}

	public FingerprintSensorException(String message, Throwable cause) {
		super(message, cause);
	}

	public FingerprintSensorException(String message) {
		super(message);
	}

	public FingerprintSensorException(Throwable cause) {
		super(cause);
	}

}
