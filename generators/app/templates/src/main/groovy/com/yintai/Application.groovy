package <%=packageName%>

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
<% if( hasSample ) {%>import org.springframework.context.annotation.ComponentScan<%}%>

@CompileStatic
<% if( hasSample ) {%>@ComponentScan(["<%=packageName%>","org.nofdev"])<%}%>
@SpringBootApplication
class <%=applicationName%> {

    static void main(String[] args) {
        SpringApplication.run <%=applicationName%>, args
    }
}
