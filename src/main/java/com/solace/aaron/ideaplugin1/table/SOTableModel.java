package com.solace.aaron.ideaplugin1.table;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.table.TableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.util.NlsContexts.ColumnName;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;

public class SOTableModel extends ListTableModel<EventPortalDomain> {
	
	private static final long serialVersionUID = 1L;
	
	public SOTableModel(ColumnInfo<EventPortalDomain, String> @NotNull [] columnNames, @NotNull List<EventPortalDomain> stackOverflowQuestions) {
        super(columnNames, stackOverflowQuestions);
        
    }

    static final String[] COLUMNS = {
            "Application Domain",
            "ID",
            "Link",
            "# Applications",
            "# Events",
            "# Schemas",
            "# Event APIs",
            "Created By",
            "Created On",
            "Last Updated",
            "Last Updated By"
    };
    
    private static class AaronColumnInfo extends ColumnInfo<EventPortalDomain, String> {

		public AaronColumnInfo(@ColumnName String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		@Override
		public @Nullable String valueOf(EventPortalDomain item) {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
    
    public static ColumnInfo<EventPortalDomain, String>[] generateColumnInfo() {


    	AaronColumnInfo[] columnInfos = new AaronColumnInfo[COLUMNS.length];
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(COLUMNS).forEach(eachColumnName -> {
                    columnInfos[i.get()] = new AaronColumnInfo(eachColumnName) {
                        @Nullable
                        @Override
                        public String valueOf(EventPortalDomain o)
                        {
                            switch (eachColumnName)
                            {
                                case "ID":
                                    return o.getId();
                                case "Link":
                                    return o.getLink();
                                case "Application Domain":
                                    return o.getTitle();
                                case "Answer Count":
                                    return o.getAnswerCount().toString();
                                case "Created On":
                                    return o.getCreationDate().toString();
                                case "Last Updated":
                                    return o.getLastActivity().toString();
                                default:
                                    return "Not Available";
                            }
                        }

                        @Override
                        public TableCellRenderer getCustomizedRenderer(EventPortalDomain o, TableCellRenderer renderer) {
//                            LinkLabel<String> l = new LinkLabel<>("View in Portal", AllIcons.Ide.External_link_arrow);
//                            ActionLink l2 = new ActionLink("View in Portal");
                            
                            switch (eachColumnName) {
                                case "Title":
                                    return (table, value, isSelected, hasFocus, row, column) -> new BoldLabel(value.toString());
                                case "Link":
                                    ActionLink externalLink = new ActionLink("View in Portal", event -> {
//                                        BrowserUtil.browse("http://google.com");
                                    });
                                    externalLink.setExternalLinkIcon();
                                    return (table, value, isSelected, hasFocus, row, column) -> externalLink;
                                	
//                                    return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
                                default:
                                    return super.getCustomizedRenderer(o, renderer);
                            }
                            
                        }

                    };
                    i.getAndIncrement();
                }	
        );
        return columnInfos;
    }
}
