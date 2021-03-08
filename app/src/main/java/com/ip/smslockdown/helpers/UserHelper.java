package com.ip.smslockdown.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ip.smslockdown.R;
import com.ip.smslockdown.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserHelper {

    public void saveUserToCache(Context context, User user) {
        final File suspend_f = new File(context.getCacheDir(), "user");

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            Log.d("save obj", "get obj " + user.getFullName());
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

    public void deleteUserFromCache(Context c) {
        String path = c.getCacheDir().getAbsolutePath() + "/" + "user";
        if (new File(path).exists()) {
            Toast.makeText(c, R.string.toast_user_delete_success, Toast.LENGTH_LONG).show();
            new File(path).delete();
        } else {
            Toast.makeText(c, R.string.toast_user_delete_error, Toast.LENGTH_LONG).show();
            Log.d("Delete operation ", " Failed to delete user");
        }
    }
}
