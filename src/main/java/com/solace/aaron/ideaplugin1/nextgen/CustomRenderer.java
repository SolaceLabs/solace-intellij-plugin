package com.solace.aaron.ideaplugin1.nextgen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.PlatformColors;
import com.intellij.util.ui.UIUtil;

import community.solace.ep.client.model.StateDTO;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;

public class CustomRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	final PortalRowObject item;

	public CustomRenderer(PortalRowObject item) {
		this.item = item;
	}

	final static Color GREEN1 = new Color(0, 200, 149);
	final static Color GREEN2 = new Color(0, 173, 147);
	final static Color GREEN3 = new Color(0, 145, 147);
	final static Color ORANGE = new Color(243, 112, 34);

	static Color blend(Color orig, Color accent, float amount) {
		return new Color((int)(orig.getRed() * (1-amount) + accent.getRed() * amount),
				(int)(orig.getGreen() * (1-amount) + accent.getGreen() * amount),
				(int)(orig.getBlue() * (1-amount) + accent.getBlue() * amount));
	}

	static Color opposite(Color orig) {
		return new Color(255-orig.getRed(), 255-orig.getGreen(), 255-orig.getBlue());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);

		//			cellComponent.setBackground(new Color(0.25f * column / COLUMNS.length, 0.25f * column / COLUMNS.length, 0.25f * column / COLUMNS.length));

		if (column == 3) {
			switch (item.getState()) {
			case "Draft": 
				cellComponent.setForeground(blend(cellComponent.getForeground(), Color.BLUE, 0.25f));
				break;
			case "Released":
				cellComponent.setForeground(blend(cellComponent.getForeground(), Color.GREEN, 0.25f));
				break;
			case "Deprecated":
				cellComponent.setForeground(blend(cellComponent.getForeground(), Color.ORANGE, 0.25f));
				break;
			case "Retired":
				cellComponent.setForeground(blend(cellComponent.getForeground(), Color.RED, 0.25f));
				break;
			case "--":
				cellComponent.setForeground(blend(cellComponent.getForeground(), cellComponent.getBackground(), 0.5f));
				break;
			default:
//				cellComponent.setForeground(blend(cellComponent.getForeground(), Color.GREEN, 0.5f));
				break;
					
			}
			for (StateDTO state : EventPortalWrapper.INSTANCE.getStates()) {
				state.getName();
			}
		}


		if (column == 4) {
//			if (Math.random() < 0.5) {
				//					cellComponent.setFont(new Font(  Font.MONOSPACED, Font.BOLD, 12));
				cellComponent.setFont(new Font("Lucida Console", Font.PLAIN, 14));
//			} else {
//				cellComponent.setFont(new Font("Lucida Console", Font.PLAIN, 12));
//			}

		} else {
			Font curFont = cellComponent.getFont();
			//				cellComponent.setFont(curFont.deriveFont(Font.BOLD));
		}

		table.getSelectionForeground();


		float blendAmount;
		if (row % 2 == 1) {
			blendAmount = 0.1f;
		}

		switch (item.getTypeEnum()) {
		case DOMAIN:
			cellComponent.setBackground(blend(table.getBackground(), opposite(table.getBackground()), 0.05f));
			break;
		case APPLICATION:
			cellComponent.setBackground(blend(table.getBackground(), GREEN1, 0.1f));
			break;
		case APPLICATION_VERSION:
			cellComponent.setBackground(blend(table.getBackground(), GREEN1, 0.05f));
			break;
		case EVENT:
			cellComponent.setBackground(blend(table.getBackground(), GREEN2, 0.1f));
			break;
		case EVENT_VERSION:
			cellComponent.setBackground(blend(table.getBackground(), GREEN2, 0.05f));
			break;
		case SCHEMA:
			cellComponent.setBackground(blend(table.getBackground(), GREEN3, 0.1f));
			break;
		case SCHEMA_VERSION:
			cellComponent.setBackground(blend(table.getBackground(), GREEN3, 0.05f));
			break;
		default:
			//				cellComponent.setBackground(blend(table.getBackground(), GREEN1, 0.1f));
		}


		if (column == 1) {  // Name into Portal link
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(0,0));
			panel.setBorder(BorderFactory.createEmptyBorder());
			panel.setBackground(cellComponent.getBackground());

//			AnActionLink externalLink2 = new AnActionLink("hello", null, com.intellij.openapi.actionSystem.ActionPlaces.UNKNOWN);
			AnActionLink externalLink2 = new AnActionLink(item.getName(), new AnAction() {
				@Override
				public void actionPerformed(@NotNull AnActionEvent e) { }			
			});
			externalLink2.setExternalLinkIcon();
			panel.add(externalLink2);
			if ("a".equals("a")) return panel;

			cellComponent.setForeground(blend(cellComponent.getForeground(), new Color(0.25f, 0.25f, 1f), 0.5f));
			Color c = UIUtil.getDecoratedRowColor();
			cellComponent.setForeground(PlatformColors.BLUE);


//			cellComponent.setForeground(new Color(63,63,191));
			if (cellComponent instanceof JLabel) {
				JLabel label = (JLabel)cellComponent;
				label.setText(item.getName() + " ↗");
				panel.add(cellComponent, BorderLayout.LINE_START);
				return panel;
			}



		} else if (column == 6) {
			if (item.getTypeEnum() == EventPortalObjectType.APPLICATION_VERSION ) {
				ActionLink externalLink = new ActionLink("Download AsyncAPI...", event -> {
					//                                          BrowserUtil.browse("http://google.com");
				});
//				return (table, value, isSelected, hasFocus, row, column) -> externalLink;
				return externalLink;

				//                                      return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
			} else if (item.getTypeEnum() == EventPortalObjectType.EVENT_VERSION) {
				ActionLink externalLink = new ActionLink("Generate Schema POJO...", event -> {
					                                          BrowserUtil.browse("http://google.com");
				});
//				return (table, value, isSelected, hasFocus, row, column) -> externalLink;
				return externalLink;
				//                                      return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
			}
			
		}


		return cellComponent;
	}
}
