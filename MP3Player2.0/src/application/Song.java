package application;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song extends RecursiveTreeObject<Song>{

	private StringProperty artistName;
	private StringProperty songName;
	private StringProperty time;
	private StringProperty album;
	private StringProperty url;

	public Song() {
	}

	public Song(String url) {
		this.url = new SimpleStringProperty(url);
	}
	
	public Song(String songName, String time, String artistName, String album, String url) {
		this.songName = new SimpleStringProperty(songName);
		this.time = new SimpleStringProperty(time);
		this.artistName = new SimpleStringProperty(artistName);
		this.album = new SimpleStringProperty(album);
		this.url = new SimpleStringProperty(url);
	}

	
	public String getArtistName() {
		return artistName.get();
	}

	public void setArtistName(String artistName) {
		this.artistName.set(artistName);
	}

	public StringProperty artistNameProperty() {
		return artistName;
	}

	public String getSongName() {
		return songName.get();
	}

	public void setSongName(String songName) {
		this.songName.set(songName);
	}

	public StringProperty songNameProperty() {
		return songName;
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String time) {
		this.time.set(time);
	}

	public StringProperty timeProperty() {
		return time;
	}

	public String getAlbum() {
		return album.get();
	}

	public StringProperty albumProperty() {
		return album;
	}

	public void setalbum(String album) {
		this.album.set(album);
	}

	public String getUrl() {
		return url.get();
	}

	public StringProperty urlProperty() {
		return url;
	}

	public void setUrl(String url) {
		this.url.set(url);
	}

}
