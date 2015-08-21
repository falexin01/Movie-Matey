package com.alexin.address.view;

import org.controlsfx.control.CheckListView;

import com.alexin.address.MovieList;
import com.alexin.address.MovieList.Genres;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class GenresDialogController 
{
	@FXML
	private CheckListView<Genres> checkListView;
	
	private Stage dialogStage;
	private ObservableList<Genres> checkedItems = FXCollections.emptyObservableList();
	
	public GenresDialogController()
	{
		
	}
	
	@FXML
    private void initialize() 
    {
		ObservableList<MovieList.Genres> genres = FXCollections.observableArrayList();
		genres.addAll(MovieList.Genres.values());
		checkListView.setItems(genres);
    }
	
	public void setDialogStage(Stage dialogStage) 
	{
        this.dialogStage = dialogStage;
    }
	
	@FXML
	private void handleConfirmed()
	{
		checkedItems = checkListView.getCheckModel().getCheckedItems();
		dialogStage.close();
	}
	
	@FXML
    private void handleCancel() 
	{
        dialogStage.close();
    }
	
	public ObservableList<Genres> getCheckedItems()
	{
		return checkedItems;
	}
	
}
