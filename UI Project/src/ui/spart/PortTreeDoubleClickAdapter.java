package ui.spart;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ctrl.spart.StatePresenter;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-27 17:36:11
 */
public class PortTreeDoubleClickAdapter extends MouseAdapter {
	
	private StatePresenter fPresenter;
	private JTree fJTree ;
	public PortTreeDoubleClickAdapter(JTree tree, StatePresenter presenter) {
		fJTree = tree;
		fPresenter = presenter;
	}

	@Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TreePath selPath = fJTree.getPathForLocation(e.getX(), e.getY());
            if (selPath != null)
            {
            	DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if(node.getLevel() == 0) return;
//                System.out.println(node.toString());
                
        		EventQueue.invokeLater(new Runnable() {
        			public void run() {
    					fPresenter.switchPortOpenState(node.toString());
        			}
        		});
            }
        }

    }
}
