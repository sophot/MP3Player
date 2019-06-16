package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
	private Label songNameLabel;
	@FXML
	private Label artistLabel;
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

	private MP3Player mp3player;
	private Stage stage;
	private ObservableList<Song> songs;

	private boolean showingMore;
	private boolean isRepeating;
	private long startTime;
	private long currTime;
	private long playedTime = 0;
	private double volume = 0.3;
	private double xOffset = 0;
	private double yOffset = 0;
	private int nextPrev=0;
	private Song currSong;
	private File currPlaylist;
	

	// CONSTRUCTOR
	public Controller() {
		isRepeating = false;
		showingMore = false;
		stage = Main.getStage();
		songs = FXCollections.observableArrayList();
	}

	@FXML
	private void initialize() {
		// SONG TABLE COLUMNS INITIALIZATION //
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
		// ***ENDS SONG TABLE COLUMNS INITIALIZATION*** //

		// APPLICATION FRAME CONTROLS //
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
		// ***ENDS FRAME CONTROLS*** //

		// CLOSE or EXIT BUTTON //
		closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.exit(0);
			}
		}); // END OF "closeBtn" CLICKED

		// MINIMIZE BUTTON //
		minimizeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setIconified(true);
			}
		}); // END OF "minimizeBtn" CLICKED

		// SHOW MORE BUTTON //
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
		});
		// ***END OF "showMoreBtn" CLICKED***
		
		// SONG TABLE CONTROLS //
		songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		songTable.setOnMouseClicked((MouseEvent e) -> {
			if ((e.getClickCount() > 0) && (e.getClickCount() < 2)) {
				songSelected();
			}
		});		
		// ***ENDS SONG TABLE CONTROLS*** //
		
		// VOLUME CONTROLS //
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				volume = volumeSlider.getValue() / 100;
				volumeValue.setText(Integer.toString((int) volumeSlider.getValue()));
				updateVolume();
				changeVolumeIcon();
			}
		});
		// ***ENDS VOLUME CONTROLS*** //

		////////////////////////////////////
		openFileBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("openFIle clicked");
				FileChooser fc = new FileChooser();
				File selectedFile = fc.showOpenDialog(null);

				if (selectedFile != null) {		//check if a file is selected
					String fileName = selectedFile.getName();
					if(fileName.endsWith("mp3")) {	//check if that file is mp3 file 
						try {
							
							Mp3File mp3 = new Mp3File(selectedFile.getAbsolutePath());
							ID3v2 tag = mp3.getId3v2Tag();
							
							currSong = new Song(tag.getTitle(), secTOmin(mp3.getLengthInSeconds()),tag.getArtist(), tag.getAlbum(), selectedFile.getAbsolutePath());
							
							if(mp3player != null) {		//if mp3player is running then stop mp3player
								mp3player.stop();
							}
							
							songs.clear();
							songs.add(currSong);
							currPlaylist = null;	//current playlist file is null unless save is clicked
							
							showSongTable();
							updateVolume();
							setSongDurationInfo();
							setDefaultInfo();
							
							
						}catch(Exception e) {}
					}else {
						messageDialog("select MP3 file", "[File Mismath] Not MP3 File");
					}
				}
			}
		}); 
		// ***END OF "openFileBtn" CLICKED*** //
		//////////////////////////////////////////////////////////////////////////////////

		//////////////////////////////////
		addSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("addSong clicked");
				
				if(currPlaylist != null) {		//check if a playlist is currently opened (not Null)
					FileChooser fc = new FileChooser();	
					fc.getExtensionFilters()
						.add(new FileChooser.ExtensionFilter("MP3 file (*.mp3)", "*.mp3"));
					File selectedFile = fc.showOpenDialog(null);
					
					if (selectedFile != null) { // check if an mp3 file is chosen (not Null)
						try {

							Mp3File mp3 = new Mp3File(selectedFile.getPath());
							ID3v2 tag = mp3.getId3v2Tag();
							Song newSong = new Song(tag.getTitle(), secTOmin(mp3.getLengthInSeconds()), tag.getArtist(),
									tag.getAlbum(), selectedFile.getAbsolutePath());

							songs.add(newSong); // create new Song object and show in songTable
							showSongTable();

						} catch (Exception e) {}
					} else {
						System.out.println("No File Selected");
					}
				}else {
					messageDialog("Help: select Open Playlist or Open File or Save Playlist", "No Playlist is opened yet");
				}
			}
		}); 
		// ***END OF "addSongBtn" CLICKED*** //
		//////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////
		newPlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("newPlaylist clicked");
				
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
				File file = fc.showSaveDialog(stage);	//ask user to name and save the new empty playlist
				
				if(file != null) {	//if not cancel
					try {
						currPlaylist = file;	//set current playlist file to the empty playlist
						songs.clear();		//remove all songs from song table
						showSongTable();

					}catch (Exception e) {
						e.printStackTrace();
					}
						
				}else {
					System.out.println("Nothing happened");
				}
			}
		}); 
		// ***END OF "newPlaylistBtn" CLICKED*** //
		//////////////////////////////////////////////////////////////////////////////////

		////////////////////////////////
		savePlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("savePlaylist clicked");
				
				if (currPlaylist != null) {	//check if a playlist file is opened (not Null)
					save();
				} else {	// if null and there are songs in song table then save as new
					if(!songs.isEmpty()) {
						FileChooser fc = new FileChooser();
						fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
						File file = fc.showSaveDialog(stage);
						
						if(file != null) {						
							currPlaylist = file;
							save();
						}
					}else {
						messageDialog("Help: select Open Playlist or Open File", "No playlist is opened to Save");
					}
				}
			}
		}); 
		// END OF "savePlaylistBtn" CLICKED //
		//////////////////////////////////////////////////////////////////////////////////

		
		//////////////////////////////////////
		deletePlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (currPlaylist != null) {	//check if a playlist is opened (not Null)

					songs.clear();		//remove all songs from song table
					currSong = null;
					currPlaylist.delete();	//delete playlist file
					currPlaylist = null;
					
					showSongTable();
					updateVolume();
					setDefaultInfo();
					
					if(mp3player != null) {
						mp3player.stop();	//if a song in that playlist is playing, stop
					}
					
				} else {
					messageDialog("Help: select Open Playlist or Open File", "No playlist is opened to delete");
				}
			}
		});
		// ***END OF "deletePlaylistBtn" CLICKED*** //
		//////////////////////////////////////////////////////////////////////////////////

		//////////////////////////////////////
		openPlaylistBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("openPlaylist clicked");
				
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt file (*.txt)", "*.txt"));
				File selectedFile = fc.showOpenDialog(null);
				
				if (selectedFile != null) {		//if a playlist is selected from pc
					currPlaylist = selectedFile;	//set current playlist to selected playlist
					songs.clear();		//remove all songs in song table from previous playlist
					
					songs = txtsTOsongs();	//show all songs from current playlist
					showSongTable();
				}else{
					System.out.println("No playlist selected!");
				}
			}
		}); 
		// ***END OF "openPlayList"*** //
		//////////////////////////////////////////////////////////////////////////////////
		
	} 
	//##### END OF initialize() #####//


	public ObservableList<Song> txtsTOsongs() {
		//create Song objects from playlist file
		ObservableList<Song> songs = FXCollections.observableArrayList();
		try {
			FileReader in = new FileReader(currPlaylist);	
			BufferedReader reader = new BufferedReader(in);
			String fileUrl;	
			
			while ((fileUrl = reader.readLine()) != null) {
				//read all song paths from playlist line by line
				if (fileUrl.endsWith("mp3")) {	//rechecked if the path is to mp3
					Mp3File mp3 = new Mp3File(fileUrl);
					ID3v2 tag = mp3.getId3v2Tag();
					Song song = new Song(tag.getTitle(), secTOmin(mp3.getLengthInSeconds()), tag.getArtist(),
							tag.getAlbum(), fileUrl);
					songs.add(song);
				}
			}
			reader.close();
		} catch (Exception e) {
			messageDialog("Warning", e.getMessage());
		}
		
		messageDialog("", ("read " + songs.size() + " songs"));
		return songs;
	}
	//*** ENDS txtsTOsongs() //
	
	public void save() {
		try {
			FileOutputStream out = new FileOutputStream(currPlaylist);
			PrintWriter writer = new PrintWriter(out);

			for (Song song : songs) {	//get each song from song table
				writer.println(song.getUrl()); //write song path to playlist file
			}

			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	//*** ENDS save() //

	public void songSelected() {
		if (songTable.getSelectionModel().getSelectedItem() != null) {
			//if a song is selected from song table (not Null)
			currSong = songTable.getSelectionModel().getSelectedItem().getValue();
				//get selected song and assign to currSong field

			playSelectedSong();
			
			removeSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					ArrayList<Song> songsToDel = new ArrayList<Song>();

					//count selected songs and save them in arraylist to delete
					for(int i = 0 ; i< songTable.getSelectionModel().getSelectedItems().toArray().length; i++) {				
						songsToDel.add(songTable.getSelectionModel().getSelectedItems().get(i).getValue());	
					}
					
					songs.removeAll(songsToDel);	//remove all selected songs from song table 
					
					setDefaultInfo();
					currSong = null;	//because current Song is also selected then reset to null
					mp3player.stop();
					
					showSongTable();
				}
			}); 
			// ***ENDS removeSongBtn CLICKED*** //
			
		}else{
			System.out.println("Nothing is selected");
		}
	} 
	//*** ENDS songSelected() //
	
	

	public void playSelectedSong() {
		if (currSong != null) {	//check if there is song to play (currSong not Null)
//			File file = new File(song.getUrl());
//			String path = file.getAbsolutePath();
//			path.replace("\\", "/");

			if (mp3player != null) {	//stop any playing song
				mp3player.stop();
			}

			mp3player = new MP3Player(new File(currSong.getUrl())); //set player to selected song
			showPlayIcon();

			volumeValue.setText(String.valueOf((int) volumeSlider.getValue()));
			volumeSlider.setValue(volume * 100);
			setDefaultInfo();
			setSongDurationInfo();
			showSongLabel();
			updateDurationValues();
			
			nextPrev = 0;	//for nextSongBtn and previousBtn

			////  PLAY + PAUSE + NEXT + BACK  ////
			playBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("play Clicked");
					startTime = System.currentTimeMillis() / 1000; //player start time in seconds
					mp3player.play();
					showPauseIcon();
				}
			});
			// ***END OF "playBtn" CLICKED*** //

			pauseBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("pause Clicked");
					playedTime += currTime - startTime;	//get played time until pause location
					mp3player.pause();
					showPlayIcon();
				}
			});
			// ***END OF "pauseBtn" CLICKED*** //

			previousBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("previousBtn clicked");
					mp3player.stop();	//stop playing song
					nextPrev--;
					int index = songTable.getSelectionModel().getSelectedIndex() + nextPrev;
					if(index < 0) {	//loop song table
						index = songs.size()-1;
						nextPrev += songs.size();
					}
					
					currSong = songs.get(index);
					mp3player = new MP3Player(new File(currSong.getUrl()));
					showSongLabel();
					setSongDurationInfo();
					showPauseIcon();
					startTime = System.currentTimeMillis() / 1000;
					mp3player.play();
				}
			});
			// ***END OF "previousBtn" CLICKED*** //

			nextSongBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("nextSong clicked");
					playNextSong();
				}
			});
			// ***END OF "nextSongBtn" CLICKED*** //
			
			repeatBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					System.out.println("repeat clicked");
					if(isRepeating == true) {
						isRepeating = false;
					}else {
						isRepeating = true;
					}
					mp3player.setRepeat(isRepeating);
				}
			}); 
			// ***END OF "repeatBtn" CLICKED*** //

		}
	} 
	//***END OF playPauseSong() //
	
	
	public void playNextSong() {
		mp3player.stop();	//stop current player
		nextPrev++;
		int index = songTable.getSelectionModel().getSelectedIndex() + nextPrev;
			//set index to next song based on song selected from song table
		
		if(index > songs.size()-1) {
			//if selected song is last in song table set index to first song
			index = 0;		
			nextPrev -= songs.size();
		}
		
		currSong = songs.get(index);
		mp3player = new MP3Player(new File(currSong.getUrl()));
		showSongLabel();
		setSongDurationInfo();
		showPauseIcon();
		startTime = System.currentTimeMillis() / 1000;
		mp3player.play();
	}
	//*** ENDS playNextSong() //

	public void updateSongProgress(double currentTime) {
		//update song progress location
		try {
			Mp3File mp3 = new Mp3File(currSong.getUrl());
			double totalDuration = mp3.getLengthInSeconds();
			songProgress.setProgress((currentTime / totalDuration));
		} catch (Exception e) {
		}
	} 
	//*** END OF updateSongProgress() //
	

	public void updateDurationValues() {
		//update duration values using thread
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							
							if (!mp3player.isPaused() && !mp3player.isStopped()) {	//if player is playing
								currTime = System.currentTimeMillis() / 1000;
								currentDuration.setText(secTOmin((long) (currTime - startTime + playedTime)));
								updateSongProgress(currTime - startTime + playedTime);
							}
							
							if(currentDuration.getText().equals(totalDuration.getText()) && (isRepeating == false)) {
								//if a song is finished
								playNextSong();
							}
						}
					});
					try {
						Thread.sleep(300);
					} catch (InterruptedException ex) {
						break;
					}
				} while (mp3player != null);
			}
		});
		thread.start();

	}
	//*** END OF updateDurationValues() //
	
	
	public void showSongTable() {
		//display all songs in ObservableList to song table
		final TreeItem<Song> root = new RecursiveTreeItem<Song>(songs, RecursiveTreeObject::getChildren);
		songTable.getColumns().setAll(songNameColumn, timeColumn, artistColumn, albumColumn);
		songTable.setRoot(root);
		songTable.setShowRoot(false);
	}
	//*** ENDS showSongTable() //
	
	
	public void setDefaultInfo() {
		songNameLabel.setText("");
		artistLabel.setText("");
		currentDuration.setText("0:00");
		totalDuration.setText("0:00");
		songProgress.setProgress(0);
	}
	//*** ENDS setDefaultInfo() //
	
	
	public void setSongDurationInfo() {
		//set a song total Length in application
		try {
			Mp3File mp3 = new Mp3File(currSong.getUrl());
			long tduration = mp3.getLengthInSeconds();
			totalDuration.setText(secTOmin((long) tduration));
			currentDuration.setText("0:00");
			playedTime = 0;
		}catch(Exception e) {}
	}
	//*** ENDS setSongDurationInfo() //
	
	
	public void showSongLabel() {
		songNameLabel.setText(currSong.getSongName());
		artistLabel.setText(currSong.getArtistName());
	}
	//*** ENDS showSongLabel() //
	

	public void updateVolume() {
		//use volume data to set volume level
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
	} 
	//*** END OF updateVolume() //
	

	private void changeVolumeIcon() {
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
	} 
	//*** END OF volumeIconChanger() //
	

	public String secTOmin(long totalSec) {
		long s = totalSec;
		String minute;
		if ((s % 60) < 10) {
			minute = s / 60 + ":0" + s % 60;
		} else {
			minute = s / 60 + ":" + s % 60;
		}
		return minute;
	}
	//*** ENDS secTOmin() //

	
	public void showPauseIcon() {
		playBtn.setVisible(false);
		playBtn.setDisable(true);
		pauseBtn.setVisible(true);
		pauseBtn.setDisable(false);
	}
	//*** ENDS showPauseIcon() //

	
	public void showPlayIcon() {
		pauseBtn.setVisible(false);
		pauseBtn.setDisable(true);
		playBtn.setVisible(true);
		playBtn.setDisable(false);
	}
	//*** ENDS showPlayIcon() //
	
	
	public void showTransition(Pane pane) {
		FadeTransition show = new FadeTransition(Duration.millis(500), pane);
		show.setFromValue(0.0);
		show.setToValue(1.0);
		pane.setVisible(true);
		show.play();
	}
	//*** ENDS showTransition() //

	
	public void hideTransition(Pane pane) {
		FadeTransition hide = new FadeTransition(Duration.millis(500), pane);
		hide.setFromValue(1.0);
		hide.setToValue(0.0);
		hide.play();
	}
	//*** ENDS hideTransition() //

	public void messageDialog(String help, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText(message);
		alert.setContentText(help);

		alert.showAndWait();
	}
}
