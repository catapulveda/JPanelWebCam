package JPanelWebCam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class JPanelWebCam extends JPanel implements MouseListener, WebcamDiscoveryListener {

    private JComboBox<Webcam> combo;
    private Webcam webcam = Webcam.getDefault();
    
    private Image image;
    
    public JPanelWebCam() {
        
        addMouseListener(this);
        
        this.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    if (dtde.getDropAction() == 2) {
                        dtde.acceptDrop(dtde.getDropAction());
                        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                            dtde.acceptDrop(DnDConstants.ACTION_COPY);
                            List<File> lista = (List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            if(lista.size()==1){                        
                                setImagen(ImageIO.read(new File(lista.get(0).getAbsolutePath())));
                                setBorder(javax.swing.BorderFactory.createEtchedBorder());
                            }else if(lista.size()>1){
                                JOptionPane.showMessageDialog(null, "SÓLO UNA IMGAEN A LA VEZ.");
                            }
                        }else{
                            JOptionPane.showMessageDialog(null, "ÉSTE TIPO DE ARCHIVO NO ES SOPORTADO.");
                        }
                    }
                } catch (UnsupportedFlavorException | IOException | HeadlessException e) {
                    JOptionPane.showMessageDialog(null, "ERROR AL IMPORTAR LA IMAGEN\n"+e);
                }
            }
        }));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image!=null){
            int pw = getWidth();
            int ph = getHeight();
            int iw = image.getWidth(null);
            int ih = image.getHeight(null);
            
            int x = 0;
            int y = 0;
            int w = 0;
            int h = 0;
            
            double s = Math.max((double) iw / pw, (double) ih / ph);
            double niw = iw / s;
            double nih = ih / s;
            double dx = (pw - niw) / 2;
            double dy = (ph - nih) / 2;
            w = (int) niw;
            h = (int) nih;
            x = (int) dx;
            y = (int) dy;
            
            if(getHeight()>image.getHeight(null)&&getWidth()>image.getWidth(null)){
                w = image.getWidth(null);
                h = image.getHeight(null);
                x = (w<getWidth())?(getWidth()/2)-(w/2):0;
                y = (h<getHeight())?(getHeight()/2)-(h/2):0;
            }
            
            g.drawImage(image, x, y, w, h, this);
        }
    }

    
    
    @Override
    public void mouseClicked(MouseEvent e){
        
        if(webcam==null){
            JOptionPane.showMessageDialog(this, "NO HAY CAMARAS DISPONIBLES", "CAMARA NO ENCONTRADA", JOptionPane.INFORMATION_MESSAGE);
        }else{
            if(webcam.isOpen()){
                //webcampanel.stop();
                webcam.close();
            }else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==1) {
                combo = new JComboBox();
                for (Webcam cam : Webcam.getWebcams()) {
                    combo.addItem(cam);                
                }

                JOptionPane opciones = new JOptionPane();
                opciones.setMessage(combo);
                JDialog dialog = opciones.createDialog("Seleccione una camara");
                dialog.setVisible(true);

                if(null!=opciones.getValue()){
                    webcam = combo.getItemAt(combo.getSelectedIndex());
                    setImagen((new ImageIcon(getClass().getResource("/icons/cargandocamara.gif")).getImage()));                    
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            //webcampanel.start();
                            if(webcam.open()){
                                while(webcam.isOpen()){
                                    setImagen(webcam.getImage());
                                    repaint();
                                }
                            }
                        }
                    };
                    t.setName("Iniciando camara");
                    t.setDaemon(true);
                    t.start();
                }
            }
        }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void webcamFound(WebcamDiscoveryEvent wde) {
        
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent wde) {
        
    }

    public Image getImage() {
        return image;
    }

    public void setImagen(Image image) {
        this.image = image;
        repaint();
    }
    
    public void setImagen(byte[] imagenBytes) {
        try {
            if(imagenBytes!=null){
                this.image = ImageIO.read(new ByteArrayInputStream(imagenBytes));
                repaint();
            }            
        } catch (IOException ex){
            Logger.getLogger(JPanelWebCam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] getBytes(){
        try {
            ByteArrayOutputStream os =  new  ByteArrayOutputStream ();
            if(image!=null){                
                ImageIO.write((RenderedImage) image,  "jpg" , os);
                os.flush();
            }            
            return os.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(JPanelWebCam.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
