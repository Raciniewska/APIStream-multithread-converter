/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab3;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Basia
 */
public class Lab3 extends Application {
private static String mode="sek";
private static int pool=3;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Processing.fxml"));
        Parent root = loader.load();
        ProcessingControler controller = loader.<ProcessingControler>getController();
      //  Parent root = FXMLLoader.load(getClass().getResource("Processing.fxml"));
        
        controller.setMode(mode,pool);
        Scene scene = new Scene(root);
        
        stage.setMinWidth(600);
        stage.setMinHeight(200);
        stage.setTitle("Konwerter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if(arg.equals("--sek")){
               mode="sek";
            }
           if (arg.equals("--wspol")) {//najpierw katalogi potem pliki
              mode="wspol";
            }
           if (arg.matches("-?\\d+")){
              pool=Integer.parseInt(arg);
            }
            
        }
        
        launch(args);
    }
}