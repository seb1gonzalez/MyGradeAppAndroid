package edu.utep.cs.cs4330.mygrade;

/**
 * Created by sebas on 2/15/2018.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Find a student's grade using the grade web service available at:
 * <code>http://www.cs.utep.edu/cheon/cs4330/grade/index.php</code>.
 * The web service take a user name and a PIN as a query string, e.g.,
 * <code>?user=staff&pin=1234</code>, and return the requested grade,
 * or an error if no grade is found.
 *
 * The <code>query</code> method must be called in a background
 * thread as shown below; it performs a network operation.
 *
 * <pre>
 *     WebClient web = new WebClient(new GradeListener() {
 *         public void onGrade(String date, Grade grade) {
 *             runOnUiThread(...);
 *         }
 *         public void onError(String msg) {
 *             runOnUiThread(...);
 *         }
 *     });
 *     new Thread(() -> web.query("staff", "1234")).start();
 * </pre>
 *
 * Note that the callback methods (onGrade and onError) are invoked
 * in the caller's thread  -- typically, a background thread.
 */
public class WebClient {

    /** To notify a requested grade, or an error. */
    public interface GradeListener {

        /** Called when a grade is received. This method is
         * invoked in the caller's thread.
         *
         * @param date The posting date of the grade.
         * @param grade The requested grade.
         */
        void onGrade(String date, Grade grade);

        /** Called when an error is encountered. This method is invoked
         * in the caller's thread.
         */
        void onError(String msg);
    }

    private static final String WS_URL = "http://www.cs.utep.edu/cheon/cs4330/grade/index.php";
    private static final String QS_USER = "?user=";
    private static final String QS_PIN = "&pin=";
    private static final String GET = "GET";
    private static final int RESPONSE_OK = 200;

    private static final String JS_RESPONSE = "response";
    private static final String JS_REASON = "reason";
    private static final String JS_DATE = "date";
    private static final String JS_GRADE = "grade";
    private static final String JS_TOTAL = "total";
    private static final String JS_DETAIL = "detail";
    private static final String JS_NAME = "name";
    private static final String JS_MAX = "max";
    private static final String JS_EARNED = "earned";

    /** The consumer of the requested grade. */
    private final GradeListener listener;

    /** Create a new instance. */
    public WebClient(GradeListener listener) {
        this.listener = listener;
    }

    /** Query a grade and notify it to the listener. */
    public void query(String user, String pin) {
        try {
            StringBuilder query = new StringBuilder(WS_URL);
            query.append(QS_USER);
            query.append(URLEncoder.encode(user, "utf-8"));
            query.append(QS_PIN);
            query.append(URLEncoder.encode(pin, "utf-8"));
            URL url = new URL(query.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(GET);
            if (con.getResponseCode() == RESPONSE_OK) {
                parseResponse(readAll(con.getInputStream()));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onError("Connection failed.");
    }

    private static String readAll(InputStream stream) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuffer response = new StringBuffer();
        String output;
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        return response.toString();
    }

    /**
     * Parse a response from the grade web service and notify it
     * to the listener. The response is a JSON string of the form:
     *
     *  { "response": true,
     *    "date": "Generated on Sun, Jan 21, 2018 5:35:09 PM",
     *    "sid": "1234",
     *    "grade": "A",
     *    "total": 98,
     *    "detail": [
     *      { "name": "hw1", "max": 100, "earned": 96 },
     *      { "name": "hw2", "max": 100, "earned": 100 }
     *    ]
     *  }
     *
     * or
     *  { "response": false, "reason": "PIN not specified" }
     */
    private void parseResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean ok = obj.getBoolean(JS_RESPONSE);
            if (!ok) {
                listener.onError(obj.getString(JS_REASON));
                return;
            }

            String date = obj.getString(JS_DATE);
            String letter = obj.getString(JS_GRADE);
            int total = obj.getInt(JS_TOTAL);
            JSONArray details = obj.getJSONArray(JS_DETAIL);

            List<Grade.Score> scores = new ArrayList<>();
            for (int i = 0; i < details.length(); i++) {
                JSONObject score = details.getJSONObject(i);
                String name = score.getString(JS_NAME);
                int max = score.getInt(JS_MAX);
                int earned = score.getInt(JS_EARNED);
                scores.add(new Grade.Score(name, max, earned));
            }
            Grade grade = new Grade(letter, total, scores);
            listener.onGrade(date, grade);
            return;
        } catch (JSONException e) {
        }
        listener.onError("Malformed response.");
    }
}