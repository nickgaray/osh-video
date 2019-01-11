import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sensorhub.api.common.Event;
import org.sensorhub.api.common.IEventListener;
import org.sensorhub.api.sensor.ISensorDataInterface;
import org.sensorhub.api.sensor.SensorDataEvent;
import org.sensorhub.impl.sensor.kinect.KinectConfig;
import org.sensorhub.impl.sensor.kinect.KinectConfig.Mode;
import org.sensorhub.impl.sensor.kinect.KinectSensor;

public class TestKinectDriver implements IEventListener {

	private KinectConfig config = null;

	private KinectSensor driver = null;

	private static final int MAX_FRAMES = 100;

	private int frameCount;

	private KinectDisplayFrame displayFrame;

	@Before
	public void init() throws Exception {

		config = new KinectConfig();

		config.tiltAngle = 0.0;

		driver = new KinectSensor();

		displayFrame = new KinectDisplayFrame();

		frameCount = 0;
	}

	@After
	public void stop() throws Exception {

		displayFrame.dispose();

		driver.requestStop();
	}

	@Test
	public void testDepth() throws Exception {

		config.videoMode = Mode.DEPTH;
		config.samplingTime = 0;
		config.pointCloudScaleDownFactor = 10;

		driver.setConfiguration(config);
		driver.requestInit(false);
		driver.requestStart();

		displayFrame.initialize("Depth Test", config, Mode.DEPTH, false);

		// register listener on data interface
		ISensorDataInterface di = driver.getObservationOutputs().values().iterator().next();

		assertTrue("No video output", di != null);

		di.registerListener(this);

		// start capture and wait until we receive the first frame
		synchronized (this) {

			while (frameCount < MAX_FRAMES) {

				this.wait();
			}
		}

		di.unregisterListener(this);
	}

	@Test
	public void testRgb() throws Exception {

		config.videoMode = Mode.VIDEO;

		displayFrame.initialize("RGB Test", config.frameWidth, config.frameHeight, Mode.VIDEO, false);

		driver.setConfiguration(config);
		driver.requestInit(false);
		driver.requestStart();

		// register listener on data interface
		ISensorDataInterface di = driver.getObservationOutputs().values().iterator().next();

		assertTrue("No video output", di != null);

		di.registerListener(this);

		// start capture and wait until we receive the first frame
		synchronized (this) {
			
			while (frameCount < MAX_FRAMES) {

				this.wait();
			}
		}

		di.unregisterListener(this);
	}

	@Test
	public void testIr() throws Exception {

		config.videoMode = Mode.IR;

		displayFrame.initialize("IR Test", config.frameWidth, config.frameHeight, Mode.IR, false);

		driver.setConfiguration(config);
		driver.requestInit(false);
		driver.requestStart();

		// register listener on data interface
		ISensorDataInterface di = driver.getObservationOutputs().values().iterator().next();

		assertTrue("No video output", di != null);

		di.registerListener(this);

		// start capture and wait until we receive the first frame
		synchronized (this) {
			
			while (frameCount < MAX_FRAMES) {

				this.wait();
			}
		}

		di.unregisterListener(this);
	}
	
	@Test
	public void testRgbMjpeg() throws Exception {

		config.videoMode = Mode.VIDEO;
		config.jpegVideoOutput = true;

		displayFrame.initialize("MJPEG Test", config.frameWidth, config.frameHeight, Mode.VIDEO, true);

		driver.setConfiguration(config);
		driver.requestInit(false);
		driver.requestStart();

		// register listener on data interface
		ISensorDataInterface di = driver.getObservationOutputs().values().iterator().next();

		assertTrue("No video output", di != null);

		di.registerListener(this);

		// start capture and wait until we receive the first frame
		synchronized (this) {
			
			while (frameCount < MAX_FRAMES) {

				this.wait();
			}
		}

		di.unregisterListener(this);
	}

	@Override
	public void handleEvent(Event<?> e) {

		assertTrue(e instanceof SensorDataEvent);
		SensorDataEvent newDataEvent = (SensorDataEvent) e;

		displayFrame.drawFrame(newDataEvent.getRecords()[0]);

		frameCount++;

		synchronized (this) {

			this.notify();
		}
	}
}
