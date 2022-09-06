package com.solace.aaron.ideaplugin1.nextgen;

import java.awt.FlowLayout;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.panels.NonOpaquePanel;

import icons.MyIcons;


public class PortalTabToolbar extends NonOpaquePanel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getInstance(PortalTabToolbar.class);

	private ActionToolbar toolbar;
	private DefaultActionGroup actionGroup;
	private final PortalToolbarListener listener;  // who is listening to me, for when I get clicked
	

	public PortalTabToolbar(PortalToolbarListener listener) {
		this.listener = listener;
		this.init();
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
		actionGroup.add(new ActionExpandAll());
		actionGroup.add(new ActionCollapseAll());
		actionGroup.addSeparator();
		actionGroup.add(new ActionSortDomains());
		actionGroup.add(new ActionSortAlpha());
		actionGroup.addSeparator();
		actionGroup.add(new HideEmptyDomains());
		actionGroup.addSeparator();
		actionGroup.add(new ActionFilter());
//        actionGroup.add(new SettingsAction());

		return ActionManager.getInstance().createActionToolbar("ep-apps-toolbar", actionGroup, false);
	}





	public AtomicBoolean currentSortStateObjects = new AtomicBoolean(true);

    private class ActionSortDomains extends ToggleAction {
    	
        protected ActionSortDomains() {
            super("Sort By Domains", "Sort the list of Applications By Domains", AllIcons.ObjectBrowser.SortByType);
        }

		@Override
		public boolean isSelected(@NotNull AnActionEvent e) {
			return currentSortStateObjects.get();
		}

		@Override
		public void setSelected(@NotNull AnActionEvent e, boolean state) {
			boolean existing = currentSortStateObjects.compareAndSet(false, true);
//			currentSortStateObjects.set(true);
			if (existing) listener.sortObjects();  // only sort if we change
		}
    }

    volatile boolean hideEmptyDomains = false;
    
    private class HideEmptyDomains extends ToggleAction {
    	
        protected HideEmptyDomains() {
            super("Hide Empty Domains", "Simply hides any Domains that are empty", MyIcons.ToggleVisibility);
        }

		@Override
		public boolean isSelected(@NotNull AnActionEvent e) {
			return hideEmptyDomains;
		}

		@Override
		public void setSelected(@NotNull AnActionEvent e, boolean state) {
			hideEmptyDomains = state;
			listener.hideEmptyDomains();
		}
    }

    private class ActionSortAlpha extends ToggleAction {

        protected ActionSortAlpha() {
            super("Sort Alphabeically", "Sort the list of Applications alphabetically", AllIcons.ObjectBrowser.Sorted);
        }

		@Override
		public boolean isSelected(@NotNull AnActionEvent e) {
			return !currentSortStateObjects.get();
		}

		@Override
		public void setSelected(@NotNull AnActionEvent e, boolean state) {
			boolean existing = currentSortStateObjects.compareAndSet(true, false);
			if (existing) listener.sortAlpha();
		}
    }

	public class ActionExpandAll extends DumbAwareAction {

		protected ActionExpandAll() {
			super("Expand Next", "Expand Next", AllIcons.Actions.Expandall);
		}

		@Override
		public void actionPerformed(@NotNull AnActionEvent e) {
			listener.expandAll();
		}
	}

	public class ActionCollapseAll extends DumbAwareAction {

		protected ActionCollapseAll() {
			super("Collapse All", "Collapse All", AllIcons.Actions.Collapseall);
		}

		@Override
		public void actionPerformed(@NotNull AnActionEvent e) {
			listener.collapseAll();
		}
	}

	public class ActionFilter extends DumbAwareAction  {

		protected ActionFilter() {
			super("Filter", "Filter", AllIcons.General.Filter);
		}
		
		@Override
		public void update(@NotNull AnActionEvent e) {
			super.update(e);
			e.getPresentation().setEnabled(false);
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
