package com.mossle.disk.web.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.util.Select2Item;

// import com.mossle.disk.component.SearchUserHelper;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "协作者")
@RestController
@RequestMapping("disk/rs/member")
public class DiskMemberRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskMemberRestController.class);
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;

    // private SearchUserHelper searchUserHelper;
    @Operation(summary = "搜索协作者")
    @RequestMapping(value = "select2", method = RequestMethod.GET)
    public Result select2(
            @Parameter(description = "搜索内容") @RequestParam(value = "q", required = false) String q)
            throws Exception {
        logger.info("select2 : {}", q);

        String userId = currentUserHolder.getUserId();

        if (StringUtils.isBlank(q)) {
            return Result.success();
        }

        List<UserDTO> userDtos = userClient.search(q);

        // List<UserDTO> userDtos = searchUserHelper.search(q);
        List<Select2Item> list = new ArrayList<Select2Item>();

        for (UserDTO userDto : userDtos) {
            list.add(new Select2Item(userDto.getUsername(), userDto
                    .getDisplayName()));
        }

        return Result.success(list);
    }

    // ~ ======================================================================
    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    // @Resource
    // public void setSearchUserHelper(SearchUserHelper searchUserHelper) {
    // this.searchUserHelper = searchUserHelper;
    // }
}
