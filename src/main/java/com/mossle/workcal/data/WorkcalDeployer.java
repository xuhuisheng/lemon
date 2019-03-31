package com.mossle.workcal.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.workcal.persistence.domain.WorkcalPart;
import com.mossle.workcal.persistence.domain.WorkcalRule;
import com.mossle.workcal.persistence.domain.WorkcalType;
import com.mossle.workcal.persistence.manager.WorkcalPartManager;
import com.mossle.workcal.persistence.manager.WorkcalRuleManager;
import com.mossle.workcal.persistence.manager.WorkcalTypeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkcalDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(WorkcalDeployer.class);
    private String defaultTenantId = "1";
    private String dataFilePath = "data/workcal.json";
    private String dataEncoding = "UTF-8";
    private WorkcalDTO workcal = new WorkcalDTO();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private WorkcalTypeManager workcalTypeManager;
    private WorkcalRuleManager workcalRuleManager;
    private WorkcalPartManager workcalPartManager;

    public void init() throws Exception {
        this.parseJson();
        this.processDatabase();
    }

    // ~
    public void processDatabase() {
        this.processType();

        for (YearDTO year : workcal.getYears()) {
            this.processYear(year);
        }
    }

    public WorkcalType findDefaultWorkcalType() {
        List<WorkcalType> workcalTypes = workcalTypeManager.getAll();

        if (workcalTypes.isEmpty()) {
            throw new IllegalStateException("cannot find default workcal type");
        }

        return workcalTypes.get(0);
    }

    public void processType() {
        List<WorkcalType> workcalTypes = workcalTypeManager.getAll();

        if (!workcalTypes.isEmpty()) {
            logger.info("skip workcal type");

            return;
        }

        WorkcalType workcalType = new WorkcalType();
        workcalType.setName(workcal.getType().getName());
        workcalType.setTenantId(defaultTenantId);
        workcalTypeManager.save(workcalType);
    }

    public void processYear(YearDTO year) {
        for (RuleDTO rule : workcal.getRules()) {
            this.processRule(rule, year);
        }

        for (HolidayDTO holiday : year.getHolidays()) {
            this.processHoliday(holiday, year);
        }
    }

    // INSERT INTO WORKCAL_RULE(ID,YEAR,WEEK,NAME,STATUS,TYPE_ID) VALUES(1,2014,2,'锟斤拷一',0,1);
    public void processRule(RuleDTO rule, YearDTO year) {
        WorkcalType defaultWorkcalType = this.findDefaultWorkcalType();
        String hqlRule = "from WorkcalRule where year=? and week=?";
        WorkcalRule workcalRule = workcalRuleManager.findUnique(hqlRule,
                year.getYear(), rule.getWeek());

        if (workcalRule != null) {
            logger.info("skip exists rule : {} {}", year.getYear(),
                    rule.getWeek());

            return;
        }

        workcalRule = new WorkcalRule();
        workcalRule.setYear(year.getYear());
        workcalRule.setWeek(rule.getWeek());
        workcalRule.setName(rule.getName());
        workcalRule.setStatus(0);
        workcalRule.setWorkcalType(defaultWorkcalType);
        workcalRule.setTenantId(defaultTenantId);
        workcalRuleManager.save(workcalRule);

        this.processPart(workcalRule);
    }

    public void processHoliday(HolidayDTO holiday, YearDTO year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(holiday.getStartDate());

        Date currentDate = holiday.getStartDate();

        while (!currentDate.after(holiday.getEndDate())) {
            this.processHolidayByDate(holiday, year, currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            currentDate = calendar.getTime();
        }
    }

    // INSERT INTO WORKCAL_RULE(ID,NAME,YEAR,WORK_DATE,STATUS,TYPE_ID) VALUES(11,'元旦', 2014,'2014-01-01 00:00:00',1,1);
    public void processHolidayByDate(HolidayDTO holiday, YearDTO year,
            Date currentDate) {
        WorkcalType defaultWorkcalType = this.findDefaultWorkcalType();
        String hqlRule = "from WorkcalRule where year=? and workDate=?";
        WorkcalRule workcalRule = workcalRuleManager.findUnique(hqlRule,
                year.getYear(), currentDate);

        if (workcalRule != null) {
            logger.info("skip exists rule : {} {}", year.getYear(), currentDate);

            return;
        }

        workcalRule = new WorkcalRule();
        workcalRule.setName(holiday.getName());
        workcalRule.setYear(year.getYear());
        workcalRule.setWorkDate(currentDate);
        workcalRule.setStatus(holiday.getStatus());
        workcalRule.setWorkcalType(defaultWorkcalType);
        workcalRule.setTenantId(defaultTenantId);
        workcalRuleManager.save(workcalRule);

        this.processPart(workcalRule);
    }

    // INSERT INTO WORKCAL_PART(ID,SHIFT,START_TIME,END_TIME,RULE_ID) VALUES(1,0,'9:00','12:00',1);
    // INSERT INTO WORKCAL_PART(ID,SHIFT,START_TIME,END_TIME,RULE_ID) VALUES(2,1,'13:00','18:00',1);
    public void processPart(WorkcalRule workcalRule) {
        WorkcalPart workcalPart0 = new WorkcalPart();
        workcalPart0.setShift(0);
        workcalPart0.setStartTime("9:00");
        workcalPart0.setEndTime("12:00");
        workcalPart0.setWorkcalRule(workcalRule);
        workcalPart0.setTenantId(defaultTenantId);
        workcalPartManager.save(workcalPart0);

        WorkcalPart workcalPart1 = new WorkcalPart();
        workcalPart1.setShift(1);
        workcalPart1.setStartTime("13:00");
        workcalPart1.setEndTime("18:00");
        workcalPart1.setWorkcalRule(workcalRule);
        workcalPart1.setTenantId(defaultTenantId);
        workcalPartManager.save(workcalPart1);
    }

    // ~
    public void parseJson() throws Exception {
        Map<String, Object> map = new JsonParser().parseMap(dataFilePath,
                dataEncoding);

        this.parseType(map);
        this.parseRules(map);
        this.parseYears(map);
    }

    public void parseType(Map<String, Object> map) {
        Map<String, Object> typeMap = (Map<String, Object>) map.get("type");

        workcal.getType().setCode((String) typeMap.get("code"));
        workcal.getType().setName((String) typeMap.get("name"));
    }

    public void parseRules(Map<String, Object> map) {
        List<Map<String, Object>> ruleList = (List<Map<String, Object>>) map
                .get("rules");

        for (Map<String, Object> ruleMap : ruleList) {
            RuleDTO rule = new RuleDTO();
            rule.setWeek((Integer) ruleMap.get("week"));
            rule.setName((String) ruleMap.get("name"));
            workcal.getRules().add(rule);
        }
    }

    public void parseYears(Map<String, Object> map) throws Exception {
        List<Map<String, Object>> yearList = (List<Map<String, Object>>) map
                .get("years");

        for (Map<String, Object> yearMap : yearList) {
            YearDTO year = new YearDTO();
            year.setYear((Integer) yearMap.get("year"));
            workcal.getYears().add(year);

            this.parseHolidays(year, yearMap);
        }
    }

    public void parseHolidays(YearDTO year, Map<String, Object> map)
            throws Exception {
        List<Map<String, Object>> holidayList = (List<Map<String, Object>>) map
                .get("holidays");

        for (Map<String, Object> holidayMap : holidayList) {
            HolidayDTO holiday = new HolidayDTO();
            holiday.setName((String) holidayMap.get("name"));
            holiday.setStatus((Integer) holidayMap.get("status"));
            holiday.setStartDate(this.parseDate(holidayMap.get("startDate")));
            holiday.setEndDate(this.parseDate(holidayMap.get("endDate")));
            year.getHolidays().add(holiday);
        }
    }

    public Date parseDate(Object object) throws Exception {
        return dateFormat.parse((String) object);
    }

    @Resource
    public void setWorkcalTypeManager(WorkcalTypeManager workcalTypeManager) {
        this.workcalTypeManager = workcalTypeManager;
    }

    @Resource
    public void setWorkcalRuleManager(WorkcalRuleManager workcalRuleManager) {
        this.workcalRuleManager = workcalRuleManager;
    }

    @Resource
    public void setWorkcalPartManager(WorkcalPartManager workcalPartManager) {
        this.workcalPartManager = workcalPartManager;
    }
}
