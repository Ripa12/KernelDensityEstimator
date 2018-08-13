package Indexer.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


/**
 * Created by Richard on 2018-08-11.
 */
public class Menu extends ScrollPane {

    private static TextField capField;
    private static TextField minSupField;
    private static TextField samplesField;

    private static TextField nrOfQueriesField;
    private static TextField duplicatesField;
    private static TextField columnField;

    private static TextField clustersField;

    private static TextField cellsField;
    private static TextField clusterSupportField;

    private static TextField dataSetField;

    private static TextField queryBatchField;
    private static TextField outField;

//    private Console console;

    public Menu(EventHandler ev){
        /**
         * Experiment
         */
        Label capLabel = new Label("STORAGE CAPACITY:");
        capField = new TextField(Settings.getStringProperty("STORAGE_CAPACITY"));
        capField.textProperty().addListener(new NumericConstraint(capField, 0));
        Label minSupLabel = new Label("MINIMUM SUPPORT:");
        minSupField = new TextField(Settings.getStringProperty("MINIMUM_SUPPORT"));
        Label samplesLabel = new Label("NR OF SAMPLES:");
        samplesField = new TextField(Settings.getStringProperty("NR_OF_SAMPLES"));
        samplesField.textProperty().addListener(new NumericConstraint(samplesField, 1));

        VBox experimentVB = new VBox();
        experimentVB.getChildren().addAll(capLabel, capField);
        experimentVB.getChildren().addAll(minSupLabel, minSupField);
        experimentVB.getChildren().addAll(samplesLabel, samplesField);

        /**
         * Queries
         */
        Label nrOfQueriesLabel = new Label("NR OF QUERIES:");
        nrOfQueriesField = new TextField(Settings.getStringProperty("NR_OF_QUERIES"));
        nrOfQueriesField.textProperty().addListener(new NumericConstraint(nrOfQueriesField, 1));
        Label duplicatesLabel = new Label("NR OF DUPLICATES:");
        duplicatesField = new TextField(Settings.getStringProperty("NR_OF_DUPLICATES"));
        duplicatesField.textProperty().addListener(new NumericConstraint(duplicatesField, 0));
        Label columnLabel = new Label("NR OF COMPOSITE COLUMNS:");
        columnField = new TextField(Settings.getStringProperty("NR_OF_COMPOSITE_COLUMNS"));
        columnField.textProperty().addListener(new NumericConstraint(columnField, 1));

        experimentVB.getChildren().addAll(nrOfQueriesLabel, nrOfQueriesField);
        experimentVB.getChildren().addAll(duplicatesLabel, duplicatesField);
        experimentVB.getChildren().addAll(columnLabel, columnField);

        /**
         * Table properties
         */
        Label clustersLabel = new Label("MEAN NR OF CLUSTERS:");
        clustersField = new TextField(Settings.getStringProperty("MEAN_NR_OF_CLUSTERS"));
        clustersField.textProperty().addListener(new NumericConstraint(clustersField, 0));

        experimentVB.getChildren().addAll(clustersLabel, clustersField);

        /**
         * Sub-clustering
         */
        Label cellsLabel = new Label("NR OF CELLS:");
        cellsField = new TextField(Settings.getStringProperty("NR_OF_CELLS"));
        cellsField.textProperty().addListener(new NumericConstraint(cellsField, 1));
        Label clusterSupportLabel = new Label("CLUSTER MINIMUM SUPPORT:");
        clusterSupportField = new TextField(Settings.getStringProperty("CLUSTER_MINIMUM_SUPPORT"));

        experimentVB.getChildren().addAll(cellsLabel, cellsField);
        experimentVB.getChildren().addAll(clusterSupportLabel, clusterSupportField);

        /**
         * Input
         */
        Label dataSetLabel = new Label("DATA SET:");
        dataSetField = new TextField(Settings.getStringProperty("DATA_SET"));

        experimentVB.getChildren().addAll(dataSetLabel, dataSetField);

        /**
         * Output
         */
        Label queryBatchLabel = new Label("QUERY BATCH FILE:");
        queryBatchField = new TextField(Settings.getStringProperty("QUERY_BATCH_FILE"));
        Label outLabel = new Label("OUTCOME:");
        outField = new TextField(Settings.getStringProperty("OUTCOME"));

        experimentVB.getChildren().addAll(queryBatchLabel, queryBatchField);
        experimentVB.getChildren().addAll(outLabel, outField);

        Button btn = new Button();
        btn.setText("Run");
        btn.setOnAction(ev);
        experimentVB.getChildren().addAll(btn);
        experimentVB.setPrefWidth(1000); // ToDo: should be same size as window, always!
        setContent(experimentVB);
    }

    class NumericConstraint implements ChangeListener<String> {

        TextField txtField;
        int min;

        NumericConstraint(TextField tf, int min){
            txtField = tf;
            this.min = min;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("\\d*")) {
                txtField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (Integer.parseInt(txtField.getText()) < min) {
                txtField.setText(Integer.toString(min));
            }
        }
    };

    public String getStringProperty(String key) {
        switch (key){
            case "DATA_SET":
                return dataSetField.getText();
            case "OUTCOME":
                return outField.getText();
            case "QUERY_BATCH_FILE":
                return queryBatchField.getText();
            default:
                return "";
        }
    }
    public Integer getIntegerProperty(String key) {
        switch (key){
            case "STORAGE_CAPACITY":
                return Integer.parseInt(capField.getText());
            case "NR_OF_SAMPLES":
                return Integer.parseInt(samplesField.getText());
            case "NR_OF_QUERIES":
                return Integer.parseInt(nrOfQueriesField.getText());
            case "NR_OF_DUPLICATES":
                return Integer.parseInt(duplicatesField.getText());
            case "NR_OF_COMPOSITE_COLUMNS":
                return Integer.parseInt(columnField.getText());
            case "NR_OF_CELLS":
                return Integer.parseInt(cellsField.getText());
            case "MEAN_NR_OF_CLUSTERS":
                return Integer.parseInt(clustersField.getText());
            default:
                return -1;
        }
    }
    public Double getDoubleProperty(String key) {
        switch (key){
            case "MINIMUM_SUPPORT":
                return Double.parseDouble(minSupField.getText());
            case "CLUSTER_MINIMUM_SUPPORT":
                return Double.parseDouble(clusterSupportField.getText());
            default:
                return -1.0;
        }
    }

    public void saveProperties(){
        Settings.updateProperty("STORAGE_CAPACITY", capField.getText());
        Settings.updateProperty("MINIMUM_SUPPORT", minSupField.getText());
        Settings.updateProperty("NR_OF_SAMPLES", samplesField.getText());

        Settings.updateProperty("NR_OF_QUERIES", nrOfQueriesField.getText());
        Settings.updateProperty("NR_OF_DUPLICATES", duplicatesField.getText());
        Settings.updateProperty("NR_OF_COMPOSITE_COLUMNS", columnField.getText());

        Settings.updateProperty("MEAN_NR_OF_CLUSTERS", clustersField.getText());

        Settings.updateProperty("NR_OF_CELLS", cellsField.getText());
        Settings.updateProperty("CLUSTER_MINIMUM_SUPPORT", clusterSupportField.getText());

        Settings.updateProperty("DATA_SET", dataSetField.getText());

        Settings.updateProperty("QUERY_BATCH_FILE", queryBatchField.getText());
        Settings.updateProperty("OUTCOME", outField.getText());

        Settings.storeProperties();
    }
}
