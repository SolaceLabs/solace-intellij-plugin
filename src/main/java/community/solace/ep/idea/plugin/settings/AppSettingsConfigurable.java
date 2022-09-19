// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package community.solace.ep.idea.plugin.settings;

import javax.swing.JComponent;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.options.Configurable;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

  private AppSettingsComponent mySettingsComponent;

  // A default constructor with no arguments is required because this implementation
  // is registered as an applicationConfigurable EP

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Solace Event Portal";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return mySettingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    mySettingsComponent = new AppSettingsComponent();
    return mySettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    AppSettingsState settings = AppSettingsState.getInstance();
    boolean modified = !mySettingsComponent.getTokenText().equals(settings.tokenId);
    modified |= !mySettingsComponent.getBaseUrl().equals(settings.baseUrl);
    modified |= mySettingsComponent.getDateTimeFormat() != settings.dateTimeFormat;
    modified |= mySettingsComponent.getIdeaUserStatus() != settings.devShowIds;
    return modified;
  }

  @Override
  public void apply() {
    AppSettingsState settings = AppSettingsState.getInstance();
    settings.tokenId = mySettingsComponent.getTokenText();
    settings.baseUrl = mySettingsComponent.getBaseUrl();
    settings.dateTimeFormat = mySettingsComponent.getDateTimeFormat();
    settings.devShowIds = mySettingsComponent.getIdeaUserStatus();
  }

  @Override
  public void reset() {
    AppSettingsState settings = AppSettingsState.getInstance();
    mySettingsComponent.setTokenText(settings.tokenId);
    mySettingsComponent.setBaseUrl(settings.baseUrl);
    mySettingsComponent.setDateTimeFormat(settings.dateTimeFormat);
    mySettingsComponent.setIdeaUserStatus(settings.devShowIds);
  }

  @Override
  public void disposeUIResources() {
    mySettingsComponent = null;
  }

}
