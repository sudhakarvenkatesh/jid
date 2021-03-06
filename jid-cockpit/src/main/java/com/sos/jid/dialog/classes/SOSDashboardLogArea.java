package com.sos.jid.dialog.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import sos.scheduler.editor.app.SchedulerEditorFontDialog;

import com.sos.dashboard.globals.DashBoardConstants;
import com.sos.dialog.classes.SOSPrinter;
import com.sos.dialog.components.SOSSearchFilter;
import com.sos.hibernate.classes.SOSSearchFilterData;
import com.sos.localization.Messages;

public class SOSDashboardLogArea extends StyledText {

    private static final String EMPTY_STRING = "";
    private static final String JID_LOG = "jid_log";
    private static final String DEBUG_MARKER = "[debug]";
    private static final String DEBUG2_MARKER = "[debug2]";
    private static final String DEBUG3_MARKER = "[debug3]";
    private static final String DEBUG4_MARKER = "[debug4]";
    private static final String DEBUG5_MARKER = "[debug5]";
    private static final String DEBUG6_MARKER = "[debug6]";
    private static final String DEBUG7_MARKER = "[debug7]";
    private static final String DEBUG8_MARKER = "[debug8]";
    private static final String DEBUG9_MARKER = "[debug9]";
    private static final String WARN_MARKER = "[WARN]";
    private static final String ERROR_MARKER = "[ERROR]";
    private static final Logger LOGGER = Logger.getLogger(SOSDashboardLogArea.class);
    private Composite composite;
    boolean flgInit = false;
    private String logContent;
    private boolean filtered;
    private MenuItem itemFilter;
    private MenuItem itemSearch;
    private Messages messages;
    private SOSSearchFilterData sosSearchFilterData;
    int lenghtOfLinebreak = 1;

    public SOSDashboardLogArea(Composite composite_, int arg1, Messages messages_) {
        super(composite_, arg1);
        this.messages = messages_;
        composite = composite_;
        createContextMenue();
        final GridData gridData_1 = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1);
        gridData_1.minimumHeight = 40;
        gridData_1.widthHint = 454;
        gridData_1.heightHint = 139;
        setLayoutData(gridData_1);
        SchedulerEditorFontDialog objFontDialog = new SchedulerEditorFontDialog(getFont().getFontData()[0], getForeground().getRGB());
        objFontDialog.setContext(JID_LOG);
        objFontDialog.readFontData();
        setFont(objFontDialog.getFontData(), objFontDialog.getForeGround());
    }

    public StyledText getControl() {
        return this;
    }

    private void colorLine(StringBuffer line, String s, int iColor, Color color, int actPos, int actLength, int ftType) {
        int pos = line.indexOf(s);
        if (pos > 0) {
            StyleRange styleRange = new StyleRange();
            styleRange.start = actPos;
            if (iColor != 0) {
                styleRange.foreground = composite.getShell().getDisplay().getSystemColor(iColor);
            } else {
                styleRange.foreground = color;
            }
            styleRange.length = actLength;
            styleRange.fontStyle = ftType;
            setStyleRange(styleRange);
        }
    }

    private void addStyles() {
        Color cWarn = new Color(composite.getDisplay(), 255, 99, 71);
        Color cDebug2 = new Color(composite.getDisplay(), 64, 128, 64);
        Color cDebug3 = new Color(composite.getDisplay(), 128, 128, 128);
        Color cDebug4 = new Color(composite.getDisplay(), 128, 128, 255);
        Color cDebug5 = new Color(composite.getDisplay(), 128, 128, 255);
        Color cDebug6 = new Color(composite.getDisplay(), 128, 128, 255);
        Color cDebug7 = new Color(composite.getDisplay(), 128, 128, 255);
        Color cDebug8 = new Color(composite.getDisplay(), 128, 128, 255);
        Color cDebug9 = new Color(composite.getDisplay(), 160, 160, 160);
        int actPos = 0;
        for (int i = 0; i < this.getLineCount() - 1; i++) {
            StringBuffer line = new StringBuffer(this.getLine(i));
            int actLength = line.length();
            colorLine(line, ERROR_MARKER, SWT.COLOR_RED, null, actPos, actLength, SWT.BOLD);
            colorLine(line, WARN_MARKER, 0, cWarn, actPos, actLength, SWT.BOLD);
            colorLine(line, DEBUG_MARKER, SWT.COLOR_DARK_GREEN, null, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG2_MARKER, 0, cDebug2, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG3_MARKER, 0, cDebug3, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG4_MARKER, 0, cDebug4, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG5_MARKER, 0, cDebug5, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG6_MARKER, 0, cDebug6, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG7_MARKER, 0, cDebug7, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG8_MARKER, 0, cDebug8, actPos, actLength, SWT.NORMAL);
            colorLine(line, DEBUG9_MARKER, 0, cDebug9, actPos, actLength, SWT.NORMAL);
            actPos = actPos + actLength + lenghtOfLinebreak;
        }
    }

    private void searchInLog() {
        if (sosSearchFilterData != null && sosSearchFilterData.getSearchfield() != null
                && !sosSearchFilterData.getSearchfield().trim().equals(EMPTY_STRING)) {
            boolean first = true;
            this.unmark();
            String s = sosSearchFilterData.getSearchfield();
            if (sosSearchFilterData.isWildcardExpression()) {
                s = s.replaceAll("([^a-zA-Z0-9*\\ ])", "\\\\$1");
                s = s.replaceAll("\\*", ".*?");
            }
            Pattern p = Pattern.compile(s);
            Matcher m = p.matcher(this.getText());
            while (m.find()) {
                this.mark(m.start(), m.end());
                if (first) {
                    this.setSelection(m.start(), m.start());
                    first = false;
                }
            }
        }
    }

    public void setText(String logContent) {
        this.logContent = logContent;
        super.setText(logContent);
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < this.getLineCount(); i++) {
            String s = this.getLine(i);
            if (filtered && sosSearchFilterData != null && !sosSearchFilterData.getSearchfield().trim().equals(EMPTY_STRING)) {
                Pattern p = Pattern.compile(sosSearchFilterData.getSearchfield());
                Matcher m = p.matcher(s);
                if (m.find()) {
                    line.append(s);
                    line.append("\n");
                }
            } else {
                line.append(s);
                line.append("\n");
            }
        }
        super.setText(line.toString());
        addStyles();
        searchInLog();
    }

    public void unmark() {
        StyleRange styleRanges[] = new StyleRange[0];
        setStyleRanges(styleRanges);
    }

    public void mark(int start, int end) {
        StyleRange styleRange = new StyleRange();
        styleRange.start = start;
        styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
        styleRange.background = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
        styleRange.length = end - start;
        styleRange.fontStyle = SWT.NORMAL;
        setStyleRange(styleRange);
    }

    private void createContextMenue() {
        Menu objContextMenu = getMenu();
        if (objContextMenu == null) {
            objContextMenu = new Menu(this.getControl());
        }
        MenuItem itemCopy = new MenuItem(objContextMenu, SWT.PUSH);
        itemCopy.addListener(SWT.Selection, getCopyListener());
        itemCopy.setText(messages.getLabel(DashBoardConstants.conSOSDashB_Copy));
        MenuItem itemSelectAll = new MenuItem(objContextMenu, SWT.PUSH);
        itemSelectAll.addListener(SWT.Selection, getSelectAllListener());
        itemSelectAll.setText(messages.getLabel(DashBoardConstants.conSOSDashB_SelectAll));
        MenuItem itemSelectFont = new MenuItem(objContextMenu, SWT.PUSH);
        itemSelectFont.addListener(SWT.Selection, getSelectFontListener());
        itemSelectFont.setText(messages.getLabel(DashBoardConstants.conSOSDashB_SelectFont));
        new MenuItem(objContextMenu, SWT.SEPARATOR);
        itemSearch = new MenuItem(objContextMenu, SWT.PUSH);
        itemSearch.addListener(SWT.Selection, getSearchListener());
        itemSearch.setText(messages.getLabel(DashBoardConstants.conSOSDashB_Search));
        itemFilter = new MenuItem(objContextMenu, SWT.CHECK);
        itemFilter.addListener(SWT.Selection, getFilterListener());
        itemFilter.setText(messages.getLabel(DashBoardConstants.conSOSDashB_Filter));
        new MenuItem(objContextMenu, SWT.SEPARATOR);
        MenuItem itemSaveAsFile = new MenuItem(objContextMenu, SWT.PUSH);
        itemSaveAsFile.addListener(SWT.Selection, getSaveAsFileListener());
        itemSaveAsFile.setText(messages.getLabel(DashBoardConstants.conSOSDashB_SaveAsFile));
        MenuItem itemPrint = new MenuItem(objContextMenu, SWT.PUSH);
        itemPrint.addListener(SWT.Selection, getPrintListener());
        itemPrint.setText(messages.getLabel(DashBoardConstants.conSOSDashB_Print));
        this.setMenu(objContextMenu);
    }

    private Listener getSelectFontListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                changeFont();
            }
        };
    }

    private Listener getSaveAsFileListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                try {
                    saveFileAs();
                } catch (IOException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
        };
    }

    private Listener getPrintListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                try {
                    print();
                } catch (Exception e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
        };
    }

    private Listener getFilterListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                setFiltered(itemFilter.getSelection());
            }
        };
    }

    private Listener getSearchListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                SOSSearchFilter sosSearchFilter = new SOSSearchFilter(composite.getShell());
                sosSearchFilter.setEnableFilterCheckbox(true);
                sosSearchFilter.execute(EMPTY_STRING);
                if (sosSearchFilter.getSosSearchFilterData() != null
                        && !sosSearchFilter.getSosSearchFilterData().getSearchfield().equals(EMPTY_STRING)) {
                    try {
                        setSOSSearchFilterData(sosSearchFilter.getSosSearchFilterData());
                    } catch (Exception ee) {
                        LOGGER.error(ee.getMessage(), ee);
                    }
                }
            }
        };
    }

    private Listener getCopyListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                _copy();
            }
        };
    }

    private Listener getSelectAllListener() {
        return new Listener() {

            public void handleEvent(Event e) {
                _selectAll();
            }
        };
    }

    private void _copy() {
        this.copy();
    }

    private void _selectAll() {
        this.selectAll();
    }

    private void setFont(FontData f, RGB foreGround) {
        setFont(new Font(this.getDisplay(), f));
        setForeground(new Color(this.getDisplay(), foreGround));
    }

    private void changeFont() {
        SchedulerEditorFontDialog fd = new SchedulerEditorFontDialog(getFont().getFontData()[0], getForeground().getRGB());
        fd.setContext(JID_LOG);
        fd.setParent(getShell());
        fd.show(getDisplay());
        setFont(fd.getFontData(), fd.getForeGround());
    }

    public void saveFileAs() throws IOException {
        FileDialog dlg = new FileDialog(this.getShell(), SWT.SAVE);
        String filename = dlg.open();
        if (filename != null) {
            File outputFile = new File(filename);
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(this.getText().getBytes());
            out.close();
        }
    }

    public void print() {
        SOSPrinter p = new SOSPrinter(this.getShell());
        p.setText(this.getText());
        p.setFont(this.getFont());
        p.setOrientation(PrinterData.LANDSCAPE);
        try {
            p.print();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void setFiltered(boolean filtered) {
        this.filtered = filtered;
        setText(this.logContent);
    }

    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setSOSSearchFilterData(SOSSearchFilterData sosSearchFilterData_) {
        this.sosSearchFilterData = sosSearchFilterData_;
        this.setText(this.logContent);
        setFiltered(sosSearchFilterData.isFiltered());
        itemFilter.setSelection(sosSearchFilterData.isFiltered());
        if (!sosSearchFilterData_.getSearchfield().trim().equals(EMPTY_STRING)) {
            this.searchInLog();
        }
    }

}