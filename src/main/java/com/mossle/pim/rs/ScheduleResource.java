package com.mossle.pim.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
import javax.ws.rs.core.MediaType;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.pim.persistence.domain.PimSchedule;
import com.mossle.pim.persistence.manager.PimScheduleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("schedule")
public class ScheduleResource {
    private static Logger logger = LoggerFactory
            .getLogger(ScheduleResource.class);
    private PimScheduleManager pimScheduleManager;
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
            PimSchedule pimSchedule = new PimSchedule();
            pimSchedule.setName(title);
            pimSchedule.setStartTime(start);
            pimSchedule.setEndTime(end);
            pimSchedule.setContent(content);
            pimSchedule.setUserId(userId);
            pimScheduleManager.save(pimSchedule);

            BaseDTO result = new BaseDTO();

            result.setCode(200);

            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", pimSchedule.getId());
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
            PimSchedule pimSchedule = pimScheduleManager.get(id);
            pimSchedule.setName(title);
            pimSchedule.setStartTime(start);
            pimSchedule.setEndTime(end);
            pimSchedule.setContent(content);
            pimScheduleManager.save(pimSchedule);

            BaseDTO result = new BaseDTO();

            result.setCode(200);

            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", pimSchedule.getId());
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
            pimScheduleManager.removeById(id);

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

            List<PimSchedule> pimSchedules = pimScheduleManager.findBy(
                    "userId", userId);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            DateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss");

            for (PimSchedule pimSchedule : pimSchedules) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", pimSchedule.getId());
                map.put("title", pimSchedule.getName());
                map.put("start", dateFormat.format(pimSchedule.getStartTime()));
                map.put("end", dateFormat.format(pimSchedule.getEndTime()));
                map.put("content", pimSchedule.getContent());
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
    public void setPimScheduleManager(PimScheduleManager pimScheduleManager) {
        this.pimScheduleManager = pimScheduleManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
