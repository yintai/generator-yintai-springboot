package <%=packageName%>.sample.facade

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 *
 */
@Service
@Slf4j
@CompileStatic
class HelloFacadeImpl implements HelloFacade {
    @Override
    String hello(String something) {
        log.debug("Begin a hello.")
        return "Hello ${something}"
    }
}
