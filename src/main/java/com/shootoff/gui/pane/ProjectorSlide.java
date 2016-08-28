package com.shootoff.gui.pane;

import java.util.Optional;

import com.shootoff.camera.CameraManager;
import com.shootoff.config.Configuration;
import com.shootoff.gui.CalibrationConfigurator;
import com.shootoff.gui.CalibrationManager;
import com.shootoff.gui.CalibrationOption;
import com.shootoff.gui.MirroredCanvasManager;
import com.shootoff.gui.Resetter;
import com.shootoff.plugins.ProjectorTrainingExerciseBase;
import com.shootoff.targets.CameraViews;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ProjectorSlide extends Slide implements CalibrationConfigurator {
	private final Pane parentControls;
	private final Pane parentBody;
	private final Configuration config;
	private final CameraViews cameraViews;
	private final Stage shootOffStage;
	private final Resetter resetter;
	private final ExerciseSlide exerciseSlide;
	private final Button calibrateButton;
	
	private ArenaBackgroundsSlide backgroundsSlide;
	
	private ProjectorArenaPane arenaPane;
	private Optional<CalibrationManager> calibrationManager = Optional.empty();
	
	public ProjectorSlide(Pane parentControls, Pane parentBody, Configuration config, CameraViews cameraViews,
			Stage shootOffStage, Resetter resetter, ExerciseSlide exerciseSlide) {
		super(parentControls, parentBody);
		
		this.parentControls = parentControls;
		this.parentBody = parentBody;
		this.config = config;
		this.cameraViews = cameraViews;
		this.shootOffStage = shootOffStage;
		this.resetter = resetter;
		this.exerciseSlide = exerciseSlide;
		
		calibrateButton = addSlideControlButton("Calibrate", (event) -> {
			if (!calibrationManager.isPresent()) return;

			if (!calibrationManager.get().isCalibrating()) {
				calibrationManager.get().enableCalibration();
			} else {
				calibrationManager.get().stopCalibration();
			}
		});
		
		addSlideControlButton("Background", (event) -> {
			backgroundsSlide.showControls();
			backgroundsSlide.showBody();
		});
		
		addSlideControlButton("Courses", (event) -> {
			final ArenaCoursesSlide coursesSlide = new ArenaCoursesSlide(parentControls, 
					parentBody, arenaPane, shootOffStage);
			coursesSlide.setOnSlideHidden(() -> {
				if (coursesSlide.choseCourse()) {
					hide();
				}
			});
			coursesSlide.showControls();
			coursesSlide.showBody();
		});
	}
	
	@Override
	public CalibrationOption getCalibratedFeedBehavior() {
		return config.getCalibratedFeedBehavior();
	}
	
	@Override 
	public void calibratedFeedBehaviorsChanged() {
		if (calibrationManager.isPresent())
			calibrationManager.get().configureArenaCamera(config.getCalibratedFeedBehavior());
		
		if (arenaPane != null) 
			arenaPane.getCanvasManager().setShowShots(config.showArenaShotMarkers());
	}
	
	@Override
	public void toggleCalibrating() {
		final Runnable toggleCalibrationAction = () -> {
			if (calibrateButton.getText().equals("Calibrate"))
				calibrateButton.setText("Stop Calibrating");
			else
				calibrateButton.setText("Calibrate");
		};

		if (Platform.isFxApplicationThread()) {
			toggleCalibrationAction.run();
		} else {
			Platform.runLater(toggleCalibrationAction);
		}
	}
	
	public ProjectorArenaPane getArenaPane() {
		return arenaPane;
	}
	
	public Optional<CalibrationManager> getCalibrationManager() {
		return calibrationManager;
	}
	
	public void startArena() {
		if (arenaPane == null) {
			final Stage arenaStage = new Stage();

			arenaPane = new ProjectorArenaPane(arenaStage, shootOffStage, config, resetter);
			
			final ProjectorArenaPane arenaTabPane = new ProjectorArenaPane(arenaStage, shootOffStage, config, resetter); 
			cameraViews.addCameraView("Arena", arenaTabPane, arenaTabPane.getCanvasManager());
			
			final MirroredCanvasManager projectorCanvasManager = (MirroredCanvasManager) arenaPane.getCanvasManager();
			final MirroredCanvasManager tabCanvasManager = (MirroredCanvasManager) arenaTabPane.getCanvasManager();
			
			projectorCanvasManager.setMirroredManager(tabCanvasManager);
			tabCanvasManager.setMirroredManager(projectorCanvasManager);
			projectorCanvasManager.updateBackground(null, Optional.empty());
			tabCanvasManager.setCameraManager(new CameraManager(tabCanvasManager, config));
			
			arenaStage.setTitle("Projector Arena");
			arenaStage.setScene(new Scene(arenaPane));
			arenaStage.setFullScreenExitHint("");

			final CameraManager calibratingCameraManager = cameraViews.getSelectedCameraManager();
			calibrationManager = Optional.of(new CalibrationManager(this, calibratingCameraManager, arenaPane));
			arenaPane.setCalibrationManager(calibrationManager.get());
			
			exerciseSlide.toggleProjectorExercises(false);
			arenaPane.getCanvasManager().setShowShots(config.showArenaShotMarkers());

			calibrateButton.fire();
			
			backgroundsSlide = new ArenaBackgroundsSlide(parentControls, 
					parentBody, arenaPane, shootOffStage);
			backgroundsSlide.setOnSlideHidden(() -> { 
				if (backgroundsSlide.choseBackground()) {
					backgroundsSlide.setChoseBackground(false);
					hide(); 
				}
			});
			
			arenaPane.toggleArena();
			arenaPane.autoPlaceArena();
			
			arenaStage.setOnCloseRequest((e) -> {
				cameraViews.removeCameraView("Arena");
				
				if (config.getExercise().isPresent()
						&& config.getExercise().get() instanceof ProjectorTrainingExerciseBase) {
					exerciseSlide.toggleProjectorExercises(true);
				}
				
				if (calibrationManager.isPresent()) {
					if (calibrationManager.get().isCalibrating()) {
						calibrationManager.get().stopCalibration();
					} else {
						calibrationManager.get().arenaClosing();
					}
				}
				
				arenaPane.setFeedCanvasManager(null);
				arenaPane = null;
			});
		}
	}
	
	public void closeArena() {
		if (arenaPane != null) {
			arenaPane.getCanvasManager().close();
			arenaPane.close();
		}
	}
}