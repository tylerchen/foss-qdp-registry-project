/*******************************************************************************
 * Copyright (c) Feb 18, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This module directs Feign's http requests to <a href="http://square.github.io/okhttp/">OkHttp</a>,
 * which enables SPDY and better network control. Ex.
 * <pre>
 * GitHub github = Feign.builder().client(new OkHttpClient()).target(GitHub.class,
 * "https://api.github.com");
 */
public class OkHttpRestClient implements RestClient {

    private okhttp3.OkHttpClient delegate;

    public OkHttpRestClient() {
        this(new okhttp3.OkHttpClient());
    }

    public OkHttpRestClient(okhttp3.OkHttpClient delegate) {
        this.delegate = delegate;
    }

    static okhttp3.Request toOkHttpRequest(Request input) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        requestBuilder.url(input.url());

        MediaType mediaType = null;
        boolean hasAcceptHeader = false;
        for (String field : input.headers().keySet()) {
            if (field.equalsIgnoreCase("Accept")) {
                hasAcceptHeader = true;
            }

            for (String value : input.headers().get(field)) {
                if (field.equalsIgnoreCase("Content-Type")) {
                    mediaType = MediaType.parse(value);
                    if (input.charset() != null) {
                        mediaType.charset(input.charset());
                    }
                }
                requestBuilder.addHeader(field, value);
            }
        }
        // Some servers choke on the default accept string.
        if (!hasAcceptHeader) {
            requestBuilder.addHeader("Accept", "*/*");
        }

        byte[] inputBody = input.body();
        boolean isMethodWithBody = "POST".equals(input.method()) || "PUT".equals(input.method());
        if (isMethodWithBody && inputBody == null) {
            // write an empty BODY to conform with okhttp 2.4.0+
            // http://johnfeng.github.io/blog/2015/06/30/okhttp-updates-post-wouldnt-be-allowed-to-have-null-body/
            inputBody = new byte[0];
        }

        RequestBody body = inputBody != null ? RequestBody.create(mediaType, inputBody) : null;
        requestBuilder.method(input.method(), body);
        return requestBuilder.build();
    }

    private static Response toResponse(okhttp3.Response input) throws IOException {
        return Response.builder().status(input.code()).reason(input.message()).headers(toMap(input.headers()))
                .body(toBody(input.body())).build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map<String, Collection<String>> toMap(Headers headers) {
        return (Map) headers.toMultimap();
    }

    private static Response.Body toBody(final ResponseBody input) throws IOException {
        if (input == null || input.contentLength() == 0) {
            if (input != null) {
                input.close();
            }
            return null;
        }
        final Integer length = input.contentLength() >= 0 && input.contentLength() <= Integer.MAX_VALUE
                ? (int) input.contentLength() : null;

        return new Response.Body() {

            @Override
            public void close() throws IOException {
                input.close();
            }

            @Override
            public Integer length() {
                return length;
            }

            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return input.byteStream();
            }

            @Override
            public Reader asReader() throws IOException {
                return input.charStream();
            }
        };
    }

    @Override
    public Response execute(Request input, Options options) throws IOException {
        okhttp3.OkHttpClient requestScoped;
        if (delegate.connectTimeoutMillis() != options.connectTimeoutMillis()
                || delegate.readTimeoutMillis() != options.readTimeoutMillis()) {
            requestScoped = delegate.newBuilder().connectTimeout(options.connectTimeoutMillis(), TimeUnit.MILLISECONDS)
                    .readTimeout(options.readTimeoutMillis(), TimeUnit.MILLISECONDS).build();
        } else {
            requestScoped = delegate;
        }
        okhttp3.Request request = toOkHttpRequest(input);
        okhttp3.Response response = requestScoped.newCall(request).execute();
        return toResponse(response).toBuilder().request(input).build();
    }
}
