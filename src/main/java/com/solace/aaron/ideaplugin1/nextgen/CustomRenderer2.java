package com.solace.aaron.ideaplugin1.nextgen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.AnActionLink;
import com.solace.aaron.ideaplugin1.utils.EPObjectHelper;

import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;

public class CustomRenderer2 extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	private static final EventPortalWrapper EPW = EventPortalWrapper.INSTANCE;

//	final PortalRowObjectTreeNode item;

	public CustomRenderer2() { //PortalRowObjectTreeNode item) {
//		this.item = item;
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
	
	@NotNull
	private static final AnAction EMPTY_EVENT = new AnAction() {
		@Override
		public void actionPerformed(@NotNull AnActionEvent e) {
			// nothing
		}
	};

	@Override
	public Component getTableCellRendererComponent(JTable table, Object rowNode, boolean isSelected, boolean hasFocus, int row, int column) {
		PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)rowNode;
		Component cellComponent = super.getTableCellRendererComponent(table, pro, false, hasFocus, row, column);
/*		switch (column) {
		case 0: 
			cellComponent = super.getTableCellRendererComponent(table, EPObjectHelper.getName(pro.getTypeEnum()), false, hasFocus, row, column);
			break;
		case 1:
			cellComponent = super.getTableCellRendererComponent(table, pro.getName(), false, hasFocus, row, column);
			break;
		case 2:
			cellComponent = super.getTableCellRendererComponent(table, pro.getNotes(), false, hasFocus, row, column);
			break;
		case 3:
			cellComponent = super.getTableCellRendererComponent(table, pro.getState(), false, hasFocus, row, column);
			break;
		case 4:
			cellComponent = super.getTableCellRendererComponent(table, pro.getDetails(), false, hasFocus, row, column);
			break;
		case 5:
			cellComponent = super.getTableCellRendererComponent(table, pro.getLastUpdated(), false, hasFocus, row, column);
			break;
		case 6:
			cellComponent = super.getTableCellRendererComponent(table, "--case6--", false, hasFocus, row, column);
			break;
		default:
			cellComponent = super.getTableCellRendererComponent(table, "--DEFAULT--", false, hasFocus, row, column);
			break;
			
		}
*/		
		
//		Component cellComponent = super.getTableCellRendererComponent(table, ((PortalRowObjectTreeNode)rowNode).getName(), false, hasFocus, row, column);
//		PortalRowObjectTreeNode item = (PortalRowObjectTreeNode)rowNode;

		// background colour first
		Color mix = EPObjectHelper.getColor(pro.getType());
		switch (pro.getType()) {
		case DOMAIN:
		case APPLICATION:
		case EVENT:
		case SCHEMA:
		case EVENT_API:
		case EVENT_API_PRODUCT:
//			cellComponent.setBackground(blend(table.getBackground(), opposite(table.getBackground()), 0.05f));
			cellComponent.setBackground(blend(table.getBackground(), mix, 0.05f));
			break;
		case APPLICATION_VERSION:
		case EVENT_VERSION:
		case SCHEMA_VERSION:
		case EVENT_API_VERSION:
		case EVENT_API_PRODUCT_VERSION:
			cellComponent.setBackground(blend(table.getBackground(), mix, 0.03f));
			break;
		default:
			cellComponent.setBackground(blend(table.getBackground(), mix, 0.1f));
		}

		
        JPanel panel = new JPanel();
        panel.setBackground(cellComponent.getBackground());
		switch (column) {
		case 0:  // the type
            panel.setBorder(BorderFactory.createEmptyBorder());
            panel.setLayout(new GridBagLayout());
            GridBagConstraints con = new GridBagConstraints();
            con.ipadx = 3;
            con.insets = new Insets(0, 20 * (pro.getDepth()-1), 0, 0);
            con.anchor = GridBagConstraints.LINE_START;
//            panel.setBackground(getColor(p, false));
//            JLabel iconArrow = new JLabel(Math.random() > 0.5 ? AllIcons.Actions.Play_forward : AllIcons.Actions.FindAndShowNextMatches);
//            if (!pro.hasChildren()) iconArrow.setIcon(AllIcons.Nodes.EmptyNode);
//            iconArrow.setBackground(getColor(p, false));
//            panel.add(iconArrow, con);
            
            if (!pro.hasChildren()) panel.add(new JLabel(AllIcons.Nodes.EmptyNode), con);
            else if (pro.isExpanded()) panel.add(new JLabel(AllIcons.Actions.FindAndShowNextMatches), con);
            else panel.add(new JLabel(AllIcons.Actions.Play_forward), con);
            
            con.insets = new Insets(0, 0, 0, 0);
            Icon rowIcon = pro.getIcon();
//            switch (item.getTypeEnum()) {
//            case DOMAIN: rowIcon = MyIcons.domainLarge; break;
//            case APPLICATION: rowIcon = MyIcons.hexLarge; break;
//            case APPLICATION_VERSION: rowIcon = MyIcons.hexSmall; break;
//            case EVENT: rowIcon = MyIcons.circleLarge; break;
//            case EVENT_VERSION: rowIcon = MyIcons.circleSmall; break;
//            default: rowIcon = MyIcons.triangleLarge;
//            }
            JLabel folder = new JLabel(rowIcon);
            con.gridx = 1;
            panel.add(folder, con);
            con.gridx = 2;
            con.weightx = 1;
            panel.add(new JLabel(EPObjectHelper.getName(pro.getType())), con);
            return panel;
		case 1:
			panel.setBorder(BorderFactory.createEmptyBorder());
            panel.setLayout(new GridBagLayout());
            con = new GridBagConstraints();
            con.ipadx = 3;
            con.insets = new Insets(0, 10 * (pro.getDepth()-1), 0, 0);
            con.anchor = GridBagConstraints.LINE_START;
            con.weightx = 0;
            panel.add(new JLabel(pro.getIcon()), con);
            con.gridx = 1;
            con.weightx = 1;
            con.insets = new Insets(0, 0, 0, 0);
            

//			AnActionLink externalLink2 = new AnActionLink("hello", null, com.intellij.openapi.actionSystem.ActionPlaces.UNKNOWN);
			ActionLink externalLink2 = new AnActionLink(pro.getName(), EMPTY_EVENT);
			externalLink2.setExternalLinkIcon();
			Font f = externalLink2.getFont();
			externalLink2.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
//			externalLink2.setForeground(table.getForeground());
			externalLink2.setForeground(blend(table.getForeground(), EPObjectHelper.getColor(pro.getType()), 0.15f));
			panel.add(externalLink2, con);
			return panel;

//			cellComponent.setForeground(blend(cellComponent.getForeground(), new Color(0.25f, 0.25f, 1f), 0.5f));
//			Color c = UIUtil.getDecoratedRowColor();
//			cellComponent.setForeground(PlatformColors.BLUE);


//			cellComponent.setForeground(new Color(63,63,191));
//			if (cellComponent instanceof JLabel) {
//				JLabel label = (JLabel)cellComponent;
//				label.setText(pro.getName() + " ↗");
//				panel.add(cellComponent, BorderLayout.LINE_START);
//				return panel;
//			}

		case 2:
			panel.setBorder(BorderFactory.createEmptyBorder());
            panel.setLayout(new GridBagLayout());
            con = new GridBagConstraints();
            con.ipadx = 5;
            con.insets = new Insets(0, 0, 0, 5);
            con.anchor = GridBagConstraints.LINE_START;
            con.weightx = 0.0;
            int x = 0;
            for (String note : pro.getNotes()) {
            	con.gridx = x++;
            	panel.add(new JLabel(note + (x < pro.getNotes().size() ? "; " : "")), con);
            }
        	con.gridx = x++;
            con.weightx = 1;
        	panel.add(new JLabel(""), con);
            return panel;			
		case 3:
			((JLabel)cellComponent).setText(pro.getState());
			switch (pro.getState()) {
			case "Draft": 
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#389FD6"), 0.3333f));
//				cellComponent.setForeground(Color.decode("#389FD6"));
				break;
			case "Released":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#59A869"), 0.3333f));
//				cellComponent.setForeground(Color.decode("#59A869"));
				break;
			case "Deprecated":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#EDA200"), 0.3333f));
//				cellComponent.setForeground(Color.decode("#EDA200"));
				break;
			case "Retired":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#DB5860"), 0.3333f));
//				cellComponent.setForeground(Color.decode("#DB5860"));
				break;
			default:
				cellComponent.setForeground(Color.GREEN);
				break;
					
			}
//			for (StateDTO state : EventPortalWrapper.INSTANCE.getStates()) {
//				state.getName();
//			}
//			cellComponent.setForeground(Color.GRAY);
			break;
		case 4:
			((JLabel)cellComponent).setText(pro.getDetails());
			cellComponent.setFont(new Font("Lucida Console", Font.PLAIN, 14));
			break;
		case 5:
			JLabel l = (JLabel)cellComponent;
			l.setText(pro.getLastUpdated());
			if (pro.getCreatedByUserId().equals(pro.getLastUpdatedByUserId())) {
				l.setToolTipText(String.format("Created and last updated by %s",
						EPW.getUserName(pro.getLastUpdatedByUserId())));
			} else {
				l.setToolTipText(String.format("Created by %s; Last updated by %s",
						EPW.getUserName(pro.getCreatedByUserId()),
						EPW.getUserName(pro.getLastUpdatedByUserId())));
			}
//			JToolTip jt = l.createToolTip();
//			jt.pai
			break;
		case 6:  // links / actions
			((JLabel)cellComponent).setText("");
			panel.setBorder(BorderFactory.createEmptyBorder());
            panel.setLayout(new GridBagLayout());
            panel.setLayout(new GridBagLayout());
            con = new GridBagConstraints();
            con.ipadx = 3;
            con.insets = new Insets(0, 0, 0, 10);
            con.anchor = GridBagConstraints.LINE_START;
            con.weightx = 1;
//            int x = 0;
			if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION ) {
				AnActionLink externalLink = new AnActionLink("Download AsyncAPI...", EMPTY_EVENT);
				externalLink.setExternalLinkIcon();
//				externalLink.setBackground(cellComponent.getBackground());
				panel.add(externalLink, con);
//				return externalLink;
			} else if (pro.getType() == EventPortalObjectType.EVENT_VERSION) {
//				ActionLink externalLink = new AnActionLink("Generate Schema POJO...", EMPTY_EVENT);
				ActionLink externalLink = new AnActionLink("Copy schema to clipboard...", EMPTY_EVENT);
//				externalLink.setBackground(cellComponent.getBackground());
				panel.add(externalLink, con);
			}
			return panel;
		default:
		}
		return cellComponent;
	}
}
