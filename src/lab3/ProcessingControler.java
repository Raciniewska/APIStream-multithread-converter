/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab3;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

import static java.lang.String.format;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import static javafx.scene.control.Alert.AlertType.ERROR;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
/**
 *
 * @author Basia
 */
public class ProcessingControler implements Initializable {
private String mode;
private int pool;
@FXML
   Text text1;
File outputDir;
@FXML
    TableView<ProcessingJob> imagesTable;
 @FXML 
 TableColumn<ProcessingJob, String> imageNameColumn;
 @FXML 
 TableColumn<ProcessingJob, Double> progressColumn;
 @FXML 
 TableColumn<ProcessingJob, String> statusColumn;
 ObservableList<ProcessingJob> jobs = FXCollections.observableArrayList();
 public void setMode(String mode1, int pool1 )   {
     pool=pool1;
     mode=mode1;
 }
 @FXML
    void addDirectory(ActionEvent event) {
        
        Window window = imagesTable.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        outputDir = directoryChooser.showDialog(window);
        //ZMIANA PLIKOW
    }
    @FXML
    void addFiles(ActionEvent event) {
   // Window window = imageNameColumn.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG images", "*.jpg"));
    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
    for (File fileOne : selectedFiles){
        jobs.add(ProcessingJob.of(fileOne));
    }
     imagesTable.setItems(jobs);
       if (outputDir != null) {
            new Thread(this::backgroundJob).start();
        }
    }

    
          
//metoda uruchamiana w tle(w tej samej klasie)
    private void backgroundJob(){
  long duration=0;
//...operacje w tle...
if(mode=="sek"){
// PRZETWARZANIE SEKWENCYJNE
long start = System.currentTimeMillis();//zwraca aktualny czas[ms]
jobs.stream().forEach(job->convertToGrayscale(job.getFile(),outputDir,job));
long end = System.currentTimeMillis();//czas po zakończeniu operacji[ms]
duration = end-start;//czas przetwarzania[ms]
}
// PRZETWARZANIE Z POLA WATKOW
else if(mode=="wspol"){
long start = System.currentTimeMillis();//zwraca aktualny czas[ms]
ForkJoinPool forkJoinPool = new ForkJoinPool(pool);
forkJoinPool.submit(() -> jobs.parallelStream().forEach(job->convertToGrayscale(job.getFile(),outputDir,job)));
while(forkJoinPool.getRunningThreadCount()!=0){
 int i=0;
}
long end = System.currentTimeMillis();//czas po zakończeniu operacji[ms]
duration = end-start;//czas przetwarzania[ms]
}
text1.setText("czas przetwarzania w ms: "+String.valueOf(duration));

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        imageNameColumn.setCellValueFactory(//nazwa pliku
               p -> new SimpleStringProperty(p.getValue().getFile().getName()));
        statusColumn.setCellValueFactory(//status przetwarzaniap 
               p -> p.getValue().getStatusProperty());
        progressColumn.setCellFactory(//wykorzystanie paska postępu
                ProgressBarTableCell.<ProcessingJob>forTableColumn()); 
        progressColumn.setCellValueFactory(//postęp przetwarzania 
                p -> p.getValue().getProgressProperty().asObject());
        
    }
  
private void convertToGrayscale(File originalFile, //oryginalny plik graficzny
        File outputDir, //katalog docelowy
        ProcessingJob job
       // DoubleProperty progressProp//własność określająca postępoperacji
                ) {
    try {//wczytanie oryginalnego pliku do pamięci
        //File originalFile = job.getFile();
        BufferedImage original = ImageIO.read(originalFile);//przygotowanie bufora na grafikę w skali szarości
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
//przetwarzanie piksel po pikselu
for (int i = 0; i < original.getWidth(); i++) {
        for (int j = 0; j < original.getHeight(); j++) {//pobranie składowych RGB
            int red = new Color(original.getRGB(i, j)).getRed();
            int green = new Color(original.getRGB(i, j)).getGreen();
            int blue = new Color(original.getRGB(i, j)).getBlue();
//obliczenie jasności piksela dla obrazu w skali szarości
int luminosity = (int) (0.21*red + 0.71*green + 0.07*blue);
//przygotowanie wartości koloru w oparciuoobliczoną jaskość
int newPixel = new Color(luminosity, luminosity, luminosity).getRGB();
//zapisanie nowego piksela w buforze
grayscale.setRGB(i, j, newPixel);
        }//obliczenie postępuprzetwarzania jako liczby z przedziału [0, 1]
        double progress = (1.0 + i) / original.getWidth();
//aktualizacja własności zbindowanej z paskiem postępu w tabeli
Platform.runLater(() -> {
                    job.setProgress(progress);
                    job.setStatus(format("%.1f%%", progress * 100));
                });
}
Platform.runLater(() -> job.setStatus(ProcessingJob.STATUS_DONE));
Path outputPath =Paths.get(outputDir.getAbsolutePath(), originalFile.getName());
//zapisanie zawartości bufora do pliku na dysku
ImageIO.write(grayscale, "jpg", outputPath.toFile());
     

    }catch (IOException ex) {//translacja wyjątkuthrow 
        new RuntimeException(ex);
            }
    }
}
