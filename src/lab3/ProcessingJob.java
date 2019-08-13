/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab3;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Basia
 */
public class ProcessingJob {
    final DoubleProperty progress;
    final SimpleStringProperty status;
    final File file;
    public static final String STATUS_WAITING = "oczekuje";
    public static final String STATUS_INIT = "przetwarzanie";
    public static final String STATUS_DONE = "zakonczone";
    
    public ProcessingJob(File file) throws MalformedURLException {
        this.file = file;
        this.status = new SimpleStringProperty(STATUS_WAITING);
        this.progress = new SimpleDoubleProperty(0);
    }

    
    public File getFile() {
        return file;
    }
    public SimpleStringProperty getStatusProperty() {
        return status;
    }
    
    public DoubleProperty getProgressProperty() {
        return progress;
    }
    
    public static ProcessingJob of(File file) {
        try {
            ProcessingJob job = new ProcessingJob(file);
            return job;

        } catch (MalformedURLException e) {
            return null;
        }
    }
    public Double getProgress() {
        return progress.get();
    }

    public void setProgress(Double progress) {
        this.progress.set(progress);
    }

    public String getStatus() {
        return this.status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

}
