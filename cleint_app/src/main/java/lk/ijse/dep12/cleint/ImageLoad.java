package lk.ijse.dep12.cleint;

import java.io.IOException;
import java.io.Serializable;

public class ImageLoad implements Serializable {
    public byte[] image;

    public ImageLoad(byte[] image) throws IOException {

        this.image = image;
    }

}
