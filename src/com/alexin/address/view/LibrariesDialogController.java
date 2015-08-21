package com.alexin.address.view;

import java.io.File;

import org.controlsfx.control.CheckListView;
import com.alexin.address.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class LibrariesDialogController 
{
	@FXML
	private CheckListView<File> checkListView;
	
	private Stage dialogStage;
	private MainApp mainApp;
	private ObservableList<File> libraries;
	
	@FXML
    private void initialize() 
    {
		libraries = FXCollections.observableArrayList();
    }
	
	public void setDialogStage(Stage dialogStage, MainApp mainApp) 
	{
        this.dialogStage = dialogStage;
        this.mainApp = mainApp;
        for(File file : mainApp.getLibraries().getLibraries())
        {
        	libraries.add(file);
        }
        checkListView.setItems(libraries);
    }
	
	@FXML
	private void handleAdd()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selDir = dirChooser.showDialog(dialogStage);
		if(selDir != null)
		{	
			libraries.add(selDir);
		}
	}
	
	@FXML
	private void handleRemove()
	{
		libraries.removeAll(checkListView.getCheckModel().getCheckedItems());
		checkListView.getCheckModel().clearChecks();
	}
	
	@SuppressWarnings("static-access")
	@FXML
	private void handleConfirm()
	{
		mainApp.getLibraries().clearLibrary();
		for(File file : libraries)
		{
			mainApp.getLibraries().addLibrary(file);
		}
		mainApp.getLibraries().saveLibrary(mainApp.librariesSaveLoc);
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel()
	{
		dialogStage.close();
	}
}
