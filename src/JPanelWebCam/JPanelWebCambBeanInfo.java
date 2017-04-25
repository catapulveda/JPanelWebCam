package JPanelWebCam;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class JPanelWebCambBeanInfo extends SimpleBeanInfo{
    
    Image icon16;
    Image icon32;

    public JPanelWebCambBeanInfo() {
        icon16 = loadImage("/icons/camera16.png");
        icon32 = loadImage("/icons/camera32.png");
    }

    @Override
    public Image getIcon(int iconKind) {
        switch(iconKind){
            case 1:
                return icon16;
            case 2: 
                return icon32;
            case 3: 
                return icon16;
            case 4:
                return icon32;            
        }
        return null;
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        super.getBeanDescriptor();
        BeanDescriptor bd = new BeanDescriptor(getClass());
        bd.setExpert(true);
        bd.setHidden(false);
        bd.setDisplayName("JPanelWebCam");
        bd.setPreferred(true);
        bd.setShortDescription("Muestra la imagen detectada por las camaras seleccionada.");
        return bd;
        
    }
    
    
    
}
