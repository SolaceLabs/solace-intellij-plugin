package com.solace.aaron.ideaplugin1.panel;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.table.TableView;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;
import com.solace.aaron.ideaplugin1.service.EventPortalRestService;
import com.solace.aaron.ideaplugin1.table.SOTableModel;

import community.solace.ep.client.model.ApplicationDomain;



public class ApplicationsToolbarPanelDomains extends NonOpaquePanel {

	private static final long serialVersionUID = 1L;
    ApplicationDomain domain = null;

	private static final Logger LOG = Logger.getInstance(ApplicationsToolbarPanelDomains.class);

//    private SearchTextField tagSearchField;
//    private SearchTextField titleSearchField;
    private final TableView<EventPortalDomain> resultsTable;
    private final SOTableModel soTableModel;
//    private AppsTree tree;
    private ActionToolbar toolbar;
    private DefaultActionGroup actionGroup;

    public ApplicationsToolbarPanelDomains(SOTableModel tableModel, TableView<EventPortalDomain> resultsTable) {
        this.soTableModel = tableModel;
        this.resultsTable = resultsTable;
//        this.tree = tree;
        this.init();
//        JBPopupFactory.getInstance().createMessage("ApplicationsToolbarPanel initialized").showInFocusCenter();
    }

    private void init() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createEmptyBorder());

/*
        this.tagSearchField = new SearchTextField();
        String tagText = "Search by Tags";
        StatusText tagSearchEmptyText = this.tagSearchField.getTextEditor().getEmptyText();
        tagSearchEmptyText.appendText(tagText, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ListPluginComponent.GRAY_COLOR));
        this.tagSearchField.addKeyboardListener(this.keyAdapterSearchStackOverflow());

        this.titleSearchField = new SearchTextField();
        String titleText = "Search by Title";
        StatusText titleSearchEmptyText = this.titleSearchField.getTextEditor().getEmptyText();
        titleSearchEmptyText.appendText(titleText, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ListPluginComponent.GRAY_COLOR));
        this.titleSearchField.addKeyboardListener(this.keyAdapterSearchStackOverflow());

        this.add(this.tagSearchField);
        this.add(this.titleSearchField);
*/

        toolbar = this.createToolbar();
        toolbar.setTargetComponent(this);
        this.add(toolbar.getComponent());
    }

    @NotNull
    private ActionToolbar createToolbar() {
        actionGroup = new DefaultActionGroup();
//        actionGroup.add(new ConnectRefreshAction());
//        actionGroup.add(new SearchActionRefresh());
        actionGroup.add(new ActionSortDomains());
        actionGroup.add(new ActionSortAlpha());
        actionGroup.add(new ActionExpandAll());
        actionGroup.add(new ActionCollapseAll());
        actionGroup.add(new ActionFilter());
//        actionGroup.add(new SettingsAction());


        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, false);
    }

    @NotNull
    private KeyAdapter keyAdapterSearchStackOverflow() {
        return new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    ApplicationsToolbarPanelDomains.this.searchStackOverflow();
                }
            }
        };
    }
    
    public void refresh() {
    	searchStackOverflow();
    }

    // old way
    private void searchStackOverflow() {
        List<EventPortalDomain> stackOverflowPosts = new ArrayList<>();
        try {
//            stackOverflowPosts = StackOverflowRestService.getStackOverflowPosts(this.tagSearchField.getText(), this.titleSearchField.getText());
            stackOverflowPosts = EventPortalRestService.getEventPortalDomains();
        } catch (RuntimeException e) {
            LOG.error(e.getMessage());
        }
        if (stackOverflowPosts.isEmpty()) {
            this.resultsTable.getEmptyText().setText("No results found for your search criteria");
        }
        this.soTableModel.setItems(stackOverflowPosts);
        this.resultsTable.updateColumnSizes();
    }

    private void clearResults() {
//        this.soTableModel.setItems(new ArrayList<>());
//        this.tagSearchField.setText("");
//        this.titleSearchField.setText("");
    }
    
    public class AlertRunnable implements Runnable {

    	final CompletableFuture<Boolean> future;
    	
    	public AlertRunnable(CompletableFuture<Boolean> future) {
    		this.future = future;
    	}
    	
    	
		@Override
		public void run() {
	        JBPopupFactory.getInstance().createMessage("alert runnable starting").showInFocusCenter();
			try {
				if (future.get()) {
			        JBPopupFactory.getInstance().createMessage("loading done").showInFocusCenter();
				} else {
			        JBPopupFactory.getInstance().createMessage("loading failed").showInFocusCenter();
				}
			} catch (InterruptedException e) {
		        JBPopupFactory.getInstance().createMessage("Interrupted during loading").showInFocusCenter();
				e.printStackTrace();
			} catch (ExecutionException e) {
		        JBPopupFactory.getInstance().createMessage("Execution Exception during loading").showInFocusCenter();
				e.printStackTrace();
			}
		}
    	
    }

    public class ConnectRefreshAction extends DumbAwareAction {

        protected ConnectRefreshAction() {
//            super("Get EP App Domains", "Get Event Portal Application Domains", AllIcons.Actions.Refresh);
            super("Connect to Event Portal", "Load and fetch all Event Portal Information", AllIcons.Actions.Execute);
        }

        

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {

	        JBPopupFactory.getInstance().createMessage("button clicked").showInFocusCenter();


        
        }
    }



    public class ActionSortAlpha extends DumbAwareAction {

        protected ActionSortAlpha() {
            super("Sort Alphabeically", "Sort Alphabetelic", AllIcons.ObjectBrowser.Sorted);
            this.setEnabledInModalContext(false);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

        }
    }

    public class ActionSortDomains extends DumbAwareAction {

        protected ActionSortDomains() {
            super("Sort By Domains", "Sort By Domains", AllIcons.ObjectBrowser.SortByType);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

        }
    }

    public class ActionExpandAll extends DumbAwareAction {

        protected ActionExpandAll() {
            super("Expand All", "Expand All", AllIcons.Actions.Expandall);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
        	
        }
    }

    public class ActionCollapseAll extends DumbAwareAction {

        protected ActionCollapseAll() {
            super("Collapse All", "Collapse All", AllIcons.Actions.Collapseall);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
        	
        }
    }

    public class ActionFilter extends DumbAwareAction {

        protected ActionFilter() {
            super("Filter", "Filter", AllIcons.General.Filter);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

        }
    }

    public class SettingsAction extends DumbAwareAction {

        protected SettingsAction() {
            super("Settings", "Settings", AllIcons.General.GearPlain);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {

        }
    }

}
