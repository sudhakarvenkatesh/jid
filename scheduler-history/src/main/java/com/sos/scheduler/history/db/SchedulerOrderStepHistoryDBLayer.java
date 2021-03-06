package com.sos.scheduler.history.db;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.sos.hibernate.layer.SOSHibernateDBLayer;
import com.sos.scheduler.history.SchedulerOrderStepHistoryFilter;

public class SchedulerOrderStepHistoryDBLayer extends SOSHibernateDBLayer {

    protected SchedulerOrderStepHistoryFilter filter = null;
    private static final Logger LOGGER = Logger.getLogger(SchedulerOrderStepHistoryDBLayer.class);

    public SchedulerOrderStepHistoryDBLayer(final String configurationFilename) {
        super();
        this.setConfigurationFileName(configurationFilename);
        this.initConnection(this.getConfigurationFileName());
        resetFilter();
    }

    public SchedulerOrderStepHistoryDBLayer(final File configurationFile) {
        super();
        try {
            this.setConfigurationFileName(configurationFile.getCanonicalPath());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            this.setConfigurationFileName("");
        }
        this.initConnection(this.getConfigurationFileName());
        resetFilter();
    }

    public SchedulerOrderStepHistoryDBItem get(final SchedulerOrderStepHistoryCompoundKey id) throws Exception {
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        connection.beginTransaction();
        return (SchedulerOrderStepHistoryDBItem) ((Session) connection.getCurrentSession()).get(SchedulerOrderStepHistoryDBItem.class, id);
    }

    public void resetFilter() {
        filter = new SchedulerOrderStepHistoryFilter();
        filter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        filter.setOrderCriteria("startTime");
        filter.setSortMode("desc");
    }

    protected String getWhereFromTo() {
        String where = "";
        String and = "";
        if (filter.getExecutedFromUtc() != null) {
            where += and + " startTime>= :startTimeFrom";
            and = " and ";
        }
        if (filter.getExecutedToUtc() != null) {
            where += and + " startTime <= :startTimeTo ";
            and = " and ";
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;
    }

    protected String getWhere() {
        String where = "";
        String and = "";
        if (filter.getHistoryId() != null) {
            where += and + " id.historyId = :historyId";
            and = " and ";
        }
        if (filter.getStartTime() != null && !"".equals(filter.getStartTime())) {
            where += and + " startTime>= :startTime";
            and = " and ";
        }
        if (filter.getEndTime() != null && !"".equals(filter.getEndTime())) {
            where += and + " endTime <= :endTime ";
            and = " and ";
        }
        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            where += and + " state = :state";
            and = " and ";
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;
    }

    public int deleteFromTo() throws Exception {
        String hql = "delete from SchedulerOrderStepHistoryDBItem " + getWhereFromTo();
        int row = 0;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        connection.beginTransaction();
        Query query = connection.createQuery(hql);
        query.setTimestamp("startTimeFrom", filter.getExecutedFromUtc());
        query.setTimestamp("startTimeTo", filter.getExecutedToUtc());
        row = query.executeUpdate();
        return row;
    }

    public void deleteInterval(final int interval) throws Exception {
        GregorianCalendar now = new GregorianCalendar();
        now.add(GregorianCalendar.DAY_OF_YEAR, -interval);
        filter.setExecutedTo(new Date());
        filter.setExecutedFrom(now.getTime());
        this.deleteFromTo();
    }

    public List<SchedulerOrderStepHistoryDBItem> getSchedulerOrderStepHistoryListFromTo(final int limit) throws Exception {
        List<SchedulerOrderStepHistoryDBItem> schedulerHistoryList = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        connection.beginTransaction();
        Query query = connection.createQuery("from SchedulerOrderStepHistoryDBItem " + getWhereFromTo() + filter.getOrderCriteria()
                + filter.getSortMode());
        if (filter.getExecutedFromUtc() != null && !"".equals(filter.getExecutedFromUtc())) {
            query.setTimestamp("startTimeFrom", filter.getExecutedFromUtc());
        }
        if (filter.getExecutedToUtc() != null && !"".equals(filter.getExecutedToUtc())) {
            query.setTimestamp("startTimeTo", filter.getExecutedToUtc());
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        schedulerHistoryList = query.list();
        return schedulerHistoryList;
    }

    public List<SchedulerOrderStepHistoryDBItem> getOrderStepHistoryItems(final int limit, long historyId) throws Exception {
        filter.setHistoryId(historyId);
        List<SchedulerOrderStepHistoryDBItem> historyList = null;
        if (connection == null) {
            initConnection(getConfigurationFileName());
        }
        connection.beginTransaction();
        Query query = connection.createQuery("from SchedulerOrderStepHistoryDBItem " + getWhere());
        if (filter.getHistoryId() != null) {
            query.setLong("historyId", filter.getHistoryId());
        }
        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            query.setParameter("state", filter.getStatus());
        }
        if (filter.getStartTime() != null && !"".equals(filter.getStartTime())) {
            query.setTimestamp("startTime", filter.getStartTime());
        }
        if (filter.getStartTime() != null && !"".equals(filter.getStartTime())) {
            query.setTimestamp("startTime", filter.getStartTime());
        }
        if (filter.getEndTime() != null && !"".equals(filter.getEndTime())) {
            query.setTimestamp("endTime", filter.getEndTime());
        }
        if (limit != 0) {
            query.setMaxResults(limit);
        }
        historyList = query.list();
        return historyList;
    }

    public SchedulerOrderStepHistoryFilter getFilter() {
        return filter;
    }

}