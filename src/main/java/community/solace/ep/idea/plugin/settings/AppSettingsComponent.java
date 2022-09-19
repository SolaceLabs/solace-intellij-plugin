// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package community.solace.ep.idea.plugin.settings;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import community.solace.ep.idea.plugin.utils.TimeUtils;
import community.solace.ep.idea.plugin.utils.TimeUtils.Format;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

	private final JPanel myMainPanel;
	private final JBPasswordField myTokenText = new JBPasswordField();
	private final JBTextField baseUrl = new JBTextField();
	private final JBCheckBox myIdeaUserStatus = new JBCheckBox("Show EP Objects IDs?");
	private final ComboBox<TimeUtils.Format> myDateTimeFormat;
	private final ComboBox<String> myOrg;


	public AppSettingsComponent() {
		
		myTokenText.setSize(100, 20);
		
		myDateTimeFormat = new ComboBox<>();
		myDateTimeFormat.addItem(Format.RELATIVE);
		myDateTimeFormat.addItem(Format.CASUAL);
		myDateTimeFormat.addItem(Format.TIMESTAMP);

		myOrg = new ComboBox<>();
		myOrg.addItem("CTO");

		myMainPanel = FormBuilder.createFormBuilder()
				.addLabeledComponent(new JBLabel("EP 2.0 Token:"), myTokenText, 1, false)
				.addLabeledComponent(new JBLabel("URL for web access:"), baseUrl, 1, false)
				.addLabeledComponent(new JBLabel("Date format preference:"),myDateTimeFormat, 5, false)
				.addSeparator(10)
				.addLabeledComponent(new JBLabel("Developer Tools"), myIdeaUserStatus, 5, false)
//				.addComponent(myIdeaUserStatus, 5)
				.addComponentFillVertically(new JPanel(), 0)
				.getPanel();

	}

	public JPanel getPanel() {
		return myMainPanel;
	}

	public JComponent getPreferredFocusedComponent() {
		return myTokenText;
	}

	@NotNull
	public String getTokenText() {
		return String.copyValueOf(myTokenText.getPassword());
	}

	public void setTokenText(@NotNull String newText) {
		myTokenText.setText(newText);
	}

	@NotNull
	public String getBaseUrl() {
		return baseUrl.getText();
	}

	public void setBaseUrl(@NotNull String baseUrl) {
		this.baseUrl.setText(baseUrl);
	}

	@NotNull
	public TimeUtils.Format getDateTimeFormat() {
		return (TimeUtils.Format)myDateTimeFormat.getSelectedItem();
	}
	
	public void setDateTimeFormat(@NotNull TimeUtils.Format dateTimeFormat) {
		myDateTimeFormat.setSelectedItem(dateTimeFormat);
	}

	public boolean getIdeaUserStatus() {
		return myIdeaUserStatus.isSelected();
	}

	public void setIdeaUserStatus(boolean newStatus) {
		myIdeaUserStatus.setSelected(newStatus);
	}

}
