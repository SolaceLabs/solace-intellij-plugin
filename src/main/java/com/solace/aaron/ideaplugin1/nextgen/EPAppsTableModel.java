package com.solace.aaron.ideaplugin1.nextgen;

import java.util.List;

import javax.swing.table.TableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.util.NlsContexts.ColumnName;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;

/** A TableModel */
public class EPAppsTableModel extends ListTableModel<PortalRowObject> {

	private static final long serialVersionUID = 1L;

	public EPAppsTableModel(PortalColumnInfo @NotNull [] columnNames, @NotNull List<PortalRowObject> stackOverflowQuestions) {
		super(columnNames, stackOverflowQuestions);
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

	public static class PortalColumnInfo extends ColumnInfo<PortalRowObject, String> {
		// https://intellij-support.jetbrains.com/hc/en-us/community/posts/206123419-About-ColumnInfo
		// https://github.com/joewalnes/idea-community/blob/master/platform/util/src/com/intellij/util/ui/ListTableModel.java

		public PortalColumnInfo(@ColumnName String name) {
			super(name);
		}

		@Override
		public @Nullable String valueOf(PortalRowObject item) {
			switch (getName()) {
			case "Type":
				return item.getType();
			case "Name":
				return item.getLink();
			case "Notes":
				return item.getNotes();
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
		}

		@Override
		public @Nullable TableCellRenderer getRenderer(PortalRowObject item) {
			return new CustomRenderer(item);
		}
	}




	public static PortalColumnInfo[] generateColumnInfo() {
		PortalColumnInfo[] columnInfos = new PortalColumnInfo[COLUMNS.length];
		for (int i=0; i<COLUMNS.length; i++) {
			String eachColumnName = COLUMNS[i];
			columnInfos[i] = new PortalColumnInfo(eachColumnName);// {
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
