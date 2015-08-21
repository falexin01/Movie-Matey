package com.alexin.address.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.alexin.address.*;
import com.alexin.address.MovieList.Genres;
import com.alexin.address.MovieList.SearchParams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

public class RootLayoutController 
{
	@FXML
	private MenuItem searchFolder;
	@FXML
	private Slider columns;
	@FXML
	private Label numCols;
	@FXML
	private TextField titleSearch;
	@FXML
	private ProgressBar pb;
	@FXML
	private Label statusLabel;
	@FXML
	private HBox searchingBox;
	@FXML
	private ComboBox<MovieList.SearchParams> searchBy;
	@FXML
	private Button genreButton;
	@FXML
	private ComboBox<Object> sortBy;
	@FXML
	private ComboBox<MovieList> listChoose;
	
	private MovieList movieList;
	private MainApp mainApp;
	
	@FXML
    private void initialize() 
    {
		ObservableList<Object> sorts = FXCollections.observableArrayList();
		sorts.add(new LexoComparator());
		sorts.add(new YearComparator());
		sorts.add(new RuntimeComparator());
		sorts.add(new RatingComparator());
		sortBy.setItems(sorts);
		
		ObservableList<MovieList.SearchParams> searches = FXCollections.observableArrayList();
		searches.addAll(MovieList.SearchParams.values());
		searchBy.setItems(searches);
		
		genreButton.setDisable(true);
    }
	
	@FXML
	public void closeWindow()
	{
		mainApp.getPrimaryStage().close();
	}
	
	@FXML
	public void playRandomAll()
	{
		try 
		{
			Desktop.getDesktop().open(mainApp.getMovieList().getEntryAt((int)(Math.random()*mainApp.getMovieList().getMovieList().size())).getFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	public void playRandomCur()
	{
		try 
		{
			Desktop.getDesktop().open(mainApp.getActiveList().getEntryAt((int)(Math.random()*mainApp.getActiveList().getMovieList().size())).getFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	public void syncLibraries()
	{
		mainApp.syncLibraries();
	}
	
	@FXML
	public void addRemoveLibraries()
	{
		mainApp.showLibrariesDialog();
	}
	
	@SuppressWarnings("static-access")
	@FXML
	public void exportDatabase()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selDir = dirChooser.showDialog(mainApp.getPrimaryStage());
		if(selDir != null)
		{	
			ZipUtil.pack(new File(mainApp.appDataLoc), new File(selDir + "\\MovieMatey.mm"));
		}
	}
	
	@SuppressWarnings("static-access")
	@FXML
	public void importDatabase()
	{
		FileChooser fileChooser = new FileChooser();
		File selFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if(selFile != null && FilenameUtils.getExtension(selFile.getAbsolutePath()).equals("mm"))
		{	
			try 
			{
				FileUtils.cleanDirectory(new File(mainApp.appDataLoc));
				ZipUtil.unpack(selFile, new File(mainApp.appDataLoc));
				mainApp.setup();
				fillListChoose();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	public void resetFileSizes()
	{
		mainApp.movies.resetAllFileSize();
		mainApp.favorites.resetAllFileSize();
		mainApp.saveMovieList();
		mainApp.saveFavoriteList();
	}
	
	@FXML
	public void setHQPosters()
	{		
		Object[] heightAndKey = mainApp.showPosterDialog();
		if(heightAndKey != null)
		{
			searchingBox.setVisible(true);
			final Task<Void> task = new Task<Void>()
			{
				@Override
				protected Void call() throws Exception 
				{
					try 
					{
						int totFiles = mainApp.movies.getMovieList().size();
						int curFiles = 0;
						
						for(MovieEntry mvEntry : mainApp.movies.getMovieList())
						{
							updateMessage(mvEntry.getTitle());
							mvEntry.switchHQPoster((int)heightAndKey[0], (String)heightAndKey[1]);
							updateProgress(curFiles++, totFiles);
						}
							
						updateMessage("Search complete, " + totFiles + " files added.");
						updateProgress(1,1);
						mainApp.saveMovieList();
						mainApp.refreshView();
						Thread.sleep(1000);
						Platform.runLater(new Runnable(){
							public void run() 
							{
								searchingBox.setVisible(false);
							}
						});
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}	
						return null;
				}	
			};
			pb.progressProperty().bind(task.progressProperty());
			statusLabel.textProperty().bind(task.messageProperty());	
			new Thread(task).start();
		}
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	public void sort()
	{
		Collections.sort(mainApp.getActiveList().getMovieList(), (Comparator<? super MovieEntry>)sortBy.getSelectionModel().getSelectedItem());
		mainApp.saveMovieList();
		mainApp.refreshView();
	}
	
	@FXML
	public void chooseList()
	{
			mainApp.setActiveList(listChoose.getSelectionModel().getSelectedItem());
			mainApp.refreshView();
	}
	
	@FXML
	public void search()
	{
		if(titleSearch.getText() == null)
		{
			mainApp.setActiveList(mainApp.getMovieList());
			mainApp.refreshView();
		}
		else if(searchBy.getSelectionModel().getSelectedItem() != null)
		{
			mainApp.setTempList(mainApp.getMovieList().searchMovieList(titleSearch.getText().toLowerCase(), searchBy.getSelectionModel().getSelectedItem()));
			mainApp.setActiveList(mainApp.getTempList());
			mainApp.refreshView();
		}
	}
	
	@FXML
	public void searchByHandled()
	{
		if(searchBy.getSelectionModel().getSelectedItem() == SearchParams.GENRE)
			genreButton.setDisable(false);
		else
			genreButton.setDisable(true);
	}
	
	@FXML
	public void genresHandled()
	{
		if(searchBy.getSelectionModel().getSelectedItem() == SearchParams.GENRE)
		{
			ObservableList<Genres> temp = mainApp.showGenresDialog();
			if(!temp.isEmpty())
			{
				String genreSearch = "";
				for(Genres g : temp)
				{
					genreSearch += g + " ";
				}
				genreSearch = genreSearch.trim();
				titleSearch.setText(genreSearch);
				search();
			}
		}
	}
	
	public void searchWith(String search, SearchParams params)
	{
		searchBy.getSelectionModel().select(params);
		titleSearch.setText(search);
		search();
	}
	
	@FXML
	public void searchFolder()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selDir = dirChooser.showDialog(mainApp.getPrimaryStage());
		if(selDir != null)
		{	
			searchingBox.setVisible(true);
			movieList = mainApp.getMovieList();
			movieList.fillByDir(selDir.getAbsolutePath(), pb, statusLabel, mainApp, searchingBox);
		}
	}
	
	@FXML
	public void addFile()
	{
		FileChooser fChooser = new FileChooser();
		File selFile = fChooser.showOpenDialog(mainApp.getPrimaryStage());
		if(selFile != null)
		{
			if(mainApp.getMovieList().addMovie(new MovieEntry(selFile)));
			mainApp.refreshView();
		}
	}
	
	@SuppressWarnings("static-access")
	@FXML
	public void deleteEntry()
	{
		if(mainApp.getSelectedEntry() != null)
		{
			MovieEntry temp = mainApp.getSelectedEntry();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Movie");
			alert.setContentText("Are you sure you want to delete " + temp.getTitle() + "?");
			alert.setResizable(true);
			alert.initStyle(StageStyle.UTILITY);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK)
			{
			    mainApp.getMovieList().removeEntry(temp);
			    mainApp.favorites.removeEntry(mainApp.favorites.getMatchingEntry(temp));
			    mainApp.getLibraries().addDeletedFile(temp.getFile());
			    mainApp.refreshView();
			    mainApp.closeMovieInfo();
			    mainApp.saveMovieList();
			    mainApp.saveFavoriteList();
			    mainApp.getLibraries().saveDeletedFiles(mainApp.doNotSyncSaveLoc);
			} 
			else 
			{
			    // ... user chose CANCEL or closed the dialog
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@FXML
	public void deleteList()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Movies");
		alert.setContentText("Are you sure you want to delete all movies?");
		alert.setResizable(true);
		alert.initStyle(StageStyle.UTILITY);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
			mainApp.getMovieList().getMovieList().clear();
			mainApp.favorites.getMovieList().clear();
			mainApp.getLibraries().clearDeletedFiles();
			mainApp.refreshView();
			mainApp.saveMovieList();
			mainApp.saveFavoriteList();
			mainApp.getLibraries().saveDeletedFiles(mainApp.doNotSyncSaveLoc);
			try
			{
				FileUtils.cleanDirectory(new File(mainApp.posterSaveLoc));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} 
	}
	
	@FXML
	public void showTileView()
	{
		mainApp.showMovieTileView();
	}
	
	@FXML
	public void showListView()
	{
		mainApp.showListView();
	}
	
	@FXML
	public void showStatisticsView()
	{
		mainApp.showStatisticsView();
	}
	
	@FXML
	public void refresh()
	{
		mainApp.refreshView();
	}
	
	@FXML
	public void sliderAdjust()
	{
		numCols.setText(Integer.toString((int)columns.getValue()));
		mainApp.setColumns((int)columns.getValue());
	}
	
	private void fillListChoose()
	{
		ObservableList<MovieList> lists = FXCollections.observableArrayList();
		lists.addAll(mainApp.movies, mainApp.favorites);
		listChoose.setItems(lists);
		listChoose.getSelectionModel().selectFirst();
	}
	
	public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
        movieList = this.mainApp.getMovieList();
        fillListChoose(); 
    }
}
