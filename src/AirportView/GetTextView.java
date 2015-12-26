package AirportView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;


public class GetTextView {
	
    private static GetTextView mSingleton;
    private javax.swing.JFrame frame;
	
    public static GetTextView getDragDrop() 
	{
		if(mSingleton == null)
			mSingleton = new GetTextView();
		return mSingleton;
	}

    private static void setMview(GetTextView v){
    	mSingleton = v;
    }
    
	public GetTextView(){
		 frame = new javax.swing.JFrame( "FileDrop" );
        javax.swing.border.TitledBorder dragBorder = new javax.swing.border.TitledBorder( "Drop File here" );
        final javax.swing.JTextArea text = new javax.swing.JTextArea();
        frame.getContentPane().add( 
            new javax.swing.JScrollPane( text ), 
            java.awt.BorderLayout.CENTER );
        
        frame.setJMenuBar(new MenuBar());
        
        new FileDrop( System.out, text, dragBorder, new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {  try
                    {   text.append( files[0].getCanonicalPath() );
                    }   // end try
                    catch( java.io.IOException e ) {}
            }   // end filesDropped
        }); // end FileDrop.Listener

        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
               frame.dispose();
               mSingleton = null;
            }
        });
        frame.setBounds( 100, 100, 300, 400 );
        frame.setVisible(true);
	}
	
    @SuppressWarnings("serial")
	public class MenuBar extends JMenuBar{
    	/**
		 * 
		 */

		public MenuBar(){
    		
    		JMenu menu = new JMenu("File");
            
    		JMenuItem item = new JMenuItem("Charger Fichier ...");
            item.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                	JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileFilter() {
						
						@Override
						public String getDescription() {
							// TODO Auto-generated method stub
							return null;
						}
						
						@Override
						public boolean accept(File f) {
							 if (f.isDirectory()) {
							        return true;
							    }

							 String ext = null;
					         String s = f.getName();
					         int i = s.lastIndexOf('.');

					         if (i > 0 &&  i < s.length() - 1) {
					                ext = s.substring(i+1).toLowerCase();
					         }
							    if (ext != null) {
							        if (ext.equals("txt") ||
							            ext.equals("simu")) {
							                return true;
							        } else {
							            return false;
							        }
							    }

							    return false;
						}
					});
                	int returnVal = fc.showOpenDialog(GetTextView.this.frame);
                	
                	if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would open the file.
                        System.out.println("Opening: " + file.getName() + "." +"\n");
                    } else {
                    	System.out.println("Open command cancelled by user." + "\n");
                    }

                }

            });
            
            JMenuItem item2 = new JMenuItem("Exit");
            item2.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    GetTextView.setMview(null);
                    GetTextView.this.frame.dispose();
                }

            });
            menu.add(item);
            menu.add(item2);
            this.add(menu);
    	}
    }
	
}
