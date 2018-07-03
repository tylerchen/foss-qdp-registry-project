/*******************************************************************************
 * Copyright (c) 2018-07-02 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.config;

import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import org.iff.infra.util.JsonHelper;
import org.iff.infra.util.PreCheckHelper;
import org.iff.infra.util.StreamHelper;
import org.iff.infra.util.XStreamHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * CustomSerializerConfiguration
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since 2018-07-02
 * auto generate by qdp.
 */
@ConditionalOnProperty(name = "custom.spring.mvc.serializer.enable", matchIfMissing = true, havingValue = "true")
@Configuration
public class CustomSerializerConfiguration {
    /**
     * Use Gson instead of Jackson.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @date 2018-07-01
     * @since 2018-07-01
     */
    @Bean
    @ConditionalOnMissingBean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(JsonHelper.GSON);
        return converter;
    }

    /**
     * Use xstream instead of Jackson.
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @date 2018-07-01
     * @since 2018-07-01
     */
    @Bean
    @ConditionalOnMissingBean
    public XStreamHttpMessageConverter xstreamHttpMessageConverter() {
        return new XStreamHttpMessageConverter();
    }

    /**
     * XStreamHttpMessageConverter
     *
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @date 2018-07-01
     * @since 2018-07-01
     */
    public static class XStreamHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

        public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

        private XStream xstream = XStreamHelper.getXstream();

        public XStreamHttpMessageConverter() {
            super(MediaType.APPLICATION_XML, new MediaType("application", "*+xml"));
            this.setDefaultCharset(DEFAULT_CHARSET);
        }

        public XStream getXstream() {
            return xstream;
        }

        public void setXstream(XStream xstream) {
            this.xstream = PreCheckHelper.checkNotNull(xstream, "XStream is required!");
        }

        @Override
        @SuppressWarnings("deprecation")
        public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {

            TypeToken<?> token = getTypeToken(type);
            return readTypeToken(token, inputMessage);
        }

        @Override
        @SuppressWarnings("deprecation")
        protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {

            TypeToken<?> token = getTypeToken(clazz);
            return readTypeToken(token, inputMessage);
        }

        /**
         * @deprecated as of Spring Framework 4.3.8, in favor of signature-based resolution
         */
        @Deprecated
        protected TypeToken<?> getTypeToken(Type type) {
            return TypeToken.get(type);
        }

        private Object readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
            Reader xml = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
            try {
                return this.xstream.fromXML(xml);
            } catch (Exception ex) {
                throw new HttpMessageNotReadableException("XStream XML parse error: " + ex.getMessage(), ex);
            }
        }

        private Charset getCharset(HttpHeaders headers) {
            if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
                return DEFAULT_CHARSET;
            }
            return headers.getContentType().getCharset();
        }

        @Override
        protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage)
                throws IOException, HttpMessageNotWritableException {

            Charset charset = getCharset(outputMessage.getHeaders());
            OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
            try {
                this.xstream.toXML(o, writer);
                StreamHelper.closeWithoutError(writer);
            } catch (Exception ex) {
                throw new HttpMessageNotWritableException("Could not write XStream XML: " + ex.getMessage(), ex);
            }
        }
    }
}
