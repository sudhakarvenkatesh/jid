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
 
 

/**
 * 
 * \class SchedulerEventDBLayer \brief SchedulerEventDBLayer -
 * 
 * \details
 * 
 * \section SchedulerEventDBLayer.java_intro_sec Introduction
 * 
 * \section SchedulerEventDBLayer.java_samples Some Samples
 * 
 * \code .... code goes here ... \endcode
 * 
 * <p style="text-align:center">
 * <br />
 * --------------------------------------------------------------------------- <br />
 * APL/Software GmbH - Berlin <br />
 * ##### generated by ClaviusXPress (http://www.sos-berlin.com) ######### <br />
 * ---------------------------------------------------------------------------
 * </p>
 * \author Uwe Risse \version 13.09.2011 \see reference  
 * Created on 13.09.2011 14:40:18
 */

public class SchedulerEventDBLayer extends SOSHibernateDBLayer {

	private final static Logger logger = Logger.getLogger(SchedulerEventDBLayer.class);

	private final static String EVENT_ID = "eventId";
	private final static String EVENT_CLASS = "eventClass";	
	
	@SuppressWarnings("unused")
	private final String conClassName = "SchedulerEventDBLayer";
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
		return (SchedulerEventDBItem) this.getSession().get(
				SchedulerEventDBItem.class, id);
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
	
	private Query getQuery(String hql){
		Query query = null;
		query = session.createQuery(hql);
		
		if (filter.hasEvents()) {
	        query.setParameterList(EVENT_ID, filter.getEventList());
		}
		
		if (filter.getSchedulerId() != null
				&& !filter.getSchedulerId().equals("")) {
			query.setParameter("schedulerId", filter.getSchedulerId());
		}
		
		if (filter.getRemoteSchedulerHost() != null
				&& !filter.getRemoteSchedulerHost().equals("")) {
			query.setParameter("remoteSchedulerHost", filter.getRemoteSchedulerHost());
		}
		
		if (filter.getRemoteSchedulerPort() != null
				&& !filter.getRemoteSchedulerPort().equals("")) {
			query.setParameter("remoteSchedulerPort", filter.getRemoteSchedulerPort());
		}
		
		if (filter.getJobChain() != null
				&& !filter.getJobChain().equals("")) {
			query.setParameter("jobChain", filter.getJobChain());
		}
		
		if (filter.getJobName() != null
				&& !filter.getJobName().equals("")) {
			query.setParameter("jobName", filter.getJobName());
		}
		
		if (filter.getEventClass() != null
				&& !filter.getEventClass().equals("")) {
			query.setParameter("eventClass", filter.getEventClass());
		}
		
		if (filter.getEventId() != null
				&& !filter.getEventId().equals("")) {
			query.setParameter("eventId", filter.getEventId());
		}
		
		if (filter.getOrderId() != null
				&& !filter.getOrderId().equals("")) {
			query.setParameter("orderId", filter.getOrderId());
		}
 
		if (filter.getExitCode() != null
				&& !filter.getExitCode().equals("")) {
			query.setParameter("exitCode", filter.getExitCode());
		}		
		
		return query;
				
	}
	
    private Query setQueryParams(String hql) {
		try {
			Query query = getQuery(hql);
			return query;

		} catch(HibernateException e) {
			throw new JobSchedulerException("Error creating Query",e);
		}
	}

	public int delete() {

		if (session == null) {
			beginTransaction();
		}

		String hql = "delete from SchedulerEventDBItem " + getWhere();
		Query query = setQueryParams(hql);

		int row = query.executeUpdate();

		return row;
	}

	private String getWhere() {
		String where = "";
		String and = "";
		 
		 if (filter.hasEvents()) {
	            where += and + " eventId in ( :eventId )";
	            and = " and ";
	     }
		 
	     if (filter.getRemoteSchedulerPort() != null	&& !filter.getRemoteSchedulerPort().equals("")) {
			where += and + " remoteSchedulerPort = :remoteSchedulerPort";
			and = " and ";
		}

		if (filter.getRemoteSchedulerHost() != null	&& !filter.getRemoteSchedulerHost().equals("")) {
			where += and + " remoteSchedulerHost = :remoteSchedulerHost";
			and = " and ";
		}

		if (filter.getSchedulerId() != null	&& !filter.getSchedulerId().equals("")) {
			where += and + " schedulerId = :schedulerId";
			and = " and ";
		}

		if (filter.getJobChain() != null	&& !filter.getJobChain().equals("")) {
			where += and + " jobChain = :jobChain";
			and = " and ";
		}

		if (filter.getJobName() != null	&& !filter.getJobName().equals("")) {
			where += and + " jobName = :jobName";
			and = " and ";
		}
		
		if (filter.getOrderId() != null	&& !filter.getOrderId().equals("")) {
			where += and + " orderId = :orderId";
			and = " and ";
		}
		
		if (filter.getEventId() != null	&& !filter.getEventId().equals("")) {
			where += and + " eventId = :eventId";
			and = " and ";
		}
		
		if (filter.getEventClass() != null	&& !filter.getEventClass().equals("")) {
			where += and + " eventClass = :eventClass";
			and = " and ";
		}
		
		if (filter.getExitCode() != null	&& !filter.getExitCode().equals("")) {
			where += and + " exitCode = :exitCode";
			and = " and ";
		}
		
		if (where.trim().equals("")) {

		} else {
			where = "where " + where;
		}
		return where;

	}

	public List<SchedulerEventDBItem> getScheduleEventList(final int limit) {
		initSession();

		String hql = "from SchedulerEventDBItem " + getWhere()	+ filter.getOrderCriteria() + filter.getSortMode();
		Query query = setQueryParams(hql);
		
		if (limit > 0) {
			query.setMaxResults(limit);
		}

		@SuppressWarnings("unchecked")
		List<SchedulerEventDBItem> scheduleEventList = query.list();
		return scheduleEventList;
	}

	public boolean checkEventExists() {
		List<SchedulerEventDBItem> events = getScheduleEventList(1);
		return (events.size() > 0);
	}
	
    public boolean checkEventExists(SchedulerEventDBItem event) {
        resetFilter();
        filter.setEventClass(event.getEventClass());
        filter.setEventId(event.getEventId());
        List<SchedulerEventDBItem> eventList = getScheduleEventList(1);;
        return eventList.size() > 0;
    }

    public boolean checkEventExists(SchedulerEventFilter filter) {
        resetFilter();
        this.filter = filter;
        List<SchedulerEventDBItem> eventList = getScheduleEventList(1);;
        return eventList.size() > 0;
    }

    public boolean checkEventExists(String condition) {
        resetFilter();
        List<SchedulerEventDBItem> listOfActiveEvents = getEventsFromDb();
        boolean result = false;
       
        Iterator <SchedulerEventDBItem> iExit = listOfActiveEvents.iterator();

        BooleanExp exp = new BooleanExp(condition);
        while (iExit.hasNext()) {
            SchedulerEventDBItem e =  iExit.next();
            exp.replace(e.getEventName()+":"+e.getExitCode(), "true");
            exp.replace(e.getEventId()+":"+e.getExitCode(), "true");
            logger.debug(exp.getBoolExp());
        }
        
        Iterator <SchedulerEventDBItem> iClass = listOfActiveEvents.iterator();
        while (iClass.hasNext()) {
            SchedulerEventDBItem e =  iClass.next();
            exp.replace(e.getEventName(), "true");
            logger.debug(exp.getBoolExp());
        }
         
        //Alles, was noch nicht ersetzt wurde, mit evt. vorhandenen Event IDs ersetzen.
        Iterator <SchedulerEventDBItem> iEventId = listOfActiveEvents.iterator();
        while (iEventId.hasNext()) {
            SchedulerEventDBItem e =  iEventId.next();
            exp.replace(e.getEventId(), "true");
            logger.debug(exp.getBoolExp());
        }
        
        logger.debug("--------->" + exp.getBoolExp());
        result = exp.evaluateExpression();
        return result;
    }


    public boolean checkEventExists(String condition, String eventClass) {
        resetFilter();
        filter.setEventClass(eventClass);
        logger.debug("eventClass:" + eventClass);
        boolean result = false;

        List<SchedulerEventDBItem> listOfActiveEvents = getEventsFromDb();
        Iterator <SchedulerEventDBItem> iExit = listOfActiveEvents.iterator();

        BooleanExp exp = new BooleanExp(condition);
        while (iExit.hasNext()) {
            SchedulerEventDBItem e =  iExit.next();
            exp.replace(e.getEventId()+":"+e.getExitCode(), "true");
        }
        
        Iterator <SchedulerEventDBItem> iEventId = listOfActiveEvents.iterator();
        while (iEventId.hasNext()) {
            SchedulerEventDBItem e =  iEventId.next();
            exp.replace(e.getEventId(), "true");
        }
        
        result = exp.evaluateExpression();
        return result;
    }

    
	public List<SchedulerEventDBItem>  getEventsFromDb() {
       if (!getSession().isOpen()) initSession();
       String getWhere = getWhere();
       Query query = setQueryParams("from SchedulerEventDBItem  " + getWhere);
       logger.debug("where:" + getWhere);
       @SuppressWarnings("unchecked")
       List<SchedulerEventDBItem> resultList = query.list();
       return resultList;
	}
	
	/**
	 * Checks the events in the eventList if present and returns a list of all events still missing.
	 * @param eventList
	 * @return
	 */
	public SchedulerEventsCollection getMissingEvents(List <SchedulerEventDBItem> eventList) {
	    resetFilter();

        SchedulerEventsCollection missingEvents = new SchedulerEventsCollection();
		filter.setEventList(eventList);
		if(filter.getEventList().isEmpty()) {
			logger.info("no events to check - eventList is empty.");
		} else {
	    	 
			List<SchedulerEventDBItem> resultList = getEventsFromDb();
			missingEvents.addAll(eventList);
			for(SchedulerEventDBItem item : resultList) {
				missingEvents.remove(item.getEventId());
			}
			logger.info("missing events " + missingEvents.toString());
		}
		return missingEvents;
	} 
	
	
	
    public void createEvent(SchedulerEventDBItem event) {
        if(!checkEventExists(event)) {
            DateTime now = new DateTime();
            DateTime expired = now.plusDays(60);
            beginTransaction();
            event.setCreated( new DateTime() );
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
