package community.solace.ep.idea.plugin.nextgen;

import javax.swing.table.TableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.util.NlsContexts.ColumnName;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;

/** A TableModel */
public class PortalTableModel extends ListTableModel<PortalRowObjectTreeNode> {

	private static final long serialVersionUID = 1L;
	private PortalRowObjectTreeNode root;

	public PortalTableModel(PortalColumnInfo @NotNull [] columnNames) {
		super(columnNames);
	}
	
	void setRoot(PortalRowObjectTreeNode root) {
		this.root = root;
	}
	
	PortalRowObjectTreeNode getRoot() {
		return root;
	}
	
	void flatten() {
		this.setItems(root.flatten(true));
	}

/*	public enum COLS {
		TYPE("Type"),
		NAME("Name"),
		NOTES("Notes"),
		STATE("State"),
		TOPIC("Topic"),
		LAST_UPDATED("Last Updated"),
		ACTION("Action"),
		;

		private final String name;

		COLS(String name) {
			this.name = name;
		}
	}
*/
	static final String[] COLUMNS = {
			"Type",
			"Name",
			"Notes",
			"State",
			"Topic",
			"Last Updated",
			"Action",
			"ID",
	};

	public static class PortalColumnInfo extends ColumnInfo<PortalRowObjectTreeNode, PortalRowObjectTreeNode> {
		// https://intellij-support.jetbrains.com/hc/en-us/community/posts/206123419-About-ColumnInfo
		// https://github.com/joewalnes/idea-community/blob/master/platform/util/src/com/intellij/util/ui/ListTableModel.java

		final TableCellRenderer renderer = new CustomTableCellRenderer();
		
		public PortalColumnInfo(@ColumnName String name) {
			super(name);
		}

		@Override
		public @Nullable PortalRowObjectTreeNode valueOf(PortalRowObjectTreeNode item) {
			return item;
		}

		@Override
		public @Nullable TableCellRenderer getRenderer(PortalRowObjectTreeNode item) {
			return renderer;
		}
	}


	public static PortalColumnInfo[] generateColumnInfo() {
		PortalColumnInfo[] columnInfos = new PortalColumnInfo[COLUMNS.length];
		for (int i=0; i<COLUMNS.length; i++) {
			String eachColumnName = COLUMNS[i];
			columnInfos[i] = new PortalColumnInfo(eachColumnName);
		}
		return columnInfos;
	}

}
