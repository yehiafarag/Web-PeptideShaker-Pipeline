package com.uib.web.peptideshaker.presenter.core;

import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.RegistrableFilter;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents table that support search and selection
 *
 * @author Yehia Farag
 */
public class SearchableTable extends VerticalLayout implements Property.ValueChangeListener, RegistrableFilter {

    private Label searchResultsLabel;
    private final String tableMainTitle;
    private final Map<Integer, String> tableSearchingResults;
    private boolean resetSearching = false;
    private final Map<String, String> tableSearchingMap;
    private final Table mainTable;
    private final String filterId;

    public SearchableTable(String title, String filterId, String defaultSearchingMessage, TableColumnHeader[] tableHeaders) {
        SearchableTable.this.setSizeFull();
        SearchableTable.this.setMargin(new MarginInfo(false, false, true, false));
        this.filterId = filterId;

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

    }

    /**
     * Initialize the proteins table.
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
        table.setSortEnabled(true);
        table.setColumnReorderingAllowed(false);

        table.setColumnCollapsingAllowed(false);
        table.addColumnResizeListener((Table.ColumnResizeEvent event) -> {
            table.setColumnWidth(event.getPropertyId(), event.getPreviousWidth());
        });
        table.setImmediate(true);
        for (TableColumnHeader header : tableHeaders) {
            table.addContainerProperty(header.getPropertyId(), header.getType(), header.getDefaultValue(), header.getColumnHeader(), header.getColumnIcon(), header.getColumnAlignment());
        }

        return table;
    }

    public void upateTableData(Object[][] tableData, int[] searchingIndexes, int keyIndex) {
        this.tableSearchingMap.clear();
        mainTable.removeValueChangeListener(SearchableTable.this);
        mainTable.removeAllItems();
        for (Object[] data : tableData) {
            this.mainTable.addItem(data, data[keyIndex]);
            String searchKey = "";
            for (int i : searchingIndexes) {
                searchKey += data[i] + "_";
            }
            this.tableSearchingMap.put(searchKey, data[keyIndex] + "");
        }
        mainTable.setCaption("<b>" + tableMainTitle + "</b> ( " + mainTable.getItemIds().size() + " )");
        mainTable.addValueChangeListener(SearchableTable.this);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Object objcetId = mainTable.getValue();//"P01889";
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

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public void selectData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
