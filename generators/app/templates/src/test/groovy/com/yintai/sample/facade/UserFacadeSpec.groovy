package <%=packageName%>.sample.facade

import com.fasterxml.jackson.databind.ObjectMapper
import <%=packageName%>.<%=applicationName%>
import <%=packageName%>.sample.service.UserCriteria
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.StringBody
import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import java.time.OffsetDateTime
import java.time.ZoneOffset

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
                		.withMethod("POST")//method matches
                        .withPath("/service/json/<%=packageName%>.sample.service/User/findUsersByCriteria")//path matches
                        .withBody(new StringBody("params=" + URLEncoder.encode(//body matches
                        objectMapper.writeValueAsString(
                                [
                                        new UserCriteria(nameLike: "tom"),
                                        Paginator.page(1, 10)
                                ]
                        ), "UTF-8")))
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(objectMapper.writeValueAsString([callId: UUID.randomUUID().toString(), val: PagedList.wrap([new UserDTO(name: 'tom', age: 10, birthday: OffsetDateTime.now(ZoneOffset.UTC)), new UserDTO(name: 'tomato', age: 20, birthday: OffsetDateTime.now(ZoneOffset.UTC))]), err: null]).toString())
        )
        def result = userFacade.findUsersByNameLike('tom', Paginator.page(1, 10))

        expect:
        result.totalCount == 2
    }
    
    void "测试远程 Service 服务宕机的情况. "() {
        when:
        userFacade.findUsersByNameLike('tom', Paginator.page(1, 10))
        then:
        thrown(Exception)
    }
}
