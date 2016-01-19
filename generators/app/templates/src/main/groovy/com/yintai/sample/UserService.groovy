package <%=packageName%>.sample

import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator

interface UserService {
    PagedList<UserDTO> findUsersByCriteria(UserCriteria criteria, Paginator paginator)
}