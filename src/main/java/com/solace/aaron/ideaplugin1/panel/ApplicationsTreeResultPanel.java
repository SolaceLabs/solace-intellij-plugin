package com.solace.aaron.ideaplugin1.panel;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.treeStructure.Tree;
import com.solace.aaron.ideaplugin1.table.AppsTree;

public class ApplicationsTreeResultPanel extends NonOpaquePanel
{
//    private TableView<EventPortalDomain> resultsTable;

	private static final long serialVersionUID = 1L;
	
	private AppsTree tree;

    public ApplicationsTreeResultPanel(AppsTree tree) {
        this.tree = tree;
//        this.init();
        
    }
    
    public void load() {
    	init();
    }

    private void init() {
        this.setBorder(BorderFactory.createEmptyBorder());
        JPanel scrollPanel = new JPanel();
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setLayout(new BorderLayout());

        DefaultMutableTreeNode root;
        DefaultMutableTreeNode cursor;
        DefaultMutableTreeNode next;
        root = new DefaultMutableTreeNode("root");
        cursor = root;
        next = new DefaultMutableTreeNode("first");
        cursor.add(next);
        next = new DefaultMutableTreeNode("second");
        cursor.add(next);
        next = new DefaultMutableTreeNode("third");
        cursor.add(next);
        cursor = next;
        next = new DefaultMutableTreeNode("grandchild");
        cursor.add(next);
//        Tree tree = new Tree(root);


        MutableTreeNode rootNode = AppsTree.drawAppsTree();
        Tree tree = new Tree(rootNode);
        tree.getEmptyText().setText("Load soem data here!!!");
        class ATree extends Tree {

            String converValueToText() {

                return "";
            }
        }


        scrollPanel.add(ScrollPaneFactory.createScrollPane(/*this.*/tree), BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(scrollPanel, BorderLayout.CENTER);
    }
}
