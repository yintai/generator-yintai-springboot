package <%=packageName%>.sample

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/**
 * Created by Qiang on 12/23/15.
 */
@Service
@Slf4j
@CompileStatic
class UserFacadeImpl implements UserFacade {

    @Autowired
    private UserService userService

    @Override
    PagedList<UserDTO> findUsersByNameLike(String nameLike, Paginator paginator) {
        log.debug("Begin a rpc.")
        def users = userService.findUsersByCriteria(new UserCriteria(nameLike:'zhangsan'),Paginator.page(1,10))
        log.debug("I have get the result.")
        users
    }
}
