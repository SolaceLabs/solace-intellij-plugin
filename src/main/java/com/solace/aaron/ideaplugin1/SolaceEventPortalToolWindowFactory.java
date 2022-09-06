package com.solace.aaron.ideaplugin1;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.solace.aaron.ideaplugin1.nextgen.ApplicationsTabWindow;
import com.solace.aaron.ideaplugin1.nextgen.EventApiProductsTabWindow;
import com.solace.aaron.ideaplugin1.nextgen.EventApisTabWindow;
import com.solace.aaron.ideaplugin1.nextgen.EventsTabWindow;
import com.solace.aaron.ideaplugin1.nextgen.SchemaTabWindow;

import icons.MyIcons;

/**
 * This Factory will load up the Tool Window, which contains a number of "tabs" (contents)
 *
 */
public class SolaceEventPortalToolWindowFactory implements ToolWindowFactory {

	// -Dide.ui.scale=1.5  // for larger fonts
	
	private static final Logger logger = Logger.getInstance(SolaceEventPortalToolWindowFactory.class);
	private static Project project = null;
	
	public static Project getProject() {
		return project;
	}
	
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    	SolaceEventPortalToolWindowFactory.project = project;
    	
    	LoadRefreshButton loadButton = new LoadRefreshButton();
    	List<AnAction> titleActions = new ArrayList<>();
    	titleActions.add(loadButton);
        toolWindow.setTitleActions(titleActions);
        

        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content;  // each tab
    	
        ApplicationsTabWindow appsTableViewWindow = new ApplicationsTabWindow();
        loadButton.addListener(appsTableViewWindow);
        content = contentFactory.createContent(appsTableViewWindow.getContent(), "Applications", false);
        content.setIcon(MyIcons.appLarge);
        toolWindow.getContentManager().addContent(content);
        
        EventsTabWindow eventsTabWindow2 = new EventsTabWindow();
        loadButton.addListener(eventsTabWindow2);
        content = contentFactory.createContent(eventsTabWindow2.getContent(), "Events", false);
        content.setIcon(MyIcons.EventLarge);  // doesn't work!  :-(
        toolWindow.getContentManager().addContent(content);
        
        SchemaTabWindow schemaTabWindow = new SchemaTabWindow();
        loadButton.addListener(schemaTabWindow);
        content = contentFactory.createContent(schemaTabWindow.getContent(), "Schemas", false);
        toolWindow.getContentManager().addContent(content);
        
        EventApisTabWindow eventApisTabWindow = new EventApisTabWindow();
        loadButton.addListener(eventApisTabWindow);
        content = contentFactory.createContent(eventApisTabWindow.getContent(), "Event APIs", false);
        toolWindow.getContentManager().addContent(content);
        
        EventApiProductsTabWindow eventApiProductsTabWindow = new EventApiProductsTabWindow();
        loadButton.addListener(eventApiProductsTabWindow);
        content = contentFactory.createContent(eventApiProductsTabWindow.getContent(), "Event API Products", false);
        toolWindow.getContentManager().addContent(content);
    }
}
