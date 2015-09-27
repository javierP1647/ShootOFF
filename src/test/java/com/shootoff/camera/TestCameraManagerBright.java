package com.shootoff.camera;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;

import org.junit.Before;
import org.junit.Test;

import com.shootoff.camera.ShotDetection.ShotDetectionManager;
import com.shootoff.config.Configuration;
import com.shootoff.config.ConfigurationException;
import com.shootoff.gui.MockCanvasManager;

public class TestCameraManagerBright {
	private Configuration config;
	private MockCanvasManager mockManager;
	private boolean[][] sectorStatuses;
	
	@Before
	public void setUp() throws ConfigurationException {
		config = new Configuration(new String[0]);
		config.setDetectionRate(0);
		config.setDebugMode(true);
		mockManager = new MockCanvasManager(config, true);
		sectorStatuses = new boolean[ShotDetectionManager.SECTOR_ROWS][ShotDetectionManager.SECTOR_COLUMNS];
		
		for (int x = 0; x < ShotDetectionManager.SECTOR_COLUMNS; x++) {
			for (int y = 0; y < ShotDetectionManager.SECTOR_ROWS; y++) {
				sectorStatuses[y][x] = true;
			}
		}
		
	}
	
	private List<Shot> findShots(String videoPath, Optional<Bounds> projectionBounds) {
		Object processingLock = new Object();
		File videoFile = new  File(TestCameraManagerBright.class.getResource(videoPath).getFile());
		CameraManager cameraManager = new CameraManager(videoFile, processingLock, mockManager, config, sectorStatuses,
				projectionBounds);
		
		try {
			synchronized (processingLock) {
				while (!cameraManager.isVideoProcessed())
					processingLock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return mockManager.getShots();
	}
	
	@Test
	// BRIGHT
	public void testPS3EyeHardwareDefaultsBrightRoom() {
		// Turn off the top sectors because they are all just noise.
		/*for (int x = 0; x < ShotSearcher.SECTOR_COLUMNS; x++) {
			sectorStatuses[0][x] = false;
		}*/
		
		List<Shot> shots = findShots("/shotsearcher/ps3eye_hardware_defaults_bright_room.mp4", Optional.empty());
		
		assertEquals(4, shots.size());
		
		assertEquals(236.5, shots.get(0).getX(), 1);
		assertEquals(169.5, shots.get(0).getY(), 1);
		assertEquals(Color.RED, shots.get(0).getColor());

		
		assertEquals(175, shots.get(1).getX(), 1);
		assertEquals(191.5, shots.get(1).getY(), 1);
		assertEquals(Color.RED, shots.get(1).getColor());
		
		assertEquals(229.5, shots.get(2).getX(), 1);
		assertEquals(227.5, shots.get(2).getY(), 1);
		assertEquals(Color.RED, shots.get(2).getColor());
		
		assertEquals(176, shots.get(3).getX(), 1);
		assertEquals(251.5, shots.get(3).getY(), 1);
		assertEquals(Color.RED, shots.get(3).getColor());		
	}	
	
	@Test
	// BRIGHT
	public void testPS3EyeHardwareDefaultsRedLaserRoomLightOnSafari() {
		List<Shot> shots = findShots("/shotsearcher/ps3eye_hardware_defaults_safari_red_laser_lights_on.mp4", 
				Optional.empty());
		
		// This misses the bottom two shots on the water and far left shot in middle row
		
		//assertEquals(7, shots.size());
		assertEquals(9, shots.size());
		
		assertEquals(473.5, shots.get(0).getX(), 1);
		assertEquals(127.0, shots.get(0).getY(), 1);
		assertEquals(Color.RED, shots.get(0).getColor());

		assertEquals(350.5, shots.get(1).getX(), 1);
		assertEquals(142, shots.get(1).getY(), 1);
		assertEquals(Color.RED, shots.get(1).getColor());

		assertEquals(208.5, shots.get(2).getX(), 1);
		assertEquals(115.0, shots.get(2).getY(), 1);
		assertEquals(Color.RED, shots.get(2).getColor());

		
		assertEquals(184.0, shots.get(3).getX(), 1);
		assertEquals(228.5, shots.get(3).getY(), 1);
		assertEquals(Color.RED, shots.get(3).getColor());
		
		assertEquals(311.0, shots.get(4).getX(), 1);
		assertEquals(229, shots.get(4).getY(), 1);
		assertEquals(Color.RED, shots.get(4).getColor());

		assertEquals(468.0, shots.get(5).getX(), 1);
		assertEquals(220.0, shots.get(5).getY(), 1);
		assertEquals(Color.RED, shots.get(5).getColor());

		assertEquals(469.5, shots.get(6).getX(), 1);
		assertEquals(269.0, shots.get(6).getY(), 1);
		assertEquals(Color.RED, shots.get(6).getColor());
	}
	
	@Test
	// BRIGHT
	public void testPS3EyeHardwareDefaultsGreenLaserRoomLightOnSafari() {
		List<Shot> shots = findShots("/shotsearcher/ps3eye_hardware_defaults_safari_green_laser_lights_on.mp4", Optional.empty());
	
		// This gets a few dupes due to sloppy shots (laser pointer was used) and
		// misses the far left shot in the middle row and bottom row
		

		assertEquals(10, shots.size());
		
		assertEquals(464.0, shots.get(0).getX(), 1);
		assertEquals(24.0, shots.get(0).getY(), 1);
		assertEquals(Color.GREEN, shots.get(0).getColor());

		assertEquals(454.0, shots.get(1).getX(), 1);
		assertEquals(102.0, shots.get(1).getY(), 1);
		assertEquals(Color.GREEN, shots.get(1).getColor());

		/*assertEquals(452.5, shots.get(2).getX(), 1);
		assertEquals(103.0, shots.get(2).getY(), 1);
		assertEquals(Color.GREEN, shots.get(2).getColor());*/

		assertEquals(314.0, shots.get(3).getX(), 1);
		assertEquals(98.5, shots.get(3).getY(), 1);
		assertEquals(Color.GREEN, shots.get(3).getColor());

		assertEquals(145.5, shots.get(4).getX(), 1);
		assertEquals(88.0, shots.get(4).getY(), 1);
		assertEquals(Color.GREEN, shots.get(4).getColor());

		// Dupe of the shot above
		/*assertEquals(142.5, shots.get(5).getX(), 1);
		assertEquals(86.0, shots.get(5).getY(), 1);
		assertEquals(Color.GREEN, shots.get(5).getColor());*/

		assertEquals(345.5, shots.get(6).getX(), 1);
		assertEquals(225.0, shots.get(6).getY(), 1);
		assertEquals(Color.GREEN, shots.get(6).getColor());

		assertEquals(488.0, shots.get(7).getX(), 1);
		assertEquals(227.0, shots.get(7).getY(), 1);
		assertEquals(Color.GREEN, shots.get(7).getColor());

		assertEquals(475.5, shots.get(8).getX(), 1);
		assertEquals(312.5, shots.get(8).getY(), 1);
		assertEquals(Color.GREEN, shots.get(8).getColor());

		assertEquals(338.0, shots.get(9).getX(), 1);
		assertEquals(271.0, shots.get(9).getY(), 1);
		assertEquals(Color.GREEN, shots.get(9).getColor());
		

		
	}
}