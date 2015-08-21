package com.alexin.address.view;

import java.util.Map.Entry;

import com.alexin.address.MainApp;
import com.alexin.address.MovieEntry;
import com.alexin.address.MovieList.SearchParams;
import com.alexin.address.model.Actor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StatisticsViewController 
{
	@FXML
	private BarChart<String, Number> genreChart;
	@FXML
	private NumberAxis genreNumAxis;
	@FXML
	private TableView<Actor> actorTable;
	@FXML
	private TableColumn<Actor, String> actorCol;
	@FXML
	private TableColumn<Actor, Number> numMoviesCol;
	@FXML
	private Label totalMovies;
	@FXML
	private Label highestRated;
	@FXML
	private Label longestMovie;
	
	private MainApp mainApp;
	private ObservableList<Actor> observableActors;
	
	@FXML
	private void initialize() 
    {
		actorCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numMoviesCol.setCellValueFactory(cellData -> cellData.getValue().numMoviesProperty());
		
		actorTable.setRowFactory( tv -> 
		{
		    TableRow<Actor> row = new TableRow<>();
		    row.setOnMouseClicked(event -> 
		    {
		        if (event.getClickCount() == 2 && (!row.isEmpty())) 
		        {
		            Actor rowData = row.getItem();
		            searchActor(rowData);
		        }
		    });
		    return row ;
		});
    }
	
	private void searchActor(Actor actor)
	{
		mainApp.searchWith(actor.getName(), SearchParams.ACTOR);
	}
	
	public void setStatistics()
	{
		MovieEntry tempEntry = mainApp.movies.getLongest();
		totalMovies.setText(Integer.toString(mainApp.movies.getMovieList().size()));
		highestRated.setText(mainApp.movies.getHighestRated().getTitle());
		longestMovie.setText(tempEntry.getTitle() + " (" + tempEntry.getRuntime() + " mins)");
	}
	
	public void fillChart()
	{
		ObservableList<XYChart.Series<String, Number>> genreList = FXCollections.observableArrayList();
		Series<String, Number> series = new Series<String, Number>();
		int greatestGenre = 0;
		for(Entry<String, Integer> e : mainApp.movies.getGenreMap().entrySet())
		{
			if(e.getValue() > 0)
			{
				XYChart.Data<String, Number> data = new XYChart.Data<String, Number>(e.getKey(), e.getValue());
				
				if(e.getValue() > greatestGenre)
					greatestGenre = e.getValue();
				
				data.nodeProperty().addListener(new ChangeListener<Node>() 
				{
			        @Override public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) 
			        {
			
			          if (node != null) 
			          {
			        	  int r = 256 / e.getValue() * 2;
			        	  node.setStyle("-fx-bar-fill: rgb(" + r + "," + r + "," + r*3 +");");
			        	  
			        	  data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>()
			        	  {
			        		  	@Override
			        			public void handle(MouseEvent arg0) 
			        			{
			        				node.setStyle("-fx-bar-fill: grey");
			        			}
			        	  });
			        	  
			        	  data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>()
					      {
					        	@Override
					            public void handle(MouseEvent arg0) 
					        	{
					        		node.setStyle("-fx-bar-fill: rgb(" + r + "," + r + "," + r*3 +");");
					        	}
					      });
			        	  
			        	  displayLabelForData(data);
			          } 
			        }
			    });
				
				series.getData().add((data));
			}
		}
		genreList.add(series);
		genreNumAxis.setUpperBound((greatestGenre + 4) / 5 * 5);
		genreChart.setData(genreList);
	}
	
	private void displayLabelForData(XYChart.Data<String, Number> data) 
	{
		final Node node = data.getNode();
		final Text dataText = new Text(data.getYValue() + "");
		if(data.getYValue().intValue() > 2)
			dataText.setFill(Color.WHITE);
		else
			dataText.setFill(Color.BLACK);
		
		dataText.setFont(Font.font("Segoe UI Semibold"));
		  
		node.parentProperty().addListener(new ChangeListener<Parent>() 
		{
			@Override public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) 
			{
				Group parentGroup = (Group) parent;
				parentGroup.getChildren().add(dataText);
			}
		});

		node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() 
		{
			@Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) 
			{
				dataText.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
				dataText.setLayoutY(Math.round(bounds.getMinY() + dataText.prefHeight(-1) * 0.75));
			}
		});
		  
		data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				mainApp.searchWith(data.getXValue(), SearchParams.GENRE);
			}
		});
	}
	
	public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
        observableActors = FXCollections.observableArrayList();
        for(Entry<String, Integer> e : mainApp.movies.getActorMap().entrySet())
		{
			observableActors.add(new Actor(e.getKey(), e.getValue()));
		}
        actorTable.setItems(observableActors);
    }
}
