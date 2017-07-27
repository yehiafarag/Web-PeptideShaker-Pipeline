package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.Resource;
import com.vaadin.ui.Table;

/**
 * This class include information required to initialize table headers
 *
 * @author Yehia Farag
 */
public class TableColumnHeader {

    private  final Object propertyId;
    private  final Class<?> type;
    private  final Object defaultValue;
    private  final String columnHeader;    
    private  final  Resource columnIcon;
   private final Table.Align columnAlignment;

    public TableColumnHeader(Object propertyId, Class<?> type, Object defaultValue, String columnHeader, Resource columnIcon, Table.Align columnAlignment) {
        this.propertyId = propertyId;
        this.type = type;
        this.defaultValue = defaultValue;
        this.columnHeader = columnHeader;
        this.columnIcon = columnIcon;
        this.columnAlignment = columnAlignment;
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getColumnHeader() {
        return columnHeader;
    }

    public Resource getColumnIcon() {
        return columnIcon;
    }

    public Table.Align getColumnAlignment() {
        return columnAlignment;
    }

}
