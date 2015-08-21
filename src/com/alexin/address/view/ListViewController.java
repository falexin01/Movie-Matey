package com.alexin.address.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.alexin.address.MainApp;
import com.alexin.address.MovieEntry;
import com.alexin.address.model.Movie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

public class ListViewController 
{
	@FXML
	private TableView<Movie> movieTable;
	@FXML
	private TableColumn<Movie, String> titleCol;
	@FXML
	private TableColumn<Movie, Number> yearCol;
	@FXML
	private TableColumn<Movie, Number> ratingCol;
	@FXML
	private TableColumn<Movie, Number> runtimeCol;
	@FXML
    private Label fileSize;
	@FXML
    private Label fileLocation;
	@FXML
    private Label ratedLabel;
	@FXML
    private Label releasedLabel;
	@FXML
    private Label runtimeLabel;
	@FXML
    private Label genreLabel;
	@FXML
    private Label actorsLabel;
	@FXML
    private Label directorLabel;
	@FXML
    private Label writerLabel;
	@FXML
    private Label plotLabel;
	@FXML
	private ImageView poster;
	@FXML
	private ImageView blurPoster;
	@FXML
	private StackPane posterPane;
	@FXML
	private WebView webView;
	
	private MainApp mainApp;
	private ObservableList<Movie> observableMovies;
	private Movie selectedMovie;
	
	public ListViewController()
	{
		
	}
	
	@FXML
    private void initialize() 
    {
		titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
		yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
		ratingCol.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
		runtimeCol.setCellValueFactory(cellData -> cellData.getValue().runtimeProperty());
		
		showMovieDetails(null);
		movieTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showMovieDetails(newValue));
    }
	
	private void showMovieDetails(Movie movie)
	{
		if(movie != null)
		{
			selectedMovie = movie;
			fileSize.setText(movie.getEntry().getfileSize());
			fileLocation.setText(movie.getEntry().getFile().getAbsolutePath());
			ratedLabel.setText(movie.getRated());
			releasedLabel.setText(movie.getReleased());
			runtimeLabel.setText(Integer.toString(movie.getRuntime()));
			genreLabel.setText(movie.getEntry().getGenre());
			actorsLabel.setText(movie.getEntry().getActors());
			directorLabel.setText(movie.getEntry().getDirector());
			writerLabel.setText(movie.getEntry().getWriter());
			plotLabel.setText(movie.getPlot());
			setPosterPane(movie);
			
			WebEngine webEngine = webView.getEngine();
			webEngine.load("http://imdb.com/title/" + movie.getEntry().getimdbID() + "/");
		}
		else
		{
			selectedMovie = null;
			fileSize.setText("");
			fileLocation.setText("");
			ratedLabel.setText("");
			releasedLabel.setText("");
			runtimeLabel.setText("");
			genreLabel.setText("");
			actorsLabel.setText("");
			directorLabel.setText("");
			writerLabel.setText("");
			plotLabel.setText("");
			poster.setImage(new Image("com/alexin/res/notFound.png"));
		}
	}
	
	public void setPosterPane(Movie movie)
	{
		if(movie.getEntry().getPosterSaveLoc() != null)
		{
			blurPoster.setFitWidth(posterPane.getWidth());
			blurPoster.setFitHeight(posterPane.getHeight());
			GaussianBlur gb = new GaussianBlur(63);
			gb.setInput(new BoxBlur(255, 255, 3));
			blurPoster.setEffect(gb);
			blurPoster.setImage(new Image(new File(movie.getEntry().getPosterSaveLoc()).toURI().toString()));
			
			poster.setFitWidth(posterPane.getWidth() * 0.4);
			poster.setEffect(new DropShadow());
			poster.setImage(new Image(new File(movie.getEntry().getPosterSaveLoc()).toURI().toString()));
			
		}
		else
		{
			poster.setImage(new Image("com/alexin/res/notFound.png"));
		}
	}
	
	public void refreshObsMovieList()
	{
		observableMovies.clear();
		for(MovieEntry mv : this.mainApp.getActiveList().getMovieList())
		{
			observableMovies.add(new Movie(mv));
		}
		
	}
	
	public void selectEntry(int index)
	{
		
		movieTable.getSelectionModel().select(index);
	}
	
	@FXML
	public void playMovie()
	{
		try 
		{
			Desktop.getDesktop().open(selectedMovie.getEntry().getFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	public void deleteMovie()
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
			    mainApp.refreshView();
			    mainApp.closeMovieInfo();
			    mainApp.saveMovieList();
			    mainApp.saveFavoriteList();
			} 
			else 
			{
			    // ... user chose CANCEL or closed the dialog
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@FXML
	public void changePoster()
	{
		FileChooser fChooser = new FileChooser();
		File sourceFile = fChooser.showOpenDialog(mainApp.getPrimaryStage());
		if(sourceFile != null && selectedMovie != null)
		{
			try 
			{
				File destFile = new File(mainApp.posterSaveLoc + "\\" + selectedMovie.getEntry().getimdbID() + "." + FilenameUtils.getExtension(sourceFile.getName()));
				FileUtils.deleteQuietly(new File(selectedMovie.getEntry().getPosterSaveLoc()));
				FileUtils.copyFile(sourceFile, destFile);
				selectedMovie.getEntry().setPosterSaveLoc(destFile.getAbsolutePath());
				mainApp.getMovieList().getMatchingEntry(selectedMovie.getEntry()).setPosterSaveLoc(destFile.getAbsolutePath());
				mainApp.saveMovieList();
				if(mainApp.getFavoritesList().contains(selectedMovie.getEntry()))
				{
					mainApp.getFavoritesList().getMatchingEntry(selectedMovie.getEntry()).setPosterSaveLoc(destFile.getAbsolutePath());
					mainApp.saveFavoriteList();
				}
				setPosterPane(selectedMovie);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	public void openFile()
	{
		try 
		{
			Desktop.getDesktop().open(selectedMovie.getEntry().getFile().getParentFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public MovieEntry getSelectedEntry()
	{
		return selectedMovie.getEntry();
	}
	
	public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
        observableMovies = FXCollections.observableArrayList();
		for(MovieEntry mv : this.mainApp.getActiveList().getMovieList())
		{
			observableMovies.add(new Movie(mv));
		}
		movieTable.setItems(observableMovies);
    }
	
}
