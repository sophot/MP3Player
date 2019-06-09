package application;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import jaco.mp3.player.MP3Player;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class Controller {

	@FXML
	private AnchorPane mainWindow;
	@FXML
	private JFXTreeTableView<Song> songTable;
	@FXML
	private TreeTableColumn<Song, String> songNameColumn;
	@FXML
	private TreeTableColumn<Song, String> timeColumn;
	@FXML
	private TreeTableColumn<Song, String> artistColumn;
	@FXML
	private TreeTableColumn<Song, String> albumColumn;
	@FXML
	private ImageView muteIcon;
	@FXML
	private ImageView volumeLowIcon;
	@FXML
	private ImageView volumeMidIcon;
	@FXML
	private ImageView volumeHighIcon;
	@FXML
	private Label volumeValue;
	@FXML
	private Label currentDuration;
	@FXML
	private Label totalDuration;
	@FXML
	private JFXSlider volumeSlider;
	@FXML
	private JFXProgressBar songProgress;
	@FXML
	private ImageView previousBtn;
	@FXML
	private ImageView pauseBtn;
	@FXML
	private ImageView playBtn;
	@FXML
	private ImageView nextSongBtn;
	@FXML
	private ImageView repeatBtn;
	@FXML
	private ImageView shuffleBtn;
	@FXML
	private ImageView showMoreBtn;
	@FXML
	private ImageView openFileBtn;
	@FXML
	private ImageView addSongBtn;
	@FXML
	private ImageView removeSongBtn;
	@FXML
	private ImageView openPlaylistBtn;
	@FXML
	private ImageView newPlaylistBtn;
	@FXML
	private ImageView savePlaylistBtn;
	@FXML
	private ImageView deletePlaylistBtn;
	@FXML
	private Pane minimizeBtn;
	@FXML
	private Pane closeBtn;
	@FXML
	private Pane moreFunctionPane;

	private List<MP3Player> playlist;
	private MP3Player mp3player;
	private Stage stage;

	private boolean showingMore;
	private long startTime;
	private long currTime;
	private long playedTime = 0;
	private double volume = 0.3;
	private double xOffset = 0;
	private double yOffset = 0;
	private int nextPrev=0;
	private Song currSong;
//	private FadeTransition show;
//	private FadeTransition hide;

	// CONSTRUCTOR
	public Controller() {
		playlist = new ArrayList<>();
		showingMore = false;
		stage = Main.getStage();
	}

	@FXML
	private void initialize() {

		songNameColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Song, String> param) {
						return param.getValue().getValue().songNameProperty();
					}
				});

		timeColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Song, String> param) {
						return param.getValue().getValue().timeProperty();
					}
				});

		artistColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Song, String> param) {
						return param.getValue().getValue().artistNameProperty();
					}
				});

		albumColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Song, String> param) {
						return param.getValue().getValue().albumProperty();
					}
				});

		mainWindow.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = stage.getX() - event.getScreenX();
				yOffset = stage.getY() - event.getScreenY();
			}
		});

		mainWindow.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() + xOffset);
				stage.setY(event.getScreenY() + yOffset);
			}
		});

		closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.exit(0);
			}
		}); // END OF "closeBtn" CLICKED

		minimizeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setIconified(true);
			}
		}); // END OF "minimizeBtn" CLICKED

		showMoreBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (showingMore == false) {
					showTransition(moreFunctionPane);
					showingMore = true;
				} else {
					hideTransition(moreFunctionPane);
					showingMore = false;
				}
			}
		}); // END OF "showMoreBtn" CLICKED

		openFileBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("openFIle clicked");
			}
		}); // END OF "openFileBtn" CLICKED

		addSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("addSong clicked");
			}
		}); // END OF "addSongBtn" CLICKED

		removeSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("removeSong clicked");
			}
		}); // END OF "removeSongBtn" CLICKED

		newPlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("newPlaylist clicked");
			}
		}); // END OF "newPlaylistBtn" CLICKED

		savePlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("savePlaylist clicked");
			}
		}); // END OF "savePlaylistBtn" CLICKED

		deletePlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("deletePlaylist clicked");
			}
		}); // END OF "shuffleBtn" CLICKED


		openPlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("openPlaylist clicked");
				DirectoryChooser dirChooser = new DirectoryChooser();
				File selectedDirectory = dirChooser.showDialog(stage);
				if (selectedDirectory == null) {
					System.out.println("No directory selected!");
				} else {
					if (!playlist.isEmpty()) {
						playlist.clear();
						System.out.println("new array list");
					}
					final TreeItem<Song> root = new RecursiveTreeItem<Song>((songsUrls(selectedDirectory)),
							RecursiveTreeObject::getChildren);
					songTable.getColumns().setAll(songNameColumn, timeColumn, artistColumn, albumColumn);
					songTable.setRoot(root);
					songTable.setShowRoot(false);
					songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

					
					songTable.setOnMouseClicked((MouseEvent e) -> {
						// after select in table mediaplayer status is Ready (not UNKNOWN)
						if ((e.getClickCount() > 0) && (e.getClickCount() < 2)) {
							playSelectedSong();
						}
					});
				}
			}
		}); // END OF "openPlayList"

		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				volume = volumeSlider.getValue() / 100;
				volumeValue.setText(Integer.toString((int) volumeSlider.getValue()));
				updateVolume();
				volumeIconChanger();
			}
		});
	} // end initialize()

	///// SUB FUNCTIONS <<LOADING FILES>>/////
	public ObservableList<Song> songsUrls(File dir) {
		ObservableList<Song> songs = FXCollections.observableArrayList();
		File[] files = dir.listFiles();
		String fileName;
		int i = 0;
		for (File file : files) {

			if (file.isFile()) {
				fileName = file.getName();

				if (fileName.endsWith("mp3")) {
					try {
						i++;
						Mp3File mp3 = new Mp3File(file.getPath());
						ID3v2 tag = mp3.getId3v2Tag();
						Song song = new Song(String.valueOf(i), tag.getTitle(), secToMin(mp3.getLengthInSeconds()),
								tag.getArtist(), tag.getAlbum(), file.getAbsolutePath());
						playlist.add(createPlayer(file.getAbsolutePath()));
						songs.add(song);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		i = 0;
		System.out.println("read " + playlist.size() + " songs");
		return songs;
	}

	public MP3Player createPlayer(String url) {
		url.replace("\\", "/");
		MP3Player player = new MP3Player(new File(url));

		return player;
	}

	public void playSelectedSong() {
		if (songTable.getSelectionModel().getSelectedItem() != null) {
			currSong = songTable.getSelectionModel().getSelectedItem().getValue();

			resetInfo();
			nextPrev = 0;

			playPauseSong();
		} else {
			System.out.println("Found Nth");
		}
	} /// ^^END OF playSelectedSong^^//

	public void playPauseSong() {
		if (currSong != null) {
			System.out.println(currSong.getUrl());
//			File file = new File(song.getUrl());
//			String path = file.getAbsolutePath();
//			path.replace("\\", "/");

			if (mp3player != null) {
//				updateVolume();
				mp3player.stop();
				mp3player = null;
			}

			mp3player = new MP3Player(new File(currSong.getUrl()));
			showPlayIcon();

			volumeValue.setText(String.valueOf((int) volumeSlider.getValue()));
			volumeSlider.setValue(volume * 100);
			updateSongProgress(0);
			updateValues();

			//// ^^ PLAY + PAUSE + NEXT + BACK ^^////
			playBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("play Clicked");
					startTime = System.currentTimeMillis() / 1000;
					mp3player.play();
					showPauseIcon();
				}
			});

			pauseBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("pause Clicked");
					playedTime += currTime - startTime;
					mp3player.pause();
					showPlayIcon();
				}
			});

			previousBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("previousBtn clicked");
					mp3player.stop();
					nextPrev--;
					mp3player = playlist.get(Integer.parseInt(currSong.getID()) + nextPrev);
					resetInfo();
					mp3player.play();
					showPauseIcon();
				}
			});

			nextSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("nextSong clicked");
					mp3player.stop();
					mp3player = playlist.get(Integer.parseInt(currSong.getID()) + nextPrev);
					resetInfo();
					mp3player.play();
					showPauseIcon();
					nextPrev++;
				}
			});
			
			repeatBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("repeat clicked");
					mp3player.setRepeat(true);
				}
			}); // END OF "repeatBtn" CLICKED

			shuffleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("shuffle clicked");
					mp3player.setShuffle(true);
				}
			}); // END OF "shuffleBtn" CLICKED

		}
	} //// END OF playPauseSong////

//	public void seekAndUpdate(Duration duration) {
//		final MediaPlayer player = playlist.get(playlist.indexOf(mediaView.getMediaPlayer()));
//		player.seek(duration);
//	}

	public void updateSongProgress(double currentTime) {
		try {
			Mp3File mp3 = new Mp3File(currSong.getUrl());
			double totalDuration = mp3.getLengthInSeconds();
			songProgress.setProgress((currentTime / totalDuration));
		} catch (Exception e) {
		}
	} /// ^^END OF updateSliderPosition^^//

	public void updateValues() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							
							if (!mp3player.isPaused() && !mp3player.isStopped()) {
								currTime = System.currentTimeMillis() / 1000;
								currentDuration.setText(secToMin((long) (currTime - startTime + playedTime)));
								updateSongProgress(currTime - startTime + playedTime);
							}
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						break;
					}
				} while (!playlist.isEmpty());
			}
		});
		thread.start();

	} /// ^^END OF updateValues^^//
	
	public void resetInfo() {
		try {
			Mp3File mp3 = new Mp3File(currSong.getUrl());
			long tduration = mp3.getLengthInSeconds();
			totalDuration.setText(secToMin((long) tduration));
			currentDuration.setText("0:00");
			playedTime = 0;
		}catch(Exception e) {}
	}

	///// HELPER FUNCTION /////
	public void updateVolume() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		for (Mixer.Info mixerInfo : mixerInfos) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			Line.Info[] lineInfos = mixer.getTargetLineInfo();

			for (Line.Info lineInfo : lineInfos) {
				try {
					Line line = mixer.getLine(lineInfo);
					line.open();
					if (line.isControlSupported(FloatControl.Type.VOLUME)) {
						FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
						volumeControl.setValue((float) volume);
					}
				} catch (Exception e) {
				}
			}
		}
	} //// END OF updateVolume////

	private void volumeIconChanger() {
		if (volume == 0.0) {
			muteIcon.setVisible(true);
			volumeLowIcon.setVisible(false);
			volumeMidIcon.setVisible(false);
			volumeHighIcon.setVisible(false);
		} else if (volume > 0 && volume <= 0.3) {
			muteIcon.setVisible(false);
			volumeLowIcon.setVisible(true);
			volumeMidIcon.setVisible(false);
			volumeHighIcon.setVisible(false);
		} else if (volume > 0.3 && volume <= 0.7) {
			muteIcon.setVisible(false);
			volumeLowIcon.setVisible(false);
			volumeMidIcon.setVisible(true);
			volumeHighIcon.setVisible(false);
		} else if (volume > 0.7 && volume <= 1) {
			muteIcon.setVisible(false);
			volumeLowIcon.setVisible(false);
			volumeMidIcon.setVisible(false);
			volumeHighIcon.setVisible(true);
		}
	} //// END OF volumeIconChanger////

	public String secToMin(long totalSec) {
		long s = totalSec;
		String minute;
		if ((s % 60) < 10) {
			minute = s / 60 + ":0" + s % 60;
		} else {
			minute = s / 60 + ":" + s % 60;
		}
		return minute;
	}

	public void showPauseIcon() {
		playBtn.setVisible(false);
		playBtn.setDisable(true);
		pauseBtn.setVisible(true);
		pauseBtn.setDisable(false);
	}

	public void showPlayIcon() {
		pauseBtn.setVisible(false);
		pauseBtn.setDisable(true);
		playBtn.setVisible(true);
		playBtn.setDisable(false);
	}

	public void showTransition(Pane pane) {
		FadeTransition show = new FadeTransition(Duration.millis(500), pane);
		show.setFromValue(0.0);
		show.setToValue(1.0);
		pane.setVisible(true);
		show.play();
	}

	public void hideTransition(Pane pane) {
		FadeTransition hide = new FadeTransition(Duration.millis(500), pane);
		hide.setFromValue(1.0);
		hide.setToValue(0.0);
		hide.play();
	}
}
