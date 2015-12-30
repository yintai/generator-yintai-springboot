package <%=packageName%>.sample

import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/**
 * Created by Qiang on 12/23/15.
 */
@Service
class UserFacadeImpl implements UserFacade {

    @Autowired
    private UserService userService

    @Override
    PagedList<UserDTO> findUsersByNameLike(String nameLike, Paginator paginator) {
        userService.findUsersByCriteria(new UserCriteria(nameLike:'zhangsan'),Paginator.page(1,10))
    }
}
