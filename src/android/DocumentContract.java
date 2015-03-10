/**
 * Cordova Document Contract Plugin
 *
 * (c) Dan Jarvis 2015 :: License MIT
 */
package com.danjarvis.documentcontract;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract.*;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class DocumentContract extends CordovaPlugin {
    private String INVALID_PARAMS_ERROR = "Invalid parameters specified.";
    private String INVALID_URI_ERROR = "Invalid URI specified.";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final CallbackContext cb = callbackContext;
        final JSONObject queryArgs;
        if (action.equals("getData")) {
            queryArgs = args.getJSONObject(0);
            if (null == queryArgs) {
                cb.error(INVALID_PARAMS_ERROR);
                return false;
            }

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    getData(queryArgs, cb);
                }
            });
            return true;
        } else if (action.equals("getContract")) {
            queryArgs = args.getJSONObject(0);
            if (null == queryArgs) {
                cb.error(INVALID_PARAMS_ERROR);
                return false;
            }

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    getContract(queryArgs, cb);
                }
            });
            return true;
        }

        return false;
    }

    /**
     * Gets the file data for the provided content URI.
     *
     * @return byte[] of file data. This will pass through as an ArrayBuffer
     *         in the JavaScript plugin.
     */
    private void getData(JSONObject args, CallbackContext callback) {
        try {
            Uri uri;
            ContentResolver contentResolver;
            InputStream is;
            ByteArrayOutputStream bs;
            byte[] buffer;
            int read = 0;

            uri = getUri(args);
            if (null == uri || !(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))) {
                callback.error(INVALID_URI_ERROR);
                return;
            }

            contentResolver = cordova.getActivity().getContentResolver();
            if (null == contentResolver) {
                callback.error("Failed to get ContentResolver object.");
                return;
            }

            is = contentResolver.openInputStream(uri);
            bs = new ByteArrayOutputStream();

            buffer = new byte[8192];
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                bs.write(buffer, 0, read);
            }

            callback.success(bs.toByteArray());

            bs.close();
            is.close();
        } catch (FileNotFoundException fe) {
            callback.error(fe.getMessage());
        } catch (IOException ie) {
            callback.error(ie.getMessage());
        }
    }

    /**
     * Gets the contract details for the provided content URI.
     *
     * @return Contract serialized to a JSONObject
     */
    private void getContract(JSONObject args, CallbackContext callback) {
        try {
            Uri uri;
            Cursor cursor;
            JSONObject response = new JSONObject();

            uri = getUri(args);
            if (null == uri || !(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))) {
                callback.error(INVALID_URI_ERROR);
                return;
            }

            cursor = cordova.getActivity().getContentResolver().query(uri, getColumns(args), null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                for (String col : cursor.getColumnNames())
                    response.put(col, cursor.getString(cursor.getColumnIndex(col)));
            }
            cursor.close();
            callback.success(response);
        } catch (JSONException je) {
            callback.error(je.getMessage());
        }
    }

    private Uri getUri(JSONObject args) {
        try {
            if (!args.has("uri"))
                return null;

            return Uri.parse(args.getString("uri"));
        } catch (JSONException je) {
            return null;
        }
    }

    private String[] getColumns(JSONObject args) {
        try {
            String[] projection;
            JSONArray cols;
            int len;

            if (!args.has("columns"))
                return null;

            cols = args.getJSONArray("columns");
            len = cols.length();
            if (len > 0) {
                projection = new String[len];
                for (int i = 0; i < len; i++)
                    projection[i] = cols.getString(i);
                return projection;
            }

            return null;
        } catch (JSONException je) {
            return null;
        }
    }
}
