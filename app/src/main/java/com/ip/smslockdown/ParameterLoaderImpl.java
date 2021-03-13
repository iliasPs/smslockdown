package com.ip.smslockdown;

import android.content.Context;

final class ParameterLoaderImpl implements ParameterLoader {

    // attributes
    private final Context context;

    // constructors
    ParameterLoaderImpl(final Context context) {

        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        this.context = context.getApplicationContext();
    }

    private int getResourceIdForType(final String key, final String type) {

        if (this.context == null) {
            return 0;
        }

        return this.context.getResources().getIdentifier(key, type, context.getPackageName());
    }

    @Override
    public String getString(final String key) {

        int id = getResourceIdForType(key, "string");
        if (id == 0) {
            return null;
        }

        return this.context.getString(id);
    }


}
