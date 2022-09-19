package community.solace.ep.idea.plugin;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;

import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBTextField;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.idea.plugin.utils.Notify;
import community.solace.ep.wrapper.AsyncApiCodegen;
import community.solace.ep.wrapper.AsyncApiCodegen.SolaceSpringCloudStreamBuilder;

public class MyAsyncApiDialog extends DialogWrapper  {

	final Project project;
	final Application app;
	final ApplicationVersion appVer;
	final String asyncApiContent;
	String appName;
	
	JBTextField appNameField;
	JBTextField packageNameField;
	JBTextField fileLocationField;
	
	protected MyAsyncApiDialog(Project project, Application app, ApplicationVersion appVer, String asyncApi) {
		super(project, false);
		this.project = project;
		this.app = app;
		this.appVer = appVer;
		this.asyncApiContent = asyncApi;
		appName = String.format("%sV%s", app.getName().replaceAll("[^a-zA-Z0-9]", ""), appVer.getVersion());
		setTitle("Configure for AsyncAPI Code Generation...");
		this.setOKButtonText("Generate");
		init();
		Dimension d = this.getPreferredSize();
		this.setSize((int)(d.height * 1.618), d.height);
//		this.myOKAction = new CustomAction("blah");	
	}
	
	
	
	public void generateCode() {
		
		SolaceSpringCloudStreamBuilder b = new SolaceSpringCloudStreamBuilder()
				.withArtifactId(appNameField.getText())
				.withJavaPackage(packageNameField.getText());
		b.withDestDir(new File(fileLocationField.getText()));
		b.withAsyncApi(asyncApiContent);
		try {
			File f = AsyncApiCodegen.getCode2(b);
			Notify.msg("Successfully generate code now to: " + f.getPath());

			Project p = ProjectUtil.openOrImport(new File(f,"template").toPath());
			
//	        ProjectUtil.updateLastProjectLocation(projectFile);
//	        OpenProjectTask options = OpenProjectTask.withCreatedProject(p);
//	        Path fileName = projectFile.getFileName();
//	        if (fileName != null) {
//	          options = options.withProjectName(fileName.toString());
//	        }
//	        TrustedPaths.getInstance().setProjectPathTrusted(projectDir, true);
//	        ProjectManagerEx.getInstanceEx().openProject(projectDir, options);			
			

		} catch (Exception e) {
			Notify.msg("DID NOT generate code now: " + e.toString());
			e.printStackTrace();
		}
	}
	
	public static class BoldLabel extends JLabel {
		
		BoldLabel(String text) {
			super(text);
			Font f = this.getFont();
			this.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
		}
	}
	
	
	

	@Override
	protected @Nullable JComponent createCenterPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(panel.getBackground());
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder());
		GridBagConstraints con = new GridBagConstraints();
        con.ipadx = 10;
        con.ipady = 10;
        con.insets = new Insets(0, 10, 20, 10);
        con.anchor = GridBagConstraints.LINE_START;
        int y = 0;
		con.gridy = 0;
		con.gridx = 0;
        con.gridwidth = 2;
        con.weightx = 1.0;
		JLabel label = new JLabel("This dialogue is a work-in-progress.  More configuration options will be added in later releases.");
		panel.add(label, con);
		
		
//		.withParam("binder", "solace")
//		.withParam("dynamicType", "header")
//		.withParam("javaPackage", "com.solace.test.aaron")
//		.withParam("host", "localhost")
//		.withParam("msgVpn", "default")
//		.withParam("username", "aaron");


        con.insets = new Insets(0, 10, 0, 10);
		con.gridwidth = 1;
		y++;
		con.gridy = y;
		con.gridx = 0;
        con.weightx = 0;
		con.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Template:"), con);
		con.gridx = 1;
        con.weightx = 1.0;
		con.fill = GridBagConstraints.HORIZONTAL;
		ComboBox<String> cb = new ComboBox<>();
		cb.addItem("Spring Cloud Stream Java");
		panel.add(cb, con);

		y++;
		con.gridy = y;
		con.gridx = 0;
        con.weightx = 0;
		con.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("Binder:"), con);
		con.gridx = 1;
        con.weightx = 1.0;
		con.fill = GridBagConstraints.HORIZONTAL;
		cb = new ComboBox<>();
		cb.addItem("Solace PubSub+");
		cb.addItem("Kafka");
		cb.addItem("RabbitMQ");
		panel.add(cb, con);

		y++;
		con.gridy = y;
		con.gridx = 0;
        con.weightx = 0;
		con.fill = GridBagConstraints.NONE;
        con.insets = new Insets(10, 10, 0, 10);
		panel.add(new JLabel("App Name:"), con);
		con.gridx = 1;
        con.weightx = 1.0;
		con.fill = GridBagConstraints.HORIZONTAL;
		appNameField = new JBTextField(appName);
		panel.add(appNameField, con);

		y++;
		con.gridy = y;
		con.gridx = 0;
        con.weightx = 0;
        con.insets = new Insets(0, 10, 0, 10);
		panel.add(new JLabel("Base Package:"), con);
		con.fill = GridBagConstraints.NONE;
		con.gridx = 1;
        con.weightx = 1.0;
		packageNameField = new JBTextField("com.example.app");
		con.fill = GridBagConstraints.HORIZONTAL;
		panel.add(packageNameField, con);
		
		y++;
		con.gridy = y;
		con.gridx = 0;
        con.weightx = 0;
		con.fill = GridBagConstraints.NONE;
		panel.add(new JLabel("File Location:"), con);
		con.gridx = 1;
        con.weightx = 1.0;
		con.fill = GridBagConstraints.HORIZONTAL;
		try {
			fileLocationField = new JBTextField(new File(System.getProperty("user.home") + "/Downloads").getCanonicalPath());
			TextFieldWithBrowseButton browse = new TextFieldWithBrowseButton(fileLocationField);
			FileChooserDescriptor fcd = FileChooserDescriptorFactory.createSingleFolderDescriptor();
			browse.addBrowseFolderListener(new TextBrowseFolderListener(fcd) {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					super.actionPerformed(e);
					Notify.msg("addBrowseFolderListener action has been performed! " + browse.getText());
				}
				
			});
//			browse.addActionListener(new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// TODO Auto-generated method stub
//					Notify.msg("addActionListener action has been performed! " + browse.getText());
//					
//				}
//			});
			
			panel.add(browse, con);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return panel;
	}
	
}
