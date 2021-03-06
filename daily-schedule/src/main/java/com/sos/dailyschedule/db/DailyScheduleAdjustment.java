package com.sos.dailyschedule.db;

import com.sos.dailyschedule.job.CheckDailyScheduleOptions;
import com.sos.dashboard.globals.DashBoardConstants;
import com.sos.scheduler.history.db.SchedulerOrderHistoryDBItem;
import com.sos.scheduler.history.db.SchedulerOrderHistoryDBLayer;
import com.sos.scheduler.history.db.SchedulerTaskHistoryDBItem;
import com.sos.scheduler.history.db.SchedulerTaskHistoryDBLayer;

import org.apache.log4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DailyScheduleAdjustment {

    private static final Logger LOGGER = Logger.getLogger(DailyScheduleAdjustment.class);
    private DailyScheduleDBLayer dailyScheduleDBLayer;
    private SchedulerTaskHistoryDBLayer schedulerTaskHistoryDBLayer;
    private SchedulerOrderHistoryDBLayer schedulerOrderHistoryDBLayer;
    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private String schedulerId;
    private Date from;
    private Date to;
    private int dayOffset;
    private CheckDailyScheduleOptions options = null;

    public DailyScheduleAdjustment(File configurationFile) {
        dailyScheduleDBLayer = new DailyScheduleDBLayer(configurationFile);
        schedulerTaskHistoryDBLayer = new SchedulerTaskHistoryDBLayer(configurationFile);
        schedulerOrderHistoryDBLayer = new SchedulerOrderHistoryDBLayer(configurationFile);
    }

    private void adjustDaysScheduleItem(DailyScheduleDBItem dailyScheduleItem, List<SchedulerTaskHistoryDBItem> schedulerHistoryList) throws Exception {
        LOGGER.debug(String.format("%s records in schedulerHistoryList", schedulerHistoryList.size()));
        for (int i = 0; i < schedulerHistoryList.size(); i++) {
            SchedulerTaskHistoryDBItem schedulerHistoryDBItem = (SchedulerTaskHistoryDBItem) schedulerHistoryList.get(i);
            if (!schedulerHistoryDBItem.isAssignToDaysScheduler() && dailyScheduleItem.isStandalone()
                    && dailyScheduleItem.isEqual(schedulerHistoryDBItem)) {
                LOGGER.debug(String.format("... assign %s to %s", schedulerHistoryDBItem.getId(), dailyScheduleItem.getJobName()));
                dailyScheduleItem.setScheduleExecuted(schedulerHistoryDBItem.getStartTime());
                dailyScheduleItem.setSchedulerHistoryId(schedulerHistoryDBItem.getId());
                dailyScheduleItem.setStatus(DashBoardConstants.STATUS_ASSIGNED);
                dailyScheduleItem.setResult(schedulerHistoryDBItem.getExitCode());
                dailyScheduleDBLayer.getConnection().update(dailyScheduleItem);
                schedulerHistoryDBItem.setAssignToDaysScheduler(true);
                break;
            }
        }
        LOGGER.debug(String.format("... could not assign %s planned at:%s", dailyScheduleItem.getJobName(), 
                dailyScheduleItem.getSchedulePlannedFormated()));
    }

    private void adjustDaysScheduleOrderItem(DailyScheduleDBItem dailyScheduleItem, List<SchedulerOrderHistoryDBItem> schedulerOrderHistoryList) throws Exception {
        if (dailyScheduleDBLayer.getConnection() == null) {
            dailyScheduleDBLayer.initConnection(dailyScheduleDBLayer.getConfigurationFileName());
        }
        LOGGER.debug(String.format("%s records in schedulerOrderHistoryList", schedulerOrderHistoryList.size()));
        for (int i = 0; i < schedulerOrderHistoryList.size(); i++) {
            SchedulerOrderHistoryDBItem schedulerOrderHistoryDBItem = (SchedulerOrderHistoryDBItem) schedulerOrderHistoryList.get(i);
            if (!schedulerOrderHistoryDBItem.isAssignToDaysScheduler() && dailyScheduleItem.isOrderJob()
                    && dailyScheduleItem.isEqual(schedulerOrderHistoryDBItem)) {
                LOGGER.debug(String.format("... assign %s to %s/%s", schedulerOrderHistoryDBItem.getHistoryId(), 
                        dailyScheduleItem.getJobChainNotNull(), dailyScheduleItem.getOrderId()));
                dailyScheduleItem.setScheduleExecuted(schedulerOrderHistoryDBItem.getStartTime());
                dailyScheduleItem.setSchedulerOrderHistoryId(schedulerOrderHistoryDBItem.getHistoryId());
                dailyScheduleItem.setStatus(DashBoardConstants.STATUS_ASSIGNED);
                if (schedulerOrderHistoryDBItem.haveError()) {
                    dailyScheduleItem.setResult(1);
                } else {
                    dailyScheduleItem.setResult(0);
                }
                dailyScheduleDBLayer.getConnection().update(dailyScheduleItem);
                dailyScheduleDBLayer.getConnection().commit();
                schedulerOrderHistoryDBItem.setAssignToDaysScheduler(true);
                break;
            }
        }
    }

    public void adjustWithHistory() throws Exception {
        String lastSchedulerId = "***";
        dailyScheduleDBLayer.setWhereSchedulerId(this.schedulerId);
        dailyScheduleDBLayer.getFilter().setOrderCriteria("schedulerId");
        dailyScheduleDBLayer.setWhereFrom(from);
        dailyScheduleDBLayer.setWhereTo(to);
        List<DailyScheduleDBItem> dailyScheduleList = dailyScheduleDBLayer.getWaitingDailyScheduleList(-1);
        schedulerTaskHistoryDBLayer.getFilter().setLimit(-1);
        schedulerTaskHistoryDBLayer.getFilter().setExecutedFrom(from);
        schedulerTaskHistoryDBLayer.getFilter().setExecutedTo(dailyScheduleDBLayer.getWhereUtcTo());
        schedulerOrderHistoryDBLayer.getFilter().setLimit(-1);
        schedulerOrderHistoryDBLayer.getFilter().setExecutedFrom(from);
        schedulerOrderHistoryDBLayer.getFilter().setExecutedTo(dailyScheduleDBLayer.getWhereUtcTo());
        try {
            dailyScheduleDBLayer.getConnection().beginTransaction();
            List<SchedulerTaskHistoryDBItem> schedulerHistoryList = null;
            List<SchedulerOrderHistoryDBItem> schedulerOrderHistoryList = null;
            for (int i = 0; i < dailyScheduleList.size(); i++) {
                DailyScheduleDBItem daysScheduleItem = (DailyScheduleDBItem) dailyScheduleList.get(i);
                String schedulerId = daysScheduleItem.getSchedulerId();
                if (daysScheduleItem.isStandalone()) {
                    if (schedulerHistoryList == null || !schedulerId.equals(lastSchedulerId)) {
                        dailyScheduleDBLayer.getConnection().commit();
                        dailyScheduleDBLayer.getConnection().connect();
                        dailyScheduleDBLayer.getConnection().beginTransaction();
                        schedulerTaskHistoryDBLayer.getFilter().setSchedulerId(schedulerId);
                        schedulerHistoryList = schedulerTaskHistoryDBLayer.getUnassignedSchedulerHistoryListFromTo();
                        lastSchedulerId = schedulerId;
                    LOGGER.debug(String.format("... Reading scheduler_id: %s", schedulerId));
                    }
                    adjustDaysScheduleItem(daysScheduleItem, schedulerHistoryList);
                } else {
                    if (schedulerOrderHistoryList == null || !schedulerId.equals(lastSchedulerId)) {
                        dailyScheduleDBLayer.getConnection().commit();
                        dailyScheduleDBLayer.getConnection().connect();
                        dailyScheduleDBLayer.getConnection().beginTransaction();
                        schedulerOrderHistoryDBLayer.getFilter().setSchedulerId(schedulerId);
                        schedulerOrderHistoryList = schedulerOrderHistoryDBLayer.getUnassignedSchedulerOrderHistoryListFromTo();
                        LOGGER.debug(String.format("... Reading scheduler_id: %s", schedulerId));
                        lastSchedulerId = schedulerId;
                    }
                    adjustDaysScheduleOrderItem(daysScheduleItem, schedulerOrderHistoryList);
                }
            }
            dailyScheduleDBLayer.getConnection().commit();
        } catch (Exception e) {
            LOGGER.error("Error occurred adjusting the history: " + e.getMessage(), e);
        }
    }

    private void setFrom() throws ParseException {
        Date now = new Date();
        if (dayOffset < 0) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(now);
            calendar.add(GregorianCalendar.DAY_OF_MONTH, dayOffset);
            now = calendar.getTime();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String froms = formatter.format(now);
        froms = froms + "T00:00:00";
        formatter = new SimpleDateFormat(dateFormat);
        this.from = formatter.parse(froms);
    }

    private void setTo() throws ParseException {
        Date now = new Date();
        if (dayOffset > 0) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(now);
            calendar.add(GregorianCalendar.DAY_OF_MONTH, dayOffset);
            now = calendar.getTime();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String tos = formatter.format(now);
        tos = tos + "T23:59:59";
        formatter = new SimpleDateFormat(dateFormat);
        this.to = formatter.parse(tos);
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public void setOptions(CheckDailyScheduleOptions options) throws ParseException {
        this.options = options;
        schedulerId = this.options.getscheduler_id().getValue();
        dayOffset = this.options.getdayOffset().value();
        setFrom();
        setTo();
    }

    public DailyScheduleDBLayer getDailyScheduleDBLayer() {
        return dailyScheduleDBLayer;
    }

    public void setDailyScheduleDBLayer(DailyScheduleDBLayer dailyScheduleDBLayer) {
        this.dailyScheduleDBLayer = dailyScheduleDBLayer;
    }

    public void setTo(Date to) {
        this.to = to;
    }

}