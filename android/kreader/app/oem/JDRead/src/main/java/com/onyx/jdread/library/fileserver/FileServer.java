package com.onyx.jdread.library.fileserver;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxCopyBookToLibraryRequest;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.content.ContentType;
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
        ContentType ct = new ContentType(session.getHeaders().get("content-type")).tryUTF8();
        session.getHeaders().put("content-type", ct.getContentTypeHeader());

        Map<String, String> files = new HashMap<>();
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
            msg = getPage(selectPage(header.get("user-agent")));
        } else if (Method.POST.equals(method) && "/upload".equalsIgnoreCase(uri)) {
            File file = new File(files.get("file"));
            msg = "success";
            File targetFile = new File(Device.currentDevice.getExternalStorageDirectory() + "/Books/" + parameters.get("file"));
            final String fileName = parameters.get("file");
            RxCopyBookToLibraryRequest request = new RxCopyBookToLibraryRequest(LibraryDataBundle.getInstance().getDataManager(), file, targetFile);
            request.execute(new RxCallback<RxCopyBookToLibraryRequest>() {
                @Override
                public void onNext(RxCopyBookToLibraryRequest request1) {
                    ToastUtils.showToast(JDReadApplication.getInstance(), String.format(JDReadApplication.getInstance().getString(R.string.pass_succeed), fileName));
                }
            });
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

    public String selectPage(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*") || ua.substring(0, 4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
            return "mobile_index.html";
        }
        return "pc_index.html";
    }
}
