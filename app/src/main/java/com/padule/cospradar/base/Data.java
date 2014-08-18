package com.padule.cospradar.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Base64;
import android.util.Log;

public class Data implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    public String serializeToString() {
        String encoded = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(this);
            os.close();
            encoded = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        } catch (IOException e) {
            Log.e(Data.class.getName(), e.getMessage());
        }
        return encoded;
    }

    public static Data deSerializeFromString(String string) {
        if (string == null) {
            return null;
        }
        byte[] bytes = Base64.decode(string.getBytes(), Base64.DEFAULT);
        Data data = null;
        try {
            ObjectInputStream is = new ObjectInputStream( new ByteArrayInputStream(bytes) );
            data = (Data)is.readObject();
        } catch (IOException e) {
            Log.e(Data.class.getName(), e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(Data.class.getName(), e.getMessage());
        } catch (ClassCastException e) {
            Log.e(Data.class.getName(), e.getMessage());
        }
        return data;
    }

}
