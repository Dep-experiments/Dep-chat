package lk.ijse.dep12.cleint.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginViewController {
    public TextField txtHost;
    public TextField txtNickName;
    public TextField txtPort;

    public void btnLoginOnAction(ActionEvent event) {
        String nickName = txtNickName.getText();
        String port = txtPort.getText();
        String host = txtHost.getText();
        if(nickName.isBlank()){
            txtNickName.requestFocus();
            txtNickName.selectAll();
        }if(host.isBlank()){
            txtHost.requestFocus();
            txtHost.selectAll();
        }if(port.isBlank()){
            txtPort.requestFocus();
            txtPort.selectAll();
        }

        try{
            Socket remorteSocket = new Socket(host.strip(), Integer.parseInt(port));
            ((Stage)(txtPort.getScene().getWindow())).close();
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
           MainViewController controller = loader.getController();
           controller.initData(remorteSocket,nickName);
            primaryStage.setTitle("DEP 12 General Chat App");
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.centerOnScreen();

        }catch(UnknownHostException e){
            new Alert(Alert.AlertType.ERROR,"Invalid IP").show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,"Invalid port Number").show();
            throw new RuntimeException(e);
        } catch(IOException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Faild to connect to Server").show();
        }


    }
}
