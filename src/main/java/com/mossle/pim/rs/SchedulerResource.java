package com.mossle.pim.rs;

import java.text.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.pim.domain.PimScheduler;
import com.mossle.pim.manager.PimSchedulerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("scheduler")
public class SchedulerResource {
    private static Logger logger = LoggerFactory
            .getLogger(SchedulerResource.class);
    private PimSchedulerManager pimSchedulerManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private CurrentUserHolder currentUserHolder;

    @POST
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO save(@FormParam("title") String title,
            @FormParam("start") Date start, @FormParam("end") Date end,
            @FormParam("content") String content) {
        try {
            String userId = currentUserHolder.getUserId();
            PimScheduler pimScheduler = new PimScheduler();
            pimScheduler.setName(title);
            pimScheduler.setStartTime(start);
            pimScheduler.setEndTime(end);
            pimScheduler.setContent(content);
            pimScheduler.setUserId(userId);
            pimSchedulerManager.save(pimScheduler);

            BaseDTO result = new BaseDTO();

            result.setCode(200);

            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", pimScheduler.getId());
            map.put("title", title);
            map.put("start", dateFormat.format(start));
            map.put("end", dateFormat.format(end));
            map.put("content", content);
            result.setData(map);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO update(@FormParam("id") Long id,
            @FormParam("title") String title, @FormParam("start") Date start,
            @FormParam("end") Date end, @FormParam("content") String content) {
        try {
            PimScheduler pimScheduler = pimSchedulerManager.get(id);
            pimScheduler.setName(title);
            pimScheduler.setStartTime(start);
            pimScheduler.setEndTime(end);
            pimScheduler.setContent(content);
            pimSchedulerManager.save(pimScheduler);

            BaseDTO result = new BaseDTO();

            result.setCode(200);

            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", pimScheduler.getId());
            map.put("title", title);
            map.put("start", dateFormat.format(start));
            map.put("end", dateFormat.format(end));
            map.put("content", content);
            result.setData(map);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @POST
    @Path("remove")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO remove(@FormParam("id") Long id) {
        try {
            pimSchedulerManager.removeById(id);

            BaseDTO result = new BaseDTO();

            result.setCode(200);
            result.setData(true);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO get() {
        try {
            String userId = currentUserHolder.getUserId();

            List<PimScheduler> pimSchedulers = pimSchedulerManager.findBy(
                    "userId", userId);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");

            for (PimScheduler pimScheduler : pimSchedulers) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", pimScheduler.getId());
                map.put("title", pimScheduler.getName());
                map.put("start", dateFormat.format(pimScheduler.getStartTime()));
                map.put("end", dateFormat.format(pimScheduler.getEndTime()));
                map.put("content", pimScheduler.getContent());
                list.add(map);
            }

            BaseDTO result = new BaseDTO();

            result.setCode(200);

            result.setData(list);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            BaseDTO result = new BaseDTO();
            result.setCode(500);
            result.setMessage(ex.getMessage());

            return result;
        }
    }

    // ~ ======================================================================
    @Resource
    public void setPimSchedulerManager(PimSchedulerManager pimSchedulerManager) {
        this.pimSchedulerManager = pimSchedulerManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
