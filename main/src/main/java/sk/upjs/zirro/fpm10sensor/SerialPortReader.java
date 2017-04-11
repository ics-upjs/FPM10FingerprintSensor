package sk.upjs.zirro.fpm10sensor;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Helper class for reading and buffering data from a serial port.
 */
class SerialPortReader {

	/**
	 * The serial port.
	 */
	private final SerialPort serialPort;

	/**
	 * Nanoseconds to sleep when no data are available.
	 */
	private final int nanosSleep;

	/**
	 * Milliseconds to sleep when no data are available.
	 */
	private final long millisSleep;

	/**
	 * The receive buffer.
	 */
	private byte[] buffer = new byte[128];

	/**
	 * The position in buffer where next byte will be written.
	 */
	private int writeIdx = 0;

	/**
	 * The position in buffer from where a next byte will be read.
	 */
	private int readIdx = 0;

	/**
	 * Number of bytes available in the buffer.
	 */
	private int availableBytes = 0;

	/**
	 * Constructs buffered serial port reader.
	 * 
	 * @param serialPort
	 *            the serial port.
	 * @param baudRate
	 *            the baud rate of the serial port.
	 */
	public SerialPortReader(SerialPort serialPort, long baudRate) {
		this.serialPort = serialPort;

		// compute sleep interval with respect to baud rate.
		long nanosPerByte = Math.max((1_000_000_000L / baudRate) * 8, 100);
		nanosSleep = (int) (nanosPerByte % 1_000_000);
		millisSleep = nanosPerByte / 1_000_000;
	}

	/**
	 * Reads a single byte from the serial port.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @return the read byte or -1 if the operation failed (e.g., timeout
	 *         expired, thread has been interrupted, etc.).
	 * @throws SerialPortException
	 *             thrown when reading from port failed.
	 */
	public int readByte(long timeout) throws SerialPortException {
		ensureBytes(1, timeout);
		if (availableBytes == 0) {
			return -1;
		}

		return readByteFromBuffer();
	}

	/**
	 * Reads given number of bytes from the serial port.
	 * 
	 * @param count
	 *            the number of requested received bytes.
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @return the array with received data or null if the operation failed
	 *         (e.g., timeout expired, thread has been interrupted, etc.).
	 * @throws SerialPortException
	 *             thrown when reading from port failed.
	 */
	public int[] readBytes(int count, long timeout) throws SerialPortException {
		ensureBytes(count, timeout);
		if (availableBytes < count) {
			return null;
		}

		int[] result = new int[count];
		for (int i = 0; i < count; i++) {
			result[i] = readByteFromBuffer();
		}

		return result;
	}

	/**
	 * Reads a single byte from the read buffer.
	 * 
	 * @return the byte (as integer).
	 */
	private int readByteFromBuffer() {
		if (availableBytes == 0) {
			throw new RuntimeException("Invalid state of the SerialPortReader.");
		}

		int result = buffer[readIdx] & 0xFF;
		availableBytes--;
		readIdx++;
		if (readIdx >= buffer.length) {
			readIdx = 0;
		}

		return result;
	}

	/**
	 * Ensures given number of bytes available in the read buffer.
	 * 
	 * @param byteCount
	 *            the count of bytes.
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @throws SerialPortException
	 *             thrown when reading from port failed.
	 */
	private void ensureBytes(int byteCount, long timeout) throws SerialPortException {
		if (byteCount <= availableBytes) {
			return;
		}

		// convert timeout to nanoseconds
		timeout = timeout * 1_000_000;

		// read bytes
		long startTime = System.nanoTime();
		while (byteCount > availableBytes) {
			if (System.nanoTime() - startTime > timeout) {
				return;
			}

			byte[] data = serialPort.readBytes();
			if (data == null) {
				try {
					Thread.sleep(millisSleep, nanosSleep);
				} catch (InterruptedException exception) {
					return;
				}

				continue;
			}

			for (byte b : data) {
				buffer[writeIdx] = b;
				availableBytes++;
				writeIdx++;
				if (writeIdx >= buffer.length) {
					writeIdx = 0;
				}

				if (availableBytes == buffer.length) {
					enlargeBuffer();
				}
			}
		}
	}

	/**
	 * Enlarges the receive buffer.
	 */
	private void enlargeBuffer() {
		byte[] newBuffer = new byte[buffer.length * 2];
		for (int i = 0; i < availableBytes; i++) {
			newBuffer[i] = buffer[readIdx];
			readIdx = (readIdx + 1) % buffer.length;
		}

		buffer = newBuffer;
		readIdx = 0;
		writeIdx = availableBytes;
	}
}
