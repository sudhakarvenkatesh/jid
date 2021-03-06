package com.sos.dailyschedule.classes;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import com.sos.dailyschedule.db.DailyScheduleDBItem;
import com.sos.dialog.classes.SOSTableItem;
import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.interfaces.ISOSTableItem;

public class SosDailyScheduleTableItem extends SOSTableItem implements ISOSTableItem {

    private static final int ERROR_COLUMN_NUMBER = 7;
    private static final int STATUS_COLUMN_NUMBER = 8;
    private DailyScheduleDBItem dailyScheduleDBItem = null;
    private String[] textBuffer = null;

    private static Logger logger = Logger.getLogger(SosDailyScheduleTableItem.class);

    public SosDailyScheduleTableItem(final Table arg0, final int arg1) {
        super(arg0, arg1);
    }

    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public DbItem getData() {
        return (DailyScheduleDBItem) super.getData();
    }

    public void setDBItem(final DbItem d) {
        dailyScheduleDBItem = (DailyScheduleDBItem) d;
        this.setData(d);
    }

    public void setColor() {
        org.eclipse.swt.graphics.Color magenta = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
        org.eclipse.swt.graphics.Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED);
        org.eclipse.swt.graphics.Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
        org.eclipse.swt.graphics.Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
        org.eclipse.swt.graphics.Color gray = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        org.eclipse.swt.graphics.Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
        org.eclipse.swt.graphics.Color yellow = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);

        if (dailyScheduleDBItem.getExecutionState().singleStart()) {
            this.setForeground(black);  // SingleStart
        } else {
            this.setForeground(blue);   // Periodische Ausf�hrung
        }

        if (dailyScheduleDBItem.getScheduleExecuted() == null) {  // Keine
                                                                 // Ausf�hrung
            if (dailyScheduleDBItem.getExecutionState().isLate()) { // H�tte
                                                                    // ausgef�hrt
                                                                    // werden
                                                                    // m�ssen
                this.setBackground(0, magenta);
                this.setForeground(0, white);
                this.setBackground(STATUS_COLUMN_NUMBER, magenta);
                this.setForeground(STATUS_COLUMN_NUMBER, white);
            } else {
                this.setBackground(0, gray);  // Ausf�hrung in der Zukunft
                this.setBackground(STATUS_COLUMN_NUMBER, gray);  // Ausf�hrung in
                                                                // der Zukunft
            }
        } else {
            this.setBackground(white);
            if (dailyScheduleDBItem.haveError()) { // Fehlerhafte Ausf�hrung
                this.setBackground(0, red);
                this.setForeground(0, white);
                this.setBackground(ERROR_COLUMN_NUMBER, red);
                this.setForeground(ERROR_COLUMN_NUMBER, white);
            } else {
                if (dailyScheduleDBItem.getExecutionState().isLate()) { // Versp�tete
                                                                        // Ausf�hrung
                    this.setBackground(0, yellow);
                    this.setBackground(STATUS_COLUMN_NUMBER, yellow);
                }
            }
        }
        colorSave();

    }

    public void setColumns() {
        DailyScheduleDBItem d = dailyScheduleDBItem;

        logger.debug("...creating tableItem: " + d.getName() + ":" + getParent().getItemCount());

        textBuffer = new String[] { "", d.getSchedulerId(), d.getJobOrJobchain(),

        d.getSchedulePlannedFormated(), d.getScheduleExecutedFormated(), d.getScheduleEndedFormated(), d.getDurationFormated(),
                String.valueOf(d.getResultValue()), d.getExecutionState().getExecutionState(), d.getExecutionState().getLate() };

        this.setText(textBuffer);
    }

    public String[] getTextBuffer() {
        return textBuffer;
    }

    @SuppressWarnings("unused")
    private final String conClassName = "SosDailyScheduleTableItem";

    @Override
    public Color[] getBackgroundColumn() {
        return colorsBackground;
    }

    @Override
    public Color[] getForegroundColumn() {
        return colorsForeground;
    }

    @Override
    public Color getBackground() {
        return null;
    }

    @Override
    public Color getForeground() {
        return null;
    }

    @Override
    public void setForeground(final Color c) {
    }

    @Override
    public boolean isDisposed() {
        // TODO Auto-generated method stub
        return false;
    }

}
