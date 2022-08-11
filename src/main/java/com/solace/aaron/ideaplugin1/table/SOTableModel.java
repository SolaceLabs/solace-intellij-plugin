package com.solace.aaron.ideaplugin1.table;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.table.TableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.solace.aaron.ideaplugin1.domain.EventPortalDomain;

public class SOTableModel extends ListTableModel<EventPortalDomain>
{
	private static final long serialVersionUID = 1L;
	
	public SOTableModel(ColumnInfo<EventPortalDomain, String> @NotNull [] columnNames, @NotNull List<EventPortalDomain> stackOverflowQuestions)
    {
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
    public static ColumnInfo<EventPortalDomain, String>[] generateColumnInfo() {
        ColumnInfo<EventPortalDomain, String>[] columnInfos = new ColumnInfo[COLUMNS.length];
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(COLUMNS).forEach(eachColumn -> {
                    columnInfos[i.get()] = new ColumnInfo<>(eachColumn)
                    {
                        @Nullable
                        @Override
                        public String valueOf(EventPortalDomain o)
                        {
                            switch (eachColumn)
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
                        public TableCellRenderer getCustomizedRenderer(EventPortalDomain o, TableCellRenderer renderer)
                        {
                            switch (eachColumn)
                            {
                                case "Title":
                                    return (table, value, isSelected, hasFocus, row, column) -> new BoldLabel(value.toString());
                                case "Link":
                                    return (table, value, isSelected, hasFocus, row, column) -> new LinkLabel<String>(value.toString(), AllIcons.Ide.External_link_arrow);
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
