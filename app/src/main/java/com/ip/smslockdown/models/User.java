package com.ip.smslockdown.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ip.smslockdown.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User implements Serializable {

    private String fullName;
    private String address;

    public boolean saveUserToCache(Context context) {
        final File suspend_f = new File(context.getCacheDir(), "user");

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            Log.d("save obj", "get obj " + this.getFullName());
        } catch (Exception e) {
            Log.d("Save operation  ", "exception " + e.getMessage());
            keep = false;
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
                if (!keep) suspend_f.delete();
            } catch (Exception e) { /* do nothing */ }
        }

        return keep;
    }

    public User getUserFromCache(Context c) {
        final File suspend_f = new File(c.getCacheDir(), "user");

        User simpleClass = null;

        try (FileInputStream fis = new FileInputStream(suspend_f); ObjectInputStream is = new ObjectInputStream(fis)) {
            simpleClass = (User) is.readObject();
            Log.d("get obj", "get obj " + simpleClass.getFullName());
        } catch (Exception e) {
            Log.d("Get user operation  ", "exception " + e.getMessage());
        }

        return simpleClass;
    }

    public boolean deleteUserFromCache(Context c) {
        String path = c.getCacheDir().getAbsolutePath() + "/" + "user";
        if (new File(path).exists()) {
            Toast.makeText(c, R.string.toast_user_delete_success, Toast.LENGTH_LONG).show();
            return new File(path).delete();
        } else {
            Toast.makeText(c, R.string.toast_user_delete_error, Toast.LENGTH_LONG).show();
            Log.d("Delete operation ", " Failed to delete user");
            return false;
        }
    }

}
