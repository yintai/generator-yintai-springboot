package <%=packageName%>.sample

import org.nofdev.servicefacade.PagedList
import org.nofdev.servicefacade.Paginator

/**
 * Created by Qiang on 12/23/15.
 */
interface UserFacade {
    PagedList<UserDTO> findUsersByNameLike(String nameLike, Paginator paginator)
}