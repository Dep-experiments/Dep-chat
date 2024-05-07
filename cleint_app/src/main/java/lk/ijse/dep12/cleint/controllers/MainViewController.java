package lk.ijse.dep12.cleint.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import lk.ijse.dep12.cleint.Image;
import lk.ijse.dep12.cleint.ImageLoad;
import lk.ijse.dep12.cleint.Message;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class MainViewController {
    public TextArea txtDisplay;
    public TextField txtMessage;
    public VBox vBox1;
    private String nickName;
    private Socket remoteSocket;

    private boolean imageSent = false;
    private ObjectOutputStream oos;

    FileChooser fc = new FileChooser();

    private ImageLoad im;

    private File selectFile;

    public void initData(Socket remoteSocket, String nickName) throws IOException {
        this.nickName = nickName;
        this.remoteSocket = remoteSocket;
        oos = new ObjectOutputStream(remoteSocket.getOutputStream());
        vBox1.getChildren().add(new Label("You: Entered into the chat room \n"));

        new Thread(() -> {
            try {
                while (true) {
                    InputStream is = remoteSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);
                    Object o = ois.readObject();
                    if (o instanceof Message) {

                        ByteArrayInputStream bis = new ByteArrayInputStream(((Message) o).text);
                        while (true) {
                            byte[] byteBuffer = new byte[1024];
                            int read = bis.read(byteBuffer);
                            if (read == -1) {
                                txtDisplay.appendText("Connection Lost");
                                break;
                            }
                            String message = new String(byteBuffer, 0, read);
                            System.out.println(message);
                            appendText(message);
                        }
                    } else {
                        System.out.println("Image");
                        File file1 = new File(LocalTime.now() + ".jpg");
                        System.out.println(file1);
                        try {
                            FileOutputStream fos = new FileOutputStream(file1);
                            fos.write(((ImageLoad) o).image);
                            System.out.println("Image Sucessfully Recieved");
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Image image = new Image(file1.toURI().toString());
                        appendImage(image);
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void btnSendOnAction(ActionEvent event) throws IOException {
        if (!imageSent || txtMessage.getText().isBlank()) {
            String message = nickName + ":" + txtMessage.getText().strip();
            Label lbl = new Label(message);
            VBox.setMargin(lbl, new javafx.geometry.Insets(10));
            HBox hBox = new HBox(lbl);
            hBox.setMinWidth(vBox1.getWidth());
            hBox.setAlignment(Pos.CENTER_RIGHT);

            vBox1.getChildren().add(hBox);
            byte[] fileContent = message.getBytes();
            Message msg = new Message(fileContent);
            oos.writeObject(msg);

        } else {
            if (im.image.length == 0) return;
            oos.writeObject(im);
            Image image = new Image(selectFile.toURI().toString());
            ImageView imView = new ImageView(image);
            imView.setFitWidth(200);
            imView.setPreserveRatio(true);
            VBox.setMargin(imView, new javafx.geometry.Insets(10));
            HBox hBox = new HBox(imView);
            hBox.setMinWidth(vBox1.getWidth());
            hBox.setAlignment(Pos.CENTER_RIGHT);
            vBox1.getChildren().add(hBox);
            im = null;
            imageSent = false;
            System.out.println("Image Sucessfully send");
        }
        txtMessage.clear();

    }

    public void appendImage(Image im) {
        Platform.runLater(() -> {
            ImageView imView = new ImageView(im);
            imView.setFitWidth(200);
            imView.setPreserveRatio(true);
            vBox1.getChildren().add(imView);
            //txtDisplay.appendText(message);
        });
    }

    public void appendText(String message) {
        Platform.runLater(() -> {
            vBox1.getChildren().add(new Label(message));
            //txtDisplay.appendText(message);
        });
    }

    public void btnAttachedOnAction(ActionEvent event) throws IOException {
        selectFile = fc.showOpenDialog(new Stage());
        FileInputStream fis = new FileInputStream(selectFile);

        txtMessage.setText(fis.toString());
        byte[] fileContent = new byte[(int) selectFile.length()];

        fis.read(fileContent);
        im = new ImageLoad(fileContent);
        System.out.println(fileContent);
        imageSent = true;
    }
}
