package com.uib.web.peptideshaker.presenter.core;

import com.uib.web.peptideshaker.presenter.core.form.HorizontalLabelTextField;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents table that support search and selection
 *
 * @author Yehia Farag
 */
public abstract class SearchableTable extends VerticalLayout implements Property.ValueChangeListener {

    private Label searchResultsLabel;
    private final String tableMainTitle;
    private final Map<Integer, String> tableSearchingResults;
    private boolean resetSearching = false;
    private final Map<String, String> tableSearchingMap;
    private final Table mainTable;

    public SearchableTable(String title, String defaultSearchingMessage, TableColumnHeader[] tableHeaders) {
        SearchableTable.this.setSizeFull();
        SearchableTable.this.setMargin(new MarginInfo(false, false, true, false));

        this.tableMainTitle = title;
        this.tableSearchingResults = new TreeMap<>();
        this.tableSearchingMap = new LinkedHashMap<>();

        HorizontalLayout serachComponent = initSearchComponentLayout(defaultSearchingMessage);
        SearchableTable.this.addComponent(serachComponent);
        SearchableTable.this.setExpandRatio(serachComponent, 0);
        SearchableTable.this.setComponentAlignment(serachComponent, Alignment.TOP_RIGHT);

        this.mainTable = initProteinTable(tableHeaders);
        SearchableTable.this.addComponent(mainTable);
        SearchableTable.this.setExpandRatio(mainTable, 100);

    }

    private HorizontalLayout initSearchComponentLayout(String defaultSearchingMessage) {
        HorizontalLayout searchContainer = new HorizontalLayout();
        searchContainer.setSpacing(true);
        HorizontalLabelTextField searchField = new HorizontalLabelTextField("Search", defaultSearchingMessage, null);
        searchContainer.setHeight(25, Unit.PIXELS);
        searchContainer.addComponent(searchField);

        final Button searchBtn = new Button(VaadinIcons.SEARCH);
        searchBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        searchBtn.setWidth(25, Unit.PIXELS);
        searchBtn.setHeight(25, Unit.PIXELS);
        searchContainer.addComponent(searchBtn);
        searchBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        final Button nextBtn = new Button(VaadinIcons.STEP_FORWARD) {
            @Override
            public void setEnabled(boolean enabled) {
                if (enabled) {
                    searchBtn.removeClickShortcut();
                    this.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                } else {
                    this.removeClickShortcut();
                    searchBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                }
                super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
            }

        };
        nextBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        nextBtn.setWidth(25, Unit.PIXELS);
        nextBtn.setHeight(25, Unit.PIXELS);
        searchContainer.addComponent(nextBtn);

        searchResultsLabel = new Label("0 of 0");
        searchContainer.addComponent(searchResultsLabel);
        searchContainer.setStyleName("searchtablecontainer");

        searchBtn.addClickListener((Button.ClickEvent event) -> {
            searchforKeyword(searchField.getSelectedValue().trim());
            nextBtn.setEnabled(!tableSearchingResults.isEmpty());
            resetSearching = true;
            nextBtn.click();
            nextBtn.setEnabled(tableSearchingResults.size() > 1);
        });

        nextBtn.addClickListener(new Button.ClickListener() {
            private Iterator<Integer> itr;

            @Override
            public void buttonClick(Button.ClickEvent event) {

                if (itr == null || !itr.hasNext() || resetSearching) {
                    itr = tableSearchingResults.keySet().iterator();
                }
                if (itr.hasNext()) {
                    resetSearching = false;
                    int key = itr.next();
                    searchResultsLabel.setValue(key + " of " + tableSearchingResults.size());
                    Object itemId = tableSearchingMap.get(tableSearchingResults.get(key));
                    mainTable.setValue(itemId);
                    mainTable.setCurrentPageFirstItemId(itemId);
                }
            }
        });
        searchField.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            String txt = event.getText().trim();
            searchforKeyword(txt);
            nextBtn.setEnabled(!tableSearchingResults.isEmpty());
            resetSearching = true;
            nextBtn.click();
            nextBtn.setEnabled(tableSearchingResults.size() > 1);
        });
        return searchContainer;
    }

    private void searchforKeyword(String keyWord) {
        if (keyWord == null || keyWord.equalsIgnoreCase("")) {
            tableSearchingResults.clear();
            searchResultsLabel.setValue(0 + " of " + 0);
            mainTable.setValue(null);
            return;

        }
        keyWord = keyWord.toLowerCase();
        int index = 1;
        tableSearchingResults.clear();
        for (String key : tableSearchingMap.keySet()) {
            if (key.contains(keyWord)) {
                tableSearchingResults.put(index++, key);
            }
        }
        if (tableSearchingResults.isEmpty()) {
            Notification.show("<i>No results</i>", Notification.Type.TRAY_NOTIFICATION);
        }

    }

    /**
     * Initialise the proteins table.
     */
    private Table initProteinTable(TableColumnHeader[] tableHeaders) {
        Table table = new Table() {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                Object v = property.getValue();
                if (v instanceof Double) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    return df.format(v);
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        table.setCaption("<b>" + tableMainTitle + "</b>");
        table.setCaptionAsHtml(true);
        table.setStyleName("framedpanel");
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setHeight(100, Unit.PERCENTAGE);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setCacheRate(1);
        table.setMultiSelect(false);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.setSelectable(true);
        table.setSortEnabled(false);
        table.setColumnReorderingAllowed(false);

        table.setColumnCollapsingAllowed(true);
        table.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            table.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });
        table.setImmediate(true);
        for (TableColumnHeader header : tableHeaders) {
            table.addContainerProperty(header.getPropertyId(), header.getType(), header.getDefaultValue(), header.getColumnHeader(), header.getColumnIcon(), header.getColumnAlignment());
        }
        table.addHeaderClickListener((Table.HeaderClickEvent event) -> {
            sortTable(event.getPropertyId());
        });

        return table;
    }
    public void sortTable(Object id){
      mainTable.setSortEnabled(true);
            if ((mainTable.getSortContainerPropertyId()!=null)&&id.toString().equalsIgnoreCase(mainTable.getSortContainerPropertyId().toString())) {
                mainTable.setSortAscending(!mainTable.isSortAscending());
            } else {
                mainTable.setSortAscending(true);
                mainTable.setSortContainerPropertyId(id);
            }
            mainTable.sort();
            int index = 1;
            for (Object key : mainTable.getItemIds()) {
                mainTable.getItem(key).getItemProperty("index").setValue(index++);
            }
            mainTable.setSortEnabled(false);    
    }
  

    private Map<Comparable, Object[]> tableData;

    public Map<Comparable, Object[]> getTableData() {
        return tableData;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public void upateTableData(Map<Comparable, Object[]> tableData, int[] searchingIndexes, int keyIndex) {
        this.tableData = tableData;
        this.tableSearchingMap.clear();
        mainTable.removeValueChangeListener(SearchableTable.this);
        mainTable.removeAllItems();
        tableData.values().stream().map((data) -> {
            this.mainTable.addItem(data, data[keyIndex]);
            return data;
        }).forEachOrdered((data) -> {
            String searchKey = "";
            for (int i : searchingIndexes) {
                searchKey += data[i] + "_";
            }
            this.tableSearchingMap.put(searchKey.toLowerCase().replace(",", "_"), data[keyIndex] + "");
        });
        mainTable.setCaption("<b>" + tableMainTitle + " ( " + mainTable.getItemIds().size() + " / " + tableData.size() + " )</b>");

        mainTable.addValueChangeListener(SearchableTable.this);
        if (mainTable.getItemIds().size() == 1) {
            mainTable.select(mainTable.getCurrentPageFirstItemId());
        } else {
            mainTable.select(null);
            itemSelected(null);
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Object objcetId = mainTable.getValue();//"P01889";
        itemSelected(objcetId);
//        proteinNodes.clear();
//        peptidesNodes.clear();
//        peptides.clear();
//        unrelatedProt.clear();
//        unrelatedPeptides.clear();
//        edges.clear();
//        if (objcetId == null) {
//            graphLayout.updateGraphData(null, null, null, null);
//            return;
//        }
//        ProteinObject protein = peptideShakerVisualizationDataset.getProtein(objcetId);
//        for (String acc : protein.getProteinGroupSet()) {
//            proteinNodes.put(acc, proteinTableData.get(acc));
//            peptides.addAll(peptideShakerVisualizationDataset.getPeptides(acc));
//        }
//
//        Set<String> tunrelatedProt = new LinkedHashSet<>();
//        for (PeptideObject peptide : peptides) {
//            peptidesNodes.put(peptide.getModifiedSequence(), peptide);
////            protein.addPeptideSequence(peptide.getModifiedSequence());
//            ArrayList<String> tEd = new ArrayList<>();
//            for (String acc : peptide.getProteinsSet()) {
//                tEd.add(acc);
//                if (!proteinNodes.containsKey(acc)) {
//                    tunrelatedProt.add(acc);
//                }
//            }
//            edges.put(peptide.getModifiedSequence(), tEd);
//
//        }
//        for (String unrelated : tunrelatedProt) {
//            fillUnrelatedProteinsAndPeptides(unrelated, proteinTableData.get(unrelated));
//        }
//        proteinNodes.putAll(unrelatedProt);
//        peptidesNodes.putAll(unrelatedPeptides);
//
//        Map<String, ProteinObject> tempProteinNodes = new LinkedMap<>(proteinNodes);
//        for (String accession : tempProteinNodes.keySet()) {
//            proteinNodes.replace(accession, peptideShakerVisualizationDataset.updateProteinInformation(tempProteinNodes.get(accession), accession));
//            proteinTableData.put(accession, proteinNodes.get(accession));
//        }
//
//        graphLayout.updateGraphData(protein, proteinNodes, peptidesNodes, edges);

    }

    public abstract void itemSelected(Object itemId);

    public void filterTable(Set<Comparable> objectIds) {
        if ((objectIds == null || objectIds.isEmpty()) && this.mainTable.getItemIds().size() == tableData.size()) {
            return;
        }
        if (objectIds != null && objectIds.size() == mainTable.getItemIds().size()) {
            return;
        }

        this.tableSearchingMap.clear();
        mainTable.removeValueChangeListener(SearchableTable.this);
        mainTable.removeAllItems();
        if (objectIds != null && !objectIds.isEmpty()) {
            for (Comparable data : objectIds) {
                if (tableData.containsKey(data)) {
                    this.mainTable.addItem(tableData.get(data), data);
                } else {
                    System.out.println("at data " + data + "   " + tableData.keySet().size());
                }
            }
        } else if ((objectIds == null || objectIds.isEmpty()) && this.mainTable.getItemIds().size() != tableData.size()) {
            for (Comparable data : tableData.keySet()) {
                this.mainTable.addItem(tableData.get(data), data);
            }

        }
        mainTable.setCaption("<b>" + tableMainTitle + " ( " + mainTable.getItemIds().size() + " / " + tableData.size() + " )</b>");
        mainTable.addValueChangeListener(SearchableTable.this);
        if (mainTable.getItemIds().size() == 1) {
            mainTable.select(mainTable.getCurrentPageFirstItemId());
        } else {
            mainTable.select(null);
            itemSelected(null);
        }

    }

}
