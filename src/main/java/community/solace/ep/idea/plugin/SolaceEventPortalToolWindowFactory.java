package community.solace.ep.idea.plugin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.jetbrains.annotations.NotNull;

import com.intellij.json.json5.Json5FileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import community.solace.ep.idea.plugin.nextgen.TabApplications;
import community.solace.ep.idea.plugin.nextgen.TabEventApiProducts;
import community.solace.ep.idea.plugin.nextgen.TabEventApis;
import community.solace.ep.idea.plugin.nextgen.TabEvents;
import community.solace.ep.idea.plugin.nextgen.TabSchema;
import icons.MyIcons;

/**
 * This Factory will load up the Tool Window, which contains a number of "tabs" (contents)
 *
 */
public class SolaceEventPortalToolWindowFactory implements ToolWindowFactory {

	// -Dide.ui.scale=1.5  // for larger fonts
	
	private static final Logger logger = Logger.getInstance(SolaceEventPortalToolWindowFactory.class);
	
	private Project mainProject = null;
	private ToolWindow toolWindow = null;
//	
//	public static Project getProject() {
//		return project;
//	}
	
	public static class NewWindow implements Disposable {

		@Override
		public void dispose() {
	
		}
		
		Project project;
		ToolWindow myToolWindow;
		
		public NewWindow(Project p, ToolWindow myToolWindow) {
			this.project = p;
			this.myToolWindow = myToolWindow;
		}
		
		public static NewWindow getInstance(@NotNull Project project) {
			return project.getService(NewWindow.class);
		}

		  public void closeTab(@NotNull Content content) {
			    myToolWindow.getContentManager().removeContent(content, true, true, true);
			  }

		  private Content createContent() {
	        JPanel panel = new JPanel();
	        panel.add(new JLabel("Hello world"));
	        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "NEW", true);
	        content.setCloseable(true);
	        return content;

		  }
		  
		  private void open() {
			  Content c = createContent();
			    final ContentManager contentManager = myToolWindow.getContentManager();
			    contentManager.addContent(c);
			    
		  }
	}
	
	public void addNewContent(Project project, String textContent) {
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        toolWindow.setC
        ContentManager mgr = contentFactory.createContentManager(true, mainProject);
        Content content;  // each tab

//        JPanel panel = new JPanel();
//        panel.setBorder(BorderFactory.createEmptyBorder());
//        panel.setLayout(new BorderLayout());
//        panel.add(scrollPanel, BorderLayout.CENTER);


        
        Document d = EditorFactory.getInstance().createDocument("this is a text field for adding data.");
        EditorTextField etf = new EditorTextField(d, project, Json5FileType.INSTANCE);
//        panel.add(new JLabel("Hello world"));
//        panel.add(etf);
        JTextPane pane = new JTextPane();
        pane.setText(textContent);
        pane.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        
        
        JPanel scrollPanel = new JPanel();
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(ScrollPaneFactory.createScrollPane(pane), BorderLayout.CENTER);

//        scrollPanel.add(pane);

        content = contentFactory.createContent(scrollPanel, "NEW", true);
        content.setCloseable(true);
        content.setIcon(MyIcons.appLarge);
        mgr.addContent(content);
        toolWindow.getContentManager().addContent(content);

        
	}
	
	
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    	this.mainProject = project;
    	this.toolWindow = toolWindow;
    	
    	LoadRefreshButton loadButton = new LoadRefreshButton();
    	List<AnAction> titleActions = new ArrayList<>();
    	titleActions.add(loadButton);
        toolWindow.setTitleActions(titleActions);
        

        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content;  // each tab
    	
        TabApplications appsTableViewWindow = new TabApplications(this);
        loadButton.addListener(appsTableViewWindow);
        content = contentFactory.createContent(appsTableViewWindow.getContent(), "Applications", false);
        content.setCloseable(false);
        content.setIcon(MyIcons.appLarge);
        toolWindow.getContentManager().addContent(content);
        
        TabEvents eventsTabWindow2 = new TabEvents(this);
        loadButton.addListener(eventsTabWindow2);
        content = contentFactory.createContent(eventsTabWindow2.getContent(), "Events", false);
        content.setCloseable(false);
        content.setIcon(MyIcons.EventLarge);  // doesn't work!  :-(
        toolWindow.getContentManager().addContent(content);
        
        TabSchema schemaTabWindow = new TabSchema(this);
        loadButton.addListener(schemaTabWindow);
        content = contentFactory.createContent(schemaTabWindow.getContent(), "Schemas", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
        
        TabEventApis eventApisTabWindow = new TabEventApis(this);
        loadButton.addListener(eventApisTabWindow);
        content = contentFactory.createContent(eventApisTabWindow.getContent(), "Event APIs", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
        
        TabEventApiProducts eventApiProductsTabWindow = new TabEventApiProducts(this);
        loadButton.addListener(eventApiProductsTabWindow);
        content = contentFactory.createContent(eventApiProductsTabWindow.getContent(), "Event API Products", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
        

        String textContent = "{\n \"this is a\": \"long string\", \n \"blahblahbl\": \"alhblahba\", \n\n \"here is the\": \"4th line.\"\n}";
        addNewContent(project, textContent);
        
//        NewWindow nw = new NewWindow(mainProject, this.toolWindow);
//        nw.open();
        
        
    }
}
