package com.solace.aaron.ideaplugin1.nextgen;

import javax.swing.table.TableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.util.NlsContexts.ColumnName;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;

/** A TableModel */
public class EPAppsTableModel2 extends ListTableModel<PortalRowObjectTreeNode> {

	private static final long serialVersionUID = 1L;
	private PortalRowObjectTreeNode root;

	public EPAppsTableModel2(PortalColumnInfo2 @NotNull [] columnNames/* , @NotNull List<PortalRowObjectTreeNode> rows */) {
		super(columnNames);
	}
	
	void setRoot(PortalRowObjectTreeNode root) {
		this.root = root;
	}
	
	PortalRowObjectTreeNode getRoot() {
		return root;
	}
	
	void flatten() {
		this.setItems(root.flatten());
	}

	public enum COLS {
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

	static final String[] COLUMNS = {
			"Type",
			"Name",
			"Notes",
			"State",
			"Topic",
			"Last Updated",
			"Action",
	};

	public static class PortalColumnInfo2 extends ColumnInfo<PortalRowObjectTreeNode, PortalRowObjectTreeNode> {
		// https://intellij-support.jetbrains.com/hc/en-us/community/posts/206123419-About-ColumnInfo
		// https://github.com/joewalnes/idea-community/blob/master/platform/util/src/com/intellij/util/ui/ListTableModel.java

		final TableCellRenderer renderer = new CustomRenderer2();
		
		public PortalColumnInfo2(@ColumnName String name) {
			super(name);
		}

		@Override
		public @Nullable PortalRowObjectTreeNode valueOf(PortalRowObjectTreeNode item) {
			return item;
/*			switch (getName()) {
			case "Type":
				StringBuilder sb = new StringBuilder();
//				for (int i=1; i<item.getDepth(); i++) {
//					sb.append("    ");
//				}
				return sb.toString() + item.getTypeEnum().toString();
			case "Name":
				return item.getLink();
			case "Notes":
				return item.getNotes().toString();
			case "State":
				return item.getState();
			case "Topic":
				return item.getDetails();
			case "Last Updated":
				return item.getLastUpdated();
				//                        case "Link":
				//                            return o.getLink();
			case "Action":
				return "";
			default:
				return "---";
			}
*/		}

		@Override
		public @Nullable TableCellRenderer getRenderer(PortalRowObjectTreeNode item) {
			return renderer;
//			return new CustomRenderer2();
		}
	}




	public static PortalColumnInfo2[] generateColumnInfo() {
		PortalColumnInfo2[] columnInfos = new PortalColumnInfo2[COLUMNS.length];
		for (int i=0; i<COLUMNS.length; i++) {
			String eachColumnName = COLUMNS[i];
			columnInfos[i] = new PortalColumnInfo2(eachColumnName);// {
/*				private TableCellRenderer getCustomizedRenderer2(final PortalRowObject o, final TableCellRenderer renderer) {
					//                            LinkLabel<String> l = new LinkLabel<>("View in Portal", AllIcons.Ide.External_link_arrow);
					//                            ActionLink l2 = new ActionLink("View in Portal");

					switch (eachColumnName) {
					//                                case "Type":
					case "Description / Topic":
						//                                    return (table, value, isSelected, hasFocus, row, column) -> new BoldLabel(value.toString());
						//                                	Component render = renderer.getTableCellRendererComponent(null, i, isSortable(), isSortable(), getAdditionalWidth(), getAdditionalWidth());
						//                                	return new CustomRenderer();




						return new TableCellRenderer() {
							@Override
							public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

								//								PortalColumnInfo.this.getRenderer(value);
								//								if (table.getCellRenderer(row, column) == null) {

								return new BoldLabel(value.toString());
								//								} else {
								//									table.getCellRenderer(row, column);
								//									Component parent = table.getCellRenderer(row, column).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
								//									Color bg = parent.getBackground();
								//									parent.setBackground(new Color(0.25f * column / COLUMNS.length, 0.25f * column / COLUMNS.length, 0.25f * column / COLUMNS.length));
								//									return parent;
								//								}


							}
						};

						//                                    break;
					case "Name":
						ActionLink externalLink2 = new ActionLink(o.getName());//, event -> {
						//                                        BrowserUtil.browse(o.getLink());
						//                                    });
						externalLink2.setExternalLinkIcon();
						return new TableCellRenderer() {
							@Override
							public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
									int row, int column) {
								// TODO Auto-generated method stub
								return externalLink2;
							}
						};
						//                                    return (table, value, isSelected, hasFocus, row, column) -> externalLink2;

						//                                case "Link":
					case "Action":
						if (o.getType().endsWith("App Ver")) {
							ActionLink externalLink = new ActionLink("Download AsyncAPI...", event -> {
								//                                          BrowserUtil.browse("http://google.com");
							});
							return (table, value, isSelected, hasFocus, row, column) -> externalLink;

							//                                      return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
						} else if (o.getType().endsWith("Event")) {
							ActionLink externalLink = new ActionLink("Generate Schema POJO...", event -> {
								//                                          BrowserUtil.browse("http://google.com");
							});
							return (table, value, isSelected, hasFocus, row, column) -> externalLink;

							//                                      return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
						}
						//                                	return null;
					default:
						//						return super.getCustomizedRenderer(o, renderer);
						return new CustomRenderer(o);
					}
				}



			};          */
			//			i.getAndIncrement();
		}
		//	);
		return columnInfos;
	}

	
	
	
	
	
	
	
	

}
