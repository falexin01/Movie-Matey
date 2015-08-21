package com.alexin.address.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alexin.address.BlurPane;
import com.alexin.address.InfoRequest;
import com.alexin.address.MainApp;
import com.alexin.address.MovieEntry;
import com.alexin.address.MovieList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

public class MovieTileViewController 
{
	@FXML
    private AnchorPane rootPane;
	@FXML
    private VBox addInfoPanel;
	@FXML
	private HBox buttonPanel;
	@FXML
    private ListView<MovieEntry> searchResults;
	@FXML
    private TextField searchField;
	@FXML
    private Button searchButton;
	@FXML
    private Button imdbIDButton;
	@FXML
    private Button titleButton;
	@FXML
    private StackPane stackPane;
	@FXML
    private StackPane addStackPane;
	@FXML
    private TilePane moviePane;
	@FXML
    private ScrollPane scrollPane;
	@FXML
    private AnchorPane detailPane;
	@FXML
    private ImageView detailPoster;
	@FXML
    private ImageView playButton;
	@FXML
    private ImageView addInfoButton;
	@FXML
    private ImageView addFavoriteButton;
	@FXML
    private ImageView wwwButton;
	@FXML
    private Label titleLabel;
	@FXML
    private Label ratedLabel;
	@FXML
    private Label releasedLabel;
	@FXML
    private Label runtimeLabel;
	@FXML
    private Label ratingLabel;
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
	
	private BlurPane blurPane;
	private MovieList movieList;
	private MainApp mainApp;
	private MovieEntry selectedEntry;
	private Boolean clicked = false;
	private Boolean detailShown = false;
	
	@FXML
    private void initialize() 
    {
		
    }
	
	public void fillMovieTiles(MovieList mvList, Boolean repaint)
	{
		Task<Void> task = new Task<Void>(){
			@Override
			protected Void call() throws Exception 
			{
				Platform.runLater(new Runnable(){
					public void run() 
					{
						if(repaint)
							moviePane.getChildren().clear();
					}
				});
				
				double tileWidth = ((mainApp.getScreenBounds().getWidth() - 35) - (moviePane.getVgap() * (moviePane.getPrefColumns() - 1)) - (moviePane.getPadding().getLeft() * 2)) / moviePane.getPrefColumns();
				double tileHeight = tileWidth * 1.4545;
				moviePane.setPrefTileHeight(tileHeight);
				moviePane.setPrefTileWidth(tileWidth);
				
				if(mvList.getMovieList().isEmpty() && mvList.getListName().equals("All Movies"))
				{
					Label noMovies = new Label("Add movies by going to the edit menu and choose \"Add File\" or \"Search Folder\".");
					noMovies.setFont(Font.font("Segoe UI Light"));
					noMovies.setPadding(new Insets(10,10,10,10));
					noMovies.setWrapText(true);
					moviePane.getChildren().add(noMovies);
				}
				
				if(mvList.getMovieList().isEmpty() && mvList.getListName().equals("Favorites"))
				{
					Label noMovies = new Label("Add favorites by clicking on a movie and hitting the star button.");
					noMovies.setFont(Font.font("Segoe UI Light"));
					noMovies.setPadding(new Insets(10,10,10,10));
					moviePane.getChildren().add(noMovies);
				}
				
				for(MovieEntry mv : mvList.getMovieList())
				{
					ImageView poster = new ImageView();
					if(mv.getPosterSaveLoc() != null && !mv.getPoster().equals("N/A"))
						poster.setImage(new Image(new File(mv.getPosterSaveLoc()).toURI().toString()));
					else
						poster.setImage(new Image("com/alexin/res/notFound.png"));
						
					poster.setFitWidth(tileWidth);
					poster.setFitHeight(tileHeight);
					poster.setOnMouseClicked(new EventHandler<MouseEvent>(){
						public void handle(MouseEvent arg0) 
						{
							setDetailInfo(mv);
						}
					});
					
					Platform.runLater(new Runnable(){
						public void run() 
						{
							Tooltip.install(poster, new Tooltip(mv.getTitle()));
							moviePane.getChildren().add(poster);
						}
						
					});
				}
				return null;
			}
		};
		new Thread(task).start();
	}
	
	@FXML
	public void playMovie()
	{
		try 
		{
			Desktop.getDesktop().open(selectedEntry.getFile());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	public void addMovieInfo()
	{
		if(clicked)
		{
			clicked = false;
			addInfoPanel.setVisible(false);
			detailPoster.setVisible(true);
		}
		else
		{
			clicked = true;
			searchField.setText(null);
			searchResults.setItems(null);
			detailPoster.setVisible(false);
			addInfoPanel.setVisible(true);
		}
	}
	
	@FXML
	public void imdbButtonPressed()
	{
		if(selectedEntry.getimdbID() != null)
		{
			try 
			{
				URI uri = new URI("http://imdb.com/title/" + selectedEntry.getimdbID() + "/");
				Desktop.getDesktop().browse(uri);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	public void addFavorite()
	{
		MovieEntry tempEntry = mainApp.favorites.getMatchingEntry(selectedEntry);
	
		if(tempEntry != null)
		{
			mainApp.favorites.removeEntry(tempEntry);
			favoriteButtonState(false);
			if(mainApp.getActiveList().getListName().equals("Favorites"))
				mainApp.refreshView();
		}
		else
		{
			mainApp.favorites.addMovie(selectedEntry);
			favoriteButtonState(true);
		}
		mainApp.saveFavoriteList();
	}
	
	@FXML
	public void imdbIDButtonPressed()
	{
		Pattern pattern = Pattern.compile("[t][t][\\d][\\d][\\d][\\d][\\d][\\d][\\d]");
		Matcher matcher = pattern.matcher(searchField.getText());
		if(searchField.getText() != null && matcher.find())
		{
			int index = movieList.getMovieList().indexOf(movieList.getMatchingEntry(selectedEntry));
			movieList.getMovieList().get(index).setMovieDataByID(searchField.getText());
			ImageView curPoster = (ImageView)moviePane.getChildren().get(index);
			Platform.runLater(new Runnable(){
				public void run() 
				{
					curPoster.setImage(new Image(new File(movieList.getMovieList().get(index).getPosterSaveLoc()).toURI().toString()));
					setDetailInfo(movieList.getMovieList().get(index));
					addInfoPanel.setVisible(false);
					detailPoster.setVisible(true);
				}
			});
			clicked = false;
			mainApp.setMovieList(movieList);
			mainApp.saveMovieList();
		}
	}
	
	@FXML
	public void listButtonClicked()
	{
		mainApp.listViewShowSelect((mainApp.getActiveList().getMovieList().indexOf(selectedEntry)));
	}
	
	@FXML
	public void searchButtonPressed()
	{
		if(searchField.getText() != null)
		{
			InfoRequest search = new InfoRequest(searchField.getText(), true);
			if(search.sendRequest())
			{
				final ObservableList<MovieEntry> searchList = FXCollections.observableArrayList();
				for(MovieEntry me : search.getSearchArray())
				{
					if(me.getimdbID() != null)
						searchList.add(me);
				}
				searchResults.setItems(searchList);
			}
			else
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Server Error");
				alert.setContentText((String) search.getJSONResponse().get("Error"));
				alert.setResizable(true);
				alert.initStyle(StageStyle.UTILITY);
				alert.showAndWait();
			}
		}
	}
	
	@FXML
	public void titleButtonPressed()
	{
		if(searchField.getText() != null)
		{
			
		}
	}
	
	@FXML
	public void listViewClicked()
	{
		MovieEntry tempEntry = searchResults.getSelectionModel().getSelectedItem();
		int index = movieList.getMovieList().indexOf(selectedEntry);
		movieList.getMovieList().get(index).setMovieDataByID(tempEntry.getimdbID());
		ImageView curPoster = (ImageView)moviePane.getChildren().get(index);
		Platform.runLater(new Runnable(){
			public void run() 
			{
				if(movieList.getMovieList().get(index).getPosterSaveLoc() != null)
					curPoster.setImage(new Image(new File(movieList.getMovieList().get(index).getPosterSaveLoc()).toURI().toString()));
				setDetailInfo(movieList.getMovieList().get(index));
				addInfoPanel.setVisible(false);
				detailPoster.setVisible(true);
			}
		});
		searchResults.getItems().clear();
		clicked = false;
		mainApp.setMovieList(movieList);
		mainApp.saveMovieList();
	}
	
	public void setDetailInfo(MovieEntry mv)
	{
		if(detailShown)
			closeMovieInfo();
		
		selectedEntry = mv;
		
		blurPane = new BlurPane();
		blurPane.getChildren().add(detailPane);
		stackPane.getChildren().add(blurPane);
		detailPane.setVisible(true);
		
		titleLabel.setText(mv.getTitle() + " (" + mv.getYear() + ")");
		ratedLabel.setText(mv.getRated());
		releasedLabel.setText(mv.getRelease());
		runtimeLabel.setText(Integer.toString(mv.getRuntime()) + " minutes");
		ratingLabel.setText(mv.getimdbRating() + " with " + NumberFormat.getInstance().format(mv.getimdbVotes()) + " votes");
		genreLabel.setText(mv.getGenre());
		actorsLabel.setText(mv.getActors());
		directorLabel.setText(mv.getDirector());
		writerLabel.setText(mv.getWriter());
		plotLabel.setText(mv.getPlot());
		
		MovieEntry tempEntry = mainApp.favorites.getMatchingEntry(selectedEntry);
		if(tempEntry != null)
			favoriteButtonState(true);
		else
			favoriteButtonState(false);
		
		detailPoster = new ImageView();
		detailPoster.setPreserveRatio(true);
		detailPoster.setFitWidth(mainApp.getScreenBounds().getWidth() * 0.20);
		detailPoster.setFitHeight(detailPoster.getFitWidth() * 1.4545);
		detailPoster.setEffect(new Reflection(0, 0.3, .8, 0));
		detailPoster.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) 
			{
				closeMovieInfo();
			}
		});
		
		if(mv.getPosterSaveLoc() != null && !mv.getPoster().equals("N/A"))
			detailPoster.setImage(new Image(new File(mv.getPosterSaveLoc()).toURI().toString()));
		else
			detailPoster.setImage(new Image("com/alexin/res/notFound.png"));
		
		Platform.runLater(new Runnable(){
			public void run() 
			{
				addStackPane.getChildren().add(detailPoster);
			}
		});
		
		detailShown = true;
	}
	
	@FXML
	public void closeMovieInfo()
	{
		detailShown = false;
		detailPane.setVisible(false);
		stackPane.getChildren().remove(blurPane);
		selectedEntry = null;
		Platform.runLater(new Runnable(){
			public void run() 
			{
				addStackPane.getChildren().remove(detailPoster);
			}
		});
	}
	
	public void favoriteButtonState(Boolean favorited)
	{
		Platform.runLater(new Runnable(){
			public void run() 
			{
				if(favorited)
				{
					buttonPanel.getChildren().remove(addFavoriteButton);
					addFavoriteButton.setImage(new Image("com/alexin/res/favorited-icon.png"));
					buttonPanel.getChildren().add(addFavoriteButton);
				}
				else
				{
					buttonPanel.getChildren().remove(addFavoriteButton);
					addFavoriteButton.setImage(new Image("com/alexin/res/favorite-icon.png"));
					buttonPanel.getChildren().add(addFavoriteButton);
				}
			}
		});
	}
	
	public void setColNum(int columns)
	{
		moviePane.setPrefColumns(columns);
	}
	
	public MovieEntry getSelectedEntry()
	{
		return selectedEntry;
	}
	
	public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
        movieList = this.mainApp.getMovieList();
        int buttonSize = (int) ((mainApp.getScreenBounds().getWidth() * 0.2)/4);
        playButton.setFitWidth(buttonSize);
        addInfoButton.setFitWidth(buttonSize);
        addFavoriteButton.setFitWidth(buttonSize);
        wwwButton.setFitWidth(buttonSize);
    }
}
