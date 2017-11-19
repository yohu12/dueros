package xin.yysf.gui;

import com.baidu.duer.dcs.framework.message.DcsStreamRequestBody;
import com.baidu.duer.dcs.systeminterface.IAudioInput;
import com.baidu.duer.dcs.systeminterface.IWebView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import xin.yysf.duer.DuerSdkApp;



public class DuerOSGui extends Application implements IWebView, IAudioInput.IAudioInputListener {

    public static final String DEFAULT_URL = "http://xiaodu.baidu.com/saiya/";
    public WebEngine webEngine;

    private DuerSdkApp sdkApp;
    private Button goButton;
    private Button stopButton;


    //AudioFormat af = new AudioFormat(16000, 16, 1, true, false);


    public Parent createContent() {

        WebView webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setOnError((event -> {
            event.getException().printStackTrace(System.out);
        }));

        //final TextField locationField = new TextField(DEFAULT_URL);
        webEngine.locationProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            //locationField.setText(newValue);
        });
//        EventHandler<ActionEvent> goAction = (ActionEvent event) -> {
//            webEngine.load(locationField.getText().startsWith("http://")
//                    ? locationField.getText()
//                    : "http://" + locationField.getText());
//        };
//        locationField.setOnAction(goAction);

        goButton = new Button("录音");
        stopButton = new Button("停止");
        goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        goButton.setDefaultButton(true);

        goButton.setOnAction((ActionEvent event)->{
            sdkApp.factory.getVoiceInput().startRecord();
        });



        goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        goButton.setDefaultButton(true);
        stopButton.setDisable(true);
        stopButton.setOnAction((ActionEvent event)->{
            sdkApp.factory.getVoiceInput().stopRecord();
        });




//        goButton.setOnAction(goAction);

        // Layout logic
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(goButton,stopButton);
//        HBox.setHgrow(locationField, Priority.ALWAYS);

        VBox vBox = new VBox(5);
        vBox.getChildren().setAll(hBox, webView);
        vBox.setPrefSize(800, 400);
        VBox.setVgrow(webView, Priority.ALWAYS);
        return vBox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
        initDuerOs();
        primaryStage.setOnCloseRequest((e)->{
            sdkApp.shutdown();
            Platform.exit();
        });
    }

    private void initDuerOs() {
        sdkApp=DuerSdkApp.createDuerOs();
        sdkApp.setWebView(this);

        sdkApp.factory.getVoiceInput().registerAudioInputListener(this);
        
        webEngine.loadContent("<h1>HelloDuerOS</h1>");
        if(sdkApp.token==null){
            webEngine.loadContent("Oauth2添加回调<b>http://127.0.0.1:8080/auth</b>然后访问<p>"+sdkApp.url+"</p>");
        }
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void loadUrl(String url) {
        Platform.runLater(()-> {
            webEngine.load(url);
        });
    }

    @Override
    public void linkClicked(String url) {

    }

    @Override
    public void addWebViewListener(IWebViewListener listener) {

    }

    public void loadContent(String s) {
        Platform.runLater(()->webEngine.loadContent(s));
    }

    @Override
    public void onStartRecord(DcsStreamRequestBody dcsStreamRequestBody) {
        Platform.runLater(()->{
            goButton.setDisable(true);
            stopButton.setDisable(false);
        });
    }

    @Override
    public void onStopRecord() {
        Platform.runLater(()->{
            goButton.setDisable(false);
            stopButton.setDisable(true);
        });
    }
}
