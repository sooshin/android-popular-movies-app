package com.example.android.popularmovies.utilities;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.HttpException;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class NetworkError extends Throwable {

    public static final String DEFAULT_ERROR_MESSAGE = "Please try again.";
    public static final String NETWORK_ERROR_MESSAGE = "No Internet Connection!";
    private static final String ERROR_MESSAGE_HEADER = "Error Message";
    private final Throwable mError;

    public NetworkError(Throwable e) {
        super(e);
        mError = e;
    }

    public String getMessage() {
        return mError.getMessage();
    }

    public boolean isAuthFailure() {
        return mError instanceof HttpException &&
                ((HttpException) mError).code() == HTTP_UNAUTHORIZED;
    }

    public boolean isResponseNull() {
        return mError instanceof HttpException && ((HttpException) mError).response() == null;
    }

    public String getAppErrorMessage() {
        if (mError instanceof IOException) return NETWORK_ERROR_MESSAGE;
        if (!(mError instanceof HttpException)) return DEFAULT_ERROR_MESSAGE;
        retrofit2.Response<?> response = ((HttpException) mError).response();
        if (response != null) {
            String status = getJsonStringFromResponse(response);
            if (!TextUtils.isEmpty(status)) return status;

            Map<String, List<String>> headers = response.headers().toMultimap();
            if (headers.containsKey(ERROR_MESSAGE_HEADER))
                return headers.get(ERROR_MESSAGE_HEADER).get(0);
        }

        return DEFAULT_ERROR_MESSAGE;
    }

    protected String getJsonStringFromResponse(final retrofit2.Response<?> response) {
        try {
            String jsonString = response.errorBody().string();
            Response errorResponse = new Gson().fromJson(jsonString, Response.class);
            return errorResponse.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public Throwable getError() {
        return mError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkError that = (NetworkError) o;

        return mError != null ? mError.equals(that.mError) : that.mError == null;

    }

    @Override
    public int hashCode() {
        return mError != null ? mError.hashCode() : 0;
    }
}
