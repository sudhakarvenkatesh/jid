package com.sos.dailyschedule;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.Session;

import com.sos.dailyschedule.classes.SosDailyScheduleTableItem;
import com.sos.dailyschedule.db.DailyScheduleDBItem;
import com.sos.dailyschedule.db.DailyScheduleDBLayer;
import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSSearchFilterData;
import com.sos.hibernate.interfaces.ISOSDashboardDataProvider;
import com.sos.hibernate.interfaces.ISOSHibernateDataProvider;
import com.sos.hibernate.interfaces.ISOSHibernateFilter;
import com.sos.scheduler.history.SchedulerOrderHistoryDataProvider;
import com.sos.scheduler.history.SchedulerTaskHistoryDataProvider;

/**
 * \class DailyScheduleDataProvider
 * 
 * \brief DailyScheduleDataProvider -
 * 
 * \details
 * 
 * 
 * \code 
 *      .... code goes here ... 
 * \endcode
 * 
 * <p style="text-align:center">
 * <br />
 * --------------------------------------------------------------------------- <br />
 * APL/Software GmbH - Berlin <br />
 * ##### generated by ClaviusXPress (http://www.sos-berlin.com) ######### <br />
 * ---------------------------------------------------------------------------
 * </p>
 * \author Uwe Risse \version 19.01.2012 \see reference
 * 
 * Created on 19.01.2012 09:31:01
 */
public class DailyScheduleDataProvider implements ISOSHibernateDataProvider, ISOSDashboardDataProvider {
	@SuppressWarnings("unused")
	private final String				conClassName				= "DailyScheduleDataProvider";
    private List<DailyScheduleDBItem>   listOfDaysScheduleDBItems   = null;
// private List<DailyScheduleDBItem>   listOfDaysScheduleDBItemsDistinct = null;
	private DailyScheduleDBLayer		dailySchedulerDBLayer		= null;
	private static Logger				logger						= Logger.getLogger(DailyScheduleDataProvider.class);
	private Table						tableDailySchedule			= null;

	public DailyScheduleDataProvider(File configurationFile) {
		this.dailySchedulerDBLayer = new DailyScheduleDBLayer(configurationFile);
	}

	public DailyScheduleFilter getFilter() {
		return dailySchedulerDBLayer.getFilter();
	}

	public void resetFilter() {
		dailySchedulerDBLayer.resetFilter();
	}

	public void getData(int limit) {
           listOfDaysScheduleDBItems = dailySchedulerDBLayer.getDailyScheduleList(limit);
 	}

	public void fillSchedulerIds(CCombo cbSchedulerId) {
		if (listOfDaysScheduleDBItems != null) {
            //Es ist schneller, die vorhandenen S�tze zu verwenden.
//	        listOfDaysScheduleDBItems = dailySchedulerDBLayer.getDailyScheduleSchedulerList(0);
			Iterator <DailyScheduleDBItem> dailyScheduleEntries = listOfDaysScheduleDBItems.iterator();
			while (dailyScheduleEntries.hasNext()) {
				DailyScheduleDBItem h = (DailyScheduleDBItem) dailyScheduleEntries.next();
				if (cbSchedulerId.indexOf(h.getSchedulerId()) < 0) {
					logger.debug("... cbSchedulerId --> : " + h.getSchedulerId());
					cbSchedulerId.add(h.getSchedulerId());
				}
			}
		}
	}

	public String getLogAsString(Table tableDailySchedule, SchedulerTaskHistoryDataProvider schedulerTaskHistoryDataProvider,
			SchedulerOrderHistoryDataProvider schedulerOrderHistoryDataProvider) {
		String log = "";
		if (tableDailySchedule.getSelectionIndex() >= 0) {
			TableItem t = tableDailySchedule.getItem(tableDailySchedule.getSelectionIndex());
			DailyScheduleDBItem h = (DailyScheduleDBItem) t.getData();
			if (h.isStandalone()) {
				if (h.getSchedulerHistoryId() != null) {
					log = schedulerTaskHistoryDataProvider.getLogAsString(h.getSchedulerHistoryId());
				}
			}
			else {
				if (h.getSchedulerOrderHistoryId() != null) {
					log = schedulerOrderHistoryDataProvider.getLogAsString(h.getSchedulerOrderHistoryId());
				}
			}
		}
		return log;
	}

	public void fillTable(Table table) {
		this.tableDailySchedule = table;
		Iterator<DailyScheduleDBItem> dailyScheduleEntries = listOfDaysScheduleDBItems.iterator();
		while (dailyScheduleEntries.hasNext()) {
			DbItem h = dailyScheduleEntries.next();
			if (dailySchedulerDBLayer.getFilter().isFiltered(h)) {
			}
			else {
				final SosDailyScheduleTableItem newItemTableItem = new SosDailyScheduleTableItem(table, SWT.BORDER);
				newItemTableItem.setDBItem(h);
				newItemTableItem.setData(h);
				newItemTableItem.setColor();
				newItemTableItem.setColumns();
			}
		}
	}

	public File getConfigurationFile() {
		return dailySchedulerDBLayer.getConfigurationFile();
	}

	@Override
	public void beginTransaction() {
		dailySchedulerDBLayer.beginTransaction();
	}

	public void save(DailyScheduleDBItem dbItem) {
		dailySchedulerDBLayer.save(dbItem);
	}

	@Override
	public void update(DbItem dbItem) {
		dailySchedulerDBLayer.update(dbItem);
	}

	public Session getSession() {
		return dailySchedulerDBLayer.getSession();
	}

	@Override
	public void commit() {
		dailySchedulerDBLayer.commit();
	}

	@Override
	public void setSchedulerId(String schedulerId) {
		this.getFilter().setSchedulerId(schedulerId);
	}

	@Override
	public void setFrom(Date d) {
		this.getFilter().setPlannedFrom(d);
	}

	@Override
	public void setTo(Date d) {
		this.getFilter().setPlannedTo(d);
	}

	@Override
	public void setSearchField(SOSSearchFilterData s) {
		this.getFilter().setSosSearchFilterData(s);
	}

	@Override
	public void setShowJobs(boolean b) {
		this.getFilter().setShowJobs(b);
	}

	@Override
	public void setShowJobChains(boolean b) {
		this.getFilter().setShowJobChains(b);
	}

	@Override
	public void setIgnoreList(Preferences prefs) {}

	@Override
	public void addToIgnorelist(Preferences prefs, DbItem h) {}

	@Override
	public void disableIgnoreList(Preferences prefs) {}

	@Override
	public void resetIgnoreList(Preferences prefs) {}

	@Override
	public void setLate(boolean b) {
		this.getFilter().setLate(b);
	}
    
	
	@Override
	public void setStatus(String status) {
		this.getFilter().setStatus(status);
	}

    @Override
    public void setShowWithError(boolean b) { }

	@Override
	public void setShowRunning(boolean b) {}

     

    
}