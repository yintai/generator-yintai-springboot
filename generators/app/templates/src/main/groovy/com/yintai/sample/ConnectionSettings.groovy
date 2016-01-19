package <%=packageName%>.sample

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@CompileStatic
@Component
@ConfigurationProperties(prefix = "yintai.connection")
class ConnectionSettings {
    Integer defaultConnectionTimeout
    Integer defaultSoTimeout
    Integer defaultConnectionRequestTimeout
    Integer maxPerRoute
    Integer maxTotalConnection
}
