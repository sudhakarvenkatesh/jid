package com.sos.eventing.db;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.joda.time.DateTime;

import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.eventing.evaluate.BooleanExp;
import com.sos.hibernate.layer.SOSHibernateDBLayer;

/** @author Uwe Risse */
public class SchedulerEventDBLayer extends SOSHibernateDBLayer {

    private final static Logger LOGGER = Logger.getLogger(SchedulerEventDBLayer.class);
    private final static String EVENT_ID = "eventId";
    private SchedulerEventFilter filter = null;

    public SchedulerEventDBLayer(final File configurationFile_) {
        super();
        this.setConfigurationFile(configurationFile_);
        resetFilter();
    }

    public SchedulerEventDBLayer(File configurationFile, SchedulerEventFilter filter_) {
        super();
        this.setConfigurationFile(configurationFile);
        filter = filter_;
    }

    public SchedulerEventDBItem getEvent(final Long id) {
        return (SchedulerEventDBItem) this.getSession().get(SchedulerEventDBItem.class, id);
    }

    public void resetFilter() {
        filter = new SchedulerEventFilter();
        filter.setEventClass("");
        filter.setEventId("");
        filter.setSchedulerId("");
        filter.setJobChain("");
        filter.setJobName("");
        filter.setExitCode("");
        filter.setExpires(new Date());
        filter.setSchedulerId("");
    }

    private Query getQuery(String hql) {
        Query query = null;
        query = session.createQuery(hql);
        if (filter.hasEvents()) {
            query.setParameterList(EVENT_ID, filter.getEventList());
        }
        if (filter.getSchedulerId() != null && !"".equals(filter.getSchedulerId())) {
            query.setParameter("schedulerId", filter.getSchedulerId());
        }
        if (filter.getRemoteSchedulerHost() != null && !"".equals(filter.getRemoteSchedulerHost())) {
            query.setParameter("remoteSchedulerHost", filter.getRemoteSchedulerHost());
        }
        if (filter.getRemoteSchedulerPort() != null && !"".equals(filter.getRemoteSchedulerPort())) {
            query.setParameter("remoteSchedulerPort", filter.getRemoteSchedulerPort());
        }
        if (filter.getJobChain() != null && !"".equals(filter.getJobChain())) {
            query.setParameter("jobChain", filter.getJobChain());
        }
        if (filter.getJobName() != null && !"".equals(filter.getJobName())) {
            query.setParameter("jobName", filter.getJobName());
        }
        if (filter.getEventClass() != null && !"".equals(filter.getEventClass())) {
            query.setParameter("eventClass", filter.getEventClass());
        }
        if (filter.getEventId() != null && !"".equals(filter.getEventId())) {
            query.setParameter("eventId", filter.getEventId());
        }
        if (filter.getOrderId() != null && !"".equals(filter.getOrderId())) {
            query.setParameter("orderId", filter.getOrderId());
        }
        if (filter.getExitCode() != null && !"".equals(filter.getExitCode())) {
            query.setParameter("exitCode", filter.getExitCode());
        }
        return query;
    }

    private Query setQueryParams(String hql) {
        try {
            Query query = getQuery(hql);
            return query;
        } catch (HibernateException e) {
            throw new JobSchedulerException("Error creating Query", e);
        }
    }

    public int delete() {
        if (session == null) {
            beginTransaction();
        }
        String hql = "delete from SchedulerEventDBItem " + getWhere();
        Query query = setQueryParams(hql);
        return query.executeUpdate();
    }

    private String getWhere() {
        String where = "";
        String and = "";
        if (filter.hasEvents()) {
            where += and + " eventId in ( :eventId )";
            and = " and ";
        }
        if (filter.getRemoteSchedulerPort() != null && !"".equals(filter.getRemoteSchedulerPort())) {
            where += and + " remoteSchedulerPort = :remoteSchedulerPort";
            and = " and ";
        }
        if (filter.getRemoteSchedulerHost() != null && !"".equals(filter.getRemoteSchedulerHost())) {
            where += and + " remoteSchedulerHost = :remoteSchedulerHost";
            and = " and ";
        }
        if (filter.getSchedulerId() != null && !"".equals(filter.getSchedulerId())) {
            where += and + " schedulerId = :schedulerId";
            and = " and ";
        }
        if (filter.getJobChain() != null && !"".equals(filter.getJobChain())) {
            where += and + " jobChain = :jobChain";
            and = " and ";
        }
        if (filter.getJobName() != null && !"".equals(filter.getJobName())) {
            where += and + " jobName = :jobName";
            and = " and ";
        }
        if (filter.getOrderId() != null && !"".equals(filter.getOrderId())) {
            where += and + " orderId = :orderId";
            and = " and ";
        }
        if (filter.getEventId() != null && !"".equals(filter.getEventId())) {
            where += and + " eventId = :eventId";
            and = " and ";
        }
        if (filter.getEventClass() != null && !"".equals(filter.getEventClass())) {
            where += and + " eventClass = :eventClass";
            and = " and ";
        }
        if (filter.getExitCode() != null && !"".equals(filter.getExitCode())) {
            where += and + " exitCode = :exitCode";
            and = " and ";
        }
        if (!"".equals(where.trim())) {
            where = "where " + where;
        }
        return where;
    }

    public List<SchedulerEventDBItem> getScheduleEventList(final int limit) {
        initSession();
        String hql = "from SchedulerEventDBItem " + getWhere() + filter.getOrderCriteria() + filter.getSortMode();
        Query query = setQueryParams(hql);
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.list();
    }

    public boolean checkEventExists() {
        return !getScheduleEventList(1).isEmpty();
    }

    public boolean checkEventExists(SchedulerEventDBItem event) {
        resetFilter();
        filter.setEventClass(event.getEventClass());
        filter.setEventId(event.getEventId());
        return !getScheduleEventList(1).isEmpty();
    }

    public boolean checkEventExists(SchedulerEventFilter filter) {
        resetFilter();
        this.filter = filter;
        return !getScheduleEventList(1).isEmpty();
    }

    public boolean checkEventExists(String condition) {
        resetFilter();
        List<SchedulerEventDBItem> listOfActiveEvents = getEventsFromDb();
        boolean result = false;
        Iterator<SchedulerEventDBItem> iExit = listOfActiveEvents.iterator();
        BooleanExp exp = new BooleanExp(condition);
        while (iExit.hasNext()) {
            SchedulerEventDBItem e = iExit.next();
            exp.replace(e.getEventName() + ":" + e.getExitCode(), "true");
            exp.replace(e.getEventId() + ":" + e.getExitCode(), "true");
            LOGGER.debug(exp.getBoolExp());
        }
        Iterator<SchedulerEventDBItem> iClass = listOfActiveEvents.iterator();
        while (iClass.hasNext()) {
            SchedulerEventDBItem e = iClass.next();
            exp.replace(e.getEventName(), "true");
            LOGGER.debug(exp.getBoolExp());
        }
        Iterator<SchedulerEventDBItem> iEventId = listOfActiveEvents.iterator();
        while (iEventId.hasNext()) {
            SchedulerEventDBItem e = iEventId.next();
            exp.replace(e.getEventId(), "true");
            LOGGER.debug(exp.getBoolExp());
        }
        LOGGER.debug("--------->" + exp.getBoolExp());
        return exp.evaluateExpression();
    }

    public boolean checkEventExists(String condition, String eventClass) {
        resetFilter();
        filter.setEventClass(eventClass);
        LOGGER.debug("eventClass:" + eventClass);
        boolean result = false;
        List<SchedulerEventDBItem> listOfActiveEvents = getEventsFromDb();
        Iterator<SchedulerEventDBItem> iExit = listOfActiveEvents.iterator();
        BooleanExp exp = new BooleanExp(condition);
        while (iExit.hasNext()) {
            SchedulerEventDBItem e = iExit.next();
            exp.replace(e.getEventId() + ":" + e.getExitCode(), "true");
        }
        Iterator<SchedulerEventDBItem> iEventId = listOfActiveEvents.iterator();
        while (iEventId.hasNext()) {
            SchedulerEventDBItem e = iEventId.next();
            exp.replace(e.getEventId(), "true");
        }
        return exp.evaluateExpression();
    }

    public List<SchedulerEventDBItem> getEventsFromDb() {
        if (!getSession().isOpen()) {
            initSession();
        }
        String getWhere = getWhere();
        Query query = setQueryParams("from SchedulerEventDBItem  " + getWhere);
        LOGGER.debug("where:" + getWhere);
        return query.list();
    }

    public SchedulerEventsCollection getMissingEvents(List<SchedulerEventDBItem> eventList) {
        resetFilter();
        SchedulerEventsCollection missingEvents = new SchedulerEventsCollection();
        filter.setEventList(eventList);
        if (filter.getEventList().isEmpty()) {
            LOGGER.info("no events to check - eventList is empty.");
        } else {
            List<SchedulerEventDBItem> resultList = getEventsFromDb();
            missingEvents.addAll(eventList);
            for (SchedulerEventDBItem item : resultList) {
                missingEvents.remove(item.getEventId());
            }
            LOGGER.info("missing events " + missingEvents.toString());
        }
        return missingEvents;
    }

    public void createEvent(SchedulerEventDBItem event) {
        if (!checkEventExists(event)) {
            DateTime now = new DateTime();
            DateTime expired = now.plusDays(60);
            beginTransaction();
            event.setCreated(new DateTime());
            event.setExpires(expired);
            saveOrUpdate(event);
            commit();
        }
    }

    public void deleteEventsForClass(String eventClass) {
        resetFilter();
        SchedulerEventFilter filter = new SchedulerEventFilter();
        filter.setEventClass(eventClass);
        delete();
    }

    public SchedulerEventFilter getFilter() {
        return filter;
    }

    public void setFilter(final SchedulerEventFilter filter) {
        this.filter = filter;
    }

}