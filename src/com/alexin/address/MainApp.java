package com.alexin.address;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alexin.address.MovieList.Genres;
import com.alexin.address.MovieList.SearchParams;
import com.alexin.address.view.GenresDialogController;
import com.alexin.address.view.LibrariesDialogController;
import com.alexin.address.view.ListViewController;
import com.alexin.address.view.MovieTileViewController;
import com.alexin.address.view.PosterDialogController;
import com.alexin.address.view.RootLayoutController;
import com.alexin.address.view.StatisticsViewController;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application 
{
	public final static String appDataDirName    = "\\.MovieMatey";
	public final static String dataBaseSaveName  = "\\MovieList.dat";
	public final static String favoritesSaveName = "\\FavoriteList.dat";
	public final static String posterSaveName    = "\\MoviePosters\\";
	public final static String librarySaveName   = "\\Libraries.txt";
	public final static String doNotSyncSaveName = "\\DoNotSync.txt";
	public static String appDataLoc;
	public static String dataBaseSaveLoc;
	public static String favoritesSaveLoc;
	public static String posterSaveLoc;
	public static String librariesSaveLoc;
	public static String doNotSyncSaveLoc;
	
	private Stage primaryStage;
	private Rectangle2D screenBounds;
	private BorderPane rootLayout;
	
	public Libraries libraries;
	public MovieList movies;
	public MovieList favorites;
	private MovieList activeList;
	private MovieList tempList;
	
	private Boolean posterViewShown;
	
	private RootLayoutController rlController;
	private MovieTileViewController mvController;
	private ListViewController lvController;
	
	public MainApp()
	{
		setup();
		screenBounds = Screen.getPrimary().getVisualBounds();
	}
	
	public Libraries getLibraries()
	{
		return libraries;
	}
	
	public boolean syncLibraries()
	{
		List<File> temp = libraries.getAllFiles();
		if(temp.isEmpty()) return false;
		movies.syncLibraries(temp, libraries.getDeletedFiles(), this);
		return true;
	}
	
	public void setActiveList(MovieList list)
	{
		this.activeList = list;
	}
	
	public MovieList getActiveList()
	{
		return activeList;
	}
	
	public MovieList getMovieList()
	{
		return movies;
	}
	
	public void setMovieList(MovieList ml)
	{
		this.movies = ml;
	}
	
	public MovieList getTempList()
	{
		return tempList;
	}
	
	public void setTempList(ArrayList<MovieEntry> ml)
	{
		this.tempList = new MovieList("Temp");
		this.tempList.setMovieList(ml);
	}
	
	public Rectangle2D getScreenBounds()
	{
		return screenBounds;
	}

	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Movie Matey");
		//this.primaryStage.initStyle(StageStyle.UNDECORATED);
		
		initRootLayout();
		showMovieTileView();
	}
	
	public void initRootLayout() 
	{
        try 
        {
        	FXMLLoader loader = new FXMLLoader();
        	loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
        	rootLayout = (BorderPane) loader.load();
        	rlController = loader.getController();
        	rlController.setMainApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
	
	public void showMovieTileView()
	{
		 try 
	        {
	        	FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("view/MovieTileView.fxml"));
	            AnchorPane movieTileView = (AnchorPane) loader.load();
	            
	            rootLayout.setCenter(movieTileView);
	            
	            mvController = loader.getController();
	            mvController.setMainApp(this);
	            mvController.fillMovieTiles(activeList, false);
	            posterViewShown = true;
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	}
	
	public void showListView()
	{
		try 
        {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/ListView.fxml"));
	        AnchorPane listView = (AnchorPane) loader.load();
	        
	        rootLayout.setCenter(listView);
	        
	        lvController = loader.getController();
	        lvController.setMainApp(this);
	        posterViewShown = false;
        }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void showStatisticsView()
	{
		try 
        {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/StatisticsView.fxml"));
	        AnchorPane statisticsView = (AnchorPane) loader.load();
	        
	        rootLayout.setCenter(statisticsView);
	        
	        StatisticsViewController controller = loader.getController();
	        controller.setMainApp(this);
	        controller.fillChart();
	        controller.setStatistics();
        }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public ObservableList<Genres> showGenresDialog()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/GenresDialog.fxml"));
	        BorderPane genresDialog = (BorderPane) loader.load();
	        
	        Stage dialogStage = new Stage(StageStyle.UTILITY);
	        dialogStage.setTitle("Genres");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(genresDialog);
	        dialogStage.setScene(scene);
	        
	        GenresDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        
	        dialogStage.showAndWait();
	        return controller.getCheckedItems();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void showLibrariesDialog()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/LibrariesDialog.fxml"));
	        BorderPane librariesDialog = (BorderPane) loader.load();
	        
	        Stage dialogStage = new Stage(StageStyle.UTILITY);
	        dialogStage.setTitle("Libraries");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(librariesDialog);
	        dialogStage.setScene(scene);
	        
	        LibrariesDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage, this);
	        
	        dialogStage.showAndWait();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Object[] showPosterDialog()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/PosterDialog.fxml"));
	        BorderPane genresDialog = (BorderPane) loader.load();
	        
	        Stage dialogStage = new Stage(StageStyle.UTILITY);
	        dialogStage.setTitle("HQ Poster");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(genresDialog);
	        dialogStage.setScene(scene);
	        
	        PosterDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        
	        dialogStage.showAndWait();
	        Object[] result = new Object[2];
	        if(controller.getStatus())
	        {
	        	result[0] = controller.getHeight();
	        	result[1] = controller.getApiKey();
	        }
	        else
	        	result = null;
	        
	        return result;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void searchWith(String search, SearchParams params)
	{
		showListView();
		rlController.searchWith(search, params);
	}
	
	public Boolean getPosterViewShown()
	{
		return posterViewShown;
	}
	
	public void setColumns(int numCols)
	{
		mvController.setColNum(numCols);
		repaintMovieTiles();
	}
	
	private void repaintMovieTiles()
	{
		mvController.fillMovieTiles(activeList, true);
	}
	
	private void repaintListView()
	{
		lvController.refreshObsMovieList();
	}
	
	public void refreshView()
	{
		if(posterViewShown != null)
		{
			if(posterViewShown)
				repaintMovieTiles();  
			else
				repaintListView();
		}
	}
	
	public MovieEntry getSelectedEntry()
	{
		if(posterViewShown)
			return mvController.getSelectedEntry();
		else
			return lvController.getSelectedEntry();
	}
	
	public void listViewShowSelect(int index)
	{
		showListView();
		lvController.selectEntry(index);
	}
	
	public void closeMovieInfo()
	{
		mvController.closeMovieInfo();
	}
	
	public void showMovieInfo(MovieEntry mv)
	{
		mvController.setDetailInfo(mv);
	}
	
	public void setup()
	{
		if(!(new File(System.getenv("APPDATA") + appDataDirName).exists()))
		{
			if(!(new File(System.getenv("APPDATA") + appDataDirName)).mkdir())
			{
				System.out.println("Unable to make directory!");
			}
			
			if(!(new File(System.getenv("APPDATA") + appDataDirName + posterSaveName)).mkdir())
			{
				System.out.println("Unable to make directory!");
			}
		}
		
		appDataLoc = System.getenv("APPDATA") + appDataDirName;
		posterSaveLoc = appDataLoc + posterSaveName;
		dataBaseSaveLoc = appDataLoc + dataBaseSaveName;
		favoritesSaveLoc = appDataLoc + favoritesSaveName;
		librariesSaveLoc = appDataLoc + librarySaveName;
		doNotSyncSaveLoc = appDataLoc + doNotSyncSaveName;
		
		libraries = new Libraries();
		movies = new MovieList("All Movies");
		favorites = new MovieList("Favorites");
		loadMovieList();
		loadFavoritesList();
		libraries.loadLibrary(librariesSaveLoc);
		libraries.loadDeletedFiles(doNotSyncSaveLoc);
		if(!syncLibraries())
			System.out.println("Library not found...");
		setActiveList(movies);
	}
	
	public Stage getPrimaryStage() 
	{
        return primaryStage;
    }
	
	public boolean saveMovieList()
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(dataBaseSaveLoc);
			ObjectOutputStream oos = new ObjectOutputStream(fout); 
			oos.writeObject(movies);
			oos.close();
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean saveFavoriteList()
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(favoritesSaveLoc);
			ObjectOutputStream oos = new ObjectOutputStream(fout); 
			oos.writeObject(favorites);
			oos.close();
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean loadMovieList()
	{
		try
		{
			FileInputStream fin = new FileInputStream(dataBaseSaveLoc);
			ObjectInputStream ois = new ObjectInputStream(fin);
			movies = (MovieList) ois.readObject();
			ois.close();
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	public boolean loadFavoritesList()
	{
		try
		{
			FileInputStream fin = new FileInputStream(favoritesSaveLoc);
			ObjectInputStream ois = new ObjectInputStream(fin);
			favorites = (MovieList) ois.readObject();
			ois.close();
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	public MovieList getFavoritesList() 
	{
		return favorites;
	}

	public static void main(String[] args) 
	{
		launch(args);
	}
}
