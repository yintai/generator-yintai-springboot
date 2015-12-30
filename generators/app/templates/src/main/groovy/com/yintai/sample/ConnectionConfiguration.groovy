package <%=packageName%>.sample

import groovy.transform.CompileStatic
import org.nofdev.http.DefaultRequestConfig
import org.nofdev.http.HttpClientUtil
import org.nofdev.http.PoolingConnectionManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException

/**
 * Created by wangxuesong on 15/8/7.
 */
@CompileStatic
@Configuration
class ConnectionConfiguration {

    @Autowired
    private ConnectionSettings connectionSettings

    @Bean
    public DefaultRequestConfig defaultRequestConfig() {
        DefaultRequestConfig defaultRequestConfig = new DefaultRequestConfig()
        if (connectionSettings.defaultConnectionTimeout) {
            defaultRequestConfig.defaultConnectionTimeout = connectionSettings.defaultConnectionTimeout
        }
        if (connectionSettings.defaultSoTimeout) {
            defaultRequestConfig.defaultSoTimeout = connectionSettings.defaultSoTimeout
        }
        if (connectionSettings.defaultConnectionRequestTimeout) {
            defaultRequestConfig.defaultConnectionRequestTimeout = connectionSettings.defaultConnectionRequestTimeout
        }

        return defaultRequestConfig
    }

    @Bean
    public PoolingConnectionManagerFactory httpsPoolingConnectionManagerFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        PoolingConnectionManagerFactory poolingConnectionManagerFactory = new PoolingConnectionManagerFactory(true);
        if (connectionSettings.getMaxPerRoute() != null) {
            poolingConnectionManagerFactory.setMaxPerRoute(connectionSettings.getMaxPerRoute());
        }
        if (connectionSettings.getMaxTotalConnection() != null) {
            poolingConnectionManagerFactory.setMaxTotalConnection(connectionSettings.getMaxTotalConnection());
        }
        return poolingConnectionManagerFactory;
    }

    @Bean
    public PoolingConnectionManagerFactory httpPoolingConnectionManagerFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        PoolingConnectionManagerFactory poolingConnectionManagerFactory = new PoolingConnectionManagerFactory(false);
        if (connectionSettings.getMaxPerRoute() != null) {
            poolingConnectionManagerFactory.setMaxPerRoute(connectionSettings.getMaxPerRoute());
        }
        if (connectionSettings.getMaxTotalConnection() != null) {
            poolingConnectionManagerFactory.setMaxTotalConnection(connectionSettings.getMaxTotalConnection());
        }
        return poolingConnectionManagerFactory;
    }

    @Bean
    public HttpClientUtil httpConnection() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientUtil httpClientUtil = new HttpClientUtil(httpPoolingConnectionManagerFactory(), defaultRequestConfig());
        return httpClientUtil;
    }

    @Bean
    public HttpClientUtil httpsConnection() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientUtil httpClientUtil = new HttpClientUtil(httpsPoolingConnectionManagerFactory(), defaultRequestConfig());
        return httpClientUtil;
    }
}
