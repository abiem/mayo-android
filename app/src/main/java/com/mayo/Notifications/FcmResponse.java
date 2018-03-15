package com.mayo.Notifications;

/**
 * Created by Lakshmi on 15/03/18.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fcm downstream response <br>
 * {@see https://firebase.google.com/docs/cloud-messaging/http-server-ref} <br>
 * {@see https://firebase.google.com/docs/cloud-messaging/send-message}
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class FcmResponse {

    private final JSONObject mJson;

    /**
     * Each result from FCM
     *
     * @author Tom Misawa (riversun.org@gmail.com)
     */
    public static class FcmResult {

        private String messageId;
        private String error;
        private String registrationId;

        /**
         * Returns messageId
         * <p>
         * String specifying a unique ID for each successfully processed message
         * {@see
         * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
         * * }
         *
         * @return
         */
        public String getMessageId() {
            return messageId;
        }

        /**
         * Returns Error <br>
         * String specifying the error that occurred when processing the message
         * for the recipient. The possible values can be found in table 9.<br>
         * {@link "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en#table9"}
         *
         * @return
         */
        public String getError() {
            return error;
        }

        /**
         * Optional string specifying the canonical registration token for the
         * client app that the message was processed and sent to. Sender should
         * use this value as the registration token for future requests.
         * Otherwise, the messages might be rejected.<br>
         * <p>
         * {@see
         * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
         * * }
         *
         * @return
         */
        public String getRegistrationId() {
            return registrationId;
        }

        @Override
        public String toString() {
            return "Result [messageId=" + messageId + ", error=" + error + ", registrationId=" + registrationId + "]";
        }
    }

    /**
     * If HTTP Layer success
     */
    private final boolean mHttpLayerSuccess;

    private final int mHttpResponseCode;
    private String mHttpErrorMessage;
    private Exception mHttpLevelException;

    // service layer messages
    private Long mMulticastId;
    private Integer mSuccess;
    private int mFailure;
    private int mCanonicalIds;

    private List<FcmResult> mResultList;

    public FcmResponse(int httpResponseCode, JSONObject json) throws JSONException {
        mHttpLayerSuccess = true;
        mJson = json;
        mHttpResponseCode = httpResponseCode;
        parse(json);
    }

    public FcmResponse(int httpResponseCode, String httpErrorMsg, Exception e) {
        mHttpLayerSuccess = false;
        mJson = null;
        mHttpResponseCode = httpResponseCode;
        mHttpErrorMessage = httpErrorMsg;
        mHttpLevelException = e;
    }

    private void parse(JSONObject json) throws JSONException {

        if (!json.isNull("multicast_id")) {
            mMulticastId = (Long) json.getLong("multicast_id");
        }
        if (!json.isNull("success")) {
            mSuccess = (Integer) json.getInt("success");
        }
        if (!json.isNull("failure")) {
            mFailure = (Integer) json.getInt("failure");
        }
        if (!json.isNull("canonical_ids")) {
            mCanonicalIds = (Integer) json.getInt("canonical_ids");
        }

        JSONArray results = (JSONArray) getn(json, "results");

        if (results != null) {
            for (int i = 0; i < results.length(); i++) {

                if (mResultList == null) {
                    mResultList = new ArrayList<FcmResult>();
                }

                FcmResult rslt = new FcmResult();
                mResultList.add(rslt);

                JSONObject obj = (JSONObject) results.get(i);
                if (!obj.isNull("message_id")) {
                    rslt.messageId = (String) getn(obj, "message_id");
                }
                if (!obj.isNull("error")) {
                    rslt.error = (String) getn(obj, "error");
                }
                if (!obj.isNull("registration_id")) {
                    rslt.registrationId = (String) getn(obj, "registration_id");
                }
            }
        }
    }

    private Object getn(JSONObject json, String key) throws JSONException {
        if (json.isNull(key)) {
            return null;
        }
        return json.get(key);
    }

    public JSONObject getJson() {
        return mJson;
    }

    // Getters[begin]

    /**
     * @return Unique ID (number) identifying the multicast message.
     * <p>
     * <@link
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
     * >
     */
    public Long getMulticastId() {
        return mMulticastId;
    }

    /**
     * @return Number of messages that were processed without an error.
     * <p>
     * <@link
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
     * >
     */
    public Integer getSuccess() {
        return mSuccess;
    }

    /**
     * @return Number of messages that could not be processed.
     * <p>
     * <@link
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
     * >
     */
    public int getFailure() {
        return mFailure;
    }

    /**
     * @return Number of results that contain a canonical registration token. A
     * canonical registration ID is the registration token of the last
     * registration requested by the client app. This is the ID that the
     * server should use when sending messages to the entity(mobile
     * devices,browser front-end apps).
     * <p>
     * <@link
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
     * >
     */
    public int getCanonicalIds() {
        return mCanonicalIds;
    }

    /**
     * @return Array of objects representing the status of the messages
     * processed. The objects are listed in the same order as the
     * request (i.e., for each registration ID in the request, its
     * result is listed in the same index in the response).
     * <p>
     * message_id: String specifying a unique ID for each successfully
     * processed message.
     * <p>
     * registration_id: Optional string specifying the canonical
     * registration token for the client app that the message was
     * processed and sent to. Sender should use this value as the
     * registration token for future requests. Otherwise, the messages
     * might be rejected.
     * <p>
     * error: String specifying the error that occurred when processing
     * the message for the recipient. The possible values can be found
     * in {@see
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en#table9"
     * * }
     * <p>
     * <p>
     * <@link
     * "https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=en"
     * >
     */
    public List<FcmResult> getResult() {
        return mResultList;
    }

    public boolean isEnabled() {
        return mHttpLayerSuccess;
    }

    public int getHttpResponseCode() {
        return mHttpResponseCode;
    }

    public String getHttpErroMessage() {
        return mHttpErrorMessage;
    }

    public Exception getHttpException() {
        return mHttpLevelException;
    }

    @Override
    public String toString() {
        String resultText = "[]";
        if (mResultList != null) {
            resultText = Arrays.toString(mResultList.toArray());
        }
        return "FcmResponse [HttpLayerSuccess=" + mHttpLayerSuccess + ", HttpResponseCode=" + mHttpResponseCode + ", HttpErrorMessage=" + mHttpErrorMessage + ", MulticastId="
                + mMulticastId + ", Success=" + mSuccess + ", Failure=" + mFailure + ", CanonicalIds=" + mCanonicalIds + ", ResultList=" + resultText + "]";
    }

}