package <%=packageName%>.sample

import groovy.transform.CompileStatic
import org.nofdev.http.DefaultProxyStrategyImpl
import org.nofdev.http.DefaultRequestConfig
import org.nofdev.http.HttpJsonProxy
import org.nofdev.http.PoolingConnectionManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@CompileStatic
@Configuration
class ProxyConfiguration {

    @Autowired
    private ProxySettings proxySettings;

    @Autowired
    @Qualifier("httpPoolingConnectionManagerFactory")
    private PoolingConnectionManagerFactory httpPoolingConnectionManagerFactory;

    @Autowired
    @Qualifier("httpsPoolingConnectionManagerFactory")
    private PoolingConnectionManagerFactory httpsPoolingConnectionManagerFactory;

    @Autowired
    @Qualifier("defaultRequestConfig")
    private DefaultRequestConfig defaultRequestConfig;


    private Boolean isSSL(String baseUrl) {
        if (baseUrl?.startsWith('https://')) {
            true
        } else {
            false
        }
    }

    @Bean
    UserService userService() {
        def baseUrl = proxySettings.url.userService
        return new HttpJsonProxy(
                UserService,
                new DefaultProxyStrategyImpl(baseUrl),
                isSSL(baseUrl) ? httpsPoolingConnectionManagerFactory : httpPoolingConnectionManagerFactory,
                defaultRequestConfig
        ).getObject() as UserService
    }

}
