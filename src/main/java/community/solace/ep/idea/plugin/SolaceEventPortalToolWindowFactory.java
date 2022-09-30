package community.solace.ep.idea.plugin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.idea.plugin.nextgen.PortalRowObjectTreeNode;
import community.solace.ep.idea.plugin.nextgen.TabApplications;
import community.solace.ep.idea.plugin.nextgen.TabEventApiProducts;
import community.solace.ep.idea.plugin.nextgen.TabEventApis;
import community.solace.ep.idea.plugin.nextgen.TabEvents;
import community.solace.ep.idea.plugin.nextgen.TabSchema;
import community.solace.ep.idea.plugin.utils.Notify;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
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
	private LoadRefreshButton loadButton = null;

/*	public static class NewWindow implements Disposable {

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
*/
	

	public void openDownloadAsyncApi(PortalRowObjectTreeNode pro, String asyncApi) {
		if (pro.getType() != EventPortalObjectType.APPLICATION_VERSION) return;
		ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(pro.getId());
		Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
		MyAsyncApiDialog dialog = new MyAsyncApiDialog(mainProject, app, appVer, asyncApi);
//		dialog.show();
		if (dialog.showAndGet()) {
			Notify.msg("Looks like OK was clicked");
			dialog.generateCode();
		} else {
			Notify.msg("Looks like CANCEL was clicked");
			
		}
	}
	
	
	public void addEditorTab(String tabTitle, String textContent, FileType fileType, boolean editable, PortalRowObjectTreeNode pro) {
		final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		ContentManager contentMgr = contentFactory.createContentManager(true, mainProject);

		Document doc = EditorFactory.getInstance().createDocument(textContent);
		doc = new DocumentImpl(textContent);

		EditorTextField etf = new EditorTextField(doc, mainProject, fileType, !editable, false);
		
		// font
//		etf.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        etf.setFontInheritedFromLAF(false);
        etf.setFont(EditorUtil.getEditorFont());
        etf.setFont(etf.getFont().deriveFont(14.0f));
        etf.setCaretPosition(0);
		
		JPanel scrollPanel = new JPanel();
		scrollPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollPanel.setLayout(new BorderLayout());
		scrollPanel.add(ScrollPaneFactory.createScrollPane(etf), BorderLayout.CENTER);

		
		
		
		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		main.setBorder(BorderFactory.createEmptyBorder());
		GridBagConstraints con = new GridBagConstraints();
        con.ipadx = 5;
        con.ipady = 5;
        con.insets = new Insets(10, 10, 0, 10);
        con.anchor = GridBagConstraints.PAGE_START;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.weightx = 0;
        con.weighty = 0;
        int y = 0;
        con.gridx = 0;
        con.gridy = y;

        
//        JBLabel doamainLabel = new JBLabel(pro.get
        
        
        JButton button = new JButton("Generate code...");//, MyIcons.AsyncApi2);
        if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION) button.setIcon(MyIcons.AsyncApi2);
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openDownloadAsyncApi(pro, etf.getText());
			}
		});
        main.add(button, con);

        con.insets = new Insets(5, 10, 0, 10);
        con.gridx = 0;
        y++;
        con.gridy = y;
        main.add(new JButton("Save schema file..."), con);

        con.insets = new Insets(0, 0, 0, 0);
        con.gridx = 1;
        con.gridy = 0;
        con.gridheight = 2;
        con.weightx = 1.0;
        con.weighty = 1.0;
        con.fill = GridBagConstraints.BOTH;
        main.add(scrollPanel, con);
//		main.add(scrollPanel);
//        
        
        
		Content content = contentFactory.createContent(main, tabTitle, true);
//		Content content = contentFactory.createContent(scrollPanel, tabTitle, true);
		content.setCloseable(true);
		content.setIcon(MyIcons.AppLarge);
		contentMgr.addContent(content);
		toolWindow.getContentManager().addContent(content);
		toolWindow.getContentManager().setSelectedContent(content);
		
	}

	
	public class SearchAll extends DumbAwareAction {

		protected SearchAll() {
			super("Search for...", "Search for...", AllIcons.Actions.Search);
		}

		@Override
		public void actionPerformed(@NotNull AnActionEvent e) {
			e.getPresentation().setEnabled(false);
		}
	}
	
	public void clickLoadButton(AnActionEvent event) {
		loadButton.actionPerformed(event);
	}

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		this.mainProject = project;
		this.toolWindow = toolWindow;

		this.loadButton = new LoadRefreshButton();
		List<AnAction> titleActions = new ArrayList<>();
		titleActions.add(new SearchAll());
		titleActions.add(loadButton);
		toolWindow.setTitleActions(titleActions);


		final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content;  // each tab

		TabApplications appsTableViewWindow = new TabApplications(this);
		loadButton.addListener(appsTableViewWindow);
		content = contentFactory.createContent(appsTableViewWindow.getContent(), "Applications", false);
		content.setCloseable(false);
		content.setIcon(MyIcons.AppLarge);
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




	}
}
