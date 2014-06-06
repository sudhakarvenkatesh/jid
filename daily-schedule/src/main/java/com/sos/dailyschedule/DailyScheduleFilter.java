package com.sos.dailyschedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import com.sos.dailyschedule.db.DailyScheduleDBItem;
import com.sos.dashboard.globals.DashBoardConstants;
import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateIntervalFilter;
import com.sos.hibernate.classes.SOSSearchFilterData;
import com.sos.hibernate.classes.UtcTimeHelper;
import com.sos.hibernate.interfaces.ISOSHibernateFilter;

/**
* \class DailyScheduleFilter 
* 
* \brief DailyScheduleFilter - 
* 
* \details
*
* \section DailyScheduleFilter.java_intro_sec Introduction
*
* \section DailyScheduleFilter.java_samples Some Samples
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author Uwe Risse
* \version 14.12.2011
* \see reference
*
* Created on 14.12.2011 13:53:37
 */
public class DailyScheduleFilter extends SOSHibernateIntervalFilter implements ISOSHibernateFilter {
	@SuppressWarnings("unused")
	private final String	conClassName	= "DailyScheduleFilter";
	private Date			plannedFrom;
	private Date			executedFrom;
	private Date			plannedTo;
	private Date			executedTo;
	private String			plannedFromIso;
	private String			plannedToIso;
	private boolean         showJobs        = true;
	private boolean			showJobChains	= true;
	private boolean			late			= false;
	private String			status			= "";
	private String			schedulerId		= "";
	private SOSSearchFilterData	sosSearchFilterData;
 
	public DailyScheduleFilter() {
		super(DashBoardConstants.conPropertiesFileName);
        sosSearchFilterData = new SOSSearchFilterData();
	}

	public Date getPlannedUtcFrom() {
	    if (plannedFrom == null) {
	        return null;
	    }else {
		    return UtcTimeHelper.convertTimeZonesToDate(UtcTimeHelper.localTimeZoneString(), "UTC", new DateTime(plannedFrom));
	    }
	}

	public void setPlannedFrom(Date plannedFrom) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String d = formatter.format(plannedFrom);
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.plannedFrom = formatter.parse(d);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPlannedFrom(String plannedFrom) throws ParseException {
		if (plannedFrom.equals("")) {
			this.plannedFrom = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			Date d = formatter.parse(plannedFrom);
			setPlannedFrom(d);
		}
	}

	public void setPlannedFrom(String plannedFrom, String dateFormat) throws ParseException {
		this.dateFormat = dateFormat;
		setPlannedFrom(plannedFrom);
	}

	public void setPlannedTo(String plannedTo, String dateFormat) throws ParseException {
		this.dateFormat = dateFormat;
		setPlannedTo(plannedTo);
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Date getExecutedUtcFrom() {
	    if (executedFrom == null) {
	        return null;
	    }else {
		    return UtcTimeHelper.convertTimeZonesToDate(UtcTimeHelper.localTimeZoneString(), "UTC", new DateTime(executedFrom));
	    }
	}

	public void setExecutedFrom(Date executedFrom) {
		this.executedFrom = executedFrom;
	}

	public void setExecutedFrom(String executedFrom) throws ParseException {
		if (executedFrom.equals("")) {
			this.executedFrom = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			Date d = formatter.parse(executedFrom);
			setExecutedFrom(d);
		}
	}

	public Date getPlannedUtcTo() {
	    if (plannedTo == null) {
	        return null;
	    }else {
		    return UtcTimeHelper.convertTimeZonesToDate(UtcTimeHelper.localTimeZoneString(), "UTC", new DateTime(plannedTo));
	    }
	} 

	public void setPlannedTo(Date plannedTo) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String d = formatter.format(plannedTo);
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.plannedTo = formatter.parse(d);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setPlannedTo(String plannedTo) throws ParseException {
		if (plannedTo.equals("")) {
			this.plannedTo = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			Date d = formatter.parse(plannedTo);
			setPlannedTo(d);
		}
	}

	public Date getExecutedTo() {
		return UtcTimeHelper.convertTimeZonesToDate(UtcTimeHelper.localTimeZoneString(), "UTC", new DateTime(executedTo));
	}

	public void setExecutedTo(Date executedTo) {
		this.executedTo = executedTo;
	}

	public void setExecutedTo(String executedTo) throws ParseException {
		if (executedTo.equals("")) {
			this.executedTo = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			Date d = formatter.parse(executedTo);
			setExecutedTo(d);
		}
	}

	public boolean isFiltered(DbItem dbitem) {
		DailyScheduleDBItem h = (DailyScheduleDBItem) dbitem;
		boolean show = this.isShowJobChains() && this.isShowJobs() ||
		               this.isShowJobChains() && h.getJobChain() != null ||
		               this.isShowJobs() && h.getJob() != null;
		return (!show ||
                this.isLate() && !h.getExecutionState().isLate()  || 
                !this.getStatus().equals("") && !this.getStatus().equalsIgnoreCase(h.getExecutionState().getExecutionState()) || 
                this.getSosSearchFilterData() != null && this.getSosSearchFilterData().getSearchfield()  != null && 
                !this.getSosSearchFilterData().getSearchfield().equals("") && 
                ((h.getJobName() != null && !h.getJobName().toLowerCase().contains(this.getSosSearchFilterData().getSearchfield().toLowerCase())) || 
                 (h.getJobChain() != null && h.getOrderId() != null && !(h.getJobChain().toLowerCase() + "~*~" + h.getOrderId()).toLowerCase().contains(this.getSosSearchFilterData().getSearchfield().toLowerCase())  
                 )));
	}

	 

	public boolean isShowJobs() {
		return showJobs;
	}

	public void setShowJobs(boolean jobs) {
		this.showJobs = jobs;
	}

	public boolean isShowJobChains() {
		return showJobChains;
	}

	public void setShowJobChains(boolean showJobChains) {
		this.showJobChains = showJobChains;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSchedulerId() {
		return schedulerId;
	}

	public void setSchedulerId(String schedulerId) {
		this.schedulerId = schedulerId;
	}

	@Override
	public String getTitle() {
		
	     
		String s = "";
		if (schedulerId != null && !schedulerId.equals("")) {
			   s += String.format("Id: %s ",schedulerId);
		}
		 
 
		 
		if (plannedFrom != null) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_FROM) + ": %s ", date2Iso(plannedFrom));
		}
		if (plannedTo != null) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_TO) + ": %s ", date2Iso(plannedTo));
		}
	/*	if (executedFrom != null) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_FROM) + ": %s ", date2Iso(executedFrom));
		}
		if (executedTo != null) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_TO) + ": %s ", date2Iso(executedTo));
		}
		*/
		if (showJobs) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_JOBS));
		}
		if (showJobChains) {
			s += String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_JOBCHAINS));
		}
		if (late) {
			s += " " + String.format(Messages.getLabel(DashBoardConstants.conSOSDashB_LATE));
		}
		
		String title = String.format("%1s %2s %3s", s, status, getSosSearchFilterData().getSearchfield());
		return title;
		 
	}

	@Override
	public void setIntervalFromDate(Date d) {
       this.plannedFrom = d;		
	}

	@Override
	public void setIntervalToDate(Date d) {
	       this.plannedTo = d;		
	}

	@Override
	public void setIntervalFromDateIso(String s) {
		this.plannedFromIso = s;
 	}

	@Override
	public void setIntervalToDateIso(String s) {
		this.plannedToIso = s;
	}

    public SOSSearchFilterData getSosSearchFilterData() {
        if (sosSearchFilterData == null) {
            sosSearchFilterData = new SOSSearchFilterData();
        }
        return sosSearchFilterData;
    }

    public void setSosSearchFilterData(SOSSearchFilterData sosSearchFilterData) {
        this.sosSearchFilterData = sosSearchFilterData;
    }
}