package com.solace.aaron.ideaplugin1.panel;

import java.awt.FlowLayout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;

import community.solace.ep.client.model.ApplicationDomain;



public class ApplicationsToolbarPanel extends NonOpaquePanel {

	private static final long serialVersionUID = 1L;
    ApplicationDomain domain = null;

	private static final Logger LOG = Logger.getInstance(ApplicationsToolbarPanel.class);

//    private SearchTextField tagSearchField;
//    private SearchTextField titleSearchField;
//    private final TableView<EventPortalDomain> resultsTable;
//    private final SOTableModel soTableModel;
//    private AppsTree tree;
    private ActionToolbar toolbar;
    private DefaultActionGroup actionGroup;

    public ApplicationsToolbarPanel() { //SOTableModel tableModel, TableView<EventPortalDomain> resultsTable) {
//        this.soTableModel = tableModel;
//        this.resultsTable = resultsTable;
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
//
//    @NotNull
//    private KeyAdapter keyAdapterSearchStackOverflow() {
//        return new KeyAdapter()
//        {
//            @Override
//            public void keyPressed(KeyEvent e)
//            {
//                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
//                    ApplicationsToolbarPanel.this.searchStackOverflow();
//                }
//            }
//        };
//    }
//    

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


	AtomicBoolean currentSortStateObjects = new AtomicBoolean(true);

    public class ActionSortDomains extends ToggleAction {
    	
        protected ActionSortDomains() {
            super("Sort By Domains", "Sort By Domains", AllIcons.ObjectBrowser.SortByType);
        }

		@Override
		public boolean isSelected(@NotNull AnActionEvent e) {
			return currentSortStateObjects.get();
		}

		@Override
		public void setSelected(@NotNull AnActionEvent e, boolean state) {
			currentSortStateObjects.set(state);
		}
    }

    public class ActionSortAlpha extends ToggleAction {

        protected ActionSortAlpha() {
            super("Sort Alphabeically", "Sort Alphabetelic", AllIcons.ObjectBrowser.Sorted);
//            this.setEnabledInModalContext(false);
        }

		@Override
		public boolean isSelected(@NotNull AnActionEvent e) {
			return !currentSortStateObjects.get();
		}

		@Override
		public void setSelected(@NotNull AnActionEvent e, boolean state) {
			currentSortStateObjects.set(!state);
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
