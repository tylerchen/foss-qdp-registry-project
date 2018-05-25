/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.restfull;

import org.iff.infra.util.Assert;
import org.iff.infra.util.CacheHelper;
import org.iff.infra.util.CacheHelper.Cacheable;
import org.iff.infra.util.Exceptions;
import org.iff.infra.util.Logger;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

/**
 * 封装的支持Https连接的Okhttp客户端
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Apr 20, 2018
 * auto generate by qdp.
 */
public class HttpsHelper {

    public static final String DEFAULT_TRUST_ALL = "DEFAULT_TRUST_ALL";
    public static final String DEFAULT_TRUST_CER = "DEFAULT_TRUST_CER";
    private static Cacheable cacheable;

    /**
     * 实现X509TrustManager接口，信任所有的证书
     *
     * @author zhaochen
     */
    public static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 实现HostnameVerifier接口
     *
     * @author zhaochen
     */
    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class HttpsFactory {
        private X509TrustManager trustManager;
        private SSLSocketFactory sslSocketFactory;

        public X509TrustManager getTrustManager() {
            return trustManager;
        }

        public SSLSocketFactory getSslSocketFactory() {
            return sslSocketFactory;
        }
    }

    /**
     * 创建信任所有证书的HTTPS。
     *
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Apr 20, 2018
     */
    public static HttpsFactory createTrustAllSSL() {
        HttpsFactory factory = new HttpsFactory();
        try {
            factory.trustManager = new TrustAllManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            /*使用构建出的trustManger初始化SSLContext对象*/
            sc.init(null, new TrustManager[]{factory.trustManager}, new SecureRandom());
            /*获得sslSocketFactory对象*/
            factory.sslSocketFactory = sc.getSocketFactory();
            getCacheable().set(DEFAULT_TRUST_ALL, factory);
            Logger.info("HTTPS SSLSocketFactory [" + DEFAULT_TRUST_ALL + "] is created.");
        } catch (Exception e) {
            Exceptions.runtime("HTTPS create trust all manager error!", e, "FOSS-SSL-001");
        }
        return factory;
    }

    /**
     * 创建
     *
     * @param certificate
     * @return
     * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
     * @since Apr 20, 2018
     */
    public static HttpsFactory createCertificateSSL(InputStream certificate) {
        return createCertificateSSL(certificate, DEFAULT_TRUST_CER);
    }

    public static HttpsFactory createCertificateSSL(InputStream certificate, String certificateId) {
        Assert.notBlank(certificateId, "HTTPS certificate id is required!");
        HttpsFactory factory = new HttpsFactory();
        try {
            factory.trustManager = trustManagerForCertificates(certificate);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            /*使用构建出的trustManger初始化SSLContext对象*/
            sslContext.init(null, new TrustManager[]{factory.trustManager}, null);
            /*获得sslSocketFactory对象*/
            factory.sslSocketFactory = sslContext.getSocketFactory();
            getCacheable().set(certificateId, factory);
            Logger.info("HTTPS SSLSocketFactory [" + certificateId + "] is created.");
        } catch (Exception e) {
            Exceptions.runtime("HTTPS create certificate manager error!", e, "FOSS-SSL-002");
        }
        return factory;
    }

    public static HttpsFactory getDefaultTrustAll() {
        HttpsFactory factory = getCacheable().get(DEFAULT_TRUST_ALL);
        if (factory == null) {
            factory = createTrustAllSSL();
        }
        return factory;
    }

    public static HttpsFactory getDefaultTrustCer() {
        return getTrustCer(DEFAULT_TRUST_CER);
    }

    public static HttpsFactory getTrustCer(String certificateId) {
        return getCacheable().get(certificateId);
    }

    /**
     * 获去信任自签证书的trustManager
     *
     * @param in 自签证书输入流
     * @return 信任自签证书的trustManager
     * @throws GeneralSecurityException
     */
    private static X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        /*通过证书工厂得到自签证书对象集合*/
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        /*为证书设置一个keyStore*/
        char[] password = "password".toCharArray();
        /*Any password will work. */
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        /*将证书放入keystore中*/
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        /* Use it to build an X509 trust manager. 使用包含自签证书信息的keyStore去构建一个X509TrustManager*/
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //InputStream in = null;
            /* By convention, 'null' creates an empty key store.*/
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static Cacheable getCacheable() {
        if (cacheable == null) {
            cacheable = new CacheHelper.MapCacheable();
        }
        return cacheable;
    }

    public static void setCacheable(Cacheable cacheable) {
        if (HttpsHelper.cacheable == null) {
            HttpsHelper.cacheable = cacheable;
        } else {
            Logger.warn("Cache has been set.");
        }
    }

}
