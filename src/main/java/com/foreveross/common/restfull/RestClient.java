/*******************************************************************************
 * Copyright (c) Feb 2, 2018 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import com.foreveross.common.ResultBean;
import org.iff.infra.util.*;
import org.iff.infra.util.TypeConvertHelper.TypeConvert;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static java.lang.String.format;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Feb 2, 2018
 */
@SuppressWarnings("unchecked")
public interface RestClient {

    Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Executes a request against its {@link Request#url() url} and returns a response.
     *
     * @param request safe to replay.
     * @param options options to apply to this request.
     * @return connected response, {@link Response.Body} is absent or unread.
     * @throws IOException on a network error connecting to {@link Request#url()}.
     */
    Response execute(Request request, Options options) throws IOException;

    class Request {
        /**
         * No parameters can be null except {@code body} and {@code charset}. All parameters must be
         * effectively immutable, via safe copies, not mutating or otherwise.
         */
        public static Request create(String method, String url, Map<String, Collection<String>> headers, byte[] body,
                                     Charset charset, Class<?> resultType) {
            return new Request(method, url, headers, body, charset, resultType);
        }

        private String method;
        private String url;
        private Map<String, Collection<String>> headers;
        private byte[] body;
        private Charset charset;
        private Class<?> resultType;

        public Request(String method, String url, Map<String, Collection<String>> headers, byte[] body, Charset charset,
                       Class<?> resultType) {
            this.method = PreCheckHelper.checkNotEmpty(method, "request method can't be empty.");
            this.url = PreCheckHelper.checkNotEmpty(url, "request url can't be empty");
            this.headers = PreCheckHelper.checkNotEmpty(headers, FCS.get("request headers of {0} {1}", method, url));
            this.body = body; // nullable
            this.charset = charset; // nullable
            this.resultType = resultType; // nullable
        }

        public String method() {
            return method;
        }

        public String url() {
            return url;
        }

        public Map<String, Collection<String>> headers() {
            return headers;
        }

        public Charset charset() {
            return charset;
        }

        public byte[] body() {
            return body;
        }

        public Class<?> resultType() {
            return resultType;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(method).append(' ').append(url).append(" HTTP/1.1\n");
            for (String field : headers.keySet()) {
                for (String value : Util.valuesOrEmpty(headers, field)) {
                    builder.append(field).append(": ").append(value).append('\n');
                }
            }
            if (body != null) {
                builder.append('\n').append(charset != null ? new String(body, charset) : "Binary data");
            }
            return builder.toString();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            String url;
            String method;
            Map<String, Collection<String>> headers;
            byte[] body;
            Object tag;
            Charset charset;
            Class<?> resultType;

            public Builder() {
                this.method = "GET";
                this.headers = new LinkedHashMap<String, Collection<String>>();
                this.charset = Charset.forName("UTF-8");
            }

            Builder(Request request) {
                this.url = request.url;
                this.method = request.method;
                this.body = request.body;
                //				this.tag = request.tag;
                this.headers = new LinkedHashMap<String, Collection<String>>();
                this.charset = Charset.forName("UTF-8");
            }

            /**
             * Sets the URL target of this request.
             *
             * @throws IllegalArgumentException if {@code url} is not a valid HTTP or HTTPS URL. Avoid this
             *                                  exception by calling {@link HttpUrl#parse}; it returns null for invalid URLs.
             */
            public Builder url(String url) {
                if (url == null) {
                    throw new NullPointerException("url == null");
                }

                // Silently replace web socket URLs with HTTP URLs.
                if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                    url = "http:" + url.substring(3);
                } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                    url = "https:" + url.substring(4);
                }

                this.url = url;
                return this;
            }

            /**
             * Sets the charset named.
             */
            public Builder charset(String charset) {
                this.charset = Charset.forName(charset);
                return this;
            }

            /**
             * Sets the resultType Class.
             */
            public Builder resultType(Class<?> resultType) {
                this.resultType = resultType;
                return this;
            }

            /**
             * Sets the header named {@code name} to {@code value}. If this request already has any headers
             * with that name, they are all replaced.
             */
            public Builder header(String name, String value) {
                Collection<String> collection = headers.get(name);
                if (collection == null) {
                    collection = new LinkedHashSet<String>();
                    headers.put(name, collection);
                }
                collection.add(value);
                return this;
            }

            public Builder removeHeader(String name) {
                headers.remove(name);
                return this;
            }

            /**
             * Sets this request's {@code Cache-Control} header, replacing any cache control headers already
             * present. If {@code cacheControl} doesn't define any directives, this clears this request's
             * cache-control headers.
             */
            public Builder cacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds,
                                        boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds,
                                        int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable) {
                StringBuilder result = new StringBuilder();
                if (noCache) {
                    result.append("no-cache, ");
                }
                if (noStore) {
                    result.append("no-store, ");
                }
                if (maxAgeSeconds != -1) {
                    result.append("max-age=").append(maxAgeSeconds).append(", ");
                }
                if (sMaxAgeSeconds != -1) {
                    result.append("s-maxage=").append(sMaxAgeSeconds).append(", ");
                }
                if (isPrivate) {
                    result.append("private, ");
                }
                if (isPublic) {
                    result.append("public, ");
                }
                if (mustRevalidate) {
                    result.append("must-revalidate, ");
                }
                if (maxStaleSeconds != -1) {
                    result.append("max-stale=").append(maxStaleSeconds).append(", ");
                }
                if (minFreshSeconds != -1) {
                    result.append("min-fresh=").append(minFreshSeconds).append(", ");
                }
                if (onlyIfCached) {
                    result.append("only-if-cached, ");
                }
                if (noTransform) {
                    result.append("no-transform, ");
                }
                if (immutable) {
                    result.append("immutable, ");
                }
                if (result.length() > 0) {
                    result.delete(result.length() - 2, result.length());
                }
                return header("Cache-Control", result.toString());
            }

            public Builder get() {
                return method("GET", null);
            }

            public Builder head() {
                return method("HEAD", null);
            }

            public Builder post(byte[] body) {
                return method("POST", body);
            }

            public Builder delete(byte[] body) {
                return method("DELETE", body);
            }

            public Builder delete() {
                return delete(new byte[0]);
            }

            public Builder put(byte[] body) {
                return method("PUT", body);
            }

            public Builder patch(byte[] body) {
                return method("PATCH", body);
            }

            public Builder method(String method, byte[] body) {
                if (method == null) {
                    throw new NullPointerException("method == null");
                }
                if (method.length() == 0) {
                    throw new IllegalArgumentException("method.length() == 0");
                }
                method = method.toUpperCase();
                if (body != null && (method.equals("GET") //
                        || method.equals("OPTIONS") //
                        || method.equals("DELETE") // Permitted as spec is ambiguous.
                        || method.equals("PROPFIND") // (WebDAV) without body: request <allprop/>
                        || method.equals("MKCOL") // (WebDAV) may contain a body, but behaviour is unspecified
                        || method.equals("LOCK"))) // (WebDAV) body: create lock, without body: refresh lock
                {
                    Logger.warn("method " + method + " must not have a request body.");
                    body = null;
                }
                if (body == null && (method.equals("POST") //
                        || method.equals("PUT") //
                        || method.equals("PATCH")//
                        || method.equals("PROPPATCH") // WebDAV
                        || method.equals("REPORT"))) // CalDAV/CardDAV (defined in WebDAV Versioning)
                {
                    Exceptions.runtime("method " + method + " must have a request body.");
                }
                this.method = method;
                this.body = body;
                return this;
            }

            /**
             * Attaches {@code tag} to the request. It can be used later to cancel the request. If the tag
             * is unspecified or null, the request is canceled by using the request itself as the tag.
             */
            public Builder tag(Object tag) {
                this.tag = tag;
                return this;
            }

            public Request build() {
                if (url == null) {
                    throw new IllegalStateException("url == null");
                }
                return new RestClient.Request(method, url, headers, body, charset, resultType);
            }
        }
    }

    class Options {

        /**
         * Defaults to 10 seconds. {@code 0} implies no timeout.
         *
         * @see HttpURLConnection#getConnectTimeout()
         */
        private int connectTimeoutMillis;
        /**
         * Defaults to 60 seconds. {@code 0} implies no timeout.
         *
         * @see HttpURLConnection#getReadTimeout()
         */
        private int readTimeoutMillis;

        public Options(int connectTimeoutMillis, int readTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            this.readTimeoutMillis = readTimeoutMillis;
        }

        public Options() {
            this(10 * 1000, 60 * 1000);
        }

        public int connectTimeoutMillis() {
            return connectTimeoutMillis;
        }

        public int readTimeoutMillis() {
            return readTimeoutMillis;
        }
    }

    class Response implements Closeable {

        private final int status;
        private final String reason;
        private final Map<String, Collection<String>> headers;
        private final Body body;
        private final Request request;

        private Response(Builder builder) {
            Util.checkState(builder.status >= 200, "Invalid status code: %s", builder.status);
            this.status = builder.status;
            this.reason = builder.reason; //nullable
            this.headers = Collections.unmodifiableMap(caseInsensitiveCopyOf(builder.headers));
            this.body = builder.body; //nullable
            this.request = builder.request; //nullable
        }

        public Builder toBuilder() {
            return new Builder(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            int status;
            String reason;
            Map<String, Collection<String>> headers;
            Body body;
            Request request;

            Builder() {
            }

            Builder(Response source) {
                this.status = source.status;
                this.reason = source.reason;
                this.headers = source.headers;
                this.body = source.body;
                this.request = source.request;
            }

            public Builder status(int status) {
                this.status = status;
                return this;
            }

            public Builder reason(String reason) {
                this.reason = reason;
                return this;
            }

            public Builder headers(Map<String, Collection<String>> headers) {
                this.headers = headers;
                return this;
            }

            public Builder body(Body body) {
                this.body = body;
                return this;
            }

            public Builder body(InputStream inputStream, Integer length) {
                this.body = InputStreamBody.orNull(inputStream, length);
                return this;
            }

            public Builder body(byte[] data) {
                this.body = ByteArrayBody.orNull(data);
                return this;
            }

            public Builder body(String text, Charset charset) {
                this.body = ByteArrayBody.orNull(text, charset);
                return this;
            }

            /**
             * @see Response#request
             * <p>
             * NOTE: will add null check in version 10 which may require changes
             * to custom feign.Client or loggers
             */
            public Builder request(Request request) {
                this.request = request;
                return this;
            }

            public Response build() {
                return new Response(this);
            }
        }

        /**
         * status code. ex {@code 200}
         * See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html" >rfc2616</a>
         */
        public int status() {
            return status;
        }

        /**
         * Nullable and not set when using http/2
         * See https://github.com/http2/http2-spec/issues/202
         */
        public String reason() {
            return reason;
        }

        /**
         * Returns a case-insensitive mapping of header names to their values.
         */
        public Map<String, Collection<String>> headers() {
            return headers;
        }

        public Body body() {
            return body;
        }

        public Request request() {
            return request;
        }

        public <T> T toObject() {
            try {
                String content = SocketHelper.getContent(body.asInputStream(), false);
                if (content.startsWith("<")) {
                    // if is xml, use XStream to de-serialize
                    return (T) XStreamHelper.fromXml(content);
                } else if (request.resultType() != null && request.resultType() != Void.class) {
                    TypeConvert convert = TypeConvertHelper.me().get(request.resultType().getName());
                    if (convert == null || "null".equalsIgnoreCase(convert.getName())) {
                        // if is json, use Gson to de-serialize
                        ResultBean bean = JsonHelper.toObject(ResultBean.class, content);
                        if (bean.isError()) {
                            Exceptions.runtime(content, "FOSS-REST-0200");
                        }
                        return (T) BeanHelper.copyProperties(request.resultType(), bean.getBody());
                    } else {
                        return (T) convert.convert(request.resultType().getName(), content, String.class, null);
                    }
                } else {
                    return (T) GsonHelper.toJson(content);
                }
            } catch (Exception e) {
                Exceptions.runtime("Can't not convert body json/xstrean to Object: " + request.getClass().getName());
            }
            return null;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("HTTP/1.1 ").append(status);
            if (reason != null)
                builder.append(' ').append(reason);
            builder.append('\n');
            for (String field : headers.keySet()) {
                for (String value : Util.valuesOrEmpty(headers, field)) {
                    builder.append(field).append(": ").append(value).append('\n');
                }
            }
            if (body != null)
                builder.append('\n').append(body);
            return builder.toString();
        }

        public void close() {
            StreamHelper.closeWithoutError(body);
        }

        public interface Body extends Closeable {

            /**
             * length in bytes, if known. Null if unknown or greater than {@link Integer#MAX_VALUE}.
             *
             * <br><br><br><b>Note</b><br> This is an integer as
             * most implementations cannot do bodies greater than 2GB.
             */
            Integer length();

            /**
             * True if {@link #asInputStream()} and {@link #asReader()} can be called more than once.
             */
            boolean isRepeatable();

            /**
             * It is the responsibility of the caller to close the stream.
             */
            InputStream asInputStream() throws IOException;

            /**
             * It is the responsibility of the caller to close the stream.
             */
            Reader asReader() throws IOException;
        }

        private static final class InputStreamBody implements Response.Body {

            private final InputStream inputStream;
            private final Integer length;

            private InputStreamBody(InputStream inputStream, Integer length) {
                this.inputStream = inputStream;
                this.length = length;
            }

            private static Body orNull(InputStream inputStream, Integer length) {
                if (inputStream == null) {
                    return null;
                }
                return new InputStreamBody(inputStream, length);
            }

            public Integer length() {
                return length;
            }

            public boolean isRepeatable() {
                return false;
            }

            public InputStream asInputStream() {
                return inputStream;
            }

            public Reader asReader() {
                return new InputStreamReader(inputStream, UTF_8);
            }

            public void close() throws IOException {
                inputStream.close();
            }
        }

        private static final class ByteArrayBody implements Response.Body {

            private final byte[] data;

            public ByteArrayBody(byte[] data) {
                this.data = data;
            }

            private static Body orNull(byte[] data) {
                if (data == null) {
                    return null;
                }
                return new ByteArrayBody(data);
            }

            private static Body orNull(String text, Charset charset) {
                if (text == null) {
                    return null;
                }
                Util.checkNotNull(charset, "charset");
                return new ByteArrayBody(text.getBytes(charset));
            }

            public Integer length() {
                return data.length;
            }

            public boolean isRepeatable() {
                return true;
            }

            public InputStream asInputStream() {
                return new ByteArrayInputStream(data);
            }

            public Reader asReader() throws IOException {
                return new InputStreamReader(asInputStream(), UTF_8);
            }

            public void close() {
            }

            public String toString() {
                return Util.decodeOrDefault(data, UTF_8, "Binary data");
            }
        }

        private static Map<String, Collection<String>> caseInsensitiveCopyOf(Map<String, Collection<String>> headers) {
            Map<String, Collection<String>> result = new TreeMap<String, Collection<String>>(
                    String.CASE_INSENSITIVE_ORDER);

            for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
                String headerName = entry.getKey();
                if (!result.containsKey(headerName)) {
                    result.put(headerName.toLowerCase(Locale.ROOT), new LinkedList<String>());
                }
                result.get(headerName).addAll(entry.getValue());
            }
            return result;
        }
    }

    class Server {
        private String id;
        private String host;
        private int port;

        public static Server create(String url) {
            Server server = new Server();
            server.setId(PreCheckHelper.checkNotEmpty(url));
            try {
                URL ul = new URL(url);
                server.setHost(ul.getHost());
                server.setPort(ul.getPort());
            } catch (MalformedURLException e) {
                Exceptions.runtime("url " + url + " is not the right format.", e, "FOSS-REST-0100");
            }
            return server;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }

    /**
     * implement a round robin load balancer.
     *
     * @author zhaochen
     */
    class LoadBalancerRoundRobin implements Iterator<Server> {
        private Object[][] servers;
        private int currentIndex = 0;
        private long lastPrintLogTime = System.currentTimeMillis();

        public LoadBalancerRoundRobin(String[] urls) {
            String[] newUrls = PreCheckHelper.trimAndRemoveBlank(urls);
            Server[] servers = new Server[newUrls.length];
            for (int i = 0; i < newUrls.length; i++) {
                servers[i] = Server.create(newUrls[i]);
            }
            Assert.notEmpty(servers, "servers url can't be empty!");
            this.servers = new Object[servers.length][];
            for (int i = 0; i < servers.length; i++) {
                this.servers[i] = new Object[]{ //
                        servers[i]/*0: Server            */, //
                        false/*     1: available      */, //
                        0L/*        2: last test time */, //
                        0/*         3: service times  */, //
                        0/*         4: fail times     */, //
                        0/*         5: recover times  *///
                };
            }
            currentIndex = Math.max(0, new Random().nextInt(this.servers.length));
        }

        void disable(Server server) {
            for (int i = 0; i < servers.length; i++) {
                if (((Server) servers[i][0]).getId().equals(server.getId())) {
                    servers[i][1] = false;
                    servers[i][4] = (int) servers[i][4] + 1;
                }
            }
        }

        void test(Server server) {
            Object[] ual = null;
            for (int i = 0; i < servers.length; i++) {
                if (((Server) servers[i][0]).getId().equals(server.getId())) {
                    ual = servers[i];
                }
            }
            if (ual == null) {
                return;
            }
            try {
                if (System.currentTimeMillis() - (long) ual[2] < 5000) {
                    return;
                }
                ual[1] = SocketHelper.test(server.getHost(), server.getPort());
                ual[2] = System.currentTimeMillis();
                ual[5] = (int) ual[5] + 1;
            } catch (Exception e) {
            }
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public synchronized Server next() {
            if (System.currentTimeMillis() - lastPrintLogTime > 300000) {
                lastPrintLogTime = System.currentTimeMillis();
                Logger.info(toString());
            }
            for (int i = 0; i < servers.length * 2; i++) {
                currentIndex = currentIndex % servers.length;
                Object[] ual = servers[currentIndex++];
                if ((boolean) ual[1]) {
                    ual[3] = (int) ual[3] + 1;
                    return (Server) ual[0];
                } else {
                    test((Server) ual[0]);
                }
            }
            return (Server) servers[0][0];
        }

        @Override
        public void remove() {
            //do nothing
        }

        @Override
        public String toString() {
            return GsonHelper.toJsonString(servers);
        }
    }

    class Util {
        /**
         * The HTTP Content-Length header field name.
         */
        public static final String CONTENT_LENGTH = "Content-Length";
        /**
         * The HTTP Content-Encoding header field name.
         */
        public static final String CONTENT_ENCODING = "Content-Encoding";
        /**
         * The HTTP Retry-After header field name.
         */
        public static final String RETRY_AFTER = "Retry-After";
        /**
         * Value for the Content-Encoding header that indicates that GZIP encoding is in use.
         */
        public static final String ENCODING_GZIP = "gzip";
        /**
         * Value for the Content-Encoding header that indicates that DEFLATE encoding is in use.
         */
        public static final String ENCODING_DEFLATE = "deflate";
        /**
         * UTF-8: eight-bit UCS Transformation Format.
         */
        public static final Charset UTF_8 = Charset.forName("UTF-8");

        // com.google.common.base.Charsets
        /**
         * ISO-8859-1: ISO Latin Alphabet Number 1 (ISO-LATIN-1).
         */
        public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

        //private static final int BUF_SIZE = 0x800; // 2K chars (4K bytes)

        /**
         * Copy of {@code com.google.common.base.Preconditions#checkNotNull}.
         */
        public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
            if (reference == null) {
                // If either of these parameters is null, the right thing happens anyway
                throw new NullPointerException(String.format(errorMessageTemplate, errorMessageArgs));
            }
            return reference;
        }

        /**
         * Copy of {@code com.google.common.base.Preconditions#checkState}.
         */
        public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
            if (!expression) {
                throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
            }
        }

        public static String decodeOrDefault(byte[] data, Charset charset, String defaultValue) {
            if (data == null) {
                return defaultValue;
            }
            checkNotNull(charset, "charset");
            try {
                return charset.newDecoder().decode(ByteBuffer.wrap(data)).toString();
            } catch (CharacterCodingException ex) {
                return defaultValue;
            }
        }

        /**
         * Returns an unmodifiable collection which may be empty, but is never null.
         */
        public static <T> Collection<T> valuesOrEmpty(Map<String, Collection<T>> map, String key) {
            return map.containsKey(key) && map.get(key) != null ? map.get(key) : Collections.<T>emptyList();
        }

    }

    class DefaultRestClient implements RestClient {

        private final SSLSocketFactory sslContextFactory;
        private final HostnameVerifier hostnameVerifier;

        /**
         * Null parameters imply platform defaults.
         */
        public DefaultRestClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
            this.sslContextFactory = sslContextFactory;
            this.hostnameVerifier = hostnameVerifier;
        }

        public Response execute(Request request, Options options) throws IOException {
            HttpURLConnection connection = convertAndSend(request, options);
            return convertResponse(connection).toBuilder().request(request).build();
        }

        HttpURLConnection convertAndSend(Request request, Options options) throws IOException {
            final HttpURLConnection connection = (HttpURLConnection) new URL(request.url()).openConnection();
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection sslCon = (HttpsURLConnection) connection;
                if (sslContextFactory != null) {
                    sslCon.setSSLSocketFactory(sslContextFactory);
                }
                if (hostnameVerifier != null) {
                    sslCon.setHostnameVerifier(hostnameVerifier);
                }
            }
            connection.setConnectTimeout(options.connectTimeoutMillis());
            connection.setReadTimeout(options.readTimeoutMillis());
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod(request.method());

            Collection<String> contentEncodingValues = request.headers().get(Util.CONTENT_ENCODING);
            boolean gzipEncodedRequest = contentEncodingValues != null
                    && contentEncodingValues.contains(Util.ENCODING_GZIP);
            boolean deflateEncodedRequest = contentEncodingValues != null
                    && contentEncodingValues.contains(Util.ENCODING_DEFLATE);

            boolean hasAcceptHeader = false;
            Integer contentLength = null;
            for (String field : request.headers().keySet()) {
                if (field.equalsIgnoreCase("Accept")) {
                    hasAcceptHeader = true;
                }
                for (String value : request.headers().get(field)) {
                    if (field.equals(Util.CONTENT_LENGTH)) {
                        if (!gzipEncodedRequest && !deflateEncodedRequest) {
                            contentLength = Integer.valueOf(value);
                            connection.addRequestProperty(field, value);
                        }
                    } else {
                        connection.addRequestProperty(field, value);
                    }
                }
            }
            // Some servers choke on the default accept string.
            if (!hasAcceptHeader) {
                connection.addRequestProperty("Accept", "*/*");
            }

            if (request.body() != null) {
                if (contentLength != null) {
                    connection.setFixedLengthStreamingMode(contentLength);
                } else {
                    connection.setChunkedStreamingMode(8196);
                }
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                if (gzipEncodedRequest) {
                    out = new GZIPOutputStream(out);
                } else if (deflateEncodedRequest) {
                    out = new DeflaterOutputStream(out);
                }
                try {
                    out.write(request.body());
                } finally {
                    try {
                        out.close();
                    } catch (IOException suppressed) { // NOPMD
                    }
                }
            }
            return connection;
        }

        Response convertResponse(HttpURLConnection connection) throws IOException {
            int status = connection.getResponseCode();
            String reason = connection.getResponseMessage();

            if (status < 0) {
                throw new IOException(format("Invalid status(%s) executing %s %s", status,
                        connection.getRequestMethod(), connection.getURL()));
            }

            Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
            for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
                // response message
                if (field.getKey() != null) {
                    headers.put(field.getKey(), field.getValue());
                }
            }

            Integer length = connection.getContentLength();
            if (length == -1) {
                length = null;
            }
            InputStream stream;
            if (status >= 400) {
                stream = connection.getErrorStream();
            } else {
                stream = connection.getInputStream();
            }
            return Response.builder().status(status).reason(reason).headers(headers).body(stream, length).build();
        }
    }
}
