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

public class TestCameraManagerLogitech {
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
		File videoFile = new  File(TestCameraManagerLogitech.class.getResource(videoPath).getFile());
		
		CameraManager cameraManager;
		cameraManager = new CameraManager(videoFile, processingLock, mockManager, config, sectorStatuses, 
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
	public void testLogitechIndoorGreen() {
		List<Shot> shots = findShots("/shotsearcher/logitech-indoor-green.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.GREEN, shot.getColor());
	}
	
	
	@Test
	public void testLogitechOutdoorGreen() {
		List<Shot> shots = findShots("/shotsearcher/logitech-outdoor-green.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.GREEN, shot.getColor());
	}
	
	
	@Test
	public void testLogitechOutdoorGreen2() {
		List<Shot> shots = findShots("/shotsearcher/logitech-outdoor-green-2.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.GREEN, shot.getColor());
	}
	
	@Test
	public void testLogitechSafariGreen() {
		List<Shot> shots = findShots("/shotsearcher/logitech-safari-green.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.GREEN, shot.getColor());
	}
	
	
	@Test
	public void testLogitechIndoorRed() {
		List<Shot> shots = findShots("/shotsearcher/logitech-indoor-red.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.RED, shot.getColor());
	}
	
	
	@Test
	public void testLogitechOutdoorRed() {
		List<Shot> shots = findShots("/shotsearcher/logitech-outdoor-red.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.RED, shot.getColor());
	}
	
	
	@Test
	public void testLogitechSafariRed() {
		List<Shot> shots = findShots("/shotsearcher/logitech-safari-red.avi", Optional.empty());
		
		assertEquals(9, shots.size());
		
		for (Shot shot : shots)
			assertEquals(Color.RED, shot.getColor());
	}
}