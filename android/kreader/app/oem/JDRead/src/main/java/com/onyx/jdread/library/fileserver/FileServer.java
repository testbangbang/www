package com.onyx.jdread.library.fileserver;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.device.Device;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.library.request.RxCopyBookToLibraryRequest;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

/**
 * Created by dxw on 2017/12/25.
 */

public class FileServer extends NanoHTTPD {
    private static String TAG = "FileServer";
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    public static final int SERVER_PORT = 8083;

    public static final String
            HTTP_OK = "200 OK",
            HTTP_PARTIALCONTENT = "206 Partial Content",
            HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable",
            HTTP_REDIRECT = "301 Moved Permanently",
            HTTP_NOTMODIFIED = "304 Not Modified",
            HTTP_FORBIDDEN = "403 Forbidden",
            HTTP_NOTFOUND = "404 Not Found",
            HTTP_BADREQUEST = "400 Bad Request",
            HTTP_INTERNALERROR = "500 Internal Server Error",
            HTTP_NOTIMPLEMENTED = "501 Not Implemented";

    private Context mContext;

    public FileServer(Context context) throws IOException {
        super(SERVER_PORT);
        mContext = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                Log.e(TAG, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                Log.e(TAG, re.getMessage());
            }
        }

        Map<String, String> parms = session.getParms();
        parms.put(QUERY_STRING_PARAMETER, session.getQueryParameterString());
        return serve(session.getUri(), method, session.getHeaders(), parms, files);
    }

    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        String msg = "";
        if (Method.GET.equals(method) && "/".equalsIgnoreCase(uri)) {
            msg = getPage("index.html");
        } else if (Method.POST.equals(method) && "/upload".equalsIgnoreCase(uri)) {
            File file = new File(files.get("file"));
            msg = "success";
            File targetFile = new File(Device.currentDevice.getExternalStorageDirectory() + "/Books/" + parameters.get("file"));
            RxCopyBookToLibraryRequest request = new RxCopyBookToLibraryRequest(JDReadApplication.getDataBundle().getDataManager(), file, targetFile);
            request.execute(null);
        }
        return newFixedLengthResponse(msg);
    }

    public String getPage(String filename) {
        String res = "";
        try {
            InputStreamReader page = new InputStreamReader(mContext.getAssets().open(filename));
            BufferedReader reader = new BufferedReader(page);
            String line = "";
            while ((line = reader.readLine()) != null) {
                res += line + "\n";
            }
            reader.close();

        } catch (IOException ioe) {
            Log.w(TAG, ioe.toString());
        }
        return res;
    }
}
