package lk.ijse.dep12.cleint;

import java.io.IOException;
import java.io.Serializable;

public class Message implements Serializable {

    public byte[] text;

    public Message( byte[] text) throws IOException {

        this.text = text;
    }


}
