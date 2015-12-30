package <%=packageName%>.sample

import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator

/**
 * Created by Qiang on 12/23/15.
 */
interface UserService {
    PagedList<UserDTO> findUsersByCriteria(UserCriteria criteria, Paginator paginator)
}