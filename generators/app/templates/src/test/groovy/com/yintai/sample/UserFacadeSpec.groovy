package <%=packageName%>.sample

import com.fasterxml.jackson.databind.ObjectMapper
import <%=packageName%>.<%=applicationName%>
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.nofdev.servicefacade.PagedList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Created by Qiang on 12/28/15.
 */
@WebAppConfiguration
@ActiveProfiles(value = "test")
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = [<%=applicationName%>])
class UserFacadeSpec extends Specification {

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private UserFacade userFacade

    private ClientAndServer mockServer
    private def baseUrl

    def setup() {
        mockServer = ClientAndServer.startClientAndServer(9999)
        baseUrl = "http://localhost:9999"
    }

    def cleanup() {
        mockServer.stop()
    }

    void "Mock 了一个 Service 层服务, 演示了 Facade 层的服务实现里是如何调用一个远程服务的. "() {
        setup:
        mockServer.when(
                HttpRequest.request()
                        .withPath("/service/json/<%=packageName%>.sample/User/findUsersByCriteria")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(objectMapper.writeValueAsString([callId: UUID.randomUUID().toString(), val: PagedList.wrap([new UserDTO(name: 'zhangsan', age: 10, birthday: OffsetDateTime.now(ZoneOffset.UTC)), new UserDTO(name: 'lisi', age: 20, birthday: OffsetDateTime.now(ZoneOffset.UTC))]), err: null]).toString())
        )
        def result = userFacade.findUsersByNameLike('zhangsan', null)

        expect:
        result.totalCount == 2
    }
}
