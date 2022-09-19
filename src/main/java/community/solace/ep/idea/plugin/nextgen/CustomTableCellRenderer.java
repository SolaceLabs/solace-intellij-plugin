package community.solace.ep.idea.plugin.nextgen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.table.TableView;

import community.solace.ep.idea.plugin.utils.EPObjectHelper;
import community.solace.ep.idea.plugin.utils.TimeUtils;
import community.solace.ep.wrapper.EventPortalObjectType;
import community.solace.ep.wrapper.EventPortalWrapper;
import icons.MyIcons;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	
	public static class Test implements TableCellEditor {
		
		final TableView<PortalRowObjectTreeNode> tableView;
		
		public Test(TableView<PortalRowObjectTreeNode> tableView) {
			this.tableView = tableView;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");
			return false;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	private static final long serialVersionUID = 1L;
	private static final EventPortalWrapper EPW = EventPortalWrapper.INSTANCE;

	static Color blend(Color orig, Color accent, float amount) {
		return new Color((int)(orig.getRed() * (1-amount) + accent.getRed() * amount),
				(int)(orig.getGreen() * (1-amount) + accent.getGreen() * amount),
				(int)(orig.getBlue() * (1-amount) + accent.getBlue() * amount));
	}

	static Color opposite(Color orig) {
		return new Color(255-orig.getRed(), 255-orig.getGreen(), 255-orig.getBlue());
	}
	
	 static class MessageAction extends AnAction {
		
		final String message;
		
		public MessageAction(String message) {
			super(message);
			this.message = message;
		}
		
		@Override
		public void actionPerformed(@NotNull AnActionEvent e) {
			JOptionPane.showMessageDialog(null, message);
		}
	};

	@Override
	public Component getTableCellRendererComponent(JTable table, Object rowNode, boolean isSelected, boolean hasFocus, int row, int column) {

		PortalRowObjectTreeNode pro = (PortalRowObjectTreeNode)rowNode;
		Component cellComponent = super.getTableCellRendererComponent(table, pro, false, hasFocus, row, column);
		
		// background colour first
		Color proTypeColor = EPObjectHelper.getColor(pro.getType());
		switch (pro.getType()) {
		case DOMAIN:
		case APPLICATION:
		case EVENT:
		case SCHEMA:
		case EVENT_API:
		case EVENT_API_PRODUCT:
//			cellComponent.setBackground(blend(table.getBackground(), opposite(table.getBackground()), 0.05f));
			cellComponent.setBackground(blend(table.getBackground(), proTypeColor, 0.05f));
			break;
		case APPLICATION_VERSION:
		case EVENT_VERSION:
		case SCHEMA_VERSION:
		case EVENT_API_VERSION:
		case EVENT_API_PRODUCT_VERSION:
			cellComponent.setBackground(blend(table.getBackground(), proTypeColor, 0.03f));
			break;
		default:
			cellComponent.setBackground(blend(table.getBackground(), proTypeColor, 0.1f));
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
            
            if (!pro.hasChildren()) panel.add(new JLabel(AllIcons.Nodes.EmptyNode), con);
            else if (pro.isExpanded()) panel.add(new JLabel(AllIcons.Actions.FindAndShowNextMatches), con);
            else panel.add(new JLabel(AllIcons.Actions.Play_forward), con);
            con.insets = new Insets(0, 0, 0, 0);
            Icon rowIcon = pro.getIcon();
            JLabel folder = new JLabel(rowIcon);
            con.gridx = 1;
            panel.add(folder, con);
            con.gridx = 2;
            con.weightx = 1;
            panel.add(new JLabel(EPObjectHelper.getName(pro.getType())), con);
            return panel;
		case 1:  // the name
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
            
			ActionLink externalLink2 = new AnActionLink(pro.getName(), new MessageAction("nav link " + pro.getName()));
			if (!pro.getLink().isEmpty()) externalLink2.setExternalLinkIcon();
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
            for (String note : pro.getDetails()) {
            	con.gridx = x++;
            	panel.add(new JLabel(note + (x < pro.getDetails().size() ? "; " : "")), con);
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
				break;
			case "Released":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#59A869"), 0.3333f));
				break;
			case "Deprecated":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#EDA200"), 0.3333f));
				break;
			case "Retired":
				cellComponent.setForeground(blend(table.getForeground(), Color.decode("#DB5860"), 0.3333f));
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
			((JLabel)cellComponent).setText(pro.getTopic());
			cellComponent.setFont(new Font("Lucida Console", Font.PLAIN, 14));
			break;
		case 5:
			JLabel l = (JLabel)cellComponent;
			if (pro.getLastUpdated() > 0) {
				l.setText(TimeUtils.formatTime(pro.getLastUpdated()));
				if (pro.getCreatedByUserId().equals(pro.getLastUpdatedByUserId())) {
					l.setToolTipText(String.format("Created and last updated by %s",
							EPW.getUserName(pro.getLastUpdatedByUserId())));
				} else {
					l.setToolTipText(String.format("Created by %s; Last updated by %s",
							EPW.getUserName(pro.getCreatedByUserId()),
							EPW.getUserName(pro.getLastUpdatedByUserId())));
				}
			} else {
				l.setText("");
			}
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
			if (pro.getType() == EventPortalObjectType.APPLICATION_VERSION || pro.getType() == EventPortalObjectType.EVENT_API_VERSION) {
				AnActionLink link = new AnActionLink("View AsyncAPI", new MessageAction("asyncApi " + pro.getName()));
//				link.setIcon(MyIcons.AsyncApi2, false);
//				externalLink.setExternalLinkIcon();
//				externalLink.setBackground(cellComponent.getBackground());
				panel.add(link, con);
				
//				link = new AnActionLink("Download Codegen...", new MessageAction("download " + pro.getName()));
//				link.setExternalLinkIcon();
//				externalLink.setBackground(cellComponent.getBackground());
//				panel.add(link, con);

//				return externalLink;
			} else if (pro.getType() == EventPortalObjectType.EVENT_VERSION) {
//				ActionLink externalLink = new AnActionLink("Generate Schema POJO...", EMPTY_EVENT);
//				ActionLink externalLink = new AnActionLink("Copy schema to clipboard...", EMPTY_EVENT);
//				externalLink.setBackground(cellComponent.getBackground());
//				panel.add(externalLink, con);
			} else if (pro.getType() == EventPortalObjectType.SCHEMA_VERSION && !pro.getLink().isBlank()) {
//				ActionLink externalLink = new AnActionLink("Generate Schema POJO...", EMPTY_EVENT);
				ActionLink link = new AnActionLink("View schema", new MessageAction("view schema " + pro.getName()));
//				externalLink.setBackground(cellComponent.getBackground());
				panel.add(link, con);
//			} else if (pro.getType() == EventPortalObjectType.SCHEMA) {
//				ActionLink link = new AnActionLink("View description", new MessageAction("Nothing "));
//				panel.add(link, con);
				
			}
			return panel;
		case 7:
			((JLabel)cellComponent).setText(pro.getId());
			break;
		default:
			((JLabel)cellComponent).setText("--DEFAULT CELL RENDER--");
		}
		return cellComponent;
	}
}
